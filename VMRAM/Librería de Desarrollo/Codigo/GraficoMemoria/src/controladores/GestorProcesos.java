package controladores;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import sistema.Memoria;
import sistema.Proceso;
import sistema.Registro;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author EQUIPO
 */
public class GestorProcesos {
    
    private class ExcepcionRegistro extends Exception {
        public ExcepcionRegistro(){
            super("INSUFICIENCIA DE REGISTROS");
        }
    }
    
    public Object[] leerProcesosNucleo( ArrayList<Proceso> listaProcesos , ArrayList<Registro> listaRegistros
                                      ) throws IOException{
        //Lectura de ventana de comandos
        String tmp;
        String [] lineaDeProceso;
        String consola = System.getenv("windir")+"\\System32\\"+"tasklist.exe /v /nh";
        Process proceso=Runtime.getRuntime().exec(consola);
        BufferedReader entrada = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        //===============================
        ArrayList<Proceso> lp = listaProcesos;
        ArrayList<Registro> lr = listaRegistros;
        int tamSistema = 0;        
        Registro r = new Registro(true);
        Proceso sistema = new Proceso();
        sistema.setNombre("Sistema Operativo");
        sistema.setTipo("nucleo"); 
        tmp = entrada.readLine();
        while((tmp=entrada.readLine())!=null){            
            lineaDeProceso = tmp.split("\\s\\s+");
            if( esNativo(lineaDeProceso) ){
                tamSistema += extraerPesoProceso(lineaDeProceso);
            }               
        }
        sistema.setPeso(tamSistema);
        sistema.setRegInicio(0);
        sistema.setRegFin(tamSistema-1);
        lp.add(sistema);
        for( int i = 0 ; i < tamSistema ; i++ ){
            r.setProceso(sistema);
            lr.set(i, r);
        }
        Object[] v = new Object[2];
        v[0] = lp;
        v[1] = lr;
        entrada.close();  
        return v;
    }
    
    public void actualizarProcesos(Memoria m){
        try {
            ArrayList<Proceso> listaNuevaProcesos = extraerProcesosUsuario();
            analizarNuevosProcesos(m, listaNuevaProcesos);
            analizarProcesosTerminados( m, listaNuevaProcesos );                        
        } catch (IOException ex) {
            System.err.println("Error al leer procesos usuario: "+ex.getMessage());
        }
        
    }
    
    private ArrayList<Proceso> extraerProcesosUsuario () throws IOException {
        //Lectura de ventana de comandos
        String consola = System.getenv("windir")+"\\System32\\"+"tasklist.exe /v /nh";
        Process proceso=Runtime.getRuntime().exec(consola);
        BufferedReader entrada = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        //==============================
        ArrayList<Proceso> lp = new ArrayList<>();
        String tmp;
        String [] lineaDeProceso;
        Proceso p;
        tmp = entrada.readLine();
        while((tmp = entrada.readLine())!=null){
            lineaDeProceso = tmp.split("\\s\\s+");
            if( esUsuario(lineaDeProceso) ){
                p = new Proceso();
                p.setNombre(extraerNombreProceso(lineaDeProceso));
                p.setPeso(extraerPesoProceso(lineaDeProceso));
                p.setTipo("Usuario");
                lp.add(p);
            }
        }
        entrada.close();
        return lp ;
    }
    
