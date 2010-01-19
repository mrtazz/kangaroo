package com.kangaroo.techscout.routing.tsm.tsmtestbench;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;

import org.openstreetmap.osm.data.MemoryDataSet;

import com.kangaroo.routing.KangarooRoutingEngine;
import com.kangaroo.routing.KangarooRoutingManager;
import com.kangaroo.routing.Place;
import com.kangaroo.routing.StatusChange;
import com.kangaroo.routing.StatusListener;
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


public class TSMTestBench extends Activity implements StatusListener {
    
	private TextView textView;
	private ProgressDialog progressDialog;
	private StringBuffer text = new StringBuffer("TSMTestBench\n");	
	
	private KangarooRoutingManager routingManager = null;
	
	MovementSimulator simulator = null;
	
	private PrintStream out = null;
	
	private static final int DIALOG_READFILE_ID = 0; 
	
	private static final int DIALOG_LOOK_FOR_NEAREST_NODE_ID = 1; 
	
	private Handler routingManagerStatusChangedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onRoutingManagerStatusChanged((StatusChange)msg.obj);
		}
	};
	
	
	private StatusListener routingManagerStatusListener = new StatusListener() {
		@Override
		public void onRoutingManagerStatusChanged(StatusChange status) {
			Message msg = Message.obtain();
			msg.obj = status;
			routingManagerStatusChangedHandler.sendMessage(msg);
		}		
	};
	
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        
        textView = (TextView) findViewById(R.id.textview);
        
        try {
			out = new PrintStream(new FileOutputStream(new File("/sdcard/out.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        routingManager = new KangarooRoutingManager();
        
        showDialog(DIALOG_READFILE_ID);
        
        
        Vehicle vehicle = new Vehicle();
        vehicle.setMaxSpeed(50);
        
        simulator = new MovementSimulator();
        simulator.setVehicle(vehicle);
        
        // Am Kurzarm
        simulator.setStartingPoint(new Place(48.1216952, 7.8571635), 0);
        
        // Im JŠgeracker
        simulator.addTurningPoint(new Place(48.104424, 7.870367), 20000);
        
        // Burgstra§e
        simulator.setDestinationPoint(new Place(48.124448, 7.846498), 40000);      
		
        /*
        simulator.requestLocationUpdates("gps", 0, 0, listener);      
        
        
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch(id) {
			case DIALOG_READFILE_ID:
								
				builder.setMessage("Do you want to read the map file? This will propably take a while.")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							dialog.cancel();							
							try {								
					        	routingManager.setRoutingDataSource(new URI("file:/sdcard/map-em.osm"));
					        	routingManager.setStatusListener(routingManagerStatusListener);
					        	routingManager.init();					        	
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
				
			case DIALOG_LOOK_FOR_NEAREST_NODE_ID:
				
				builder.setMessage("Do you want to update your position?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							dialog.cancel();							
							
							LocationListener listener = routingManager.getLocationListener();
							
							simulator.requestLocationUpdates("gps", 0, 0, listener);
					        
					        try {
					        	new Thread(simulator).start();
							} catch (Exception e) {
								Log.e("MovementSimulator", e.toString());
							}
							
							/*
							Location location = new Location("gps");
							location.setLatitude(48.1216952);
							location.setLongitude(7.8571635);
							listener.onLocationChanged(location);
							*/							
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
	public void onDestroy() {
		if (routingManager != null) 
			routingManager.shutdown();
		routingManager = null;

		super.onDestroy();
	}
    
    
	private long time = 0;
	
	@Override
	public void onRoutingManagerStatusChanged(StatusChange status) {
		
		if (status.operationFinished == false) {
			time = System.currentTimeMillis();
			text.append(" > " + status.message);			
		} else {
			time = System.currentTimeMillis() - time;
			
			if (status.operationID == KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_ID) {
				text.append(" (" + Long.toString(time) + "ms)\n" + "    " + status.message + "\n");
			} else {
				text.append(" (" + Long.toString(time) + "ms)\n");
			}
		}
		
		if (status.operationEnduring && status.operationID == KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_ID 
				&& progressDialog == null) {
			progressDialog = ProgressDialog.show(this, "Please wait", status.message, true, false);
		} else if (status.operationFinished && progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
			
			if (status.operationID == KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_ID) {
				onRoutingEngineReady();
			}
		}
		
		textView.setText(text.toString());		
	}
	
	
	
		
	private void onRoutingEngineReady() {
		showDialog(DIALOG_LOOK_FOR_NEAREST_NODE_ID);
	}
	
	
}