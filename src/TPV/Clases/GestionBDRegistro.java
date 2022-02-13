/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPV.Clases;

import TPV.GUI.TPV;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.Date;

/**
 * @author Fran
 * @author Jairo
 */
public class GestionBDRegistro {

    private String usuario, pswd, bd, host; // todos los atributos tipo String los podemos poner de esta forma
    private Connection conexion;
    private Statement sentencia;

    TPV inter;

    public GestionBDRegistro(String usuario, String pswd, String bd, String host) {
        this.usuario = usuario;
        this.pswd = pswd;
        this.bd = bd;
        this.host = host;

    }

    /**
     * Método de conexion a una base de datos<br>
     *
     * @throws SQLException<br>
     * @throws ClassNotFoundException
     */
    private void conectar() throws SQLException, ClassNotFoundException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://" + host + "/" + bd
                    + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacy"
                    + "DatetimeCode=false&serverTimezone=UTC", usuario, pswd);
            sentencia = conexion.createStatement();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.toString());
            JOptionPane.showMessageDialog(inter, "<html><h2>Error de conexión "
                    + "con la base de datos</h2><br></html>" + ex.toString(), 
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método de desconexión de la base de datos<br>
     */
    private void desconectar() {
        try {
            conexion.close();
            sentencia.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(inter, "<html><h2>Error de desconexión con la base de datos</h2><br>" + ex, "Error de desconexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para la búsqueda del nombre del empleado correspondiente a la
     * introducción de la contraseña
     *
     *
     * @param pin
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String buscarNombreEmpleado(int pin) throws SQLException, ClassNotFoundException {

        String nombre = null;
        this.conectar();

        String sql = String.format("SELECT nombre FROM empleados WHERE pin = %s ", pin);

        try {
            try (ResultSet rs = sentencia.executeQuery(sql)) {
                while (rs.next()) {
                    nombre = rs.getString("nombre");
                }
            }

        } catch (SQLException ex) {
            String mensaje = String.format("<html><h2>No existe el empleado </h2><br>");
            JOptionPane.showMessageDialog(inter, mensaje);
        }
        return nombre;
    }

    /**
     * Método para calcular las horas minutos y segundos que has trabajado en un
     * turno
     *
     * @param pin
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String horasTotales(int pin) throws SQLException, ClassNotFoundException {
        String horasTotales = "";
        int id = this.conseguirId(pin);
        this.conectar();

        String sql = String.format("SELECT TIME (TIMEDIFF((SELECT MAX(fechaSalida) FROM empresa.registro WHERE idEmpleado = %s),"
                + "(SELECT MAX(fechaEntrada) FROM empresa.registro WHERE idEmpleado = %s)))", id, id);
        try {
            ResultSet rs = sentencia.executeQuery(sql);

            while (rs.next()) {
                horasTotales = rs.getString(1);
                System.out.println(horasTotales);
            }

        } catch (SQLException ex) {
            String mensaje = String.format("<html><h2>No se puede calcular el tiempo</h2><br>");
            JOptionPane.showMessageDialog(inter, mensaje);
        }

        return horasTotales;
    }

    /**
     * Método para registrar todos y cada uno de los accesos al puesto de
     * trabajo
     *
     * @param pin
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public boolean registroEntradaEmpleado(int pin) throws SQLException, ClassNotFoundException {
        boolean resultado;
        int id = this.conseguirId(pin); // obtenemos el id del empleado a partir del pin mediante el método correspondiente

        this.conectar(); // conectamos con la base de datos

        try {

            String sql = String.format("INSERT INTO registro (idEmpleado) VALUES (%s)", id); // creamos la sentencia SQL a ejecutar
            sentencia.executeUpdate(sql); // la ejecutamos
            resultado = true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(inter, "<html><h2>Contraseña Errónea</h2><br>", "Contraseña Errónea", JOptionPane.ERROR_MESSAGE);
            resultado = false;
        }

        this.desconectar();

        return resultado;
    }

    /**
     * Método para registrar la salida de los empleados siempre que haya una
     * entrada previa
     *
     * @param pin
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public boolean registroSalidaEmpleado(int pin) throws SQLException, ClassNotFoundException {
        boolean resultado;
        int id = this.conseguirId(pin);

        this.conectar();

        try {

            String sqlSalida = String.format("UPDATE registro SET fechaSalida = current_timestamp() " // aqui obtenemos la fecha y ora del servidor
                    + "WHERE idEmpleado = %s AND fechaSalida IS NULL",
                     id);

            sentencia.executeUpdate(sqlSalida);
            resultado = true;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(inter, "<html><h2>Error al salir</h2><br>", "Error al Salir", JOptionPane.ERROR_MESSAGE);
            resultado = false;
        }

        this.desconectar();
        return resultado;
    }

    /**
     * método para conseguir la Id del Usuario al poner la contraseña que se le
     * pide
     *
     * @param pin
     * @return id del Usuario
     * @throws SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public int conseguirId(int pin) throws SQLException, ClassNotFoundException {

        int id = 0;

        this.conectar();
        String selectPinId = String.format("SELECT idEmpleado FROM empresa.empleados WHERE pin = %s", pin);

        ResultSet rs = sentencia.executeQuery(selectPinId);

        while (rs.next()) {
            id = rs.getInt("idEmpleado");
        }
        this.desconectar();
        return id;
    }

    /**
     * Método para conseguir el pin de un empleado para compararlo
     * posteriormente en la unificación de métodos
     *
     *
     * @param pin
     * @return pin del usuario
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int conseguirPin(int pin) throws SQLException, ClassNotFoundException {

        int id = this.conseguirId(pin); // localizamos al empleado mediante su pin
        int pinRecogido = 0; // creamos una variable para recoger el pin que localicemos

        this.conectar();

        String sql = String.format("SELECT Pin FROM empresa.empleados WHERE idEmpleado = %s", id);

        ResultSet rs = sentencia.executeQuery(sql);

        while (rs.next()) {
            pinRecogido = rs.getInt("Pin");
        }

        this.desconectar();
        return pinRecogido; // devolvemos el pin localizado

    }

    /**
     * Método de unificación de los métodos de entrada y salida
     *
     * @param pin
     * @return Tiempo trabajado
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String registroEntradaSalida(int pin) throws SQLException, ClassNotFoundException {
        String horasTotales = "";
        int id = this.conseguirId(pin);
        Date trabajando = new Date();
        int pinRecogido = this.conseguirPin(pin);

        this.conectar();
        String status = String.format("SELECT fechaSalida FROM empresa.registro WHERE idEmpleado = %s", id);

        ResultSet rs = sentencia.executeQuery(status);

        while (rs.next()) {
            trabajando = rs.getDate("fechaSalida");
        }

        if (trabajando == null && pin == pinRecogido) {

            this.registroSalidaEmpleado(pin);
            horasTotales = this.horasTotales(pin); // computo total de las horas, minutos y segundos trabajados
        } else {

            this.registroEntradaEmpleado(pin);
            horasTotales = "A Currar se ha dicho";
        }

        this.desconectar();

        return horasTotales;
    }


    /**
     * Inserta un Producto nuevo en la base de datos
     *
     * @param p producto que se va a insertar
     * @return
     */
    public boolean insertarProducto(Producto p) {
        boolean resultado = true;

        //Instanciamos un objeto de tipo file al que le pasamos como parametro 
        //la ruta de nuestra imagen de producto
        File archivoFoto = new File(p.getRutaImg());

        String sql = "INSERT INTO productos (idProducto, nombre, stock, imgProducto,"
                + " precio, iva) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conectar();
            //Cargamos el archivo en forma de bytes, instanciando un objeto de
            //clase FileInputStream y pasandole como parametro el File anterior
            FileInputStream convertirFoto = new FileInputStream(archivoFoto);

            //Creamos un Prepared Statement al cual le pasamos nuestra sentencia
            PreparedStatement psql = conexion.prepareStatement(sql);

            //Establecemos los valores del preparedStatement
            psql.setInt(1, p.getIdProducto());
            psql.setString(2, p.getNombre());
            psql.setInt(3, p.getStock());
            psql.setBlob(4, convertirFoto);
            psql.setFloat(5, p.getPrecio());
            psql.setFloat(6, p.getIva());
            
            //Ejecutamos la sentencia
            psql.executeUpdate();

        } catch (FileNotFoundException | SQLException | ClassNotFoundException ex) {
            System.out.println("Error al introducir producto: "
                    + ex.toString());
            resultado = false;
        }

        desconectar();
        return resultado;
    }

    /**
     * Busca un Producto por su id en la base de datos
     *
     * @param idProducto Número identificador del producto
     * @return Producto Encontrado
     */
    public Producto buscarProducto(int idProducto) {
        Producto p = new Producto();
        String sql = String.format("SELECT * FROM productos WHERE idproducto=%s",
                 idProducto);

        try {
            conectar();
            //sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            byte[] image = null;

            while (rs.next()) {
                p.setIdProducto(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setStock(rs.getInt("stock"));
                p.setPrecio(rs.getFloat("precio"));
                p.setIva(rs.getFloat("iva"));
                p.setIcon(rs.getBytes("imgProducto"));
            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al buscar producto: "
                    + ex.toString());
        }

        desconectar();
        return p;
    }

      /**
     * Busca un Producto por su nombre en la base de datos
     *
     * @param nombreProducto Nombre identificador del producto
     * @return Producto Encontrado
     */
    public Producto buscarProducto(String nombreProducto) {
        Producto p = new Producto();
        String sql = String.format("SELECT * FROM productos WHERE nombre='%s'",
                 nombreProducto);

        try {
            conectar();
            //sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);
            byte[] image = null;

            while (rs.next()) {
                p.setIdProducto(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setStock(rs.getInt("stock"));
                p.setPrecio(rs.getFloat("precio"));
                p.setIva(rs.getFloat("iva"));
                p.setIcon(rs.getBytes("imgProducto"));
            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al buscar producto: "
                    + ex.toString());
        }

        desconectar();
        return p;
    }
    
    /**
     * Borra un producto de la base de datos
     *
     * @param p producto que se desea borrar
     * @return
     */
    public boolean borrarProducto(Producto p) {
        boolean resultado = true;

        String sql = String.format("DELETE FROM productos WHERE idproducto = %s",
                 p.getIdProducto());

        try {
            conectar();
            //sentencia = conexion.createStatement();
            sentencia.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al borrar producto: "
                    + ex.toString());
            resultado = false;
        }

        desconectar();
        return resultado;
    }

    /**
     * Modifica los campos de un producto concreto en la base de datos
     *
     * @param p producto que se desea modificar
     * @return
     */
    public boolean modificarProducto(Producto p) {

        boolean resultado = true;

        File archivoFoto = new File(p.getRutaImg());
        //Para evitar conflicto con las foreign keys, se almacena dos strings,
        //una para desactivarlas y otra para activarlas
        String desactivaFk = "SET FOREIGN_KEY_CHECKS=0";
        String activaFk = "SET FOREIGN_KEY_CHECKS=1";
        //Consulta de la modificación
        String sql = "UPDATE productos SET nombre = ?, stock = ?, precio = ?,"
                + "iva = ?, imgProducto = ? WHERE idProducto = ? ";

        try {
            conectar();
            //Cargamos el archivo en forma de bytes, instanciando un objeto de
            //clase FileInputStream y pasandole como parametro el File anterior
            FileInputStream convertirFoto = new FileInputStream(archivoFoto);

            //Creamos un Prepared Statement al cual le pasamos nuestra sentencia
            PreparedStatement psql;
            psql = conexion.prepareStatement(sql);

            //Establecemos los valores del preparedStatement          
            psql.setString(1, p.getNombre());
            psql.setInt(2, p.getStock());
            psql.setFloat(3, p.getPrecio());
            psql.setFloat(4, p.getIva());
            psql.setBlob(5, convertirFoto);
            psql.setInt(6, p.getIdProducto());

            //sentencia = conexion.createStatement();
            //Desactivamos las FK para poder hacer el update
            sentencia.executeUpdate(desactivaFk);
            //Ejecutamos el Prepared Statetment
            psql.executeUpdate();
            //Activamos de nuevo las FK
            sentencia.executeUpdate(activaFk);

        } catch (SQLException | FileNotFoundException | ClassNotFoundException ex) {
            System.err.println("Error al modificar producto" + ex.toString());
            resultado = false;
        }

        desconectar();
        return resultado;
    }

    /**
     * Recupera de la BD todos los productos existentes
     *
     * @return ArrayList con todos los productos existentes en la base de datos
     */
    public ArrayList listarProductos() {
        ArrayList productos = new ArrayList();
        Producto p;
        String sql = "SELECT * FROM productos;";

        try {
            conectar();
            //sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);

            while (rs.next()) {
                p = new Producto();
                p.setIdProducto(rs.getInt("idProducto"));
                p.setNombre(rs.getString("nombre"));
                p.setStock(rs.getInt("stock"));
                p.setIcon(rs.getBytes("imgProducto"));
                p.setIva(rs.getInt("iva"));

                productos.add(p);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al recuperar productos: " + ex.toString());
        }

        return productos;
    }

    /**
     * Recupera de la BD todos los usuarios existentes
     *
     * @return ArrayList con todos los usuarios existentes en la base de datos
     */
    public ArrayList listarUsuarios() {
        ArrayList usuarios = new ArrayList();
        Usuario user;
        String sql = "SELECT * FROM usuarios;";

        try {
            conectar();
            //sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);

            while (rs.next()) {
                user = new Usuario();
                user.setIdUsuario(rs.getInt("idUsuario"));
                user.setNombre(rs.getString("nombre"));
                user.setPass(rs.getInt("pass"));
                user.setAdmin(rs.getInt("Admin"));

                usuarios.add(user);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al recuperar usuarios: " + ex.toString());
        }

        return usuarios;
    }

    /**
     * Compara la contraseña introducida con la alamacenada en la bd
     *
     * @param idUsuario usuario al que le pertenece la contraseña
     * @param pswd contraseña introducida por el usuario
     * @return
     */
    public boolean comparaContraseñas(int idUsuario, int pswd) {
        boolean resultado;
        Usuario user;

        user = buscarUsuario(idUsuario);
        if (user.getPass() == pswd) {
            resultado = true;
        } else {
            resultado = false;
        }

        return resultado;
    }

    public boolean isAdministrador(int idUsuario){
        boolean resultado;
        Usuario user;

        user = buscarUsuario(idUsuario);
        if (user.getAdmin()== 1) {
            resultado = true;
        } else {
            resultado = false;
        }

        return resultado;
    }
    
    /**
     * Inserta a un nuevo usuario en la base de datos
     *
     * @param user usuario que se quiere introducir
     * @return
     */
    public boolean insertarUsuario(Usuario user) {
        boolean resultado = true;

        //Consulta de Inserción
        String sql = String.format("INSERT INTO usuarios (idUsuario, nombre,"
                + "pass, admin) VALUES (%s,'%s',%s,%s)", user.getIdUsuario(),
                user.getNombre(), user.getPass(), user.getAdmin());

        try {
            conectar();
            //sentencia = conexion.createStatement();
            sentencia.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Error al insertar usuario" + ex.toString());
            resultado = false;

        }

        desconectar();
        return resultado;
    }

    /**
     * Busca un Usuario por su id en la base de datos
     *
     * @param idUsuario Número identificador del Usuario
     * @return Usuario encontrado
     */
    public Usuario buscarUsuario(int idUsuario) {
        Usuario user = new Usuario();
        String sql = String.format("SELECT * FROM usuarios WHERE idUsuario=%s",
                 idUsuario);

        try {
            conectar();
            sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);

            while (rs.next()) {
                conectar();
                user.setIdUsuario(rs.getInt("idUsuario"));
                user.setNombre(rs.getString("nombre"));
                user.setPass(rs.getInt("pass"));
                user.setAdmin(rs.getInt("Admin"));
            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al buscar Usuario: "
                    + ex.toString());
        }

        desconectar();
        return user;
    }

    /**
     * Borra un usuario de la base de datos
     *
     * @param user usuario que se desea borrar
     * @return
     */
    public boolean borrarUsuario(Usuario user) {
        boolean resultado = true;

        String sql = String.format("DELETE FROM usuarios WHERE idusuario = %s",
                 user.getIdUsuario());

        try {
            conectar();
            sentencia = conexion.createStatement();
            sentencia.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al borrar usuario: "
                    + ex.toString());
            resultado = false;
        }

        desconectar();
        return resultado;
    }

    /**
     * Modifica los campos de un usuario concreto en la base de datos
     *
     * @param user usuario que se desea modificar
     * @return
     */
    public boolean modificarUsuario(Usuario user) {
        boolean resultado = true;

        //Para evitar conflicto con las foreign keys, se almacena dos strings,
        //una para desactivarlas y otra para activarlas
        String desactivaFk = "SET FOREIGN_KEY_CHECKS=0";
        String activaFk = "SET FOREIGN_KEY_CHECKS=1";
        // hacer la consulta de modificacion
        String sql = String.format("UPDATE usuarios SET nombre = '%s', pass = %s"
                + ", admin = %s WHERE idusuario = %s ", user.getNombre(),
                user.getPass(), user.getAdmin(), user.getIdUsuario());

        try {
            conectar();
            sentencia = conexion.createStatement();
            //Desactivamos la FK para poder hacer el update
            sentencia.executeUpdate(desactivaFk);
            //Realizamos el update
            sentencia.executeUpdate(sql);
            //Activamos de nuevo la FK
            sentencia.executeUpdate(activaFk);

        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Error al modificar usuario" + ex.toString());
            resultado = false;

        }
        desconectar();
        return resultado;

    }

    /**
     * Registra una venta en la base de datos
     *
     * @param rv RegistroVenta que se quiere registrar en la base de datos
     * @return
     */
    public boolean insertarVenta(RegistroVenta rv) {
        boolean resultado = true;
        String sql = String.format("INSERT INTO registroventas (precioTotal)"
                + " VALUES (%s)", rv.getPrecioTotal());

        try {
            conectar();
            //sentencia = conexion.createStatement();
            sentencia.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Error al insertar venta" + ex.toString());
            resultado = false;

        }

        desconectar();
        return resultado;
    }

    /**
     * Busca una venta registrada en la base de datos
     *
     * @param idVenta Número identificativo para cada venta
     * @return RegistroVenta encontrado
     */
    public RegistroVenta buscarVenta(int idVenta) {
        RegistroVenta rv = new RegistroVenta();

        String sql = String.format("SELECT * FROM registroventas WHERE idVenta=%s",
                 idVenta);
        try {
            conectar();
            //sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);

            while (rs.next()) {
                rv.setIdVenta(rs.getInt("idVenta"));
                rv.setPrecioTotal(rs.getFloat("precioTotal"));

            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al buscar Venta: "
                    + ex.toString());
        }
        desconectar();
        return rv;
    }

    /**
     * Borra una venta registrada de la base de datos
     *
     * @param rv RegistroVenta que se quiere borar
     * @return
     */
    public boolean borrarVenta(RegistroVenta rv) {
        boolean resultado = true;

        String sql = String.format("DELETE FROM registroventas WHERE"
                + " idventa = %s", rv.getIdVenta());

        try {
            conectar();
            sentencia = conexion.createStatement();
            sentencia.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al borrar venta: "
                    + ex.toString());
            resultado = false;
        }

        desconectar();
        return resultado;
    }

    /**
     * Modifica los campos de una venta concreta en la base de datos
     *
     * @param rv Venta que se desea modificar
     * @return
     */
    public boolean modificarVenta(RegistroVenta rv) {
        boolean resultado = true;

        //Para evitar conflicto con las foreign keys, se almacena dos strings,
        //una para desactivarlas y otra para activarlas
        String desactivaFk = "SET FOREIGN_KEY_CHECKS=0";
        String activaFk = "SET FOREIGN_KEY_CHECKS=1";
        // hacer la consulta de modificacion
        String sql = String.format("UPDATE registroVentas SET precioTotal = %s"
                + " WHERE idventa = %s ", rv.getPrecioTotal(), rv.getIdVenta());

        try {
            conectar();
            sentencia = conexion.createStatement();
            //Desactivamos la FK para poder hacer el update
            sentencia.executeUpdate(desactivaFk);
            //Realizamos el update
            sentencia.executeUpdate(sql);
            //Activamos de nuevo la FK
            sentencia.executeUpdate(activaFk);

        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Error al modificar usuario" + ex.toString());
            resultado = false;

        }
        desconectar();
        return resultado;
    }

    /**
     * Inserta por separado cada producto vendido en un ticket
     *
     * @param vp VentaProducto que se quiere registrar en la base de datos
     * @return
     */
    public boolean insertarVP(VentaProducto vp) {
        boolean resultado = true;

        String sql = String.format("INSERT INTO ventaProducto (producto, venta,"
                + "cant_vendida, vendedor) VALUES (%s, %s, %s, %s)",
                vp.getIdProducto(), vp.getIdVenta(), vp.getCantidad(),
                vp.getVendedor());

        try {
            conectar();
            sentencia = conexion.createStatement();
            sentencia.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Error al insertar venta" + ex.toString());
            resultado = false;

        }
        desconectar();
        return resultado;
    }

    /**
     * Borra todos los registros asociados a un registro de venta
     *
     * @param id idVenta del ticket
     * @return
     */
    public boolean borrarVP(int id) {
        boolean resultado = true;
        String sql = String.format("DELETE FROM ventaproducto WHERE "
                + "venta = %s ", id);

        try {
            conectar();
            sentencia = conexion.createStatement();
            sentencia.executeUpdate(sql);

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al borrar venta: "
                    + ex.toString());
            resultado = false;
        }

        desconectar();
        return resultado;
    }

    public int recuperaIdProducto(String nombreProducto){
        int idResultado = -1;
        String sql = String.format("SELECT * FROM productos WHERE nombre='%s'",
                 nombreProducto);

        try {
            conectar();
            sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);

            while (rs.next()) {
                idResultado = rs.getInt("idProducto");
            }

        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al buscar producto: "
                    + ex.toString());
        }

        desconectar();
        return idResultado;

    }
    
    /**
     * Recupera la id del último ticket producido
     * @return 
     */
    public int recuperaTicket(){
        int resultado = -1;
        String sql = "SELECT * FROM registroventas ORDER BY idVenta DESC LIMIT 1 ";
                

        try {
            conectar();
            sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(sql);

            while (rs.next()) {
                resultado = (rs.getInt("idVenta"));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error al encontrar la venta: "
                    + ex.toString());
        }

        desconectar();
        return resultado;
    }
}
