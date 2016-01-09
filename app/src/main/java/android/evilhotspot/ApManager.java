package android.evilhotspot;

import android.content.*;
import android.net.wifi.*;
import java.lang.reflect.*;
import java.util.List;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;


public class ApManager {
    //SOURCE:
    //http://stackoverflow.com/questions/6394599/android-turn-on-off-wifi-hotspot-programmatically
    //check whether wifi hotspot on or off

    //store string and boolean used in setting and main activity
    public static String name=null;
    public static String password =null;
    public static boolean isCheckBoxChecked = false;
    private static WifiManager wifiManager;

    protected static void setUp(Context context){
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return;
    }



    protected static boolean isApOn() {
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        }
        catch (Throwable ignored) {}
        return false;
    }


    protected static boolean configApState() {
        //null b/c we are not configuring hotspot yet ?
        //will add configuration later ?
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            //strange behavior, when my WIFI was on isApOn returned false
            //added the ! and it works, meaning it shuts down WIFI if its on
            //and turns on hotspot
            if(!isApOn()) {
                wifiManager.setWifiEnabled(false);
            }
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, wificonfiguration, !isApOn());
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    // toggle wifi hotspot on or off


    public static boolean setSSIDPass(String newName, String newPass, Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            wifiConfig.SSID = newName ;
            wifiConfig.preSharedKey = newPass;

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);




            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected static String getIfName(){
        /*NetworkInterface netInterface;

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        byte[] bytes = BigInteger.valueOf(ipAddress).toByteArray();
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            netInterface = NetworkInterface.getByInetAddress(addr);
            return netInterface.getDisplayName();
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }*/

        //code above makes the app crash, even tho theoretically it should work
        //fuck it Im just gonna hard code it
        return "wlan0";
    }
}
