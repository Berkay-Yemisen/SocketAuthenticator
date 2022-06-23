module me.t3sl4.socketauthenticator {
    requires javafx.controls;
    requires javafx.fxml;


    opens me.t3sl4.socketauthenticator to javafx.fxml;
    exports me.t3sl4.socketauthenticator;
    exports me.t3sl4.socketauthenticator.controllers;
    opens me.t3sl4.socketauthenticator.controllers to javafx.fxml;
}