    private void analizarNuevosProcesos( Memoria m , ArrayList<Proceso> lnp ) { 
        ArrayList<Proceso> lp = m.getProcesos();
        ArrayList<Proceso> lpYaIncrementados = new ArrayList<>();
        ArrayList<Registro> lr = m.getRegistros();
        int pesoAcumulado;
        boolean analizando = false ;
        String procesoAnalizado ;
        Proceso p ;  
        Proceso np , npAux;
        for( int i = 0; i < lnp.size() ; i++ ){
            np = lnp.get(i);
            pesoAcumulado = 0 ;
            p = new Proceso();
            p.setTipo("Usuario");
            p.setNombre(np.getNombre());
            procesoAnalizado = np.getNombre();
            for( int j = i ; j < lnp.size() ; j++ ){
                npAux = lnp.get(j);
                if( procesoAnalizado.equals(npAux.getNombre()) ){
                    pesoAcumulado += npAux.getPeso();
                }
            }
            p.setPeso(pesoAcumulado);
            if( !esProcesoYaRegistrado(lp,p) ){
                //System.out.println("Analizando "+np.getNombre());                
                try{
                    asignarRegistros(p, lr, lp);                        
                    lp.add(p); 
                    //lpYaIncrementados.add(p);
                }catch(ExcepcionRegistro er){
                    System.err.println("ERROR: "+er.getMessage()+" para proceso '"+p.getNombre()+"'");
                }
            }else{
                if( !esProcesoYaRegistrado(lpYaIncrementados, p) ){
                    try{
                        asignacionIncremental(p, lr, lp);                        
                        lpYaIncrementados.add(p);                        
                    }catch(ExcepcionRegistro er){
                        //System.err.println("ERROR: "+er.getMessage()+" para proceso '"+p.getNombre()+"'");
                    }
                }
            }
                
        }
        m.setProcesos(lp);
        m.setRegistros(lr);
    }
    
    private void asignacionIncremental(Proceso p, ArrayList<Registro> lr, ArrayList<Proceso> lp) throws ExcepcionRegistro{
        int i = 0 , ind = 0 , regIni = 0, regFin = 0 , cant = 0;
        Registro r ;
        for( i = 0 ; i < lp.size() ; i++ ){
            Proceso pAux = lp.get(i);
            if( pAux.getNombre().equalsIgnoreCase( p.getNombre() ) ) {
                regIni = pAux.getRegInicio();
                regFin = pAux.getRegFin();
                ind = i;
                break;
            }
        }
        if( lr.get(regFin+1).isOcupado() || p.getPeso() <= lp.get(ind).getPeso()){
            throw new ExcepcionRegistro();
        }
        for( i = regIni ; i < lr.size() ; i ++ ){
            cant = 0 ;
            while( !lr.get(i).isOcupado() || lr.get(i).getProceso().equals(p) ){                
                cant++;
                if( cant == p.getPeso() ){
                    regFin = regIni + cant - 1 ;
                    p.setRegInicio(regIni);
                    p.setRegFin(regFin);
                    for( i = regIni ; i < (regIni + cant) ; i++ ){
                        //System.out.println(ini+" : "+(ini+cont)+" ___ i = "+i);                        
                        r = new Registro(true);
                        r.setProceso(p);
                        lr.set(i, r);                        
                    }
                    lp.set(ind, p);
                    return;
                }
                i++;
                //System.out.println(i+","+cant);
                if( i == lr.size() ) break;
            }
        }
        throw new ExcepcionRegistro();
    }
    
    private void analizarProcesosTerminados(Memoria m ,ArrayList<Proceso> lnp){
        int regIni , regFin ;
        boolean aunProcesando ;        
        Registro r = new Registro(false);
        Proceso p;
        ArrayList<Proceso> lp = m.getProcesos(),
                           lpAux = new ArrayList<>();
        ArrayList<Registro> lr = m.getRegistros();
        lpAux.add(lp.get(0));
        for( int j = 1 ; j < lp.size() ; j++ ){
            p = lp.get(j);
            aunProcesando = false;
            for( Proceso np: lnp ){
                if( p.equals(np) ){
                    lpAux.add(p);
                    //System.out.println(p.toString()+" aun procesando...");
                    aunProcesando = true ;
                    break;
                }
            }
            if( !aunProcesando ){
                //System.out.println(p.toString());
                regIni = p.getRegInicio();
                regFin = p.getRegFin();
                //System.out.println("VACIANDO DESDE "+regIni+" : "+regFin);
                for( int i = regIni ; i <= regFin ; i++ ){
                    //System.out.println("Registro "+i+" vaciado...");
                    lr.set(i, r);
                }
            }            
        }
        m.setProcesos(lpAux);
        m.setRegistros(lr);
    }    
    
