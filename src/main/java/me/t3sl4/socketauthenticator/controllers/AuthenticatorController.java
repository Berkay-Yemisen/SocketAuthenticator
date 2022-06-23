package me.t3sl4.socketauthenticator.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import me.t3sl4.socketauthenticator.SocketAuthenticator;
import me.t3sl4.socketauthenticator.server.Client;
import me.t3sl4.socketauthenticator.server.ClientHandler;
import me.t3sl4.socketauthenticator.utils.AES;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AuthenticatorController implements Initializable {

    @FXML
    private TextField userNameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button loginButtonId;

    @FXML
    private Button logoutButtonId;

    Alert alert = new Alert(Alert.AlertType.ERROR);
    Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
    File file;
    private InputStream inputStream = SocketAuthenticator.class.getResourceAsStream("/database/users.txt");

    public List usersInfo = new ArrayList<>();
    private Client client;
    private Socket socket;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logoutButtonId.setVisible(false);
        //Ekran açıldığında önce sunucu durumunu kontrol ettiriyorum.
        checkServerStatusRunnable();
        //Yeni bir dosya tanımlayarak kullanıcılar dosyamı oluşturuyorum.
        file = new File("users.txt");
        try {

            copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginButton() throws Exception {

        if(userNameTextField.getText() != null && passwordTextField.getText() != null) {
            String userName = userNameTextField.getText();
            String password = passwordTextField.getText();


            BufferedReader br = new BufferedReader(new FileReader(file));
            String tempReading;
            while ((tempReading = br.readLine()) != null) {

                String[] tempUserInfo = tempReading.split("berkayyemisen");

                usersInfo = Arrays.asList(tempUserInfo);
            }

            if(usersInfo.contains(AES.encrypt(userName))) {

                if(usersInfo.toString().contains(AES.encrypt(password))) {

                    if(!ClientHandler.clientHandlers.contains(userName)) {
                        Task<Void> loginTask = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                socket = new Socket("localhost", 1234);
                                client = new Client(socket, userName, password);
                                return null;
                            }
                        };

                        Thread loginThread = new Thread(loginTask);
                        loginThread.start();
                        loginButtonId.setVisible(false);
                        userNameTextField.setVisible(false);
                        passwordTextField.setVisible(false);
                        logoutButtonId.setVisible(true);
                        alert2.setTitle("SUCCESS!");
                        alert2.setHeaderText("You are logged into the app.");
                        alert2.showAndWait();
                    } else {
                        alert.setTitle("ERROR!");
                        alert.setHeaderText("Login Error.");
                        alert.setContentText("You cannot have multiple access to the same account.");
                        alert.showAndWait();
                    }
                }
            } else {
                alert.setTitle("ERROR!");
                alert.setHeaderText("Login Error.");
                alert.setContentText("Check your username or password.");
                alert.showAndWait();
            }
        } else {
            alert.setTitle("ERROR!");
            alert.setHeaderText("Login Information Error.");
            alert.setContentText("Check your username or password.");
            alert.showAndWait();
        }
    }

    public void onLogout() {
        Task<Void> logoutTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                client.closeEverything(socket);
                return null;
            }
        };
        Thread logoutThread = new Thread(logoutTask);
        logoutThread.start();
        logoutButtonId.setVisible(false);
        userNameTextField.setVisible(true);
        passwordTextField.setVisible(true);
        loginButtonId.setVisible(true);
    }


    private static boolean isPortAvailable(int port) {
        try {
            ServerSocket srv = new ServerSocket(port);
            srv.close();
            srv = null;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void checkServerStatusRunnable() {

        Runnable serverStatusImageRunnable = new Runnable() {
            public void run() {
                if(isPortAvailable(1234)) {
                    alert.setTitle("ERROR!");
                    alert.setHeaderText("Server error.");
                    alert.setContentText("Please restart the program.");
                    alert.showAndWait();
                }
            }
        };


        ScheduledExecutorService serverStatusExec = Executors.newScheduledThreadPool(1);
        serverStatusExec.scheduleAtFixedRate(serverStatusImageRunnable , 0, 3, TimeUnit.MINUTES);
    }


    private static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }
}