package com.app.android.hashmap;

import java.util.List;

import com.app.android.hashmap.R;
import com.app.android.hashmap.util.TwitterCallTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity {
	HeatmapTileProvider mProvider;
	TileOverlay mOverlay;
	GoogleMap mMap;

    @SuppressWarnings("unchecked")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();

        List<LatLng> points = null;
        
        //get search term and display it, geo points, anf base location
        if (getIntent().getExtras() != null)
		{
			String term = getIntent().getExtras().getString(TwitterCallTask.TERM_KEY);
			TextView tv = (TextView) findViewById(R.id.textTerm);
			tv.setText("#"+term);
			points = (List<LatLng>) getIntent().getExtras().getSerializable(TwitterCallTask.POINTS_KEY);
			double[] ll = (double[]) getIntent().getExtras().getSerializable(TwitterCallTask.LOC_KEY);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ll[0], ll[1]), 4));
			
		}
        
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.53, -80.81), 4));
        addHeatMap(points);
        
        
    }
    
    private void addHeatMap(List<LatLng> list) {
        
        if(list != null && list.size() > 0)
        {
	    	try
	    	{
	    		Toast.makeText(this, list.size() + " Results", Toast.LENGTH_LONG).show();
	    		// Create a heat map tile provider, passing it the latlngs.
		        mProvider = new HeatmapTileProvider.Builder().data(list).radius(20).opacity(0.7).build();
		        // Add a tile overlay to the map, using the heat map tile provider.
		        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
		        		        
	    	}
	    	catch(Exception e)
	    	{
	    		Toast.makeText(this, "Maps API Could Not Draw Points", Toast.LENGTH_LONG).show();
	    	}
        }
        else
        {
        	Toast.makeText(this, "Did Not Recieve Any Points", Toast.LENGTH_LONG).show();
        }
    }
    
    public void setUpMapIfNeeded() 
    {
        if (mMap != null) 
        {
            return;
        }
        mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        
    }
    

    public GoogleMap getMap() 
    {
        setUpMapIfNeeded();
        return mMap;
    }
}
