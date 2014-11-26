package com.example.proyectofinalalvaroreyes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Map extends Fragment implements OnMapClickListener, OnMapLongClickListener {
	
	private GoogleMap googleMap;
	private final String TAG = "Map.java"; 
	double lat2;
	double lon2;
	boolean on = false;
	protected JSONArray mData;
	private View rootView;
	static LocationManager lm;
	static MiLocationListener mlistener;

	public static String lat, lon, loc;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.layout_finish, container,
				false);
		lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		mlistener = new MiLocationListener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5,
				mlistener);
		googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMapClickListener(this);
		googleMap.setOnMapLongClickListener(this);
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.getUiSettings().setAllGesturesEnabled(true);
		googleMap.setTrafficEnabled(true);
		return rootView;
		
	}



	public class MiLocationListener implements LocationListener {

		public MiLocationListener() {
			// TODO Auto-generated constructor stub
		}

		public synchronized void onLocationChanged(Location location) {
			
			LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
			CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(pos, 15);			
			googleMap.moveCamera(cam);

		}

		public synchronized void onProviderDisabled(String provider) {

		}

		public synchronized void onProviderEnabled(String provider) {

		}

		public synchronized void onStatusChanged(String provider, int status,
				Bundle extras) {

		}
	}



	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
		if (isNetworkAvailable()) {
			GetDataTask getDataTask = new GetDataTask();
			getDataTask.execute();
		} 
    }
	
	private boolean isNetworkAvailable() {
		rootView.getContext();
		ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		boolean isNetworkAvaible = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isNetworkAvaible = true;
			Toast.makeText(rootView.getContext(), "Network is available ", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(rootView.getContext(), "Network not available ", Toast.LENGTH_LONG)
					.show();
		}
		return isNetworkAvaible;
	}
	
	public class GetDataTask extends AsyncTask<Object, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Object... params) {
			InputStream is = null;
			String result = "";
			JSONArray jsonObject = null;
			
			// HTTP
			try {	    	
				HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
				HttpGet httppost = new HttpGet("https://proyectofinal-alvaroy.c9.io/incidencia.json");
				HttpResponse response = httpclient.execute(httppost);				
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} catch(Exception e) {
				return null;
			}
		    
			// Read response to string
			try {	    	
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();	 
				
				
			} catch(Exception e) {
				return null;
			}
	 
			// Convert string to object
			try {
				jsonObject = new JSONArray(result);   				
			} catch(JSONException e) {
				return null;				
			}
	    
			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			mData = result;
			handleBlogResponse();
		}

	}

	public void handleBlogResponse() {
		if (mData == null){
			AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
			builder.setTitle("Error");
			builder.setMessage("Ocurrio un error, los datos no fueron descargados");
			builder.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			try {
				for (int i = 0;i< mData.length();i++){
					JSONObject post = mData.getJSONObject(i);
					String latitude = post.getString("latitude");
					String longitude = post.getString("longitude");
					String level = post.getString("level");
					String date = post.getString("date");
					LatLng point = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
					googleMap.addMarker(new MarkerOptions().position(point).title(
							"Nivel: "+level+"\nFecha: "+date));
				}
			} catch (JSONException e) {
				Log.e(TAG,"Exception caught!",e);
			}
		}
	}
}
