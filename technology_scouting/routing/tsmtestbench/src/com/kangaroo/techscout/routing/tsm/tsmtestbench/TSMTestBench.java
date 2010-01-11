package com.kangaroo.techscout.routing.tsm.tsmtestbench;

import java.io.File;
import java.net.URI;

import org.openstreetmap.osm.data.MemoryDataSet;

import com.kangaroo.routing.IProgressListener;
import com.kangaroo.routing.IRoutingEngine;
import com.kangaroo.routing.Place;
import com.kangaroo.routing.TSMKangarooRoutingEngine;
import com.kangaroo.routing.Vehicle;
import com.kangaroo.techscout.routing.MovementSimulator;
import com.kangaroo.tsm.osm.io.FileLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class TSMTestBench extends Activity implements IProgressListener {
    
	private TextView textView;
	
	private ProgressDialog progressDialog;
	
	private IRoutingEngine engine = null;
	
	static final int DIALOG_READFILE_ID = 0; 
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        textView = (TextView) findViewById(R.id.textview);
        
        Vehicle vehicle = new Vehicle();
        vehicle.setMaxSpeed(50);
        
        LocationListener listener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				
				Location loc = new Location("gps");
				loc.setLatitude(47.01);
				loc.setLongitude(7.01);
				
				Log.v("LocationUpdate", "lat = " + location.getLatitude() + ", lon = " + location.getLongitude() + 
						", dist = " + loc.distanceTo(location));
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
        	
        };
                
        
		showDialog(DIALOG_READFILE_ID);
        		
        /*
        MovementSimulator simulator = new MovementSimulator();
        simulator.setVehicle(vehicle);
        simulator.requestLocationUpdates("gps", 0, 0, listener);
        
        simulator.setStartingPoint(new Place(47.0, 7.0), 0);
        simulator.addTurningPoint(new Place(47.005, 7.0), 20000);
        simulator.setDestinationPoint(new Place(47.005, 7.005), 40000);
        
        try {
        	new Thread(simulator).start();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("MovementSimulator", e.toString());
		}
		*/
    }

    
	protected Dialog onCreateDialog(int id) {
		
		Dialog dialog;
		
		switch(id) {
			case DIALOG_READFILE_ID:
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setMessage("Do you want to read the map file? This will propably take a while.")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.cancel();
							
							try {
								
					        	engine = new TSMKangarooRoutingEngine(new URI("file:/sdcard/map-em.osm"));
					        	
					        	progressDialog = ProgressDialog.show(TSMTestBench.this, "Please wait...", "Reading map file", 
					        			true, false);
					        	
					        	engine.Init(TSMTestBench.this);
								
							} catch (Exception e) {
								
								textView.setText("exception = " + e.toString());
					    		
							}
							
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							TSMTestBench.this.finish();
							
						}
					});
				
				dialog = builder.create();
				
				break;
			default:
				dialog = null;
		}
		
		return dialog;
		
	}
    
    
    @Override
	public void onDone(String status) {		
		
		Message msg = Message.obtain();
		
		msg.obj = status;
		
		handler.sendMessage(msg);
		
	}

	@Override
	public void onProgressMade(String status) {
		// TODO Auto-generated method stub
		
	}
	
	
	private Handler handler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();			
		
			textView.setText(((String)msg.obj));
		}
		
	};
}