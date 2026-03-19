module com.kampus.pertamasistemlaundry {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql; 

    requires java.naming; 
    requires java.base;

    opens com.kampus.pertamasistemlaundry to javafx.fxml;

    exports com.kampus.pertamasistemlaundry;

}