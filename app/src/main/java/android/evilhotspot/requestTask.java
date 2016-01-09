package android.evilhotspot;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import okhttp3.Headers;
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
        //make a request for a website, return response
        Request request;
        String url = "http://";
        url += parser[0].getHeaderParam("Host");
        String reqLine = parser[0].getRequestLine();
        String[] parts = reqLine.split(" ");
        url = url + parts[1];
        Log.d("proxyRequest[OUT]", url);
        String chuj = "";

        request = new Request.Builder().url(url).build();
        /*for (int i = 0; i < parser[0].getHeaderCount(); i++) {
            //chuj += (String)parser[0].getHeaderParam(i).first + ":" + (String)parser[0].getHeaderParam(i).second + "\n";\
            request.newBuilder().addHeader((String)parser[0].getHeaderParam(i).first, (String)parser[0].getHeaderParam(i).second).build();
        }*/



        if (parser[0].getHeaderParam("Cookie") != null & parser[0].getHeaderParam("Content-Type") != null) {
            request = new Request.Builder().url(url)
                    .addHeader("Connection", parser[0].getHeaderParam("Connection"))
                    .addHeader("Accept", parser[0].getHeaderParam("Accept"))
                    .addHeader("User-Agent", parser[0].getHeaderParam("User-Agent"))
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader("Accept-Language", parser[0].getHeaderParam("Accept-Language"))
                    .addHeader("Cookie", parser[0].getHeaderParam("Cookie"))
                    .addHeader("Content-Type", parser[0].getHeaderParam("Content-Type"))
                    .build();

        } else if (parser[0].getHeaderParam("Cookie") != null) {
            request = new Request.Builder().url(url)
                    .addHeader("Connection", parser[0].getHeaderParam("Connection"))
                    .addHeader("Accept", parser[0].getHeaderParam("Accept"))
                    .addHeader("User-Agent", parser[0].getHeaderParam("User-Agent"))
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader("Accept-Language", parser[0].getHeaderParam("Accept-Language"))
                    .addHeader("Cookie", parser[0].getHeaderParam("Cookie"))
                    .build();
        }
        else {
            request = new Request.Builder().url(url)
                    .addHeader("Connection", parser[0].getHeaderParam("Connection"))
                    .addHeader("Accept", parser[0].getHeaderParam("Accept"))
                    .addHeader("User-Agent", parser[0].getHeaderParam("User-Agent"))
                    .addHeader("Accept-Encoding", "identity")
                    .addHeader("Accept-Language", parser[0].getHeaderParam("Accept-Language"))
                    .build();

        }
        try {
            //Log.d("RES", request.headers().toString());
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
