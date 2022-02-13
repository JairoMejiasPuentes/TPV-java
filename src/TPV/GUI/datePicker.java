/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPV.GUI;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

/**
 *
 * @author Jairo
 */
public class datePicker extends Application {
    @Override
    public void start(Stage s) 
    { 
        // set title for the stage 
        s.setTitle("creating date picker"); 
  
        // create a tile pane 
        TilePane r = new TilePane(); 
  
        // create a date picker 
        DatePicker d = new DatePicker(); 
  
        // add button and label 
        r.getChildren().add(d); 
  
        // create a scene 
        Scene sc = new Scene(r, 200, 200); 
  
        // set the scene 
        s.setScene(sc); 
  
        s.show(); 
    } 
  
    public static void main(String args[]) 
    { 
        // launch the application 
        launch(args); 
    } 
} 
