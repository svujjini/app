/**
 * JSON parser for twitter results
 */
package com.app.android.hashmap.util;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class TwitterJSONParseUtil {

	static public class TwitterJSONParser {

		public static ArrayList<LatLng> parseTweets(String in)
				throws JSONException {
			ArrayList<LatLng> geoList = new ArrayList<LatLng>();
			LatLng point = null;
			
			// get array of statuses
			JSONObject root = new JSONObject(in);
			JSONArray statusJSONArray = root.getJSONArray("statuses");

			// loop through each staus and get lat and lng data, if it exists
			for (int i = 0; i < statusJSONArray.length(); i++) {
				// get status
				JSONObject statusJSONObject = statusJSONArray.getJSONObject(i);

				// get coordinates object, if it exists
				JSONObject geoJSONObject;
				try {
					geoJSONObject = statusJSONObject
							.getJSONObject("coordinates");
				} catch (Exception e) {
					geoJSONObject = null;
				}

				// if there is coordinate data, extract it and create latlng
				// object
				if (geoJSONObject != null) {
					if (geoJSONObject.getString("type").equals("Point")) {
						JSONArray geoJSONArray = geoJSONObject
								.getJSONArray("coordinates");
						point = new LatLng(geoJSONArray.getDouble(1),
								geoJSONArray.getDouble(0));
						
					}
				}

				// add to list if not null
				if (point != null) {
					geoList.add(point);
				}
				
				point = null;

			}

			// return list of points
			return geoList;
		}

	}
}