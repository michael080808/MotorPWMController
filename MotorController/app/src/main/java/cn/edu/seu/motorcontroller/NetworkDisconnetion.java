package cn.edu.seu.motorcontroller;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class NetworkDisconnetion implements Callable<Socket> {
    private Socket socket;

    public NetworkDisconnetion(Socket socket) {
        this.socket = socket;
    }

    @Override
    public Socket call() {
        try {
            Log.i("Socket Connect", "Try to Close Socket...");
            socket.close();
            return socket;
        } catch (IOException e) {
            Log.w("Socket Connect", e.getMessage());
            return null;
        }
    }
}
