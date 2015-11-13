package com.app.android.hashmap.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.android.hashmap.R;
import com.parse.ParseObject;

public class DefaultAdapter extends ArrayAdapter<ParseObject>{
	
	List<ParseObject> mData;
	Context mContext;
	int mResource;
	
	public DefaultAdapter(Context context, int resource, List<ParseObject> objects) 
	{
		super(context, resource, objects);
		this.mContext = context;
		this.mData = objects;
		this.mResource = resource;
		Log.d("test", "In Adapter Constructor");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(mResource, parent, false);
		}
		
		ParseObject hist = mData.get(position);
		
		TextView termTextView = (TextView) convertView.findViewById(R.id.textTerm);
		termTextView.setText(hist.getString("term"));
		
		TextView termTextBase = (TextView) convertView.findViewById(R.id.RobotoTextViewBase);
		termTextBase.setText("Base: " + hist.getString("location"));
		
		if(hist.getString("from") != null)
		{
			TextView dateTextView = (TextView) convertView.findViewById(R.id.textDates);
			dateTextView.setText("From: " + hist.getString("from") + "    To: " + hist.getString("to"));
		}
		
		if(position%2 == 0)
		{
			convertView.setBackgroundColor(android.graphics.Color.WHITE);
		}
		else
		{
			convertView.setBackgroundColor(0xFAFAFA);
		}
		return convertView;
	}

	/*@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public ParseObject getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}*/

}
