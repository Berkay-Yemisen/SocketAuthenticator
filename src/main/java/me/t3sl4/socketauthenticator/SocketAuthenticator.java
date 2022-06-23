package me.t3sl4.socketauthenticator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SocketAuthenticator extends Application {


    private Image socketAuthenticatorIcon = new Image(getClass().getResourceAsStream("/icons/icon.png"));

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SocketAuthenticator.class.getResource("authenticator.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 200);

        stage.getIcons().add(socketAuthenticatorIcon);
        stage.setTitle("Socket Authenticator!");
        stage.setScene(scene);

        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}