package android.evilhotspot.proxy;

import android.evilhotspot.ApManager;
import android.evilhotspot.SettingsActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

/**
 * Created by Nibiru on 2016-01-08.
 */
public class proxyRunnable implements Runnable {
    final static String TAG = "proxyRunnable";

    private Socket client = null;
    private String request = "";
    private String line = "";
    private BufferedReader in;
    private PrintWriter out;
    private OutputStream outIMG;

    public proxyRunnable(Socket socket) {
        super();
        this.client = socket;
        try {
            client.setSoTimeout(3000);
        } catch (SocketException e) {
            Log.d(TAG, "Socket exception");
            e.printStackTrace();
        }
    }

    public void run() {

                    //get request from client
                    try {
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        //in2 = client.getInputStream().available();
                        out = new PrintWriter(client.getOutputStream(), true);
                        outIMG = client.getOutputStream();

                        request = "";
                        Log.d(TAG, "<==================Accepted client==================>");
                        while ((line = in.readLine()) != null) {

                            Log.d("proxyRequest[IN]", line);
                            request += line + '\n';

                            //send response to client
                            if (line.isEmpty()){
                                try {
                                    HttpRequestParser parser = new HttpRequestParser();
                                    parser.parseRequest(request);
                                    //CHECK IF REQUEST IS FOR IMAGE
                                    if (parser.isIMG()){
                                        Log.d("proxyRequest[OUT]", "it is img, sending image bytes");
                                        URL url = new URL(SettingsActivity.URLfield);
                                        HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
                                        InputStream is = connection.getInputStream();
                                        byte[] bytes = new byte[16*1024];
                                        int count;
                                        while ((count = is.read(bytes)) > 0) {
                                            outIMG.write(bytes, 0, count);
                                        }

                                        client.close();
                                        in.close();
                                        out.close();
                                        outIMG.close();
                                    }
                                    Log.d(TAG, "<==================Sending response==================>");
                                    requestTask rt = new requestTask();
                                    String response = rt.doInBackground(parser);

                                    //EDIT RESPONSE TO SEND OUT
                                    /*if (parser.isHTML()) {
                                        response = HTMLEditor.editHTML(response);
                                        response = HTMLEditor.editHTMLJsoup(response);
                                    }*/
                                    out.print(response);
                                    out.flush();

                                } catch (Exception e) {
                                    Log.d(TAG, "------------ ABANDON SHIP, EXCEPTION !!! ---------------");
                                    e.printStackTrace();
                                    break;
                                }
                                Log.d(TAG, "Sent response, closing connection ---------------");
                                //close out all resources
                                if (out != null) {
                                    out.close();
                                }
                                if (in != null) {
                                    in.close();
                                }
                                if (client != null) {
                                    client.close();
                                }
                                break;
                            }
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "Connection interrupted. Please reconnect your phones.");
                        e.printStackTrace();
                    }
                }

    public String makeURL(HttpRequestParser parser){
        //make the proper URL
        String url = "http://";
        url += parser.getHeaderParam("Host");
        String reqLine = parser.getRequestLine();
        String[] parts = reqLine.split(" ");
        url = url + parts[1];
        Log.d("proxyRequest[OUT]", url);
        return url;
    }

}

