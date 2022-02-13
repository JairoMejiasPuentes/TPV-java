/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPV.Clases;

import TPV.GUI.TPV;
import TPV.GUI.VentanaLogin;

/**
 *
 * @author Jairo
 */
public class GestionPantalla {
    public TPV tpv;
    public VentanaLogin vl = new VentanaLogin();



    public GestionPantalla(boolean admin) {
        tpv = new TPV(admin);

    }


}
