package android.evilhotspot.proxy;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Nibiru
 */
public class requestTask extends AsyncTask<HttpRequestParser, Void, String> {
    //OkHTTP tutorial
    //http://www.skholingua.com/android-basic/other-sdk-n-libs/okhttp
    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(HttpRequestParser... parser) {
        //make a builder for a website, return response
        Request request;
        Request.Builder builder;
        String url = "http://";
        url += parser[0].getHeaderParam("Host");
        String reqLine = parser[0].getRequestLine();
        String[] parts = reqLine.split(" ");
        url = url + parts[1];
        Log.d("proxyRequest[OUT]", url);


        builder = new Request.Builder().url(url);
        for (int i = 0; i < parser[0].getHeaderCount(); i++) {
            //because I dont want to uzip stuff
            if(parser[0].getHeaderParam(i).first.toString().equals("Accept-Encoding")) {
                builder.addHeader((String) parser[0].getHeaderParam(i).first, "identity");
            }
            //b/c I don't want https
            else if (parser[0].getHeaderParam(i).first.toString().equals("Upgrade-Insecure-Requests")){
            }
            //TODO adding User-Agent header causes weird shit, why ?
            else if (parser[0].getHeaderParam(i).first.toString().equals("User-Agent")){
            }
            else
                builder.addHeader((String) parser[0].getHeaderParam(i).first, (String) parser[0].getHeaderParam(i).second);
        }
        request =  builder.build();

        try {
            Response response = client.newCall(request).execute();

            if (response.code() == 204)
                return "HTTP/1.1 204 No Content\r\n\r\n";
            else {
                String full = "";
                //TODO response headers get displayed as plain text
                /*Headers h = response.headers();
                full += "HTTP/1.1 200 OK\n";
                full += h.toString();
                full = full.replaceAll("\\n", "\r\n");
                Log.d("proxyResponse[IN]", full);
                //Remove two last lines (they are okhttp specific)
                //need to call three times b/c last line also has /r/n
                full = RemoveLastLine(full);
                full = RemoveLastLine(full);
                full = RemoveLastLine(full);
                full += "\r\n";
                full += "\r\n\r\n";*/
                full += response.body().string();
                return full;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    private String RemoveLastLine(String x){
        if(x.lastIndexOf("\r\n")<0) {
            return x;
        } else {
            return x.substring(0, x.lastIndexOf("\r\n"));
        }
    }

}
