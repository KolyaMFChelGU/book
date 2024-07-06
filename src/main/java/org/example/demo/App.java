package org.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Загрузка FXML-файла, связанного с контроллером
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

        // Установка заголовка окна
        primaryStage.setTitle("Контактная книга");

        // Создание и установка сцены
        primaryStage.setScene(new Scene(root));

        // Отображение окна
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
