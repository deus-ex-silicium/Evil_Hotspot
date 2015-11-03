package android.evilhotspot;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Create an ApManager to turn hotspot on and off
        ApManager ap = new ApManager();
        //Register our buttons OnClickListener
        Button hsButton = (Button) findViewById(R.id.hsButton);
        hsButton.setOnClickListener(this);
        Button rtButton = (Button) findViewById(R.id.rtButton);
        rtButton.setOnClickListener(this);
        //Log.e("MyTemp", netInterface.getDisplayName());
    }


    boolean flag = true;
    public void onClick(View v) {
        // default method for handling onClick Events for our MainActivity
        switch (v.getId()) {

            case R.id.hsButton:
                //if hs button was pressed turn on/off hotspot
                ApManager.configApState(MainActivity.this);

                //for changing button looks when pressed
                Button btn = (Button) findViewById(R.id.hsButton);
                if (flag) {
                    btn.setBackgroundResource(R.drawable.button_on);
                    flag = false;
                 } else {
                    btn.setBackgroundResource(R.drawable.button_off);
                    flag = true;
                 }
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
            startActivity(new Intent(this,SettingsActivity.class));
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
