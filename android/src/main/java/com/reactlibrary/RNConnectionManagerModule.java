
package com.reactlibrary;

//Android imports
import android.net.wifi.WifiManager;
import android.content.Context;
//React Native imports
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
//Java imports
/*import java.util.Map;
import java.util.HashMap;*/

/* 
 * Requirements
 * (0) Manifest.permission.CHANGE_WIFI_STATE
 */

public class RNConnectionManagerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private WifiManager WifiManagerInstance = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);

  public RNConnectionManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNConnectionManager";
  }

  //activates wifi if not activated (0)
  @ReactMethod
  public void activateWifi(Callback errorCallBack, Callback successCallBack) {
    if(WifiManager.isWifiEnabled()==false) {
      try {
        WifiManager.setWifiEnabled(true);
        successCallBack.invoke(true);
      } catch(Error e) {
        errorCallBack.invoke(e.getMessage());
      }
    }
    successCallBack.invoke(false); //already activated
  }

  //deactivates wifi if not deactivated (0)
  @ReactMethod
  public void deactivateWifi(Callback errorCallBack, Callback successCallBack) {
    if(WifiManager.isWifiEnabled()==true) {
      try {
        WifiManager.setWifiEnabled(false);
        successCallBack.invoke(true);
      } catch(Error e) {
        errorCallBack.invoke(e.getMessage());
      }
    }
    successCallBack.invoke(false); //already deactivated
  }

  //disconnects from the current network
  @ReactMethod
  public void connectWifi(String ssid, String password) {
    WifiManager.disconnect();
  }

  //disconnects from the current network
  @ReactMethod
  public void disconnectWifi() {
    WifiManager.disconnect();
  }
}