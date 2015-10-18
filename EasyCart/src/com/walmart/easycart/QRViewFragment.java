package com.walmart.easycart;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class QRViewFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.code_view, container,
				false);

		final String result[] = { "" };
//		Thread t = new Thread(new Runnable() {
//			public void run() {
//				HttpClient Client = new DefaultHttpClient();
//				HttpGet httpget = new HttpGet(Utils.IP + "/api/costn/"
//						+ Utils.getMACAddress("wlan0"));
//				ResponseHandler<String> responseHandler = new BasicResponseHandler();
//				try {
//					result[0] = Client.execute(httpget, responseHandler);
//					((TextView)getActivity().findViewById(R.id.qrcode)).setText("Total price: "+result[0]);
//				} catch (ClientProtocolException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
//		t.start();

//		WebView web = (WebView) container.findViewById(R.id.qrcode);
//		web.getSettings().setJavaScriptEnabled(true);
//		web.getSettings().setBuiltInZoomControls(false);
////		web.loadUrl(Utils.IP+"api/checkout/"+Utils.getMACAddress("wlan0"));
//		web.loadUrl("http://192.168.1.5/Walmart%20hackathon/qrcode.png");
		// if(MainActivity.flag)
		// i.setVisibility(1);
		// else
		// i.setVisibility(1);

		return rootView;

	}

}
