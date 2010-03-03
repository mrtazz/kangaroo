/**
 *
 */
package com.kangaroo.gui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dayplan);
	        cl = new CalendarLibrary(this);
	        tv = (TextView)findViewById(R.id.DayTitle);

	        reload();
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
