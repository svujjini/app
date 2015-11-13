package com.app.android.hashmap;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.android.hashmap.R;
import com.app.android.hashmap.adapter.DrawerAdapter;
import com.app.android.hashmap.fragment.ListViewsFragment;
import com.app.android.hashmap.fragment.LogInPageFragment;
import com.app.android.hashmap.fragment.SearchBarsFragment;
import com.app.android.hashmap.model.DrawerItem;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity {

	private ListView mDrawerList;
	private List<DrawerItem> mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//getWindow().requestFeature(Window.); 
		setContentView(R.layout.activity_main);
		
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) 
		{
			public void onDrawerClosed(View view) 
			{
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) 
			{
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mTitle = mDrawerTitle = getTitle();
		mDrawerList = (ListView) findViewById(R.id.list_view);
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		prepareNavigationDrawerItems();
		mDrawerList.setAdapter(new DrawerAdapter(this, mDrawerItems, true));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		mHandler = new Handler();
		
		if (savedInstanceState == null) 
		{
			int position = 0;
			selectItem(position, mDrawerItems.get(position).getTag());
			mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	private void prepareNavigationDrawerItems() 
	{
		mDrawerItems = new ArrayList<>();
		mDrawerItems.add(
				new DrawerItem(
						R.string.drawer_icon_search_bars,
						R.string.drawer_title_search,
						DrawerItem.DRAWER_ITEM_TAG_SEARCH_BARS));
		mDrawerItems.add(
				new DrawerItem(
						R.string.drawer_icon_list_views,
						R.string.drawer_title_history,
						DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS));
		mDrawerItems.add(
				new DrawerItem(
						R.string.drawer_icon_login_page,
						R.string.drawer_title_logout,
						DrawerItem.DRAWER_ITEM_TAG_LOGIN_PAGE_AND_LOADERS));
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) 
	{
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (mDrawerToggle.onOptionsItemSelected(item)) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			selectItem(position, mDrawerItems.get(position).getTag());
		}
	}

	private void selectItem(int position, int drawerTag) 
	{
		Fragment fragment = getFragmentByDrawerTag(drawerTag);
		commitFragment(fragment);
		
		mDrawerList.setItemChecked(position, true);
		setTitle(mDrawerItems.get(position).getTitle());
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	private Fragment getFragmentByDrawerTag(int drawerTag) 
	{
		Fragment fragment = null;
		if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_SEARCH_BARS) 
		{
			fragment = SearchBarsFragment.newInstance();
		} 
		else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_LOGIN_PAGE_AND_LOADERS) 
		{
			fragment = LogInPageFragment.newInstance();
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) 
			{
				 ParseUser.logOut();
		         currentUser = ParseUser.getCurrentUser(); // this will now be null
		         Intent intent = new Intent(MainActivity.this, LogInPageActivity.class);
		         startActivity(intent);
		         finish();
		    } 
			else 
			{
		        	 Intent intent = new Intent(MainActivity.this, LogInPageActivity.class);
			         startActivity(intent);
			         finish();
		    }
		} 
		else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS) 
		{
			fragment = ListViewsFragment.newInstance();
		} 
		else 
		{
			fragment = new Fragment();
		}
		return fragment;
	}
	
	private class CommitFragmentRunnable implements Runnable 
	{

		private Fragment fragment;
		
		public CommitFragmentRunnable(Fragment fragment) 
		{
			this.fragment = fragment;
		}
		
		@Override
		public void run() 
		{
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
	}
	
	public void commitFragment(Fragment fragment) 
	{
		//Using Handler class to avoid lagging while
		//committing fragment in same time as closing
		//navigation drawer
		mHandler.post(new CommitFragmentRunnable(fragment));
	}

	@Override
	public void setTitle(int titleId) 
	{
		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) 
	{
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
}