package cn.edu.seu.motorcontroller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class NetworkTransmission implements Callable<String> {
    Socket socket;
    String packet;

    public NetworkTransmission(Socket socket, String data) {
        this.socket = socket;
        this.packet = data;
    }

    @Override
    public String call() {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(packet);
            writer.flush();
            socket.setSoTimeout(1000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return reader.readLine();
        } catch (IOException e) {
            Log.w("Socket I/O", e.getMessage());
            return null;
        }
    }
}
