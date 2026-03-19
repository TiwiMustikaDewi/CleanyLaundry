package com.kampus.pertamasistemlaundry;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    
    public static Pelanggan pelangganSaatIni;
    public static Admin adminSaatIni;
    
    public static String modeLogin = "pelanggan";
    
    @Override
    public void start(Stage stage) throws IOException {
        Database.initializeDatabase(); 
        
        scene = new Scene(loadFXML("role_selection"), 800, 600); 
        stage.setScene(scene);
        stage.setTitle("Cleany - Sistem Laundry");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    public static void setAdminRoot() throws IOException{
        scene.setRoot(loadFXML("PrimarySeller"));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")); 
        return fxmlLoader.load();
    }
    
    public static void logout() throws IOException {
        pelangganSaatIni = null;
        adminSaatIni = null;
        setRoot("role_selection");
    }
    
    public static void main(String[] args) {
        launch();
    }
}