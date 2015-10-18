package com.walmart.easycart;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends Fragment {

	// Mandatory Constructor
	public GoogleMapActivity() {

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GoogleMap googleMap;
		// googleMap = ((MapFragment)
		// getActivity().getFragmentManager().findFragmentById(R.id.map)).getMap();
		// googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		// final LatLng walmartLoc = new LatLng(21 , 57);
		// Marker TP = googleMap.addMarker(new
		// MarkerOptions().position(walmartLoc).title("Walmart's here"));

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.googlemap, container,
				false);
		GoogleMap googleMap;
		googleMap = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		final SharedPreferences pref = getActivity().getSharedPreferences(
				"location", Context.MODE_PRIVATE);
//		LatLng walmartLoc = new LatLng(Double.parseDouble(pref.getString("lat",
//				"0")), Double.parseDouble(pref.getString("lng", "0")));
		
		LatLng walmartLoc = new LatLng(34.059807,-117.236950);
		googleMap.addMarker(new MarkerOptions().position(walmartLoc).title(
				"You're here"));
		CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15)
				.target(walmartLoc).build();
		googleMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(cameraPosition), 2000,
				null);

		final String result[] = { "" };
		Thread t = new Thread(new Runnable() {
			public void run() {
				HttpClient Client = new DefaultHttpClient();
//				HttpGet httpget = new HttpGet(Utils.IP + "/api/nearest/"
//						+ pref.getString("lat", "0") + "/"
//						+ pref.getString("lng", "0"));
				
				HttpGet httpget = new HttpGet(Utils.IP + "/api/nearest/34.059807/-117.236950");
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				try {
					result[0] = Client.execute(httpget, responseHandler);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Toast.makeText(getActivity(), result[0], Toast.LENGTH_LONG).show();
		String res[] = result[0].split(";");
		walmartLoc = new LatLng(Double.parseDouble(res[0].split(",")[0]),
				Double.parseDouble(res[0].split(",")[1]));
		googleMap.addMarker(new MarkerOptions().position(walmartLoc).title(
				"Walmart's here"));
		walmartLoc = new LatLng(Double.parseDouble(res[1].split(",")[0]),
				Double.parseDouble(res[1].split(",")[1]));
		googleMap.addMarker(new MarkerOptions().position(walmartLoc).title(
				"Walmart's here"));
		walmartLoc = new LatLng(Double.parseDouble(res[2].split(",")[0]),
				Double.parseDouble(res[2].split(",")[1]));
		googleMap.addMarker(new MarkerOptions().position(walmartLoc).title(
				"Walmart's here"));

		return rootView;

	}

}