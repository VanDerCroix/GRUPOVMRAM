/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package presentacion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import sistema.Memoria;
import sistema.Proceso;
import sistema.Registro;

public class FormularioPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form FormularioPrincipal
     */
    
    Memoria m;
    FormularioTabla ft;
    DecimalFormat df = new DecimalFormat("#,###.###");
    public static boolean ventanaProcesosAbierta = false;    
    
    
    private class PanelGrafico extends JPanel {
        
        ArrayList<Color> colores = new ArrayList<>();
        
        ArrayList<ProcesoConColor> lpc  = new ArrayList<>();
        
        int coloresElegidos = 0;
        
        int tamGraficoRegistro;
        
        public PanelGrafico(){
            super();
            inicializarColores();            
        }
        
        private void inicializarColores(){
        colores.add(Color.red);
        colores.add(Color.green);
        colores.add(Color.blue);
        colores.add(Color.orange);
        colores.add(Color.yellow);
        colores.add(Color.pink);
        colores.add(Color.magenta);
        colores.add(Color.gray);   
        colores.add(Color.DARK_GRAY);
        colores.add(Color.cyan);
        colores.add(Color.lightGray);
    }        
        public void paint(Graphics g){            
            super.paint(g);        
            tamGraficoRegistro = m.getMemoriaSistema()/this.getHeight();
            analizarProcesosNuevos();
            analizarProcesosTerminados();
            int regGraficoIni, regGraficoFin;//Registros graficos de proceso
            int regLibreIni = -1, regLibreFin = -1;
            int regGraficoLibreIni, regGraficoLibreFin;//Registros graficos de espacios libres
            int cantRegGrafLibres = 0 ;
            Color color;
            Proceso p;
            ArrayList<Registro> lr = m.getRegistros();            
            for( int i = 0 ; i < lr.size() ; i += tamGraficoRegistro ){
                if( !lr.get(i).isOcupado() ){                    
                    regGraficoLibreIni = i/tamGraficoRegistro ;
                    //Se busca la cantidad de registros graficos a llenar
                    while( !lr.get(i).isOcupado()  ){                        
                        i += tamGraficoRegistro;
                        cantRegGrafLibres++;
                        if( i >= lr.size() ) break;
                    }
                    regGraficoLibreFin = (i - tamGraficoRegistro)/tamGraficoRegistro;    
                    //Se pintan los registros graficos de blanco
                    g.setColor(Color.white);
                    g.fillRect(GraficoMemoria.getWidth()/2 - 100, GraficoMemoria.getHeight()-(i/tamGraficoRegistro),200, cantRegGrafLibres);
                    //Se escribe "ESPACIO LIBRE"
                    g.setColor(Color.black);
                    g.setFont(new Font("Times New Roman", Font.BOLD, 11));
                    g.drawString("ESPACIO LIBRE",GraficoMemoria.getWidth()/2 - 42
                            ,GraficoMemoria.getHeight() - (int)((regGraficoLibreIni+regGraficoLibreFin)/2));
                    //Se vuelve i a un estado anterior y se inicializa cantRegGrafLibres
                    cantRegGrafLibres = 0;
                    i -= tamGraficoRegistro;
                }
                else{
                    p = lr.get(i).getProceso();
                    regGraficoIni = p.getRegInicio()/tamGraficoRegistro;
                    regGraficoFin = p.getRegFin()/tamGraficoRegistro;            
                    color = buscarColor(p);
                    g.setColor(color);
                    while( lr.get(i).getProceso().equals(p) ){
                        g.fillRect(GraficoMemoria.getWidth()/2 - 100, GraficoMemoria.getHeight()-(i/tamGraficoRegistro),200, 1);
                        i += tamGraficoRegistro;
                    }
                    i -= tamGraficoRegistro;
                    escribirNombreProceso(g, p, regGraficoIni, regGraficoFin);
                    escribirRegistrosProceso(g, p, regGraficoIni, regGraficoFin);
                    dibujarLineaLimite(g, regGraficoIni,regGraficoFin);
                    if( p.getRegInicio()-1 >=0 && !lr.get(p.getRegInicio()-1).isOcupado()){
                        regLibreFin = p.getRegInicio()-1;
                    }
                    if( regLibreIni !=-1 && regLibreFin !=-1 ){
                        escribirRegistrosLibres(g, regLibreIni, regLibreFin);
                        regLibreIni = -1;
                        regLibreFin = -1;
                    }
                    if( p.getRegFin()+1<=lr.size() && !lr.get(p.getRegFin()+1).isOcupado()  ){
                        regLibreIni = p.getRegFin()+1;
                    }
                    
                }                
            }
            escribirRegistrosLibres(g, regLibreIni, lr.size()-1);
            dibujarBordes(g);
        }  
        
        private void dibujarBordes(Graphics g){
            g.setColor(Color.black);
            g.drawLine(GraficoMemoria.getWidth()/2 - 100, 0, GraficoMemoria.getWidth()/2 - 100, getHeight());
            g.drawLine(GraficoMemoria.getWidth()/2 + 100, 0, GraficoMemoria.getWidth()/2 + 100, getHeight());
            g.drawLine(GraficoMemoria.getWidth()/2 - 100, 0, GraficoMemoria.getWidth()/2 + 100, 0);
            g.drawLine(GraficoMemoria.getWidth()/2 - 100,getHeight()-1,GraficoMemoria.getWidth()/2 + 100,getHeight()-1);
        }
        
        private void escribirNombreProceso(Graphics g,Proceso p, int regGraficoIni, int regGraficoFin){
            g.setColor(Color.black);
            g.setFont(new Font("Times New Roman", Font.BOLD, 11));
            g.drawString(p.getNombre()+" - "+df.format(p.getPeso())+" KB",GraficoMemoria.getWidth()/2 - 75 
                        ,GraficoMemoria.getHeight() - (int)((regGraficoIni+regGraficoFin)/2));
        }
        
        private void escribirRegistrosProceso(Graphics g,Proceso p, int regGraficoIni, int regGraficoFin){            
            g.setFont(new Font("Times New Roman", Font.PLAIN, 11));
            /*g.drawString(p.getRegInicio()+"", GraficoMemoria.getWidth()/2 + 105 , GraficoMemoria.getHeight()- regGraficoIni - 5  );                    
            g.drawString(p.getRegFin()+"", GraficoMemoria.getWidth()/2 + 105 , GraficoMemoria.getHeight()- regGraficoFin + 15 ); */
            g.drawString("Registros ->  "+df.format(p.getRegInicio())+" : "+df.format(p.getRegFin()),GraficoMemoria.getWidth()/2 +105 
                        ,GraficoMemoria.getHeight() - (int)((regGraficoIni+regGraficoFin)/2)+3);
        }
        
        private void escribirRegistrosLibres(Graphics g, int regLibreIni, int regLibreFin){    
            int regGraficoIni = regLibreIni/tamGraficoRegistro;
            int regGraficoFin = regLibreFin/tamGraficoRegistro;
            g.setFont(new Font("Times New Roman", Font.PLAIN, 11));
            g.drawString("Reg. libres->  "+df.format(regLibreIni)+" : "+df.format(regLibreFin),GraficoMemoria.getWidth()/2 +105 
                        ,GraficoMemoria.getHeight() - (int)((regGraficoIni+regGraficoFin)/2)+3);
        }
        
        private void dibujarLineaLimite(Graphics g, int regGraficoIni, int regGraficoFin){
            g.drawLine(GraficoMemoria.getWidth()/2 - 100, GraficoMemoria.getHeight()- regGraficoIni , GraficoMemoria.getWidth()/2 + 100, GraficoMemoria.getHeight()- regGraficoIni );
            for( int i = 0 ; i < 18 ; ){
                g.drawLine(GraficoMemoria.getWidth()/2 + 100+(i++*3), GraficoMemoria.getHeight()- regGraficoIni , GraficoMemoria.getWidth()/2 + 100+(i++*3), GraficoMemoria.getHeight()- regGraficoIni );
            }
            g.drawLine(GraficoMemoria.getWidth()/2 - 100, GraficoMemoria.getHeight()- regGraficoFin , GraficoMemoria.getWidth()/2 + 100, GraficoMemoria.getHeight()- regGraficoFin );
            for( int i = 0 ; i < 18 ; ){
                g.drawLine(GraficoMemoria.getWidth()/2 + 100+(i++*3), GraficoMemoria.getHeight()- regGraficoFin , GraficoMemoria.getWidth()/2 + 100+(i++*3), GraficoMemoria.getHeight()- regGraficoFin );
            }
        }
        
        private Color buscarColor( Proceso p ){
            Color c = null;
            for( ProcesoConColor pc : lpc ){
                if( pc.getProceso().equals(p) ){
                    c = pc.getColor();
                    return c;
                }
            }
            return c;
        }
        
        private void analizarProcesosNuevos( ){
            boolean estaColoreado;
            ArrayList<Proceso> lp = m.getProcesos();
            for( Proceso p : lp ){
                estaColoreado = false;
                for( ProcesoConColor pc : lpc ){
                    if( pc.getProceso().equals(p) ){
                        estaColoreado = true;
                        break;
                    }
                }
                if(!estaColoreado){
                    lpc.add(new ProcesoConColor(p, obtenerColorAleatorio()));
                }
            }
        }
        
        private void analizarProcesosTerminados( ){
            ArrayList<Proceso> lp = m.getProcesos();
            ArrayList<ProcesoConColor> lpcAux = new ArrayList<>() ;
            for( ProcesoConColor pc : lpc ){
                for( Proceso p : lp ){
                    if( pc.getProceso().equals(p) ){
                        lpcAux.add(pc);
                    }
                } 
            }
            lpc = lpcAux;
        }
       
        private Color obtenerColorAleatorio(){
            boolean colorAsignado;
            int indColor;
            Random r = new Random();            
            do{                
                indColor = (int)(r.nextDouble()*colores.size());
                colorAsignado = false;
                if( coloresElegidos < colores.size() ){
                    for(ProcesoConColor pc : lpc){                    
                        if( colores.get( indColor ).equals(pc.getColor()) ){
                            colorAsignado = true;
                            break;
                        }
                    }
                }
            }while(colorAsignado);
            System.out.println(colores.get(indColor).toString());
            coloresElegidos++;
            return colores.get( indColor );            
        }
        
        
    }
    
    private class ProcesoConColor {
        
        Proceso proceso;
        Color color;

        public ProcesoConColor(Proceso proceso, Color color) {
            this.proceso = proceso;
            this.color = color;
        }

        public Proceso getProceso() {
            return proceso;
        }

        public void setProceso(Proceso proceso) {
            this.proceso = proceso;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }       
        
    }
       
    private void actualizarFormulario(){        
        Thread t = new Thread(){
            int intervalo;
            public void run(){
                while(true){                    
                    try{
                        m.actualizarMemoria();
                        espOcupado.setText(df.format(m.getEspacioOcupado())+" KB");
                        espLibre.setText(df.format(m.getEspacioLibre())+" KB");
                        GraficoMemoria.repaint();     
                        if(ventanaProcesosAbierta){
                            ft.listarProcesos(m.getProcesos());
                        }
                        /*try{
                            intervalo = Integer.parseInt(cmpIntervalo.getText());
                            if( intervalo < 500 || intervalo > 5000){
                                lblObservaciones.setForeground(Color.red);
                                lblObservaciones.setFont(new Font("Cambria Math",Font.BOLD,13));
                                lblObservaciones.setText("INTERVALO NO PERMITIDO, SE USARA "
                                    + "EL VALOR POR DEFECTO: 1000 ms");
                                intervalo = 1000;
                            }else{
                                lblObservaciones.setText("");
                            }
                            Thread.sleep( intervalo );                            
                        }catch(InterruptedException | NumberFormatException e){
                            lblObservaciones.setForeground(Color.red);
                            lblObservaciones.setFont(new Font("Cambria Math",Font.BOLD,13));
                            lblObservaciones.setText("INTERVALO NO PERMITIDO, SE USARA "
                                    + "EL VALOR POR DEFECTO: 1000 ms");
                            Thread.sleep(1000);
                        }*/
                        Thread.sleep(3000);
                    }catch(Exception e){
                        System.err.println("Error en hilo: "+e.getMessage());
                    }                    
                }
            }
        };
        t.start();        
    }
    
    public FormularioPrincipal() {
        initComponents();
        m = new Memoria();
        DecimalFormat df = new DecimalFormat("#,###.###");
        memTotal.setText(df.format(m.getMemoriaSistema())+" KB");
        actualizarFormulario();        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        memTotal = new javax.swing.JLabel();
        espOcupado = new javax.swing.JLabel();
        espLibre = new javax.swing.JLabel();
        GraficoMemoria = new PanelGrafico();
        btnAbrirTabProcesos = new javax.swing.JButton();
        lblObservaciones = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Grafico Memoria Dinamica");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("GRAFICO MEMORIA DINAMICA");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel2.setText("MEMORIA TOTAL: ");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setText("ESPACIO OCUPADO: ");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel4.setText("ESPACIO LIBRE: ");

        memTotal.setFont(new java.awt.Font("Cambria Math", 2, 18)); // NOI18N

        espOcupado.setFont(new java.awt.Font("Cambria Math", 2, 18)); // NOI18N

        espLibre.setFont(new java.awt.Font("Cambria Math", 2, 18)); // NOI18N

        javax.swing.GroupLayout GraficoMemoriaLayout = new javax.swing.GroupLayout(GraficoMemoria);
        GraficoMemoria.setLayout(GraficoMemoriaLayout);
        GraficoMemoriaLayout.setHorizontalGroup(
            GraficoMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 573, Short.MAX_VALUE)
        );
        GraficoMemoriaLayout.setVerticalGroup(
            GraficoMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );

        btnAbrirTabProcesos.setText("Ver Tabla de Procesos");
        btnAbrirTabProcesos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirTabProcesosActionPerformed(evt);
            }
        });

        lblObservaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblObservaciones.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(espOcupado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(memTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(espLibre, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(124, 124, 124))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(99, 99, 99))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(GraficoMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lblObservaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(15, 15, 15))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAbrirTabProcesos)
                .addGap(221, 221, 221))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(memTotal)
                    .addComponent(jLabel2))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(espOcupado, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(espLibre, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(GraficoMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAbrirTabProcesos)
                .addGap(1, 1, 1)
                .addComponent(lblObservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirTabProcesosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirTabProcesosActionPerformed
        // TODO add your handling code here:
        if( !ventanaProcesosAbierta ){
            ventanaProcesosAbierta = true;
            ft = new FormularioTabla();
            ft.setVisible(true);
        }        
    }//GEN-LAST:event_btnAbrirTabProcesosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormularioPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormularioPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GraficoMemoria;
    private javax.swing.JButton btnAbrirTabProcesos;
    private javax.swing.JLabel espLibre;
    private javax.swing.JLabel espOcupado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lblObservaciones;
    private javax.swing.JLabel memTotal;
    // End of variables declaration//GEN-END:variables
}
