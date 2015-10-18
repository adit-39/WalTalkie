package com.walmart.easycart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FormAdapter extends BaseAdapter {

	private Context context;
	List<String> items;
	List<String> prices;
	List<String> qty;

	public FormAdapter(Context context, ArrayList<String> items, ArrayList<String> price_items, ArrayList<String> qty) {
		this.items = items;
		this.context = context;
		this.prices = price_items;
		this.qty = qty;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.form_item, null);
		}

		final View convertViewF[] = { convertView };
		final int positionF[] = { position };
		final ViewGroup parentF[] = { parent };
		final Context contextF[] = { this.context };

		final FormAdapter othis = this;
		ImageButton img = (ImageButton) convertView.findViewById(R.id.remove);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				Toast.makeText(contextF[0],
//						"On Click",
//						Toast.LENGTH_LONG).show();
				//((View) convertViewF[0]).setVisibility(View.GONE);
				TextView tv = (TextView) parentF[0].findViewById(R.id.title);
				SharedPreferences sp = contextF[0].getSharedPreferences("list",
						Context.MODE_PRIVATE);
				String temp = sp.getString("ItemList", "wrong value");
				Editor ed = sp.edit();
				ed.putString("ItemList",
						Utils.removeItem(tv.getText().toString(), temp).split(";")[0]);
				ed.commit();
				
				sp = contextF[0].getSharedPreferences("price",
						Context.MODE_PRIVATE);
				temp = sp.getString("prices", "NA");
//				ed = sp.edit();
//				ed.putString("prices",
//						Utils.removeItem(tv.getText().toString(), temp).split(";")[1]);
//				ed.commit();
//				
				sp = contextF[0].getSharedPreferences("qty",
						Context.MODE_PRIVATE);
				temp = sp.getString("qty", "NA");
				ed = sp.edit();
				ed.putString("qty",
						Utils.removeItem(tv.getText().toString(), temp).split(";")[2]);
				ed.commit();
				
				
//				Toast.makeText(contextF[0],
//						Utils.removeItem(tv.getText().toString(), temp),
//						Toast.LENGTH_LONG).show();
				FormsFragment.form_items.remove(positionF[0]);
//				FormsFragment.qty_items.remove(positionF[0]);
//				FormsFragment.price_items.remove(positionF[0]);
				othis.notifyDataSetChanged();
				parentF[0].postInvalidate();
			}
		});

		TextView title = (TextView) convertView.findViewById(R.id.title);
		title.setText(items.get(position));
//		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		TextView price = (TextView) convertView.findViewById(R.id.price);
		Log.d("val", prices.toString());
		if(position<prices.size())
		{
			price.setText(prices.get(position));
		}
		else
		{
			price.setText("Rs. NA");
		}
		
		
		TextView quty = (TextView) convertView.findViewById(R.id.quantity);
		Log.d("val", qty.toString());
		
		if(position<qty.size())
		{
			quty.setText(qty.get(position));
		}
		else
		{
			quty.setText("1");
		}

		return convertView;
	}
}
