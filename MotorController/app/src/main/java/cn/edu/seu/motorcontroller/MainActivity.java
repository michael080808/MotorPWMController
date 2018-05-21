package cn.edu.seu.motorcontroller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    Socket socket;
    ExecutorService service;
    final static String[] permissions = {Manifest.permission.INTERNET};
    final static int PERMISSION_REQUEST_ON_CREATE = 0x05220112;
    final static int PERMISSION_REQUEST_ON_CONNECT = 0x05220127;

    private void connectionState() {
        final Button inc = findViewById(R.id.inc);
        final Button dec = findViewById(R.id.dec);
        final Button send = findViewById(R.id.send);
        final TextView status = findViewById(R.id.status);
        final TextView hostView = findViewById(R.id.host);
        final TextView portView = findViewById(R.id.port);
        final Button connection = findViewById(R.id.connection);

        if (connection.getText() == getString(R.string.connect)) {
            // 尝试打开连接
            String host = hostView.getText().toString();
            int port = Integer.parseInt(portView.getText().toString());
            Future<Socket> future = service.submit(new NetworkConnection(host, port));

            try {
                socket = future.get();
            } catch (Exception e) {
                Log.w("Callable Exception", e.getMessage());
            }

            if (socket != null && socket.isConnected()) {
                connection.setText(R.string.disconnect);
                status.setText(R.string.status_connected);
                hostView.setEnabled(false);
                portView.setEnabled(false);
                inc.setEnabled(true);
                dec.setEnabled(true);
                send.setEnabled(true);
            } else {
                status.setText(R.string.status_connect_error);
            }
        } else {
            // 尝试关闭连接
            Future<Socket> future = service.submit(new NetworkDisconnetion(socket));

            try {
                socket = future.get();
            } catch (Exception e) {
                Log.w("Callable Exception", e.getMessage());
            }

            if (socket.isClosed()) {
                connection.setText(R.string.connect);
                status.setText(R.string.status_disconnected);
                hostView.setEnabled(true);
                portView.setEnabled(true);
                inc.setEnabled(false);
                dec.setEnabled(false);
                send.setEnabled(false);
            } else {
                status.setText(R.string.status_close_error);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service = Executors.newCachedThreadPool();

        for (String permission : permissions)
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permissions, PERMISSION_REQUEST_ON_CREATE);
                break;
            }

        final Button inc = findViewById(R.id.inc);
        final Button dec = findViewById(R.id.dec);
        final Button send = findViewById(R.id.send);
        final TextView pwm = findViewById(R.id.pwm);
        final TextView status = findViewById(R.id.status);
        final Button connection = findViewById(R.id.connection);

        connection.setOnClickListener(view -> {
            for (String permission : permissions)
                if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(permissions, PERMISSION_REQUEST_ON_CONNECT);
                    return;
                }
            connectionState();
        });

        inc.setOnClickListener(view -> {
            Integer n = Integer.parseInt(pwm.getText().toString()) + 1;
            if (n <= Integer.MAX_VALUE)
                pwm.setText(n.toString());

            Future<String> future = service.submit(new NetworkTransmission(socket, pwm.getText().toString()));

            try {
                String recv = future.get();
                if (recv != null && recv.equals("OK"))
                    status.setText(R.string.status_send_success);
                else
                    status.setText(R.string.status_send_fail);
            } catch (Exception e) {
                Log.w("Callable Exception", e.getMessage());
            }
        });
        dec.setOnClickListener(view -> {
            Integer n = Integer.parseInt(pwm.getText().toString()) - 1;
            if (n >= 0)
                pwm.setText(n.toString());

            Future<String> future = service.submit(new NetworkTransmission(socket, pwm.getText().toString()));

            try {
                String recv = future.get();
                if (recv != null && recv.equals("OK"))
                    status.setText(R.string.status_send_success);
                else
                    status.setText(R.string.status_send_fail);
            } catch (Exception e) {
                Log.w("Callable Exception", e.getMessage());
            }
        });
        send.setOnClickListener(view -> {
            Future<String> future = service.submit(new NetworkTransmission(socket, pwm.getText().toString()));

            try {
                String recv = future.get();
                if (recv != null && recv.equals("OK"))
                    status.setText(R.string.status_send_success);
                else
                    status.setText(R.string.status_send_fail);
            } catch (Exception e) {
                Log.w("Callable Exception", e.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_ON_CONNECT) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED)
                    return;
            }
            connectionState();
        }
    }
}
