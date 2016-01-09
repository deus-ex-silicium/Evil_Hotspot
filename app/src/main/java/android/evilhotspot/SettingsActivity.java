package android.evilhotspot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jsoup.helper.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity  implements View.OnClickListener{

    public EditText pass;
    public EditText ssid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Evil Hotspot");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true); //creating home button for going up to main activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button savebutton = (Button)findViewById(R.id.savebutton);
        savebutton.setOnClickListener(this);
        Button pickbutton = (Button)findViewById(R.id.pickbutton);
        pickbutton.setOnClickListener(this);

        pass = (EditText)findViewById(R.id.editpass);
        ssid = (EditText)findViewById(R.id.editssid);

        try {
            Context context = getApplicationContext();
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            pass.setText(wifiConfig.preSharedKey);
            ssid.setText(wifiConfig.SSID);
        }
            catch(Exception e){
                e.printStackTrace();
            }
        //Saving ssid and pass when switching between activities
        //if (ApManager.name!=null && ApManager.password!=null){
            //pass.setText(ApManager.password);
            //ssid.setText(ApManager.name);
        //}
        if(ApManager.isCheckBoxChecked==true){
            CheckBox check = (CheckBox) findViewById(R.id.checkbox);
            check.setChecked(true);
        }
        else if(ApManager.isCheckBoxChecked==false){
            CheckBox check = (CheckBox) findViewById(R.id.checkbox);
            check.setChecked(false);
        }
        //Info to Log
        if(!ssid.getText().toString().equals(null) && !pass.getText().toString().equals(null)) {
            try {

                Context context = SettingsActivity.this;
//            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
//            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
//            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
//            WifiInfo wifiInfo = wifiManager.getConnectionInfo();


                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d("wifiInfo", wifiInfo.toString());
                Log.d("SSID", wifiInfo.getSSID());


                //pass.setText(wifiConfig.preSharedKey.toString());

                //String name = ssid.getText().toString();
                //String password = pass.getText().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //  Toast x = Toast.makeText(this, ssssid, Toast.LENGTH_LONG);
       // x.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) { //going back from settings
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
    public void Checkbox_clicked(View v) {

        CheckBox check = (CheckBox)v;
        if(check.isChecked()) {
            String name1 = null;
            String pass1 = null;
            ApManager.isCheckBoxChecked=true;

            if(ssid.toString()!=null || pass.toString()!=null){
                ssid.setText(null);
                pass.setText(null);

                ApManager.name = ssid.getText().toString();
                ApManager.password = pass.getText().toString();
                name1 = ssid.getText().toString();
                pass1 = pass.getText().toString();
            }

            ApManager.setSSIDPass(name1, pass1, SettingsActivity.this);
            Toast toeast = Toast.makeText(this, "Default settings.", Toast.LENGTH_LONG);
            toeast.show();
            if (!ApManager.isApOn(getApplicationContext())) {
                ApManager.configApState(SettingsActivity.this);
                try {
                    Context context = getApplicationContext();
                    WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                    Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                    WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

                    pass.setText(wifiConfig.preSharedKey);
                    ssid.setText(wifiConfig.SSID);
                }
                catch(Exception e){
                    e.printStackTrace();
                }


                Toast enablewifi = Toast.makeText(this, "Tethering ON", Toast.LENGTH_LONG);
                enablewifi.setGravity(Gravity.CENTER, 0, 0);
                enablewifi.show();
                //when on -> change -> off -> on
            } else if(ApManager.isApOn(getApplicationContext())){
                ApManager.configApState(SettingsActivity.this);
                ApManager.configApState(SettingsActivity.this);

                try {
                    Context context = getApplicationContext();
                    WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                    Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                    WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

                    pass.setText(wifiConfig.preSharedKey);
                    ssid.setText(wifiConfig.SSID);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                Toast enablewifi = Toast.makeText(this, "Tethering ON", Toast.LENGTH_LONG);
                enablewifi.setGravity(Gravity.CENTER, 0, 0);
                enablewifi.show();
            }
        }
        //when you UNclick default wifi turns off
        else if(!check.isChecked())
        {
            ApManager.isCheckBoxChecked=false;
            if (!ApManager.isApOn(getApplicationContext())) {
                ssid.setText(null);
                pass.setText(null);
                Toast enablewifi = Toast.makeText(this, "Tethering OFF", Toast.LENGTH_LONG);
                enablewifi.setGravity(Gravity.CENTER, 0, 0);
                enablewifi.show();
                //when on -> change -> off
            } else if(ApManager.isApOn(getApplicationContext())){
                ApManager.configApState(SettingsActivity.this);
                ssid.setText(null);
                pass.setText(null);
                Toast enablewifi = Toast.makeText(this, "Tethering OFF", Toast.LENGTH_LONG);
                enablewifi.setGravity(Gravity.CENTER, 0, 0);
                enablewifi.show();
            }


        }




    }

    public void onClick(View v) {
        // default method for handling onClick Events for our SettingsActivity
        switch (v.getId()) {


            case R.id.savebutton:

                ApManager.name = ssid.getText().toString();
                ApManager.password = pass.getText().toString();

                String name = ssid.getText().toString();
                String password = pass.getText().toString();

                CheckBox check = (CheckBox) findViewById(R.id.checkbox);
                if(check.isChecked()){
                    check.setChecked(false);
                }


                if (password.length() < 8) {
                    Toast toosmall = Toast.makeText(this, "Please enter more than 7 characters", Toast.LENGTH_LONG);
                    toosmall.show();
                    return;
                }

                Toast ssid = Toast.makeText(this, "ssid & pass saved", Toast.LENGTH_LONG);
                ssid.show();

                ApManager.setSSIDPass(name, password, SettingsActivity.this);
                //when saved new ssid when switched on will switch wifi
                //need to be done to reset ssid and password
                if (!ApManager.isApOn(getApplicationContext())) {
                    ApManager.configApState(SettingsActivity.this);
                    Toast enablewifi = Toast.makeText(this, "Tethering ON", Toast.LENGTH_LONG);
                    enablewifi.setGravity(Gravity.CENTER, 0, 0);
                    enablewifi.show();
                    //when on -> change -> off -> on
                } else if(ApManager.isApOn(getApplicationContext())){
                    ApManager.configApState(SettingsActivity.this);
                    ApManager.configApState(SettingsActivity.this);
                    Toast enablewifi = Toast.makeText(this, "Tethering ON", Toast.LENGTH_LONG);
                    enablewifi.setGravity(Gravity.CENTER, 0, 0);
                    enablewifi.show();
                }



                break;
            case R.id.pickbutton:
                Toast url  = Toast.makeText(this, "Url saved.", Toast.LENGTH_LONG);
                url.show();
                break;
        }
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



