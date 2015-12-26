package android.evilhotspot;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Created by Nibiru on 2015-12-24.
 */
public class startProxyTask extends AsyncTask<Context, Void, Void> {
    //start http proxy service, make the intent and start
    @Override
    protected Void doInBackground(Context... params) {
        Intent i= new Intent(params[0], HttpProxyService.class);
        params[0].startService(i);
        return null;
    }
}
