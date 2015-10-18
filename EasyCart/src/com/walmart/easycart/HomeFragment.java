package com.walmart.easycart;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment{
	
	//Mandatory Constructor
    public HomeFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    Button button;
    
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.home, container, false);
		
		button=(Button)rootView.findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(getActivity(),BankMap.class);
				startActivity(i);
				
			}
			
		});
		
		//sync server

		return rootView; 
	
	}
}

