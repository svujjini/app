package com.app.android.hashmap.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.app.android.hashmap.DatePickerFragment;
import com.app.android.hashmap.R;
import com.app.android.hashmap.font.FontelloTextView;
import com.app.android.hashmap.util.AutoCompleteUtil;
import com.app.android.hashmap.util.TwitterCallTask;
import com.app.android.hashmap.util.TwitterCallTask.IData;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class SearchBarsFragment extends Fragment implements IData {

	private static final String LOG_TAG = "Google Places Autocomplete";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyCws6dpjvMv9ZmFOGWnRCuvUNK_3bgJPj4";

	private AutoCompleteTextView search;
	AutoCompleteTextView placesView;
	private static EditText startDate;
	private static EditText endDate;
	private FontelloTextView startDateIcon, endDateIcon;
	Switch dateSwitch;
	private String str;

	public static SearchBarsFragment newInstance() {
		return new SearchBarsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_search_bars,
				container, false);

		// Note that this assumes a LayoutTransition is set on the container,
		// which is the
		// case here because the container has the attribute
		// "animateLayoutChanges" set to true
		// in the layout file. You can also call setLayoutTransition(new
		// LayoutTransition()) in
		// code to set a LayoutTransition on any container.
		container = (ViewGroup) rootView.findViewById(R.id.container);
		LayoutTransition transition = container.getLayoutTransition();

		// New capability as of Jellybean; monitor the container for *all*
		// layout changes
		// (not just add/remove/visibility changes) and animate these changes as
		// well.
		transition.enableTransitionType(LayoutTransition.CHANGING);

		//search field for search term with attached change listener
		search = (AutoCompleteTextView) rootView.findViewById(R.id.editTextSearch);
		search.getBackground().setColorFilter(getResources().getColor(R.color.material_yellow_300), Mode.SRC_ATOP);
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			//search suggestion populate as user types
			@Override
			public void afterTextChanged(Editable s) {
				new TwitterAutoCompleteTask().execute(s.toString());
			}
		});

		//field for location name with attached listener
		placesView = (AutoCompleteTextView) rootView.findViewById(R.id.AutoCompleteTextView);
		placesView.getBackground().setColorFilter(getResources().getColor(R.color.material_yellow_300), Mode.SRC_ATOP);
		placesView.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			//place suggestion populate as user types
			@Override
			public void afterTextChanged(Editable s) {
				new autoCompletePlaces().execute(s.toString());
			}
		});

		//handlers for date and switch elements
		endDate = (EditText) rootView.findViewById(R.id.editTextEndDate);
		startDate = (EditText) rootView.findViewById(R.id.editTextStartDate);
		dateSwitch = (Switch) rootView.findViewById(R.id.switchDate);
		startDateIcon = (FontelloTextView) rootView.findViewById(R.id.startDateIcon);
		endDateIcon = (FontelloTextView) rootView.findViewById(R.id.endDateIcon);
		
		//date fields visibility decided based of switch default state
		if (dateSwitch.isChecked()) {
			startDate.setVisibility(View.VISIBLE);
			startDateIcon.setVisibility(View.VISIBLE);
			endDate.setVisibility(View.VISIBLE);
			endDateIcon.setVisibility(View.VISIBLE);
		} else {
			startDate.setVisibility(View.GONE);
			startDateIcon.setVisibility(View.GONE);
			endDate.setVisibility(View.GONE);
			endDateIcon.setVisibility(View.GONE);
		}

		//date fields animate in and out of view when switch is toggled
		dateSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					startDate.setVisibility(View.VISIBLE);
					startDateIcon.setVisibility(View.VISIBLE);
					endDate.setVisibility(View.VISIBLE);
					endDateIcon.setVisibility(View.VISIBLE);
				} else {
					startDate.setVisibility(View.GONE);
					startDateIcon.setVisibility(View.GONE);
					endDate.setVisibility(View.GONE);
					endDateIcon.setVisibility(View.GONE);
				}

			}
		});

		// set listeners for start date such that a date picker is the only
		// input
		startDate.getBackground().setColorFilter(getResources().getColor(R.color.material_yellow_300),Mode.SRC_ATOP);
		startDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(
						SearchBarsFragment.this, true);
				newFragment.show(getFragmentManager(), "datePicker");

			}
		});
		startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					DialogFragment newFragment = new DatePickerFragment(
							SearchBarsFragment.this, true);
					newFragment.show(getFragmentManager(), "datePicker");
				}
			}
		});

		// set listeners for end date such that a date picker is the only input
		endDate.getBackground().setColorFilter(
				getResources().getColor(R.color.material_yellow_300),
				Mode.SRC_ATOP);
		endDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(
						SearchBarsFragment.this, false);
				newFragment.show(getFragmentManager(), "datePicker");

			}
		});
		endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					DialogFragment newFragment = new DatePickerFragment(
							SearchBarsFragment.this, false);
					newFragment.show(getFragmentManager(), "datePicker");
				}
			}
		});

		//search button to launch queries
		rootView.findViewById(R.id.textViewRegister).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (search.getText().toString().trim().length() > 0 && search.getText() != null && placesView.getText() != null && placesView.getText().toString().trim().length() > 0) {
							try {
								ParseUser user = ParseUser.getCurrentUser();
								ParseObject history = new ParseObject("SearchHistory");
								history.put("term", search.getText().toString());
								history.put("location", placesView.getText().toString());
								if (dateSwitch.isChecked() && startDate.getText().toString().trim().length() > 0 && endDate.getText().toString().trim().length() > 0) {
									history.put("from", startDate.getText().toString());
									history.put("to", endDate.getText().toString());
								}
								history.put("user", user);
								history.saveInBackground();

								str = search.getText().toString();
								if (dateSwitch.isChecked() && startDate.getText().toString().trim().length() > 0 && endDate.getText().toString().trim().length() > 0) {
									new TwitterCallTask(getActivity(), str, placesView.getText().toString()).execute(str 							+ "%20since%3A"
													+ startDate.getText()
															.toString()
													+ "%20until%3A"
													+ endDate.getText()
															.toString());
								} else {
									new TwitterCallTask(getActivity(), str, placesView.getText().toString()).execute(str);
								}

								search.setText("");
								startDate.setText("");
								endDate.setText("");
								placesView.setText("");

							} catch (Exception e) {
								Toast.makeText(getActivity(),
										"Could Not Connect To Web Services",
										Toast.LENGTH_LONG).show();
							}

						} else {
							Toast.makeText(getActivity(),
									"Please Enter A Search Term And Location",
									Toast.LENGTH_SHORT).show();
						}

					}
				});

		return rootView;
	}

	public void setStartDate(String s) {
		startDate.setText(s);
	}

	public void setEndDate(String s) {
		endDate.setText(s);
	}

	//asynctask to get suggestions for # search term  
	class TwitterAutoCompleteTask extends
			AsyncTask<String, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			try {
				String term = params[0];
				URL url = new URL("http://google.com/complete/search?q=%23"
						+ term + "&output=toolbar");
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setRequestMethod("GET");
				con.connect();
				int statusCode = con.getResponseCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					InputStream in = con.getInputStream();

					return AutoCompleteUtil.AutoCompletePullParser
							.parseSearch(in);
				}
			} catch (MalformedURLException e) {
				return null;
			} catch (IOException e) {
				return null;
			} catch (XmlPullParserException e) {
				return null;
			}

			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			try {
				if (result != null) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getActivity(),
							android.R.layout.simple_dropdown_item_1line, result);
					(search).setAdapter(adapter);
				} else {
					String[] x = new String[] { "" };
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getActivity(),
							android.R.layout.simple_dropdown_item_1line, x);
					(search).setAdapter(adapter);
				}
			} catch (Exception e) {

			}
		}
	}

	//asynctask for suggestions for places
	class autoCompletePlaces extends AsyncTask<String, Void, ArrayList<String>> {

		ArrayList<String> resultList = null;

		@Override
		protected ArrayList<String> doInBackground(String... params) {

			String input = params[0];
			HttpURLConnection conn = null;

			StringBuilder jsonResults = new StringBuilder();

			try {

				StringBuilder sb = new StringBuilder(PLACES_API_BASE
						+ TYPE_AUTOCOMPLETE + OUT_JSON);

				sb.append("?key=" + API_KEY);

				sb.append("&input=" + URLEncoder.encode(input, "utf8"));

				URL url = new URL(sb.toString());

				conn = (HttpURLConnection) url.openConnection();

				InputStreamReader in = new InputStreamReader(
						conn.getInputStream());

				// Load the results into a StringBuilder

				int read;

				char[] buff = new char[1024];

				while ((read = in.read(buff)) != -1) {

					jsonResults.append(buff, 0, read);

				}

			} catch (MalformedURLException e) {

				Log.e(LOG_TAG, "Error processing Places API URL", e);

				return resultList;

			} catch (IOException e) {

				Log.e(LOG_TAG, "Error connecting to Places API", e);

				return resultList;

			} finally {

				if (conn != null) {

					conn.disconnect();

				}

			}

			try {

				// Create a JSON object hierarchy from the results
				Log.d("test", jsonResults.toString());
				JSONObject jsonObj = new JSONObject(jsonResults.toString());

				JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

				// Extract the Place descriptions from the results

				resultList = new ArrayList<String>(predsJsonArray.length());

				for (int i = 0; i < predsJsonArray.length(); i++) {

					resultList.add(predsJsonArray.getJSONObject(i).getString(
							"description"));

				}

			} catch (JSONException e) {

			}

			return resultList;

		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			try {
				if (result != null) {
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getActivity(),
							android.R.layout.simple_dropdown_item_1line, result);
					(placesView).setAdapter(adapter);
				} else {
					String[] x = new String[] { "" };
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getActivity(),
							android.R.layout.simple_dropdown_item_1line, x);
					(placesView).setAdapter(adapter);
				}
			} catch (Exception e) {

			}
		}

	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}
}