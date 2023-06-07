package com.example.sanast;

public class Cita {

    public String fecha;
    public String tipo;

    public Cita(){

    }

    public Cita(String fecha, String tipo) {
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "fecha='" + fecha + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
