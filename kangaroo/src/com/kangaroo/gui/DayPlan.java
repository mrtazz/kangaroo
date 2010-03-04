/**
 *
 */
package com.kangaroo.gui;

import java.util.ArrayList;

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
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarLibrary;

/**
 * @author mrtazz
 *
 */
public class DayPlan extends ListActivity {

	  private ArrayList<CalendarEvent> eventlist = null;
	  private CalendarAdapter calendarAdapter;
	  private TextView tv;
	  private CalendarLibrary cl;
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
	        cl = new CalendarLibrary(this);
	        tv = (TextView)findViewById(R.id.DayTitle);

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
		  //long id = getListAdapter().getItemId(info.position);
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
		  switch (item.getItemId()) {
		    case MENU_DELETE:
			  toast = Toast.makeText(this,
			  					     "Menu item DELETE clicked",
				  				     Toast.LENGTH_SHORT);
		  	  toast.show();
		      return true;
		    case MENU_ADD_LOCATION:
			  // show the map
			  Intent intent = new Intent("com.kangaroo.SELECTPLACE");
			  intent.addCategory(Intent.CATEGORY_DEFAULT);
			  startActivityForResult(intent, 1);
			  
		      return true;
		    case MENU_TO_TASK:
			  toast = Toast.makeText(this,
  					   				 "Menu item MENU_TO_TASK clicked",
  					   				 Toast.LENGTH_SHORT);
			  toast.show();
		  }
		  return false;
		}
	
	  // callback method for intent result
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (data != null) {
				Toast.makeText(this, "lat = " + data.getExtras().getDouble("latitude") + ", " +
						"lon = " + data.getExtras().getDouble("longitude"), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "no position set! resultCode = " + resultCode, Toast.LENGTH_SHORT).show();
			}
		}

	  /**
	   * @brief method to reload today's events 
	   */
	private void reload()
	  {
	        tv.setText("Today");
	  		Toast toast = Toast.makeText(this, "Reloading events!", Toast.LENGTH_SHORT);
	  		toast.show();
	        eventlist = cl.getTodaysEvents("1");
		    // Bind the ListView to an ArrayList of strings.
	        calendarAdapter = new CalendarAdapter(this, R.layout.row, eventlist);
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

}
