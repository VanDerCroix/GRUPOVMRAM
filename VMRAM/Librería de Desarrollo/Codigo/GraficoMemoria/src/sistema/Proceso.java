package sistema;


import java.awt.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author EQUIPO
 */
public class Proceso {
    
    private String nombre;
    private int peso;
    private String tipo;
    private int regInicio, regFin;

    public Proceso(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }   

    public Proceso(String nombre, int peso, String tipo, int regInicio, int regFin) {
        this.nombre = nombre;
        this.peso = peso;
        this.tipo = tipo;
        this.regInicio = regInicio;
        this.regFin = regFin;
    }

    public Proceso() {
    }
    
    public boolean equals(Proceso that){
        if( this.nombre.equalsIgnoreCase( that.getNombre() ) ){
            return true;
        }
        else return false;
    }
    
    public String toString(){
        return nombre+"  "+peso+" KB  "+tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getRegInicio() {
        return regInicio;
    }

    public void setRegInicio(int regInicio) {
        this.regInicio = regInicio;
    }

    public int getRegFin() {
        return regFin;
    }

    public void setRegFin(int regFin) {
        this.regFin = regFin;
    }
    
    
    
    
}
