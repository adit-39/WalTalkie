package com.walmart.easycart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private String[] tabs = { "Item List", "View indoor map",
			"View nearest stores", "Checkout" };
	static Context m;

	ArrayList<String> voice_results;
	ImageButton voice;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mTitle;
	static FragmentManager fragmentManager;
	Fragment fragment;

	static boolean flag = false;
	private static final int REQUEST_CODE = 1234;

	public static final String sp = "list";
	static SharedPreferences p, pp, ppp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		p = getSharedPreferences(sp, Context.MODE_PRIVATE);
		pp = getSharedPreferences("price", Context.MODE_PRIVATE);
		ppp = getSharedPreferences("qty", Context.MODE_PRIVATE);

		stopService(new Intent(this, ChatHeadService.class));
		m = getApplicationContext();

		fragmentManager = getSupportFragmentManager();
		fragment = new FormsFragment(this);
		Bundle args = new Bundle();
		// args.putInt(AboutUs.ARG_TAB_NUMBER, 1);
		fragment.setArguments(args);
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		voice = (ImageButton) findViewById(R.id.voice);
		voice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!checkVoiceRecognition())
					Toast.makeText(getApplicationContext(),
							"No voice recogniton software", Toast.LENGTH_SHORT)
							.show();
				else
					startVoiceRecognitionActivity();

			}

		});

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mTitle = "BankPypers";
		getActionBar().setTitle(mTitle);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_sidebar, 0, 0) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.navigation_layout, tabs));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

	}

	public boolean checkVoiceRecognition() {
		// Check if voice recognition is present
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0)
			return false;
		else
			return true;

	}

	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
				.getPackage().getName());

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			voice_results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			// Toast.makeText(m, "yo",Toast.LENGTH_SHORT).show();
			if (voice_results.equals(null))
				Toast.makeText(m, "null", Toast.LENGTH_SHORT).show();
			// wordsList.setAdapter(new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_1,
			// matches));
			else
				voice();
		}
		super.onActivityResult(requestCode, resultCode, data);
		if (voice_results != null
				&& (voice_results.get(0).startsWith("ad") || voice_results.get(
						0).startsWith("remove"))) {
			dialog_callback();
		}
	}

	public void voice() {
		// Toast.makeText(m, "In Voice",Toast.LENGTH_SHORT).show();

		/**
		 * add to cart remove nonsense show cart view bill checkout
		 */
		if (voice_results != null) {
			// Toast.makeText(m,
			// voice_results.get(0),Toast.LENGTH_SHORT).show();
			if (voice_results.get(0).contains("balance")) {
				Toast.makeText(m, "Your current Balance is: Rs.50000",
						Toast.LENGTH_LONG).show();
			}

			else if (voice_results.get(0).contains("map")) {
				Intent i = new Intent(m, BankMap.class);
				startActivity(i);
			}

			else if (voice_results.get(0).startsWith("ad")) {
				// Format - add item
				String temp = p.getString("ItemList", "Item List");
				Editor s = p.edit();
				s.putString(
						"ItemList",
						temp
								+ ","
								+ voice_results
										.get(0)
										.substring(
												voice_results.get(0).indexOf(
														" ")).trim());
				s.commit();
				
				temp = ppp.getString("qty", "1");
				s = ppp.edit();
				s.putString(
						"ItemList",
						temp
								+ ","
								+ "1");
				s.commit();

				FormsFragment.qty_items.add("1");
//				final String result[] = { "" };
//				Thread t = new Thread(new Runnable() {
//					public void run() {
//						HttpClient Client = new DefaultHttpClient();
//						HttpGet httpget = new HttpGet(Utils.IP
//								+ "/api/cost/"
//								+ Utils.getMACAddress("wlan0")
//								+ "/"
//								+ voice_results
//										.get(0)
//										.substring(
//												voice_results.get(0).indexOf(
//														" ")).trim() + "/1");
//						ResponseHandler<String> responseHandler = new BasicResponseHandler();
//						try {
//							result[0] = Client
//									.execute(httpget, responseHandler);
//
//							SharedPreferences pp = getSharedPreferences(
//									"price", Context.MODE_PRIVATE);
//							String temp = pp.getString("prices", "NA");
//							Editor ed = pp.edit();
//							ed.putString("prices", temp + "," + result[0]);
//							ed.commit();
//							Toast.makeText(getApplicationContext(),
//									pp.getString("prices", "none"),
//									Toast.LENGTH_LONG).show();
//							FormsFragment.price_items.add(result[0]);
//
//						} catch (ClientProtocolException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				});
//				t.start();

			} else if (voice_results.get(0).startsWith("remove")) {
				// Format - add item
				String temp = p.getString("ItemList", "Item List");
				Editor s = p.edit();
				s.putString(
						"ItemList",
						Utils.removeItem(
								voice_results
										.get(0)
										.substring(
												voice_results.get(0).indexOf(
														" ")).trim(), temp)
								.split(";")[0]);
				s.commit();

				SharedPreferences pp = getSharedPreferences("price",
						Context.MODE_PRIVATE);
				temp = pp.getString("prices", "NA");
				Editor ed = pp.edit();
				ed.putString(
						"prices",
						Utils.removeItem(
								voice_results
										.get(0)
										.substring(
												voice_results.get(0).indexOf(
														" ")).trim(), temp)
								.split(";")[1]);
				ed.commit();

				ppp = getSharedPreferences("price", Context.MODE_PRIVATE);
				temp = pp.getString("prices", "NA");
				ed = pp.edit();
				ed.putString(
						"prices",
						Utils.removeItem(
								voice_results
										.get(0)
										.substring(
												voice_results.get(0).indexOf(
														" ")).trim(), temp)
								.split(";")[2]);
				ed.commit();

			} else {
				Toast.makeText(this, voice_results.get(0), Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(m, "voice_results is null", Toast.LENGTH_SHORT)
					.show();
		}

	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		// Create a new fragment and specify the planet to show based on
		// position

		Bundle args = new Bundle();
		switch (position) {

		case 0:
			fragment = new FormsFragment(this);
			fragment.setArguments(args);
			break;

		case 1:
//			fragment = new BankMap();
			fragment.setArguments(args);
			break;

		case 2:
			fragment = new GoogleMapActivity();
			fragment.setArguments(args);
			break;

		case 3: {
			p = this.getSharedPreferences("list", Context.MODE_PRIVATE);
			Editor edit = p.edit();
			edit.clear();
			edit.commit();
			Toast.makeText(this, "Cleared", Toast.LENGTH_LONG).show();

			edit = pp.edit();
			edit.clear();
			edit.commit();

			edit = ppp.edit();
			edit.clear();
			edit.commit();

			fragment = new QRViewFragment();
			fragment.setArguments(args);
			break;
		}
		}

		// Insert the fragment by replacing any existing fragment
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// Highlight the selected item, update the title, and close the drawer
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		startService(new Intent(this, ChatHeadService.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		stopService(new Intent(this, ChatHeadService.class));
	}

	public void addElement(View v) {
		// Toast.makeText(this, "something", Toast.LENGTH_LONG).show();
		withdraw_dialog("Add Item");
		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

	}

	/*
	 * public void withdraw_dialog(final String type) { final Dialog dialog =
	 * new Dialog(this); dialog.setContentView(R.layout.withdrawal_form);
	 * dialog.setTitle(type);
	 * 
	 * final EditText amount=(EditText)dialog.findViewById(R.id.entry);
	 * 
	 * final TextView ti =(TextView)dialog.findViewById(R.id.words); final
	 * String result[]=new String[1]; amount.addTextChangedListener(new
	 * TextWatcher(){ public void afterTextChanged(Editable s) { int i=0;
	 * android.util.Log.d("test","Called"); String str =
	 * amount.getText().toString(); if(str.length()>0) {
	 * ConvertNumberToText(Integer.parseInt(str), result);
	 * ti.setText(result[0]+" only"); android.util.Log.d("test","Called"); }
	 * //ti.setText("hello"+i++); } public void beforeTextChanged(CharSequence
	 * s, int start, int count, int after){} public void
	 * onTextChanged(CharSequence s, int start, int before, int count){} });
	 * 
	 * Button dialogButton = (Button) dialog.findViewById(R.id.OKButton); // if
	 * button is clicked, close the custom dialog
	 * dialogButton.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { double
	 * cash=Double.parseDouble(amount.getText().toString());
	 * //MainActivity.flag=true;
	 * 
	 * p.edit().putString("type", type); p.edit().putString("name", "Chiraag");
	 * p.edit().putFloat("amount", (float) cash); p.edit().commit(); //send data
	 * } });
	 * 
	 * dialog.show(); }
	 */
	public void withdraw_dialog(final String type) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.withdrawal_form);
		dialog.setTitle(type);

		final EditText itemName = (EditText) dialog.findViewById(R.id.entry);
		final EditText qty = (EditText) dialog.findViewById(R.id.qtyentry);

		final TextView ti = (TextView) dialog.findViewById(R.id.words);

		Button dialogButton = (Button) dialog.findViewById(R.id.OKButton);
		// if button is clicked, close the custom dialog
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(getApplicationContext(),
//						itemName.getText().toString() + "hello",
//						Toast.LENGTH_LONG).show();
				String temp = p.getString("ItemList", "Item List");
				Editor s = p.edit();
				s.putString("ItemList", temp + ","
						+ itemName.getText().toString());
				s.commit();
//				Toast.makeText(getApplicationContext(),
//						p.getString("ItemList", "Item List"), Toast.LENGTH_LONG)
//						.show();
				// send data

				temp = ppp.getString("qty", "NA");
				s = ppp.edit();
				s.putString("qty", temp + "," + qty.getText().toString());
				s.commit();
				Log.d("val_d",ppp.getString("qty", "na"));

//				final String result[] = { "" };
//				Thread t = new Thread(new Runnable() {
//					public void run() {
//						HttpClient Client = new DefaultHttpClient();
//						HttpGet httpget = new HttpGet(Utils.IP + "/api/cost/"
//								+ Utils.getMACAddress("wlan0") + "/"
//								+ itemName.getText().toString().trim());
//						ResponseHandler<String> responseHandler = new BasicResponseHandler();
//						try {
//							result[0] = Client
//									.execute(httpget, responseHandler);
//							SharedPreferences pp = getSharedPreferences(
//									"price", Context.MODE_PRIVATE);
//							String temp = pp.getString("prices", "NA");
//							Editor ed = pp.edit();
//							ed.putString("prices", temp + "," + result[0]);
//							ed.commit();
//						} catch (ClientProtocolException e) {
//							e.printStackTrace();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				});
//				t.start();

				dialog.dismiss();
				dialog_callback();
			}
		});

		dialog.show();
	}

	private void dialog_callback() {

//		final String result[] = { "" };
//		Thread t = new Thread(new Runnable() {
//			public void run() {
//				HttpClient Client = new DefaultHttpClient();
//				HttpPost httppost = new HttpPost(Utils.IP + "/api/updatelist/"
//						+ Utils.getMACAddress("wlan0"));
//				String Name = "";
//				for (int i = 0; i < FormsFragment.form_items.size(); i++) {
//					Name += FormsFragment.form_items.get(i) + ","
//							+ FormsFragment.qty_items.get(i) + ";";
//				}
//				Name = Name.substring(0, Name.length() - 1);
//
//				ResponseHandler<String> responseHandler = new BasicResponseHandler();
//				try {
//					BasicNameValuePair data = new BasicNameValuePair("items",
//							Name);
//					ArrayList<BasicNameValuePair> d = new ArrayList<BasicNameValuePair>();
//					d.add(data);
//					httppost.setEntity(new UrlEncodedFormEntity(d));
//					result[0] = Client.execute(httppost, responseHandler);
//					SharedPreferences pp = getSharedPreferences("price",
//							Context.MODE_PRIVATE);
//					SharedPreferences p = getSharedPreferences("ItemList",
//							Context.MODE_PRIVATE);
//					SharedPreferences ppp = getSharedPreferences("qty",
//							Context.MODE_PRIVATE);
//
//					Editor ed1 = p.edit();
//					Editor ed2 = pp.edit();
//					Editor ed3 = ppp.edit();
//					String s1 = "", s2 = "", s3 = "";
//					String res[] = result[0].split(";");
//					for (int i = 0; i < res.length; i++) {
//						s1 += res[i].split(",")[0];
//						s2 += res[i].split(",")[1];
//						s3 += res[i].split(",")[2];
//					}
//					s1 = s1.substring(0, s1.length() - 1);
//					s2 = s2.substring(0, s1.length() - 1);
//					s3 = s3.substring(0, s1.length() - 1);
//
//					ed1.putString("ItemList", s1);
//					ed2.putString("prices", s2);
//					ed3.putString("qty", s3);
//
//					ed1.commit();
//					ed2.commit();
//					ed3.commit();
//
//				} catch (ClientProtocolException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		t.start();
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		fragmentManager = getSupportFragmentManager();
		FormsFragment fragment = new FormsFragment(this);
		Bundle args = new Bundle();
		// args.putInt(AboutUs.ARG_TAB_NUMBER, 1);
		fragment.setArguments(args);
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}
}
