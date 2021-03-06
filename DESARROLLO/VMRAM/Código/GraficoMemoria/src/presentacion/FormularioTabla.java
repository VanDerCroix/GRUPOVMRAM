/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package presentacion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sistema.Proceso;

/**
 *
 * @author EQUIPO
 */
public class FormularioTabla extends javax.swing.JFrame {

    /**
     * Creates new form FormularioTabla
     */  
    
    DecimalFormat df = new DecimalFormat("#,###.###");
    
    private class RenderizadorCabecera extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel comp = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            comp.setBackground(Color.BLACK);
            comp.setForeground(Color.white);
            comp.setFont(new Font("Cambria Math", Font.BOLD, 14));            
            comp.setBorder(BorderFactory.createLineBorder(Color.white));
            comp.setVerticalAlignment(SwingConstants.CENTER);
            comp.setHorizontalAlignment(SwingConstants.CENTER);
            return comp;
        }
    }
    
    private class RenderizadorTabla extends DefaultTableCellRenderer{
        int nFilas;
        public RenderizadorTabla(int nFilas){
            super();
            this.nFilas = nFilas;
        }
        public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column){
            JLabel comp = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            comp.setForeground(Color.black);
            comp.setFont(new Font("Cambria Math", Font.PLAIN, 12));
            //comp.setBorder(BorderFactory.createLineBorder(Color.black,1));
            comp.setBorder(BorderFactory.createEtchedBorder());
            comp.setVerticalAlignment(SwingConstants.CENTER);
            comp.setHorizontalAlignment(SwingConstants.CENTER);
            if( row == nFilas -1 ){
                comp.setFont(new Font("Cambria Math", Font.BOLD , 13));
            }
            return comp;
        }
    }

    
    public void listarProcesos(ArrayList<Proceso> lp){
        DefaultTableModel t = new DefaultTableModel();
        int total = 0;
        t.setColumnCount(0);
        t.setRowCount(lp.size()+1);
        t.addColumn("NOMBRE");
        t.addColumn("PESO");
        t.addColumn("REG. INICIO");
        t.addColumn("REG. FIN");
        int index = 0;
        for(Proceso p:lp){
            t.setValueAt(p.getNombre(), index, 0);
            t.setValueAt(df.format(p.getPeso())+ " KB", index, 1);
            t.setValueAt(df.format(p.getRegInicio()), index, 2);
            t.setValueAt(df.format(p.getRegFin()), index, 3);
            total += p.getPeso();
            index++;
        }
        t.setValueAt("TAM. TOTAL", index, 0);
        t.setValueAt("-", index, 1);
        t.setValueAt("-", index, 2);
        t.setValueAt(df.format(total)+" KB", index, 3);
        tablaProcesos.setModel(t);
        RenderizarTabla(t.getColumnCount());        
    }
    
    private void RenderizarTabla(int nColumnas){
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        tcr.setFont(new Font("Cambria Math", Font.BOLD, 12));
        tcr.setForeground(Color.red);
        for( int i = 0 ; i < nColumnas ; i++ ){
            tablaProcesos.getColumnModel().getColumn(i).setCellRenderer(new RenderizadorTabla(tablaProcesos.getModel().getRowCount()));
            tablaProcesos.getColumnModel().getColumn(i).setHeaderRenderer(new RenderizadorCabecera());
        }
        tablaProcesos.setRowHeight(20);
        tablaProcesos.setEnabled(false);
        tablaProcesos.setDragEnabled(false);
        tablaProcesos.getTableHeader().setReorderingAllowed(false);   
    }
    
    public FormularioTabla() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProcesos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tabla de procesos");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tablaProcesos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaProcesos);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        FormularioPrincipal.ventanaProcesosAbierta = false;
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaProcesos;
    // End of variables declaration//GEN-END:variables
}
