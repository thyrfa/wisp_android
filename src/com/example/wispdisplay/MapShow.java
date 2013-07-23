package com.example.wispdisplay;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.example.wisp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapShow extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	private GoogleMap map;
	private Location loc;
	private LocationClient locclient;
	MapFragment mMapFragment;
	public MapShow(){

	}
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);
		mMapFragment = MapFragment.newInstance();
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.map, mMapFragment);
		fragmentTransaction.commit();
		setUpMap();
		map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    	locclient = new LocationClient(this, this, this);
        locclient.connect();
		LocationGetter locdate= new LocationGetter(mMapFragment, this);
		map.setOnMarkerClickListener(new MarkListen(locdate));
		locdate.execute((Void)null);
	}
	protected void setUpMap() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        // Check if we were successful in obtaining the map.
	        if (map == null) {
	        	Log.d("Map", "MapNull");
	        }
	    }
	}
	public void addMark(MarkerOptions[] mark){
		Log.d("Mark", "MarkergonnaAdded");
		for (int i=0;i<mark.length;i++){
			map.addMarker(mark[i]);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("Mark", mark[i].getPosition().latitude+", "+mark[i].getPosition().longitude);
		}
		setUpMap();
		mMapFragment=MapFragment.newInstance();
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.map, mMapFragment);
		fragmentTransaction.commit();
		Log.d("Mark", "MarkerAdded");
	}
	@Override
	public void onConnected(Bundle arg0) {
		if (locclient.getLastLocation()==null){
			 	final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		else{
			loc=locclient.getLastLocation();
		}
		GoogleMapOptions op= new GoogleMapOptions();
		op.tiltGesturesEnabled(false);
		op.rotateGesturesEnabled(false);
		op.zoomControlsEnabled(false);
		Log.d("Location", loc.getLatitude()+", "+loc.getLongitude());
        op.camera(new CameraPosition(new LatLng(loc.getLatitude(), loc.getLongitude()),14,0,0));
        mMapFragment=MapFragment.newInstance(op);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.map, mMapFragment);
		fragmentTransaction.commit();
		setUpMap();
		Log.d("Location", "After "+map.getCameraPosition().target.latitude+", "+map.getCameraPosition().target.longitude);
        locclient.disconnect();
		
	}
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast toast = Toast.makeText(getApplicationContext(), "Unable to Connect to Google Play Services", Toast.LENGTH_SHORT);
		toast.setDuration(5);
		toast.show();				
	}
}
