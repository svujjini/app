/**
 * Performs request for geocoding and twitter api calls
 */
package com.app.android.hashmap.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.app.android.hashmap.MapActivity;
import com.parse.ParseTwitterUtils;

public class TwitterCallTask extends AsyncTask<String, Void, String> {
	public final static String POINTS_KEY = "POINTS";
	public final static String LOC_KEY = "LOC";
	public final static String TERM_KEY = "TERM";

	Context activity;
	String str;
	String loc;

	public TwitterCallTask(Activity activity, String str, String loc) {
		this.activity = activity;
		this.str = str;
		this.loc = loc;
	}

	double lng = -200;
	double lat = -200;
	ProgressDialog progressDialog;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Querying Twitter...");
		progressDialog.show();

	}

	@Override
	protected String doInBackground(String... params) {
		try {
			try
			{
				geoCoder();
				if (lat == -200 && lng == -200) {
					Toast.makeText(activity, "Error: Could Not Find Location",
							Toast.LENGTH_LONG).show();
					return "";
				}
			}
			catch(Exception e)
			{
				return "";
			}
			/*if (lat == -200 && lng == -200) {
				Toast.makeText(activity, "Error: Could Not Find Location",
						Toast.LENGTH_LONG).show();
				return null;
			}*/

			HttpClient client = new DefaultHttpClient();
			// https://api.twitter.com/1.1/search/tweets.json?q=clt&geocode="+5.912214,-80.230182 +",3000km&lang=pt&result_type=recent
			HttpGet verifyGet = new HttpGet(
					"https://api.twitter.com/1.1/search/tweets.json?q=%23"
							+ params[0] + "&geocode=" + lat + "," + lng
							+ ",20000km&count=100");
			ParseTwitterUtils.getTwitter().signRequest(verifyGet);
			org.apache.http.HttpResponse response = client.execute(verifyGet);
			HttpEntity entity = ((org.apache.http.HttpResponse) response)
					.getEntity();
			String responseString = EntityUtils.toString(entity, "UTF-8");
			return responseString;
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	protected void onPostExecute(String result) {
		progressDialog.dismiss();
		if (!result.equals("")) 
		{
			try 
			{
				Intent intent = new Intent(activity, MapActivity.class);
				intent.putExtra(POINTS_KEY,
						TwitterJSONParseUtil.TwitterJSONParser
								.parseTweets(result));
				intent.putExtra(TERM_KEY, str);
				intent.putExtra(LOC_KEY, new double[] { lat, lng });
				activity.startActivity(intent);
			} 
			catch (JSONException e) 
			{
				Toast.makeText(activity, "Could Not Get Results From Twitter",
						Toast.LENGTH_SHORT).show();
			}

		} 
		else 
		{
			Toast.makeText(activity, "Could Not Parse Results",Toast.LENGTH_SHORT).show();
		}

	}

	private void geoCoder() {

		String uri;
		try {
			uri = "http://maps.google.com/maps/api/geocode/json?address="
					+ URLEncoder.encode(loc, "UTF-8") + "&sensor=false";
		} catch (UnsupportedEncodingException e2) {
			return;
		}

		HttpGet httpGet = new HttpGet(uri);
		HttpClient cli = new DefaultHttpClient();
		HttpResponse res;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			res = cli.execute(httpGet);
			HttpEntity entity = res.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			return;
		} catch (IOException e) {
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());

			lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			Log.d("latitude", "" + lat);
			Log.d("longitude", "" + lng);
		} catch (JSONException e) {
			return;
		}

	}

	static public interface IData {
		public Context getContext();
	}

}