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
public class VentaProducto {
     int idProducto, idVenta, Cantidad, Vendedor;

    public VentaProducto() {
    }
    
    public VentaProducto(int idProducto, int idVenta, int Cantidad, int Vendedor) {
        this.idProducto = idProducto;
        this.idVenta = idVenta;
        this.Cantidad = Cantidad;
        this.Vendedor = Vendedor;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int Cantidad) {
        this.Cantidad = Cantidad;
    }

    public int getVendedor() {
        return Vendedor;
    }

    public void setVendedor(int Vendedor) {
        this.Vendedor = Vendedor;
    }
    
    
    
}
