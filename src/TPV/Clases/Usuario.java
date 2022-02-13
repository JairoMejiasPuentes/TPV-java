/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPV.Clases;

/**
 *
 * @author Fran
 */
public class Usuario {
    private int idUsuario, pass;
    private String nombre;
    
    // 0 --> NO Administrador
    // 1 -->Administrador
    private int Admin;
    
    
    public Usuario() {
        
    }
    
    public Usuario(int idUsuario){
        this.idUsuario = idUsuario;
    }
    
    public Usuario(int idUsuario, int pass, String nombre, int Admin) {
        this.idUsuario = idUsuario;
        this.pass = pass;
        this.nombre = nombre;
        this.Admin = Admin;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAdmin() {
        return Admin;
    }

    public void setAdmin(int Admin) {
        this.Admin = Admin;
    }
    
    
}
