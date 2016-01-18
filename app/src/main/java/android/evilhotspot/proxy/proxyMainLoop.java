package android.evilhotspot.proxy;

import android.evilhotspot.ApManager;
import android.util.Log;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nibiru on 2016-01-14.
 */
public class proxyMainLoop implements Runnable {

    final static String TAG = "proxyMainLoop";
    private ServerSocket serverSocket;
    public static String SERVERIP = ApManager.getIpAddr();
    public static final int SERVERPORT = 1337;
    protected Boolean work = true;

    @Override
    public void run() {
        //http://codetheory.in/android-java-executor-framework/
        //int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                140,
                140,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );

        try {
            if (SERVERIP != null) {
                serverSocket = new ServerSocket(SERVERPORT);

                while (work) {
                    // listen for incoming clients
                    Log.d(TAG, "Listening on IP: " + SERVERIP);
                    executor.execute(new proxyRunnable(serverSocket.accept()));
                }
            } else {
                Log.d(TAG, "Couldn't detect internet connection.");
            }
        } catch (Exception e) {
            Log.d(TAG, "Error!");
            e.printStackTrace();
        }
    }
}
