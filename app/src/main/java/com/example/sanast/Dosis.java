package com.example.sanast;

public class Dosis {

    public String usuario;
    public String medicacion;
    public String dosis;

    public Dosis(){

    }

    public Dosis(String usuario, String medicacion, String dosis) {
        this.usuario = usuario;
        this.medicacion = medicacion;
        this.dosis = dosis;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMedicacion() {
        return medicacion;
    }

    public void setMedicacion(String medicacion) {
        this.medicacion = medicacion;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    @Override
    public String toString() {
        return "Dosis{" +
                "usuario='" + usuario + '\'' +
                ", medicacion='" + medicacion + '\'' +
                ", dosis='" + dosis + '\'' +
                '}';
    }
}
