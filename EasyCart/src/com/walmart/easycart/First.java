package com.walmart.easycart;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class First extends Activity implements LocationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);
		
		new splash().execute("");

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);

		 WifiManager wifi = (WifiManager)
		 getSystemService(Context.WIFI_SERVICE);
		 WifiConfiguration wc = new WifiConfiguration();
		 wc.SSID = "\"Walmart\"";
		 wc.preSharedKey = "\"qwerty123456\"";
		 wc.hiddenSSID = true;
		 wc.status = WifiConfiguration.Status.ENABLED;
		 wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		 wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		 wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		 wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		 wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		 wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		 int res = wifi.addNetwork(wc);
		 Log.d("WifiPreference", "add Network returned " + res);
		 boolean b = wifi.enableNetwork(res, true);
		 Log.d("WifiPreference", "enableNetwork returned " + b);
		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 3000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					}
					;

					startActivity(new Intent("com.example.bankpypers.HOME"));
				}

				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				finally {
					finish();
				}
			}
		};

		logoTimer.start();
	}

	private class splash extends AsyncTask<String, Void, String> {
		String mac;

		@Override
		protected String doInBackground(String... params) {
			mac = Utils.getMACAddress("wlan0");
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					Utils.IP+"/api/instantiate/" + mac);
			try {
				HttpResponse response = httpclient.execute(httpget);
				// android.util.Log.d("response", );
				String responseString = EntityUtils.toString(
						response.getEntity(), "UTF-8");
				Log.d("response", responseString);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				android.util.Log.d("d", "client exception");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				android.util.Log.d("d", "IOException" + e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String result) {

			Toast.makeText(getApplicationContext(), "Crazy:"+mac, Toast.LENGTH_LONG)
					.show();

		}

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		SharedPreferences pref = this.getSharedPreferences("location",
				Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putString("lat", arg0.getLatitude() + "");
		ed.putString("lng", arg0.getLongitude() + "");
		ed.commit();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}
}
