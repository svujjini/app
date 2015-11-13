package com.app.android.hashmap.fragment;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.app.android.hashmap.R;
import com.app.android.hashmap.adapter.DefaultAdapter;
import com.app.android.hashmap.util.TwitterCallTask;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ListViewsFragment extends Fragment{

	private DynamicListView mDynamicListView;
	private List<ParseObject> hist;
	
	public static ListViewsFragment newInstance() {
		return new ListViewsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_list_views, container, false);

		mDynamicListView = (DynamicListView) rootView.findViewById(R.id.dynamic_listview);
		mDynamicListView.setItemsCanFocus(false);
		
		//build list
		setUpListView();
		
		return rootView;
	}

	private void setUpListView() {
		
		//query parse for search history of current user
		ParseUser user = ParseUser.getCurrentUser();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("SearchHistory");
		query.whereEqualTo("user", user);
		query.orderByDescending("createdAt");
		query.setLimit(20);
		query.findInBackground(new FindCallback<ParseObject>() 
		{
			@Override
			public void done(List<ParseObject> objects, ParseException e) 
			{
				Log.d("test", objects.size()+"");
		        hist = objects;
		        
		        //craete adapter
		        BaseAdapter adapter = new DefaultAdapter(getActivity(), R.layout.list_item_default, hist);
		        Log.d("test", "adapter created");
				AnimationAdapter animAdapter;
				
				//create animated adapter
				animAdapter = new AlphaInAnimationAdapter(adapter);
					
				//build listview
				animAdapter.setAbsListView(mDynamicListView);
				mDynamicListView.setAdapter(animAdapter);
				
				//click listener to launch search
				mDynamicListView.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
					{
						try
						{
							String term = hist.get(position).getString("term").toString();
							String loc = hist.get(position).getString("location").toString();
							if(hist.get(position).getString("from") != null && hist.get(position).getString("to") != null)
							{
								String start = hist.get(position).getString("from").toString();
								String end = hist.get(position).getString("to").toString();
								new TwitterCallTask(getActivity(), term, loc).execute(term + "%20since%3A"+start + "%20until%3A" + end);
							}
							else
							{
								new TwitterCallTask(getActivity(), term, loc).execute(term);
							}
						}
						catch(Exception e)
						{
							Toast.makeText(getActivity(), "Cannot Connect To Online Services", Toast.LENGTH_LONG).show();
						}
						
					}
				});
		    }

		});
		
		
	}

	
}
