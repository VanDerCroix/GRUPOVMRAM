package sistema;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author EQUIPO
 */
public class Registro {
    
    private Proceso proceso;
    private boolean ocupado;
    
    public Registro(boolean ocupado){
        this.ocupado = ocupado;
        proceso = new Proceso("VACIO",0,"VACIO",0,0);
    }

    public Proceso getProceso() {
        return proceso;
    }

    public void setProceso(Proceso proceso) {
        this.proceso = proceso;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
    
    
}
