
package com.cusman;

//Android imports
import android.os.Build;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
//Android/general
import com.google.gson.Gson;
//React Native imports
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
//Java imports
import java.util.List;

public class RNConnectionManagerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private ConnectivityManager connectivityManagerInstance = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE); 
  private WifiManager WifiManagerInstance = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE); 

  public RNConnectionManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNConnectionManager";
  }

  /**
   * Activates wifi if not activated.
   * @param   errorCallback   a function that has an error message as argument
   * @param   successCallback a function that has the boolean true as argument
   * @see                     com.facebook.react.bridge.ReactMethod
   * @see                     com.facebook.react.bridge.Callback
   */
  @ReactMethod
  public void activateWifi(Callback errorCallback, Callback successCallback) {
    if(WifiManagerInstance.isWifiEnabled()==false) {
      try {
        WifiManagerInstance.setWifiEnabled(true);
        successCallback.invoke(true);
      } catch(Exception e) {
        errorCallback.invoke(e.getMessage());
      }
    }
  }

  /**
   * Deactivates wifi if not deactivated.
   * @param   errorCallback   a function that has an error message as argument
   * @param   successCallback a function that has the boolean true as argument
   * @see                     com.facebook.react.bridge.Callback
   * @see                     com.facebook.react.bridge.ReactMethod
   */
  @ReactMethod
  public void deactivateWifi(Callback errorCallback, Callback successCallback) {
    if(WifiManagerInstance.isWifiEnabled()==true) {
      try {
        WifiManagerInstance.setWifiEnabled(false);
        successCallback.invoke(true);
      } catch(Exception e) {
        errorCallback.invoke(e.getMessage());
      }
    }
  }

  /**
   * Enables wifi and connects the device to the specified wifi network. 
   * If the wifi network is known and its been previously configured it will connect 
   * to it without creating new connection configuration, which should be faster. 
   * It will also check if there is already an active connection to this AP and, if 
   * so, it will NOT reconnect.
   * @param   ssid            a String representing the SSID of the selected network
   * @param   password        a String representing the password of the selected network
   * @param   forceConnection a boolean indicating if connection must be stabished or no
   * @param   errorCallback   a function that has an error message as argument
   * @param   successCallback a function that has the boolean true as argument
   * @see                     com.facebook.react.bridge.ReactMethod
   * @see                     com.facebook.react.bridge.Callback
   */
  //TODO force connection
  @ReactMethod
  public void connectWifi(String ssid, String password, boolean forceConnection, Callback errorCallback, Callback successCallback) {
    try {
      int networkId = -1;
      boolean found = false;
      if(WifiManagerInstance.isWifiEnabled() == false) { //enable wifi
        WifiManagerInstance.setWifiEnabled(true);
      }
      if(!WifiManagerInstance.getConnectionInfo().getSSID().equals("\""+ssid+"\"")) {
        List<WifiConfiguration> configuredWifis = WifiManagerInstance.getConfiguredNetworks();
        int index = 0;
        while(index < configuredWifis.size() && found == false) { //search for existing config
          if(configuredWifis.get(index).SSID.equals("\""+ssid+"\"")) {
            found = true;
          } else {
            index++;
          }
        }
        if(!found) { //create new configuration
          WifiConfiguration conf = new WifiConfiguration();
          conf.SSID = "\"" + ssid + "\"";
          conf.preSharedKey = "\"" + password + "\"";
          conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
          networkId = WifiManagerInstance.addNetwork(conf);
        } else { //use existing configuration
          networkId = configuredWifis.get(index).networkId;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && forceConnection == true) { //force connection if specified
          index = 0;
          found = false;
          Network[] networkList = connectivityManagerInstance.getAllNetworks();
          while(index < networkList.length && found == false) {
            if(connectivityManagerInstance.getNetworkInfo(networkList[index]).getTypeName().equals("WIFI")) {
              found = true;
            } else {
              index++;
            }
          }
          if(found) { //TODO
            connectivityManagerInstance.bindProcessToNetwork(networkList[index]);
          }
        }
        if (networkId != -1) { //config found or created correctly, connect
          WifiManagerInstance.disconnect();
          WifiManagerInstance.enableNetwork(networkId, true);
        }
      }
      successCallback.invoke(networkId);
    } catch(Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  /**
   * Disconects wifi from the current AP or connection if wifi is enabled.
   * @param   errorCallback   a function that has an error message as argument
   * @param   successCallback a function that has the boolean true as argument
   * @param   forceDisconnect a boolean indicating if there is a socket binding and if it must be cleared
   * @see                     com.facebook.react.bridge.ReactMethod
   * @see                     com.facebook.react.bridge.Callback
   */
  @ReactMethod
  public void disconnectWifi(Callback errorCallback, Callback successCallback, boolean forceDisconnect) {
    try {
      if(WifiManagerInstance.isWifiEnabled()) {
        if(forceDisconnect == true) { //quit the network socket binding
          connectivityManagerInstance.bindProcessToNetwork(null);
        }
        WifiManagerInstance.disconnect();
      }
      successCallback.invoke(true);
    } catch(Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  /**
   * Disconects wifi from the active AP connection.
   * @param   errorCallback   a function that has an error message as argument
   * @param   successCallback a function that has a stringified json with the active wifi connection info as argument
   * @see                     com.facebook.react.bridge.ReactMethod
   * @see                     com.facebook.react.bridge.Callback
   */
  @ReactMethod
  public void getCurrentWifiInfo(Callback errorCallback, Callback successCallback) {
    try {
      successCallback.invoke(new Gson().toJson(WifiManagerInstance.getConnectionInfo()));
    } catch(Exception e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  private NetworkInfo[] getAllNetworks() {
    Network[] networkList = connectivityManagerInstance.getAllNetworks();
    NetworkInfo[] networkInfoList = new NetworkInfo[networkList.length];
    int i = 0;
    while(i < networkList.length) {
      networkInfoList[i] = connectivityManagerInstance.getNetworkInfo(networkList[i]);
      i++;
    }
    return networkInfoList;
  }

  //gets a list of available networks
  //requires ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
  //ACCESS_WIFI_STATE and CHANGE_WIFI_STATE
  /*@ReactMethod
  public void getAvailableWifiNetworks(Callback errorCallback, final Callback successCallback) {
    try {
      BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() { //registering Wifi Receiver
        @Override
        public void onReceive(Context c, Intent intent) {
          if(intent.getAction().equals(WifiManagerInstance.SCAN_RESULTS_AVAILABLE_ACTION)) {
            successCallback.invoke(new Gson().toJson(WifiManagerInstance.getScanResults()));
          }
        }
      };
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(WifiManagerInstance.SCAN_RESULTS_AVAILABLE_ACTION);
      getReactApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
      WifiManagerInstance.startScan(); //startScan is deprecated
    } catch(Exception e) { 
      errorCallback.invoke(e.getMessage());
    }
  }*/
}