package com.walmart.easycart;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

public class BankMap extends Activity {
	WebView web;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bank_map);
		web = (WebView) findViewById(R.id.webView1);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setBuiltInZoomControls(true);
		web.loadUrl("http://192.168.1.5/Walmart%20hackathon/map.html");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
