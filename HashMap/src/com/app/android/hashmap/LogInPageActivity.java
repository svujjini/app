package com.app.android.hashmap;

import com.app.android.hashmap.R;
import com.app.android.hashmap.view.FloatLabeledEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class LogInPageActivity extends Activity{

	public static final String LOGIN_PAGE_AND_LOADERS_CATEGORY = "com.csform.android.uiapptemplate.LogInPageAndLoadersActivity";
	
	FloatLabeledEditText username, password;
	ParseUser tUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
		setContentView(R.layout.activity_login_page_light);
		TextView login, register;
		login = (TextView) findViewById(R.id.textViewRegister);
		register = (TextView) findViewById(R.id.textViewCancel);
				
		ParseUser currentUser = ParseUser.getCurrentUser();
        
        //if not already logged in
        if (currentUser == null || !ParseTwitterUtils.isLinked(currentUser)) 
        {
        	        	
        	//log in button
        	login.setOnClickListener(new View.OnClickListener() 
            {

    			@Override
    			public void onClick(View v) 
    			{
    				
    				username = (FloatLabeledEditText) findViewById(R.id.editTextSearch);
    		        password = (FloatLabeledEditText) findViewById(R.id.editTextStartDate);
    		        
    		        if(username.getText().toString().trim().length() == 0 || password.getText().toString().trim().length() == 0)
    		        {
    		        	Toast.makeText(LogInPageActivity.this, "Fields Cannot Be Blank", Toast.LENGTH_SHORT).show();
    		        	return;
    		        }
    		        
    		        //LOG IN
    		  		ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() 
    		  		{
    		  			@Override
    		  			public void done(ParseUser user, com.parse.ParseException e) 
    		  			{
    		  			    if (user != null) 
    		  			    {
    		  			    	twitterLink(user);
    		  			    } 
    		  			    else if(e != null)
    		  			    {
    		  			    	Toast.makeText(LogInPageActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    		  			    }
    		  			    else 
    		  			    {
    		  			    	Toast.makeText(LogInPageActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
    		  			    }
    		  			}
    		  			
    		  		});
    				
    			}
            	
            });
            
            //create account button
            register.setOnClickListener(new View.OnClickListener() 
            {

    			@Override
    			public void onClick(View v) 
    			{
    				Intent intent = new Intent(LogInPageActivity.this, SignUpActivity.class);
    				startActivity(intent);
    				
    			}
        	
            	
            });
        } 
        
        //automatically go to AppsActivity if previously logged in
        else 
        {
        	
        	twitterLink(currentUser);
        	
        }
	}
	
	@Override
	protected void onPause() 
	{
		finish();
		super.onPause();
	}
	
	protected void twitterLink(ParseUser user) 
	{
		tUser = user;
				
		if (!ParseTwitterUtils.isLinked(tUser)) 
	    	{
	    	  ParseTwitterUtils.link(tUser, LogInPageActivity.this, new SaveCallback() 
	    	  {
	    	    @Override
	    	    public void done(ParseException ex) 
	    	    {
	    	      if (ParseTwitterUtils.isLinked(tUser) && ex == null) 
	    	      {
	    	    	Intent intent = new Intent(LogInPageActivity.this, MainActivity.class);
					//intent.putExtra("keyword", tv.getText().toString());
					startActivity(intent);
	    	      }
	    	      
	    	      else
	    	      {
	    	    	  if(ex != null)
	    	    	  {
	    	    		  ParseUser.logOut();
	    	    		  Toast.makeText(LogInPageActivity.this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
	    	    	  }
	    	    	  else
	    	    	  {
	    	    		  ParseUser.logOut();
	    	    		  Toast.makeText(LogInPageActivity.this, "Must Link Twitter To Use App", Toast.LENGTH_SHORT).show();
	    	    	  }
	    	      }
	    	    }
	    	  });
	    	}
		else
		{
			Intent intent = new Intent(LogInPageActivity.this, MainActivity.class);
			//intent.putExtra("keyword", tv.getText().toString());
			startActivity(intent);
		}
		
	}

	
	
}
