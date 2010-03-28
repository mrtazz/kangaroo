/**
 *
 */
package com.kangaroo.gui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.kangaroo.R;
import com.kangaroo.ActiveDayPlan;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapterAndroid;
import com.kangaroo.calendar.CalendarAccessAdapterMemory;
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarLibrary;

/**
 * @author mrtazz
 *
 */
public class ActivityDayPlan extends ListActivity {

	  private ArrayList<CalendarEvent> eventlist = null;
	  private ArrayAdapterCalendar calendarAdapter;
	  private TextView tv;
	  private com.kangaroo.ActiveDayPlan dp;
	  private CalendarEvent actual_calendar_event;
	  private int actual_calendar = 1;
	  private long actual_event;
	  // menu item ids
	  private final int MENU_DELETE = 0;
	  private final int MENU_TO_TASK = 1;
	  private final int MENU_ADD_LOCATION = 2;
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dayplan);
	        registerForContextMenu(getListView());
	        tv = (TextView)findViewById(R.id.DayTitle);
	        dp = new ActiveDayPlan();
	        
	        //TODO change back!
	        //CalendarAccessAdapter caa = new CalendarAccessAdapterAndroid(this);
	        CalendarAccessAdapter caa = new CalendarAccessAdapterMemory();
	        
	        caa.setContext(getApplicationContext());
		 	dp.setCalendarAccessAdapter(caa);

	        reload();
	  }
	  /* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	public void onCreateContextMenu(ContextMenu menu,
			  						  View v,
			  						  ContextMenuInfo menuInfo)
	  {
		  AdapterView.AdapterContextMenuInfo info;
		  try {
		      info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		  } catch (ClassCastException e) {
		      //Log.e(TAG, "bad menuInfo", e);
		      return;
		  }
		  actual_calendar_event = (CalendarEvent)info.targetView.getTag(R.id.row);
		  actual_event = info.id;
		  // add menu items
		  menu.add(0, MENU_DELETE, 0, R.string.delete);
		  menu.add(0, MENU_TO_TASK, 0, R.string.to_task);
		  menu.add(0, MENU_ADD_LOCATION, 0, R.string.add_location);
	  }
	  /** when press-hold option selected */
	  @Override
	  public boolean onContextItemSelected(MenuItem item) {
	    return applyMenuChoice(item) || super.onContextItemSelected(item);
	  }
	  
	  /**
	   * @brief method to distinguish context menu choices 
	   * @param item MenuItem
	   * @return true if choice was found, false otherwise
	   */
	private boolean applyMenuChoice(MenuItem item) {
		  Toast toast;
		  Boolean ret = false;
		  switch (item.getItemId()) {
		    case MENU_DELETE:
			  eventlist.remove(actual_calendar_event);
			  dp.setEvents(eventlist);
		      ret = true;
		      break;
		    case MENU_ADD_LOCATION:
			  // show the map
			  Intent intent = new Intent("com.kangaroo.SELECTPLACE");
			  intent.addCategory(Intent.CATEGORY_DEFAULT);
			  startActivityForResult(intent, 1);
		      ret = true;
		      break;
		    case MENU_TO_TASK:
			  toast = Toast.makeText(this,
  					   				 "Transform "+actual_calendar_event.getTitle()
  					   				 +"into task?",
  					   				 Toast.LENGTH_SHORT);
			  toast.show();
			  ret = true;
			  break;
		  }
		  reload();
		  return ret;
		}
	
	  // callback method for intent result
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (data != null) {
				eventlist.remove((int)actual_event);
				double lat = data.getExtras().getDouble("latitude");
				double lon = data.getExtras().getDouble("longitude");
				actual_calendar_event.setLocationLatitude(lat);
				actual_calendar_event.setLocationLongitude(lon);
				eventlist.add((int)actual_event,actual_calendar_event);
				dp.setEvents(eventlist);
			} else {
				Toast.makeText(this, "no position set! resultCode = " + resultCode, Toast.LENGTH_SHORT).show();
			}
			reload();
		}

	  /**
	   * @brief method to reload today's events 
	   */
	private void reload()
	  {
			Date today = new Date();
	        tv.setText(pad2(today.getDate()) + "/" + pad2(today.getMonth()) + "/" 
	        			+ (pad2(today.getYear()+1900)));
	        eventlist = (ArrayList<CalendarEvent>)dp.getEvents();
		    // Bind the ListView to an ArrayList of strings.
	        calendarAdapter = new ArrayAdapterCalendar(this, R.layout.row, eventlist);
	        setListAdapter(this.calendarAdapter);
	        calendarAdapter.setNotifyOnChange(true);
	  }

	  /** menu methods */
	  /* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu){

		  MenuInflater inflater = getMenuInflater();
		  inflater.inflate(R.menu.dayplan_menu, menu);
		  return true;

	  }

	  /* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected (MenuItem item){

		  switch (item.getItemId()){

		  	case R.id.reload:
		  		reload();
		  		return true;

		  	case R.id.exit:
		  		Toast toast = Toast.makeText(this, "I dont't do this anymore!", Toast.LENGTH_SHORT);
		  		toast.show();
		  		return true;

			default:
				System.out.println("ItemId: "+item.getItemId());
				return true;

		  }

	}

	/**
	 * @brief method to pad time to a length of 2
	 * @param i time as int
	 * @return padded time as string
	 */
	private String pad2(int i)
	{
		String s = Integer.toString(i);
		s= (s.length() < 2) ? ("0"+s) : (s);
		return s;
	}
}