    private void asignarRegistros(Proceso p, ArrayList<Registro> lr, ArrayList<Proceso> lp )
                                 throws ExcepcionRegistro{
        int ini , fin , cont , peso = p.getPeso() ;
        Registro r = new Registro(true) ;
        for( int i = lp.get(0).getPeso() ; i < lr.size() ; i ++ ){
            ini = i ;
            cont = 0 ;
            while( !lr.get(i).isOcupado() ){                
                cont++;
                if( cont == peso ){
                    fin = ini + cont - 1 ;
                    p.setRegInicio(ini);
                    p.setRegFin(fin);
                    for( i = ini ; i < (ini + cont) ; i++ ){
                        //System.out.println(ini+" : "+(ini+cont)+" ___ i = "+i);                        
                        r.setProceso(p);
                        lr.set(i, r);                        
                    }
                    return;
                }
                i++;
            }
        }
        throw new ExcepcionRegistro();
    }
        
    private boolean esProcesoYaRegistrado( ArrayList<Proceso> lp, Proceso np ){
        for( Proceso p: lp ){
            if( np.getNombre().equalsIgnoreCase( p.getNombre() ) ) return true;
        }
        return false;
    }    
    
    private String extraerNombreProceso(String [] lineaDeProceso){
        return lineaDeProceso[0];
    }
    
    private int extraerPesoProceso(String [] lineaDeProceso){
        int i = 0, iniSubString ;
        int peso = 0 ;
        int contadorEspacios = 0;
        if( lineaDeProceso[2].contains("KB") ){
            while( lineaDeProceso[2].charAt(i)!='K' ) i++;
            try{
                peso = Integer.parseInt(lineaDeProceso[2].substring(2, i-1).replace(".",""));
            }catch(Exception e){
                peso = Integer.parseInt(lineaDeProceso[2].substring(2, i-1).replace(",",""));
            }
        }else{
            while( lineaDeProceso[3].charAt(i)!='K' ){
                //System.out.println(i+" = "+lineaDeProceso[3].charAt(i));
                i++;
            }
            try{
                peso = Integer.parseInt(lineaDeProceso[3].substring(0, i-1).replace(".",""));
            }catch(Exception e){
                peso = Integer.parseInt(lineaDeProceso[3].substring(0, i-1).replace(",",""));
            }
        }        
        return peso;
    }
    
    private boolean esNativo(String [] lineaDeProceso){
        String pNucleo;
        FileReader fr;
        BufferedReader br;
        if( lineaDeProceso[4].contains("SYSTEM") ){
            return true;
        }
        else
        {
            try {
                fr = new FileReader("procesosSistemaOperativo.txt");
                br = new BufferedReader(fr);
                while( (pNucleo = br.readLine())!=null ){
                    if( !pNucleo.equalsIgnoreCase("") ){
                        if(lineaDeProceso[0].equalsIgnoreCase(pNucleo)) return true;
                        if(lineaDeProceso[0].contains(pNucleo)) return true;
                    }
                }
                br.close();
                fr.close();
            } catch ( FileNotFoundException ex ) {                
            } catch ( IOException ex){                
            }                                            
        }
        return false;
    }
    
    private boolean esUsuario(String [] lineaDeProceso){
        String pUsuarioExcepcion;
        FileReader fr;
        BufferedReader br;
        if ( esNativo(lineaDeProceso) ) return false;
        else
        {
            try {
                fr = new FileReader("procesosUsuarioExcepciones.txt");
                br = new BufferedReader(fr);
                while( (pUsuarioExcepcion = br.readLine())!=null ){
                    if( !pUsuarioExcepcion.equalsIgnoreCase("") ){
                        if(lineaDeProceso[0].equalsIgnoreCase(pUsuarioExcepcion)) return false;
                        if(lineaDeProceso[0].contains(pUsuarioExcepcion)) return false;
                    }
                }
                br.close();
                fr.close();
            } catch ( FileNotFoundException ex ) {                
            } catch ( IOException ex){                
            }                                            
        }
        return true;        
    }
    
}
