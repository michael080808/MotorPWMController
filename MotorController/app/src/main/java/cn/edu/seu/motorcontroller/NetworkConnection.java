package cn.edu.seu.motorcontroller;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;

public class NetworkConnection implements Callable<Socket> {
    private String host;
    private int port;

    public NetworkConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Socket call() {
        try {
            Log.i("Socket Connect", "Try to Connect to Server...");
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 1000);
            return socket;
        } catch (IOException e) {
            Log.w("Socket Connect", e.getMessage());
            return null;
        }
    }
}
