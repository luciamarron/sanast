package com.example.sanast;

public class Usuario {
    public String nombre;
    public String dni;
    public String correo;
    public String contraseña;
    public String centro_de_salud;

    public Usuario (){

    }

    public Usuario(String nombre, String dni, String correo, String contraseña, String centro_de_salud) {
        this.nombre = nombre;
        this.dni = dni;
        this.correo = correo;
        this.contraseña = contraseña;
        this.centro_de_salud = centro_de_salud;

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getCentro_de_salud() {
        return centro_de_salud;
    }

    public void setCentro_de_salud(String centro_de_salud) {
        this.centro_de_salud = centro_de_salud;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", dni='" + dni + '\'' +
                ", correo='" + correo + '\'' +
                ", contraseña='" + contraseña + '\'' +
                ", centro_de_salud='" + centro_de_salud + '\'' +
                '}';
    }
}
