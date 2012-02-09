package com.seb.chess.core.players;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.seb.chess.core.Move;
import com.seb.chess.core.Plateau;

public class NetPlayer extends ThreadPlayer {

    private final Socket connection;

    private final BufferedReader input;

    private final PrintWriter output;

    private static final String MOVE_TOKEN = "move";

    public NetPlayer(Socket connection) {
        this.connection = connection;
        try {
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            output = new PrintWriter(connection.getOutputStream(), true);
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Move computeMove(Plateau plateau) throws IOException {
        System.out.print("Waiting for remote move...");
        String moveStr = input.readLine();
        String[] tokens = moveStr.split(" ");
        if (tokens.length != 2)
            throw new IOException();
        moveStr = tokens[0];
        System.out.print("move received : " + moveStr + "...");
        Move m = Move.parseMove(moveStr);
        System.out.println("ok");
        return m;
    }

    public Socket getConnection() {
        return connection;
    }

    public void sendMove(Move move) {
        System.out.println("Send move " + move);
        output.println(MOVE_TOKEN + ":" + move);
    }
    
    @Override
    public String getDescription() {
        return "JOUEUR DISTANT ["+connection.getInetAddress()+"]";
    }
}
