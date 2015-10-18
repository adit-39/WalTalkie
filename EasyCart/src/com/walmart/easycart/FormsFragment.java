package com.walmart.easycart;


import java.util.ArrayList;
import java.util.Collections;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FormsFragment extends Fragment{
	
	ListView items;
	static ArrayList<String> form_items=new ArrayList<String>();
	static ArrayList<String> price_items=new ArrayList<String>();
	static ArrayList<String> qty_items=new ArrayList<String>();
	SharedPreferences p,pp,ppp;
	View frag;
//	public FormsFragment(){}
	public FormsFragment(Context context) {
    	p=context.getSharedPreferences("list", Context.MODE_PRIVATE);
    	pp=context.getSharedPreferences("price", Context.MODE_PRIVATE);
    	ppp=context.getSharedPreferences("qty", Context.MODE_PRIVATE);

    	form_items=new ArrayList<String>();
    	price_items=new ArrayList<String>();
    	qty_items=new ArrayList<String>();
    	
    	String s = p.getString("ItemList", "Item List");
//    	Toast.makeText(context, p.getString("ItemList", "Item List"), Toast.LENGTH_LONG).show();
    	Collections.addAll(form_items, p.getString("ItemList", "Item List").split(","));
    	
    	s = pp.getString("prices", "NA");
//    	Toast.makeText(context, p.getString("prices", "NA"), Toast.LENGTH_LONG).show();
    	Collections.addAll(price_items, pp.getString("prices", "NA").split(","));
		//form_items.add("Test");
    	
    	s = ppp.getString("qty", "1");
//    	Toast.makeText(context, p.getString("qty", "na"), Toast.LENGTH_LONG).show();
    	Collections.addAll(qty_items, ppp.getString("qty", "1").split(","));
    }
	
//	//Mandatory Constructor
//    public FormsFragment(Context context, String item) {
//    	p=context.getSharedPreferences("list", Context.MODE_PRIVATE);
//    	String s = p.getString("ItemList", "Item List");
//    	Editor edit = p.edit();
//    	edit.putString("ItemList", s+","+item);
//    	edit.commit();
//    	Collections.addAll(form_items, p.getString("ItemList", "Item List").split(","));
//    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
    public void addElement(String item)
    {
    	form_items.add(item);
    	//frag.refreshDrawableState();
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.forms, container, false);
		frag = rootView;
		
		
		
		items=(ListView)rootView.findViewById(R.id.list);
		
		
//	    items.setOnItemClickListener(new OnItemClickListener(){
//
//				@Override
//				public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
//						long arg3) {
//					switch(pos)
//					{
//					case 0:
//							break;
//					case 1:
//							break;
//					case 2:break;
//					case 3:Intent i=new Intent(getActivity(),LoanFormActivity.class);
//							startActivity(i);
//					case 4:break;
//					case 5:break;
//					
//					}
//					
//				}
//		    	 
//		     });
	    items.setAdapter(new FormAdapter(getActivity(),form_items,price_items,qty_items));

		return rootView; 
	
	}
	private void dialog_callback() {
		android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FormsFragment fragment = new FormsFragment(getActivity());
		Bundle args = new Bundle();
		// args.putInt(AboutUs.ARG_TAB_NUMBER, 1);
		fragment.setArguments(args);
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}
   
    
   
   
   public boolean HelperConvertNumberToText(int num, String[] result) {
		String[] strones = { "One", "Two", "Three", "Four", "Five", "Six",
				"Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
				"Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
				"Eighteen", "Nineteen", };

		String[] strtens = { "Ten", "Twenty", "Thirty", "Fourty", "Fifty",
				"Sixty", "Seventy", "Eighty", "Ninety", "Hundred" };

		result[0] = "";
		int single, tens, hundreds;

		if (num > 1000)
			return false;

		hundreds = num / 100;
		num = num - hundreds * 100;
		if (num < 20) {
			tens = 0; // special case
			single = num;
		} else {
			tens = num / 10;
			num = num - tens * 10;
			single = num;
		}

		if (hundreds > 0) {
			result[0] += strones[hundreds - 1];
			result[0] += " Hundred ";
		}
		if (tens > 0) {
			result[0] += strtens[tens - 1];
			result[0] += " ";
		}
		if (single > 0) {
			result[0] += strones[single - 1];
			result[0] += " ";
		}
		return true;
	}

	public boolean ConvertNumberToText(int num, String[] result) {
		String tempString[] = new String[1];
		tempString[0] = "";
		int thousands;
		int temp;
		result[0] = "";
		if (num < 0 || num > 100000) {
			System.out.println(num + " \tNot Supported");
			return false;
		}

		if (num == 0) {
			System.out.println(num + " \tZero");
			return false;
		}

		if (num < 1000) {
			HelperConvertNumberToText(num, tempString);
			result[0] += tempString[0];
		} else {
			thousands = num / 1000;
			temp = num - thousands * 1000;
			HelperConvertNumberToText(thousands, tempString);
			result[0] += tempString[0];
			result[0] += "Thousand ";
			HelperConvertNumberToText(temp, tempString);
			result[0] += tempString[0];
		}
		return true;
	}
   
}
