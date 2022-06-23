package me.t3sl4.socketauthenticator;

import me.t3sl4.socketauthenticator.server.Server;

import java.io.IOException;

public class Runner {


    public static void main(String[] args) throws IOException {

        SocketAuthenticator.main(args);

        Server.main(args);
    }
}
