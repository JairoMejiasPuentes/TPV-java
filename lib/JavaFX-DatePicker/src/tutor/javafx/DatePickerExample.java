package tutor.javafx;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;

import javafx.util.converter.LocalDateStringConverter;

public class DatePickerExample extends Application {

    @Override
    public void start(Stage primaryStage) {

        Callback<DatePicker, DateCell> dayCellFactory
                = date -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {

                super.updateItem(item, empty);
                
                this.setDisable(false);
                this.setBackground(null);
                this.setTextFill(Color.BLACK);

                // deshabilitar las fechas futuras
                if (item.isAfter(LocalDate.now())) {
                    this.setDisable(true);
                }

                // marcar los dias de quincena
                int day = item.getDayOfMonth();
                if(day == 15 || day == 30) {

                    Paint color = Color.RED;
                    BackgroundFill fill = new BackgroundFill(color, null, null);
                    
                    this.setBackground(new Background(fill));
                    this.setTextFill(Color.WHITESMOKE);
                }
                
                // fines de semana en color verde
                DayOfWeek dayweek = item.getDayOfWeek();
                if (dayweek == DayOfWeek.SATURDAY || dayweek == DayOfWeek.SUNDAY) {
                    this.setTextFill(Color.GREEN);
                }
            }
        };

        DatePicker fecha1 = new DatePicker();
        fecha1.setOnAction(e -> System.out.println("fecha: " + fecha1.getValue()));
        fecha1.setDayCellFactory(dayCellFactory);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        DatePicker fecha2 = new DatePicker(LocalDate.now());
        fecha2.setConverter(new LocalDateStringConverter(formatter, null));
        fecha2.setShowWeekNumbers(true);

        VBox root = new VBox(fecha1, fecha2);
        root.setAlignment(Pos.TOP_LEFT);
        root.setSpacing(10.0);
        root.setPadding(new Insets(5.0));

        Scene scene = new Scene(root, 800, 300);

        primaryStage.setTitle("JavaFX DatePicker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
