package android.evilhotspot;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Natalia on 05.01.2016.
 */

//Class for checking if app was send to background and then check if there was any change from outside with wifi
public class MyApplication extends Application {

  static Activity activity;

  public MyApplication(MainActivity act){
      this.activity=act;
  }
  public static boolean isActivityVisible() {
      return activityVisible;
  }

  public static void activityResumed() {
    activityVisible = true;
      Context context = MyApplication.activity.getApplicationContext();

      Log.i("RESUMED", "TRUE");



      //when app is resumed it checks if wifi was on/off from outside and change the button "on/off"
      if(ApManager.isApOn()){
          //Button btn = MainActivity.hsButton;
          MainActivity.hsButton.setBackgroundResource(R.drawable.button_on);
          context.startService(new Intent(context, HttpProxyService.class));
          //when you have default seetings and switch off and on app in MainActivity then checkbox is checked, (default seetings on)
          if(ApManager.name==null || ApManager.password==null)
          {
              ApManager.isCheckBoxChecked=true;
          }
      }
      else if(!ApManager.isApOn()){
          //Button btn = MainActivity.hsButton;
          MainActivity.hsButton.setBackgroundResource(R.drawable.button_off);
          context.stopService(new Intent(context, HttpProxyService.class));
      }
 }

  public static void activityPaused() {
    activityVisible = false;

  }

  private static boolean activityVisible;
}