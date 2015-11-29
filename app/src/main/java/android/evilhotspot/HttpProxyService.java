package android.evilhotspot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.jsoup.nodes.Document;

public class HttpProxyService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //preform a request and print response on console
        Log.d("PROXY", "starting HTTP Proxy Service");
        requestTask rt = new requestTask();
        Document response =  rt.doInBackground("http://www.joemonster.org/");
        //Log.d("PROXY", response);
        HTMLEditor.Reader(getApplicationContext(),response);
        return Service.START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
