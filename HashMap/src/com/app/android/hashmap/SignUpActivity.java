package com.app.android.hashmap;

import com.app.android.hashmap.R;
import com.app.android.hashmap.view.FloatLabeledEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class SignUpActivity extends Activity {

	FloatLabeledEditText username, email, password, cPassword;
	ParseUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); // Removing
																// ActionBar
		setContentView(R.layout.activity_sign_up);

		// Sign up button
		findViewById(R.id.textViewRegister).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// get all fields
						username = (FloatLabeledEditText) findViewById(R.id.editTextSearch);
						email = (FloatLabeledEditText) findViewById(R.id.editTextEmail);
						password = (FloatLabeledEditText) findViewById(R.id.editTextStartDate);
						cPassword = (FloatLabeledEditText) findViewById(R.id.editTextEndDate);

						// ensure fields are not blank
						if (username.getText().toString().trim().length() == 0 || cPassword.getText().toString().trim().length() == 0 || email.getText().toString().trim().length() == 0 || password.getText().toString().trim().length() == 0) {
							Toast.makeText(SignUpActivity.this, "Fields Cannot Be Blank", Toast.LENGTH_SHORT).show();
							return;
						}

						// if password fields match
						if (password.getText().toString().equals(cPassword.getText().toString())) {
							// SIGNING UP
							user = new ParseUser();
							user.setUsername(username.getText().toString());
							user.setPassword(password.getText().toString());
							user.setEmail(email.getText().toString());

							user.signUpInBackground(new SignUpCallback() {

								@Override
								public void done(com.parse.ParseException e) {
									if (e == null) {
										// LOG IN
										ParseUser.logInInBackground(username
												.getText().toString(), password
												.getText().toString(),
												new LogInCallback() {
													@Override
													public void done(
															ParseUser user,
															com.parse.ParseException e) {
														if (user != null) {
															twitterLink(user);
														} else {
															Toast.makeText(
																	SignUpActivity.this,
																	"Login Failed",
																	Toast.LENGTH_SHORT)
																	.show();
														}
													}

												});

									} else {
										Toast.makeText(SignUpActivity.this,
												e + "", Toast.LENGTH_SHORT)
												.show();
									}

								}

							});
						}

						else {
							Toast.makeText(SignUpActivity.this,
									"Passwords did not match",
									Toast.LENGTH_SHORT).show();
						}

					}
				});

		// cancel button
		findViewById(R.id.textViewCancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(SignUpActivity.this,
								LogInPageActivity.class);
						startActivity(intent);
						finish();

					}

				});
	}

	//link user to twitter account
	protected void twitterLink(ParseUser u) {
		user = u;

		if (!ParseTwitterUtils.isLinked(user)) {
			ParseTwitterUtils.link(user, SignUpActivity.this,
					new SaveCallback() {
						@Override
						public void done(ParseException ex) {

							Toast.makeText(SignUpActivity.this,
									"Account Created", Toast.LENGTH_SHORT)
									.show();

							if (ParseTwitterUtils.isLinked(user) && ex == null) {
								Intent intent = new Intent(SignUpActivity.this,
										MainActivity.class);
								startActivity(intent);
								finish();
							}

							else {
								ParseUser.logOut();
								Intent intent = new Intent(SignUpActivity.this,
										LogInPageActivity.class);
								startActivity(intent);
								finish();
							}

						}
					});
		}

		else 
		{
			Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}

	}

}
