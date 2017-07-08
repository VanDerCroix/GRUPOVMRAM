package sistema;

import controladores.GestorProcesos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.Swap;
import org.hyperic.sigar.SigarException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author EQUIPO
 */
public class Memoria {
    
    private int memoriaSistema;
    private int espacioOcupado;
    private int espacioLibre;
    private GestorProcesos gp ;
    private ArrayList<Proceso> procesos;
    private ArrayList<Registro> registros;

    public Memoria() {        
        Sigar sigar = new Sigar();        
        try {
            memoriaSistema = (int)sigar.getMem().getRam()*1024;
        } catch (SigarException ex) {
            System.err.println("Error al establecer memoria del sistema");
        }
        registros = new ArrayList<>();
        for( int i = 0; i < memoriaSistema ; i++ ){
            registros.add(new Registro(false));
        }
        gp = new GestorProcesos();
        procesos = new ArrayList<>();
        Object[] l = new Object[2];
        try {
            l = gp.leerProcesosNucleo(procesos, registros);
            procesos = (ArrayList<Proceso>) l[0];
            registros = (ArrayList<Registro>) l[1];
        } catch (Exception e) {
            System.err.println("Error al leer nativos: " + e.getMessage());
        }
    }
    
    public void actualizarMemoria(){
        gp.actualizarProcesos(this);
        int tamTotal = 0 ;
        for(Proceso p:procesos){
            tamTotal += p.getPeso();
        }
        espacioOcupado = tamTotal ;
        espacioLibre = memoriaSistema - espacioOcupado;
    }   

    public int getEspacioOcupado() {
        return espacioOcupado;
    }

    public int getEspacioLibre() {
        return espacioLibre;
    } 

    public int getMemoriaSistema() {
        return memoriaSistema;
    }

    public void setMemoriaSistema(int memoriaSistema) {
        this.memoriaSistema = memoriaSistema;
    }

    public ArrayList<Proceso> getProcesos() {
        return procesos;
    }

    public void setProcesos(ArrayList<Proceso> procesos) {
        this.procesos = procesos;
    }

    public ArrayList<Registro> getRegistros() {
        return registros;
    }

    public void setRegistros(ArrayList<Registro> registros) {
        this.registros = registros;
    }
    
    
    
    
    
}
