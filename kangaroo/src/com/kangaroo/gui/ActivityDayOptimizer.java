/**
 * 
 */
package com.kangaroo.gui;

import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.kangaroo.R;
import com.kangaroo.ActiveDayPlan;
import com.kangaroo.GreedyTaskInsertionOptimizer;
import com.kangaroo.MissingParameterException;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapterAndroid;
import com.kangaroo.calendar.CalendarEvent;
import com.mobiletsm.routing.AllStreetVehicle;
import com.mobiletsm.routing.MobileTSMRoutingEngine;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RoutingEngine;

/**
 * @author mrtazz
 *
 */
public class ActivityDayOptimizer extends ListActivity {
	
	// instance variables
	private ArrayList<CalendarEvent> events = null;
	private ArrayAdapterCalendar calendarAdapter;
	private TextView tv;
	private com.kangaroo.ActiveDayPlan adp;
	private com.kangaroo.DayPlan dp;
	private SharedPreferences prefsPrivate = null;
	private String preferencesName = "kangaroo_config";
	private Place place = null;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dayoptimizer);
		prefsPrivate = getSharedPreferences(preferencesName, MODE_PRIVATE);

        tv = (TextView)findViewById(R.id.DayOptimizeTitle);
        tv.setText("Possible Dayplan:");
		adp = new ActiveDayPlan();
		CalendarAccessAdapter ca = new CalendarAccessAdapterAndroid(this);
		ca.setContext(getApplicationContext());
		adp.setCalendarAccessAdapter(ca);
		
		// routing engine
    	adp.setRoutingEngine(MobileTSMRoutingEngine.getInstance(this));
		
    	// get current location
		LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		

		if(!(currentLocation == null))
		{
			//check the dayplan here and deal with consistency and compliance problems
			place = new Place(currentLocation.getLatitude(), currentLocation.getLongitude());
		}
		
		if (place != null)
		{
			adp.setOptimizer(new GreedyTaskInsertionOptimizer());
			try
			{
				dp = adp.optimize(new Date(), place, new AllStreetVehicle(5.0));
				events = new ArrayList<CalendarEvent>(dp.getEvents());
				// Bind the ListView to an ArrayList of strings.
		        calendarAdapter = new ArrayAdapterCalendar(this, R.layout.row, events);
		        setListAdapter(this.calendarAdapter);
		        calendarAdapter.setNotifyOnChange(true);
			}
			catch(MissingParameterException e)
			{
		  		Toast toast = Toast.makeText(this,
						   					 "Location for all events not known.",
						   					 Toast.LENGTH_SHORT);
		  		toast.show();	
			}
		}
	}

	/** menu methods */
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.dayoptimizer_menu, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected (MenuItem item)
	{
		Toast toast;
		  switch (item.getItemId())
		  {		  
		  	case R.id.accept:
		  		adp.setEvents(dp.getEvents());
		  		adp.setTasks(dp.getTasks());
		  		return true;

		  	case R.id.next:
		  		return true;

			default:
				System.out.println("ItemId: "+item.getItemId());
				return true;
		  }
	}

}
