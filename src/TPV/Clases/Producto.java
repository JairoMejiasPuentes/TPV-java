/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPV.Clases;

import javax.swing.ImageIcon;

/**
 *
 * @author Fran
 */
public class Producto {

    private int idProducto, stock;
    private String nombre, rutaImg;
    private float precio, iva;
    private ImageIcon icon;

    public Producto() {

    }
    
    public Producto(int idProducto){
        
        this.idProducto = idProducto;
    }
    
    public Producto(int idProducto, int stock, String nombre, String rutaImg,
            float precio) {
        this.idProducto = idProducto;
        this.stock = stock;
        this.nombre = nombre;
        this.rutaImg = rutaImg;
        this.precio = precio;
    }

    public float getIva() {
        return iva;
    }

    public void setIva(float iva) {
        this.iva = iva;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(byte[] imagen) {
        this.icon = new ImageIcon(imagen);
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaImg() {
        return rutaImg;
    }

    public void setRutaImg(String rutaImg) {
        this.rutaImg = rutaImg;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

}
