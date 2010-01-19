package com.kangaroo.techscout.routing.tsm.tsmtestbench;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.Route.RoutingStep;
import org.openstreetmap.travelingsalesman.routing.describers.SimpleRouteDescriber;

import com.kangaroo.routing.Car;
import com.kangaroo.routing.KangarooRoutingEngine;
import com.kangaroo.routing.KangarooRoutingManager;
import com.kangaroo.routing.Place;
import com.kangaroo.routing.TSMKangarooRoutingEngine;
import com.kangaroo.routing.Vehicle;
import com.kangaroo.statuschange.JobDoneStatusChange;
import com.kangaroo.statuschange.JobFailedStatusChange;
import com.kangaroo.statuschange.JobStartedStatusChange;
import com.kangaroo.statuschange.StatusChange;
import com.kangaroo.statuschange.StatusListener;
import com.kangaroo.statuschange.SubJobDoneStatusChange;
import com.kangaroo.statuschange.SubJobStartedStatusChange;
import com.kangaroo.techscout.routing.MovementSimulator;
import com.kangaroo.tsm.osm.io.FileLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class TSMTestBench extends Activity implements StatusListener {
    
	private Button closeButton;
	private Button clearButton;
	private TextView statusTextView;
	private TextView outputTextView;
	private TextView busyTextView;
	private ProgressDialog progressDialog;
	
	private StringBuffer statusStringBuffer = new StringBuffer();
	private StringBuffer outputStringBuffer = new StringBuffer();
	
	private KangarooRoutingManager routingManager = null;
	private LocationManager locationManager = null;
    private Vehicle vehicle = new Car();
	
	MovementSimulator simulator = null;
	
	private static final int DIALOG_READFILE_ID = 0; 
	
	private static final int DIALOG_LOOK_FOR_NEAREST_NODE_ID = 1; 
	
	private static final int DIALOG_ROUTE_FROMTO = 2; 
	
	private Handler routingManagerStatusChangedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onStatusChanged((StatusChange)msg.obj);
		}
	};
	
	
	private StatusListener routingManagerStatusListener = new StatusListener() {
		@Override
		public void onStatusChanged(StatusChange status) {
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
        
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        outputTextView = (TextView) findViewById(R.id.outputTextView);
        busyTextView = (TextView) findViewById(R.id.busyTextView);
        closeButton = (Button) findViewById(R.id.close);
        clearButton = (Button) findViewById(R.id.clear);
        
        closeButton.setOnClickListener(new OnClickListener() {
        	  @Override
        	  public void onClick(View v) {
        	    finish();
        	  }
        	});
        
        clearButton.setOnClickListener(new OnClickListener() {
      	  @Override
      	  public void onClick(View v) {
      	    statusStringBuffer = new StringBuffer();
      	  }
      	});
        
        outputTextView.setText("");
        statusTextView.setText("");
        
        routingManager = new KangarooRoutingManager();
        
     
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        showDialog(DIALOG_READFILE_ID);
        
        

        vehicle.setMaxSpeed(50);
        /*
        simulator = new MovementSimulator();
        simulator.setVehicle(vehicle);
        
        // Am Kurzarm
        simulator.setStartingPoint(new Place(48.1216952, 7.8571635), 0);
        
        // Im JŠgeracker
        simulator.addTurningPoint(new Place(48.104424, 7.870367), 20000);
        
        // Burgstra§e
        simulator.setDestinationPoint(new Place(48.124448, 7.846498), 40000);      
		
        
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
								outputTextView.setText("exception = " + e.toString());					    		
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
							
							locationManager.requestLocationUpdates("gps", 0, 0, listener);
					        /*
					        try {
					        	new Thread(simulator).start();
							} catch (Exception e) {
								Log.e("MovementSimulator", e.toString());
							}
							*/
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
			
			case DIALOG_ROUTE_FROMTO:
				
				builder.setMessage("Do you want to find a route?")
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {							
							dialog.cancel();
					        
					        try {
								routingManager.routeFromTo(
										new Place(48.1216952, 7.8571635), 
										new Place(48.104424, 7.870367), vehicle);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
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
	public void onDestroy() {
		if (routingManager != null) 
			routingManager.shutdown();
		routingManager = null;

		super.onDestroy();
	}
    
    
	private long jobTime = 0;
	private long subJobTime = 0;
	private boolean hasSubJob = false;
	private int update = 0;
	
	@Override
	public void onStatusChanged(StatusChange status) {
		
		if (status.busy)
			busyTextView.setText("busy...");
		else
			busyTextView.setText("");
		
		if (status instanceof JobStartedStatusChange) {			
			
			if (status instanceof SubJobStartedStatusChange) {
				statusStringBuffer.append("\n   ");
				subJobTime = System.currentTimeMillis();
				hasSubJob = true;
			} else {
				
				statusStringBuffer.append("\n");
				jobTime = System.currentTimeMillis();
				hasSubJob = false;
			}
			
			statusStringBuffer.append("> " + status.message);
			
			if (status.jobID == KangarooRoutingEngine.JOBID_INIT_ROUTING_ENGINE) 
				progressDialog = ProgressDialog.show(this, "Please wait", status.message, true, false);
			
		} else if (status instanceof JobDoneStatusChange) {
			
			if (status instanceof SubJobDoneStatusChange) {
				subJobTime = System.currentTimeMillis() - subJobTime;
				statusStringBuffer.append(" (" + Long.toString(subJobTime) + "ms)");	
			} else {
				jobTime = System.currentTimeMillis() - jobTime;
				if (hasSubJob)
					statusStringBuffer.append("\n   > TOTAL:");
				statusStringBuffer.append(" (" + Long.toString(jobTime) + "ms)");	
			}									
			
			//outputStringBuffer = new StringBuffer();
			
			if (status.result != null) {
				if (status.result instanceof Route) {
					
					Route route = (Route)status.result;
					
					outputStringBuffer = new StringBuffer();
					outputStringBuffer.append(String.format("dist = %.0f m\nupdate #%d\n", 
							route.distanceInMeters(), ++update));					
					
					List<RoutingStep> steps = route.getRoutingSteps();
					
					for (RoutingStep step : steps) {
						Iterator<Tag> itr = step.getWay().getTags().iterator();
						if (itr.hasNext())
							outputStringBuffer.append(">");
						boolean hasName = false;
						while (itr.hasNext()) {
							Tag tag = itr.next();
							if (tag.getKey().equals("name")) {
								hasName = true;
								if (!outputStringBuffer.toString().contains(tag.getValue()))
									outputStringBuffer.append(tag.getValue() + "\n");								
							}							
						}
						if (!hasName)
							outputStringBuffer.append("-\n");
					}
									
					
				} else if (status.result instanceof Iterator<?>) {
					
					StringBuffer wayNames = new StringBuffer();					
					
					Iterator<Way> wayitr = (Iterator<Way>)status.result;
					while(wayitr.hasNext()) {
						Way way = wayitr.next();						
						Iterator<Tag> itr = way.getTags().iterator();
						if (itr.hasNext())
							wayNames.append(">");
						while (itr.hasNext()) {
							Tag tag = itr.next();
							wayNames.append(" " + tag.getKey() + " = " + tag.getValue() + "\n");
						}
					}
					
					outputStringBuffer.append(wayNames.toString());
				}
				
			}
			
			if (status.jobID == KangarooRoutingEngine.JOBID_INIT_ROUTING_ENGINE && progressDialog != null) {
				progressDialog.dismiss();
				onRoutingEngineReady();
			}
		} else if (status instanceof JobFailedStatusChange) {
			statusStringBuffer.append(" failed!\n");
		}
			
		outputTextView.setText(outputStringBuffer.toString());
		statusTextView.setText(statusStringBuffer.toString());	
	}
	
	
	
		
	private void onRoutingEngineReady() {
		showDialog(DIALOG_LOOK_FOR_NEAREST_NODE_ID);
		//showDialog(DIALOG_ROUTE_FROMTO);
	}
	
	
}