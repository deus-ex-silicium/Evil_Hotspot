package android.evilhotspot.proxy;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Nibiru
 */
public class requestTask extends AsyncTask<HttpRequestParser, Void, String> {

    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(HttpRequestParser... parser) {
        //make a builder for a website, return response
        Request.Builder builder;
        String url = "http://";
        url += parser[0].getHeaderParam("Host");
        String reqLine = parser[0].getRequestLine();
        String[] parts = reqLine.split(" ");
        url = url + parts[1];
        Log.d("proxyRequest[OUT]", url);

        String chuj = "";
        builder = new Request.Builder().url(url);
                //.addHeader("User-Agent", parser[0].getHeaderParam("User-Agent"))
               // .build();
        for (int i = 0; i < parser[0].getHeaderCount(); i++) {
            //chuj += (String)parser[0].getHeaderParam(i).first + ":" + (String)parser[0].getHeaderParam(i).second + "\n";
            if(parser[0].getHeaderParam(i).first.toString().equals("Accept-Encoding"))
                 builder.addHeader((String) parser[0].getHeaderParam(i).first, "identity");
            else
                builder.addHeader((String) parser[0].getHeaderParam(i).first, (String) parser[0].getHeaderParam(i).second);

        }
        Request request =  builder.build();


/*
        if (parser[0].getHeaderParam("Cookie") != null && parser[0].getHeaderParam("Content-Type") != null &&
        parser[0].getHeaderParam("Accept") != null && parser[0].getHeaderParam("Accept-Language") != null) {
            builder = new Request.Builder().url(url)
                    .addHeader("Connection", parser[0].getHeaderParam("Connection"))
                    .addHeader("Accept", parser[0].getHeaderParam("Accept"))
                    .addHeader("User-Agent", parser[0].getHeaderParam("User-Agent"))
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader("Accept-Language", parser[0].getHeaderParam("Accept-Language"))
                    .addHeader("Cookie", parser[0].getHeaderParam("Cookie"))
                    .addHeader("Content-Type", parser[0].getHeaderParam("Content-Type"))
                    .build();
/*
        } else if (parser[0].getHeaderParam("Cookie") != null) {
            builder = new Request.Builder().url(url)
                    .addHeader("Connection", parser[0].getHeaderParam("Connection"))
                    .addHeader("Accept", parser[0].getHeaderParam("Accept"))
                    .addHeader("User-Agent", parser[0].getHeaderParam("User-Agent"))
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader("Accept-Language", parser[0].getHeaderParam("Accept-Language"))
                    .addHeader("Cookie", parser[0].getHeaderParam("Cookie"))
                    .build();
        }
        else if (parser[0].getHeaderParam("Accept") != null){
            builder = new Request.Builder().url(url)
                    .addHeader("Connection", parser[0].getHeaderParam("Connection"))
                    .addHeader("User-Agent", parser[0].getHeaderParam("User-Agent"))
                    .addHeader("Accept", parser[0].getHeaderParam("Accept"))
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader("Accept-Language", parser[0].getHeaderParam("Accept-Language"))
                    .build();
*/
//        } else
//            builder = new Request.Builder().url(url).build();

        try {
            //Log.d("RES", builder.headers().toString());
            Response response = client.newCall(request).execute();
            if (response.code() == 204)
                return "";
            else {
                String full = "";
                //Headers h = response.headers();
                //full += h.toString();
                //full += "\r\n";
                full += response.body().string();
                return full;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }
}
