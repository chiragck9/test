package com.test.travelsearch;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.JsonReader;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable{

	private static String RESULT_URL = "http://pre.dev.goeuro.de:12345/api/v1/suggest/position/en/name/";
	private ArrayList<String> mResultList;
	private static Context mContext;
	
	public AutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mContext = context;
	}

	@Override
	public int getCount() {
		return mResultList.size();
	}

	@Override
	 public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Get the auto complete results.
                    mResultList = getResultList(constraint.toString());
                    
                    // Assign the data to the FilterResults
                    filterResults.values = mResultList;
                    filterResults.count = mResultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }


	@Override
	public String getItem(int position) {
		return mResultList.get(position);
	}
	
	private static ArrayList<String> getResultList(String input){
		ArrayList<String> cityNameList = null;
		Map<Float,String> map= new HashMap<Float, String>();
		Location currentLocation = null;
		
		// Method to return JSON data from the URL as a string
        String jsonResults = getJsonAsString(RESULT_URL, input);
		
		try {
			JSONObject jsonObject = new JSONObject(jsonResults);
			JSONArray jsonArray = jsonObject.getJSONArray("results");
			cityNameList = new ArrayList<String>(jsonArray.length());
			
			//Location object with the current user location.
			currentLocation = getCurrentLocation();
			int length = jsonArray.length();
			Location location;
			float distance;
			for(int i=0;i<length;i++){
				if(currentLocation!=null){
					double latitude = jsonArray.getJSONObject(i).getJSONObject("geo_position").getDouble("latitude");
					double longitude = jsonArray.getJSONObject(i).getJSONObject("geo_position").getDouble("longitude");
					
					//Create location object with lat and long details for each result
					location = new Location(jsonArray.getJSONObject(i).getString("name"));
					location.setLatitude(latitude);
					location.setLongitude(longitude);
					distance = location.distanceTo(currentLocation);
					map.put(distance,location.getProvider());
				}else{
					cityNameList.add(jsonArray.getJSONObject(i).getString("name"));
				}
			}
		} catch (JSONException e) {
			System.out.println("JSONException : " + e);
		}
		
		//TreeMap to sort the data in the map according to the closest position
		// and add city names to the result list.
		if(currentLocation!=null){
			Map<Float,String> sortedMap = new TreeMap<Float, String>(map);
			for (Map.Entry<Float, String> entry : sortedMap.entrySet()){
				cityNameList.add(entry.getValue());
			}
		}
		
		return cityNameList;
	} 
	
	private static String getJsonAsString(String urlJson, String input) {

		StringBuilder resultStringBuilder = new StringBuilder();
		try {
			URL url = new URL(urlJson + input);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() != 200) {
				System.out.println("HTTP error code: " + conn.getResponseCode());
			}

			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				resultStringBuilder.append(buff, 0, read);
			}
			conn.disconnect();

		} catch (MalformedURLException e) {
			System.out.println("MalformedUrlException : " + e);
		} catch (IOException e) {
			System.out.println("IOException : " + e);
		}

		return resultStringBuilder.toString();
	}
	
	private static Location getCurrentLocation(){
		 LocationManager lm = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE); 
		 Location gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		 Location networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);	
		 Location passiveLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		 if(gpsLocation != null){
			 return gpsLocation;
		 }
		 if(networkLocation!=null){
			 return networkLocation;
		 }		
		 if(passiveLocation !=null){
			 return passiveLocation;
		 }
		return null;
	}
}
