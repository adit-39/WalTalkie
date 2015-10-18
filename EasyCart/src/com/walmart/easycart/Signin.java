package com.walmart.easycart;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signin extends Activity {
	EditText un, pwd;
	String salt = "";
	Button btn,btn1;
	boolean flag=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sigin);
		
		stopService(new Intent(this, ChatHeadService.class));
		
		un = (EditText) findViewById(R.id.usernam);
		pwd = (EditText) findViewById(R.id.passw);
		btn = (Button) findViewById(R.id.maps);
        btn1 = (Button) findViewById(R.id.fill);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (un.getText().toString().equalsIgnoreCase("")
						|| pwd.getText().toString().equalsIgnoreCase(""))
					Toast.makeText(getApplicationContext(),
							"Please Enter Valid Username and Password",
							Toast.LENGTH_LONG).show();
				else {
					splash obj = new splash();
					obj.execute();
					
					if(flag==false)
						Toast.makeText(getApplicationContext(), "Please check login credentials", Toast.LENGTH_LONG).show();
				}

			}
		});
		
		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent("com.example.bankpypers.HOME"));
				finish();
			}
		});
	}
	
	private class splash extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(
					"http://192.168.222.113:5001/api/authorize/"
			+un.getText().toString());
			try {
				HttpResponse response = httpclient.execute(httpget);
				// android.util.Log.d("response", );
				salt = EntityUtils.toString(response.getEntity(), "UTF-8");
				salt=salt.substring(1,salt.length()-1);
				android.util.Log.d("salt", salt);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		public String encrypt() {
			String password = pwd.getText().toString();
			String hash = "";
			try {
				hash = this.get_SHA_512_SecurePassword(password, salt);
				// v.setText(hash);
				android.util.Log.d("hash", hash);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return hash;
		}

		private String get_SHA_512_SecurePassword(String passwordToHash,
				String salt) {
			MessageDigest md;
			String generatedPassword = null;
			try {
				md = MessageDigest.getInstance("SHA-1");
				md.update(salt.getBytes());
				byte[] bytes = md.digest(passwordToHash.getBytes());
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < bytes.length; i++) {
					sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
							.substring(1));
				}
				generatedPassword = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return generatedPassword;
		}

		protected void onPostExecute(String result) {
			android.util.Log.d("res","res");
			if (salt.equalsIgnoreCase(""))
				doInBackground();
			else {
				new Thread(new Runnable() {

					@Override
					public void run() {
						HttpClient httpclient = new DefaultHttpClient();
						HttpGet httpget = new HttpGet(
								"http://192.168.222.113:5001/api/authorize/"+
										un.getText().toString()+ "/"+encrypt());
						try {
							HttpResponse response = httpclient.execute(httpget);
							// android.util.Log.d("response", );
							String res = EntityUtils.toString(response.getEntity(),
									"UTF-8");
							android.util.Log.d("result", res);
							if(res.contains("FALSE"))
								runOnUiThread(new Runnable(){

									@Override
									public void run() {
										Toast.makeText(getApplicationContext(), "Please check login credentials", Toast.LENGTH_LONG).show();
										
									}
									
								});
							else{
								startActivity(new Intent("com.example.bankpypers.HOME"));
								finish();}
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}).start();
			}
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		startService(new Intent(this, ChatHeadService.class));
	}

}
