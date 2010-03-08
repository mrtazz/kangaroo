package com.mobiletsm.testbench;


import com.mobiletsm.routing.AllStreetVehicle;
import com.mobiletsm.routing.MobileTSMRoutingEngine;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MobileTSMTestBench extends Activity {
    
	private Button doStuffButton;	
	private Button exitButton;	
	private TextView outputText;	
	private EditText latEdit;	
	private EditText lonEdit;
	private EditText speedEdit;
	
	private RoutingEngine engine = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        doStuffButton = (Button) findViewById(R.id.doStuffButton);        
        exitButton = (Button) findViewById(R.id.exitButton);        
        outputText = (TextView) findViewById(R.id.outputText);
        latEdit = (EditText) findViewById(R.id.latEdit);        
        lonEdit = (EditText) findViewById(R.id.lonEdit);        
        speedEdit = (EditText) findViewById(R.id.speedEdit);        
        
        doStuffButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				if (engine == null || !engine.initialized()) {
					engine = new MobileTSMRoutingEngine();
					engine.init("/sdcard/map-fr.db");
				}
				
				if (engine.initialized()) {
					Place place1 = new Place(48.0064241, 7.8521991);
					
					
					Place place2 = null;
					
					place2 = new Place(Double.parseDouble(latEdit.getText().toString()), 
							Double.parseDouble(lonEdit.getText().toString()));				
					
					
					Vehicle vehicle = new AllStreetVehicle();
					vehicle.setMaxSpeed(Double.parseDouble(speedEdit.getText().toString()));
					RouteParameter route = engine.routeFromTo(place1, place2, vehicle);
					
					System.out.println("route = " + route.toString());					
					outputText.setText(route.toString());
				}

			}
		});
        
        
        exitButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (engine != null && engine.initialized()) {
					engine.shutdown();
				}
				finish();
			}
		});
        
        
    }
}