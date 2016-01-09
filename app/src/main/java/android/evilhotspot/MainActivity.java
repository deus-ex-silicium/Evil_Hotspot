package android.evilhotspot;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
//List of android permissions:
//http://developer.android.com/reference/android/Manifest.permission.html

//How to get root permissions (will work only on rooted device/emulator:
//http://www.stealthcopter.com/blog/2010/01/android-requesting-root-access-in-your-app/

//Instructions how to get a adb shell on device
//http://android.stackexchange.com/questions/69108/how-to-start-root-shell-with-android-studio

//Phone saying "Read-only file system" when attempting to create a file in /system
//http://stackoverflow.com/questions/6066030/read-only-file-system-on-android
//# mount -o rw,remount /system  <-> # mount -o ro,remount /system

//Questions concerning our app:
//http://stackoverflow.com/questions/15557831/android-use-pcap-library

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //boolean isSettingsSet = false;
    //boolean ButtonON = false;

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

        MyApplication myapp= new MyApplication(this);

        if(ApManager.isApOn(getApplicationContext())){
            hsButton.setBackgroundResource(R.drawable.button_on);
        }
       // Context context = getApplicationContext();

        //Log.e("MyTemp", netInterface.getDisplayName());
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


    public void onClick(View v) {
        // default method for handling onClick Events for our MainActivity
        switch (v.getId()) {

            case R.id.hsButton:
                //if hs button was pressed turn on/off hotspot


                //Part for switching img of main button & enable/disable checkbox default
                Context context = MainActivity.this;
                //WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                try {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                    Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                    WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);


                    if (!ApManager.isApOn(context)) {
                        hsButton.setBackgroundResource(R.drawable.button_on);
                        //ButtonON=true;


                    Toast t = Toast.makeText(this, wifiConfig.SSID, Toast.LENGTH_LONG);
                        t.show();


                        if (wifiConfig.SSID.equals("AndroidAP")) {
                            ApManager.isCheckBoxChecked = true;
                        }
                        //ApManager.isCheckBoxChecked=false;
                        else if (wifiConfig.SSID.equals("AndroidAP")) {
                            ApManager.isCheckBoxChecked = false;
                        }


                    }
                    //WIFI IS ON Make OFF
                    else if (ApManager.isApOn(context)) {
                        hsButton.setBackgroundResource(R.drawable.button_off);

                        if (wifiConfig.SSID.equals("AndroidAP"))  {
                            ApManager.isCheckBoxChecked = true;
                        }
                        //ApManager.isCheckBoxChecked=false;
                        else if (wifiConfig.SSID.equals("AndroidAP"))  {
                            ApManager.isCheckBoxChecked = false;
                        }

                        //ButtonON=false;
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                ApManager.configApState(MainActivity.this);
                //for changing button looks when pressed



                break;
            case R.id.htmlbutton:
                toastMessage("html test");
                HTMLEditor.Reader(this.getApplicationContext());

                break;
            case R.id.rtButton:
                //if root test was pressed attempt to do something as root
                Process p;
                try {
                    //Preform su to get root privileges
                    p = Runtime.getRuntime().exec("su");
                    // Attempt to write a file to a root-only
                    DataOutputStream os = new DataOutputStream(p.getOutputStream());
                    os.writeBytes("echo \"Do I have root?\" > /system/temporary.txt\n");

                    //arpspoof (attempting to run a C program, build with NDK)
                    //get resource handle
                    InputStream raw = getResources().openRawResource(R.raw.arpspoof);
                    saveFile("arpspoof", raw);
                    os.writeBytes("chmod 700 /data/data/android.evilhotspot/files/arpspoof\n");
                    //os.writeBytes("/data/data/android.evilhotspot/files/arpspoof -i wlan0 -t 192.168.43.1 192.168.43.152 \n");


                    // Close the terminal
                    os.writeBytes("exit\n");
                    os.flush();
                    try {
                        p.waitFor();
                        //DEBUG: exit values
                        //toastMessage(Integer.toString(p.exitValue()));
                        if(p.exitValue() == 0){
                            // TODO Code to run on success
                            toastMessage("root");
                        }
                        else{
                            // TODO Code to run on unsuccessful
                            toastMessage("not root");
                        }

                    } catch(InterruptedException e){
                        // TODO Code to run in interrupted exception
                        toastMessage("not root, intrp. exeption");
                    }
                } catch(IOException e) {
                    //TODO Code to run in input/output exception
                    toastMessage("not root, I/O exeption");
            }

        }
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
                Intent setintent = new Intent(this, SettingsActivity.class);
                startActivity(setintent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
    //Function for debugging etc. (shows toast with msg text)
    public void toastMessage(String msg){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }


}
