package android.evilhotspot;

import android.content.*;
import android.net.wifi.*;
import java.lang.reflect.*;
public class ApManager {
    //SOURCE:
    //http://stackoverflow.com/questions/6394599/android-turn-on-off-wifi-hotspot-programmatically
    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //null b/c we are not configuring hotspot yet ?
        //will add configuration later ?
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            //strange behavior, when my WIFI was on isApOn returned false
            //added the ! and it works, meaning it shuts down WIFI if its on
            //and turns on hotspot
            if(!isApOn(context)) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
