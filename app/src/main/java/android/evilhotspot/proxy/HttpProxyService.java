package android.evilhotspot.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


public class HttpProxyService extends Service {


    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

        Thread proxy = new Thread(new proxyRunnable());
        proxy.start();


        /*String[] args = {"_ProxyServer"};
        _ProxyServer p = new _ProxyServer();
        try {
            p.main(args);
        } catch (IOException e) {
            Log.d("_ProxyServer", "error on start");
        }*/

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Proxy starting", Toast.LENGTH_SHORT).show();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Proxy quitting", Toast.LENGTH_SHORT).show();
    }
}

