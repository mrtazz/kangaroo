/**
 *
 */
package com.kangaroo.gui;

import java.util.ArrayList;

import android.app.ListActivity;
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
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

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
	  
	  private boolean applyMenuChoice(MenuItem item) {
		  Toast toast;
		  switch (item.getItemId()) {
		    case MENU_DELETE:
			  toast = Toast.makeText(this,
			  					     "Menu item# "+Long.toString(item.getItemId())+" clicked",
				  				     Toast.LENGTH_SHORT);
		  	  toast.show();
		      return true;
		    case MENU_ADD_LOCATION:
			  toast = Toast.makeText(this,
  					   				 "Menu item# "+Long.toString(item.getItemId())+" clicked",
  					   				 Toast.LENGTH_SHORT);
			  toast.show();
		      return true;
		    case MENU_TO_TASK:
			  toast = Toast.makeText(this,
  					   				 "Menu item# "+Long.toString(item.getItemId())+" clicked",
  					   				 Toast.LENGTH_SHORT);
			  toast.show();
		  }
		  return false;
		}

	  private void reload()
	  {
	        tv.setText("Today");
	  		Toast toast = Toast.makeText(this, "Reloading events!", Toast.LENGTH_SHORT);
	  		toast.show();
	        eventlist = new ArrayList<CalendarEvent>(cl.getTodaysEvents("1").values());
		    // Bind the ListView to an ArrayList of strings.
	        calendarAdapter = new CalendarAdapter(this, R.layout.row, eventlist);
	        setListAdapter(this.calendarAdapter);
	        calendarAdapter.setNotifyOnChange(true);
	  }

	  /** menu methods */
	  public boolean onCreateOptionsMenu(Menu menu){

		  MenuInflater inflater = getMenuInflater();
		  inflater.inflate(R.menu.dayplan_menu, menu);
		  return true;

	  }

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
