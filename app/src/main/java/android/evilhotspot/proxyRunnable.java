package android.evilhotspot;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;

/**
 * Created by Nibiru on 2016-01-08.
 */
public class proxyRunnable implements Runnable {
    final static String TAG = "proxyRunnable";

    // default IP
    public static String SERVERIP = "192.168.43.1";
    // designate a port
    public static final int SERVERPORT = 1337;
    private ServerSocket serverSocket;

    HttpRequestParser parser;

    private String request = "";
    private String line = "";
    protected Boolean work = true;
    private BufferedReader in;
    private PrintWriter out;
    String url = "";
    public void run() {

        try {
            if (SERVERIP != null) {
                /*handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Listening on IP: " + SERVERIP);
                    }
                });*/
                serverSocket = new ServerSocket(SERVERPORT);
                //serverSocket.setSoTimeout(5000);
                while (work) {
                    // listen for incoming clients
                    Log.d(TAG, "Listening on IP: " + SERVERIP);
                    final Socket client = serverSocket.accept();
                    client.setSoTimeout(5000);
                    //get request from client
                    try {
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        out = new PrintWriter(client.getOutputStream(), true);
                        request = "";
                        Log.d(TAG, "Accepted client---------------");
                        while ((line = in.readLine()) != null) {
                            Log.d("proxyRequest[IN]", line);
                            request += line + '\n';

                            //send response to client
                            if (line.isEmpty()){
                                try {
                                    HttpRequestParser parser = new HttpRequestParser();
                                    parser.parseRequest(request);
                                    Log.d(TAG, "Sending response---------------");
                                    requestTask rt = new requestTask();
                                    String response = rt.doInBackground(parser);
                                    if (response.isEmpty()) {
                                        Log.d(TAG, "Received 204, closing ---------------");
                                        client.close();
                                    }
                                    //EDIT RESPONSE TO SEND OUT
                                    response = HTMLEditor.editHTML(response);
                                    //response = HTMLEditor.editHTMLJsoup(response);
                                    out.print(response);
                                    out.flush();
                                } catch (Exception e) {
                                    Log.d("proxyEXCEPTION!!!", "ABANDON SHIP !!! ---------------");
                                    e.printStackTrace();
                                    break;
                                }
                                Log.d(TAG, "Sent response, closing connection ---------------");
                                //close out all resources
                                if (out != null) {
                                    out.close();
                                }
                                if (in != null) {
                                    in.reset();
                                    in.close();
                                }
                                if (client != null) {
                                    client.close();
                                }
                                break;
                            }
                        }

                    } catch (Exception e) {
                        Log.d("proxyEXCEPTION!!!", "Connection interrupted. Please reconnect your phones.");
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d("proxyEXCEPTION!!!", "Couldn't detect internet connection.");
            }
         } catch (Exception e) {
            Log.d("proxyEXCEPTION!!!", "Error!");
            e.printStackTrace();
        }
    }

}

