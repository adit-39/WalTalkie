package com.walmart.easycart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PortFolio extends Fragment{


	//Mandatory Constructor
    public PortFolio() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
   
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.portfolio_fragment, container, false);
		
		
		
		
		
		
		return rootView; 
	
	}
}