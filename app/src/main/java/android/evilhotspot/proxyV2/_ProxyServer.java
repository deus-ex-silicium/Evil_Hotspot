package android.evilhotspot.proxyV2;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Nibiru on 2016-01-09.
 */
public class _ProxyServer {
    final static String TAG = "_ProxyServer";

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        boolean listening = true;

        int port = 1337;	//default
        /*try {
            port = Integer.parseInt(args[]);
        } catch (Exception e) {
            //ignore me
        }*/

        try {
            serverSocket = new ServerSocket(port);
            Log.d(TAG, "Started on: " + port);
        } catch (IOException e) {
            Log.d(TAG, "Could not listen on port: " + args[0]);
            System.exit(-1);
        }

        while (listening) {
            new _ProxyThread(serverSocket.accept()).start();
        }
        serverSocket.close();
    }
}
