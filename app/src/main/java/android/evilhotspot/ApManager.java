package android.evilhotspot;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.*;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.Collections;
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
            Log.d("IP ADDRESS", getIpAddr());

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


    public static String getIpAddr() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (true) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix

                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {}
        return "";
    }

    //checks if connection with internet is on
    //go to google and checks if it make a connection
    public static boolean hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e("Connection", "Error internet connection", e);
            }
        } else {
            Log.d("Connection", "No network available");
        }
        return false;
    }
    //checks if there is network
    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
