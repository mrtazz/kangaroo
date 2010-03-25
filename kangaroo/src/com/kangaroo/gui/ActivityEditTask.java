/**
 * 
 */
package com.kangaroo.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.kangaroo.R;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintDuration;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskConstraintLocation;
import com.kangaroo.task.TaskConstraintPOI;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.routing.Place;

/**
 * @author mrtazz
 *
 */
/**
 * @author mrtazz
 *
 */
public class ActivityEditTask extends Activity {
	
	  private final int label_length = 90;
	  private final int content_length = 200;
	  private Task t;
	  private Place p = null;
	  private ArrayList<String[]> active_views;
	  private Random generator;
	  private TaskConstraintLocation actual_location = null;
	  private LinearLayout main;
	  // which constraints are already available
	  private boolean hasAmenityConstraint = false;
	  private boolean hasDayTimeConstraint = false;
	  private boolean hasDateConstraint = false;
	  private boolean hasDurationConstraint = false;
	  private boolean hasLocationConstraint = false;
	  
	  
	  private OnClickListener LocationClickListener = new OnClickListener()
	  {
		@Override
		public void onClick(View v) {
			  // show the map
			  Intent intent = new Intent("com.kangaroo.SELECTPLACE");
			  intent.addCategory(Intent.CATEGORY_DEFAULT);
			  startActivityForResult(intent, 1);
		}
	  };
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.edittask);
	        String s = (String)getIntent().getExtras().get("task");
	        t = Task.deserialize(s);
	        active_views = new ArrayList<String[]>();
	        generator = new Random();
			main = (LinearLayout)findViewById(R.id.mainedittasklayout);
	        
	        updateResultData();
	        load();
	  
	  }
	  
	/**
	 * @brief load all views
	 */
	private void load()
	  {
		  	if (active_views.size() > 0)
		  	{
		  		main.removeAllViews();
		  		active_views.removeAll(active_views);
		  	}

	        // set title view
		  	main.addView(getTaskTitleLayout(t));
	        // set description view
		  	main.addView(getTaskDescriptionLayout(t));
			
	        TaskConstraintInterface[] constraints = t.getConstraints();
			for (TaskConstraintInterface tc : constraints)
			{
				String type = tc.getType();
				if (type.equals("amenity")) 
				{
					TaskConstraintPOI ta = (TaskConstraintPOI)tc;
					main.addView(getAmenityConstraintLayout(ta));				  
				}
				else if (type.equals("daytime"))
				{
					TaskConstraintDayTime ta = (TaskConstraintDayTime)tc;
					for (LinearLayout ll : getDayTimeConstraintLayout(ta))
					{
						if (ll != null) main.addView(ll);
					}
				}	
				else if (type.equals("date"))
				{
					TaskConstraintDate td = (TaskConstraintDate)tc;
					for (LinearLayout ll : getDateConstraintLayout(td))
					{
						if (ll != null) main.addView(ll);
					}
				}
				else if (type.equals("duration"))
				{
					TaskConstraintDuration td = (TaskConstraintDuration)tc;
					main.addView(getDurationConstraintLayout(td));
				}
				else if (type.equals("location"))
				{
					TaskConstraintLocation tl = (TaskConstraintLocation)tc;
					main.addView(getLocationConstraintLayout(tl));
				}
		 }    
	  }
	  
	  
	  private void updateResultData()
	  {
		    // generic view which gets casted to
		    // specific views
		  	View v;
		  	// start and end time of possible daytime constraint
		  	Date start_time = null;
		  	Date end_time = null;
		  	Date start_date = null;
		  	Date end_date = null;
		  	if (active_views.size() > 0)
		  	{
		  		t = new Task();
		  		if (p != null) t.addConstraint(new TaskConstraintLocation(p));
		  	}
		  	for (String[] s : active_views)
		  	{
		  		if (s[1].equals("edittext"))
		  		{
		  			v = (EditText)findViewById(Integer.valueOf(s[0]));
		  			if (s[2].equals("description"))
		  			{
		  				t.setDescription(((EditText)v).getText().toString());
		  			}
		  			else if(s[2].equals("title"))
		  			{
		  				t.setName(((EditText)v).getText().toString());
		  			}
		  			else if(s[2].equals("duration"))
		  			{
		  				String ss = ((EditText)v).getText().toString().trim();
		  				Pattern p = Pattern.compile("\\d+");
		  				Matcher m = p.matcher(ss);
		  				Boolean found = m.find();
		  				if (found == true)
		  				{
			  				TaskConstraintDuration tcd = null;
		  					String res = m.group();
		  					tcd = new TaskConstraintDuration(Integer.valueOf(res));
		  					t.addConstraint(tcd);
		  				}
		  			}
		  		}
		  		else if(s[1].equals("spinner"))
		  		{
		  			v = (Spinner)findViewById(Integer.valueOf(s[0]));
		  			if (s[2].equals("amenity"))
		  			{	
		  				String as = (String)((Spinner)v).getSelectedItem();
		  				POICode id = new POICode(as);
		  				TaskConstraintPOI tcp = new TaskConstraintPOI(id);
		  				t.addConstraint(tcp);
		  				
		  			}	
		  		}
		  		else if(s[1].equals("timepicker"))
		  		{
		  			v = (TimePicker)findViewById(Integer.valueOf(s[0]));
		  			if (s[2].equals("starttime"))
		  			{
		  				start_time = new Date(0, 0, 0,
		  									  ((TimePicker)v).getCurrentHour(),
		  									  ((TimePicker)v).getCurrentMinute());
		  			}
		  			else if (s[2].equals("endtime"))
		  			{
		  				end_time = new Date(0, 0, 0,
								  			((TimePicker)v).getCurrentHour(),
								  			((TimePicker)v).getCurrentMinute());		  				
		  			}
		  			
		  		}
		  		else if(s[1].equals("datepicker"))
		  		{
		  			v = (DatePicker)findViewById(Integer.valueOf(s[0]));
		  			if (s[2].equals("startdate"))
		  			{
		  				start_date = new Date(((DatePicker)v).getYear() - 1900,
		  									  ((DatePicker)v).getMonth(),
		  									  ((DatePicker)v).getDayOfMonth());
		  			}
		  			else if (s[2].equals("enddate"))
		  			{
		  				end_date = new Date(((DatePicker)v).getYear() - 1900,
								  			((DatePicker)v).getMonth(),
								  			((DatePicker)v).getDayOfMonth());
		  			}
		  			
		  		}
		    }
		  	
		  	if (start_time != null && end_time != null)
		  	{
		  		TaskConstraintDayTime tcd = new TaskConstraintDayTime(start_time, end_time);
		  		t.addConstraint(tcd);
		  	}
		  	if (end_date != null)
		  	{
		  		if (start_date != null)
		  		{
		  			TaskConstraintDate tcd = new TaskConstraintDate(start_date, end_date);
		  			t.addConstraint(tcd);
		  		}
		  		else
		  		{
		  			TaskConstraintDate tcd = new TaskConstraintDate(end_date);
		  			t.addConstraint(tcd);
		  		}
		  	}
		    Intent resultIntent = new Intent("com.kangaroo.EDITTASK_RESULT");
			resultIntent.putExtra("task", t.serialize());
			setResult(RESULT_OK, resultIntent);
	  }
	  
	  /* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		updateResultData();
		return super.onKeyDown(keyCode, event);
	}
	  
	/**
	 * @brief method to build the eventmap for a given view object
	 * @param id id of the view
	 * @param type type of the view
	 * @param content content of the view
	 * @return array with id,type,content 
	 */
	private String[] buildEventMap(String id, String type, String content)
	  {
		  	String[] ret = new String[3];
	        ret[0] = id;
	        ret[1] = type;
	        ret[2] = content;
	        return ret;
	  }
	  
	  // callback method for intent result
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (data != null) {
				double lat = data.getExtras().getDouble("latitude");
				double lon = data.getExtras().getDouble("longitude");
				p = new Place(lat, lon);
				t.removeConstraint(actual_location);
				t.addConstraint(new TaskConstraintLocation(p));
			} else {
				Toast.makeText(this, "no position set! resultCode = " + resultCode, Toast.LENGTH_SHORT).show();
			}
			updateResultData();
			load();
		}
	  
	  // view creation code
	/**
	 * @brief method to generate EditText with Task title
	 * @param task
	 * @return LinearLayout with Textview and EditText
	 */
	private LinearLayout getTaskTitleLayout(Task task)
	{
		 LinearLayout ll_title = new LinearLayout(this);
		 TextView tv_label = new TextView(this);
		 tv_label.setText("Title:");
		 tv_label.setWidth(label_length);
       	 EditText edit_title = new EditText(this);
       	 edit_title.setWidth(content_length);
       	 edit_title.setId(generator.nextInt(Integer.MAX_VALUE));
	     edit_title.setText(t.getName());
	     active_views.add(buildEventMap(String.valueOf(edit_title.getId()), "edittext", "title"));
         ll_title.addView(tv_label);
	     ll_title.addView(edit_title);
	     return ll_title;
	}
	  
	  /**
	 * @param task
	 * @return
	 */
	 private LinearLayout getTaskDescriptionLayout(Task task)
	 {
		 LinearLayout ll_desc = new LinearLayout(this);
		 TextView tv_label = new TextView(this);
		 tv_label.setText("Title:");
		 tv_label.setWidth(label_length);
       	 EditText edit_description = new EditText(this);
       	 edit_description.setWidth(content_length);
       	 edit_description.setId(generator.nextInt(Integer.MAX_VALUE));
         edit_description.setText(t.getDescription());
         active_views.add(buildEventMap(String.valueOf(edit_description.getId()), "edittext", "description"));
         ll_desc.addView(tv_label);
         ll_desc.addView(edit_description);
         return ll_desc;
	 }	  
	  
	  /**
	 * @param tc
	 * @return
	 */
	private LinearLayout getAmenityConstraintLayout(TaskConstraintPOI tc)
	  {
		  LinearLayout ll_amenity = new LinearLayout(this);
		  ll_amenity.setVisibility(1);
		  ll_amenity.setOrientation(0);
		  TextView tv_label = new TextView(this);
		  tv_label.setText("Amenity: ");
		  tv_label.setWidth(label_length);
		  ll_amenity.addView(tv_label);
		  Spinner amenity_spinner = new Spinner(this);
		  amenity_spinner.setId(generator.nextInt(Integer.MAX_VALUE));
		  List<String> amenities = new ArrayList<String>(POICode.getPOICodeMap().keySet());
		  int pos = amenities.indexOf(tc.getText());
		  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, amenities);
		  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		  amenity_spinner.setAdapter(adapter);
		  amenity_spinner.setSelection(pos);
		  ll_amenity.addView(amenity_spinner);
		  active_views.add(buildEventMap(String.valueOf(amenity_spinner.getId()), "spinner", "amenity"));
		  hasAmenityConstraint = true;
		  return ll_amenity;
	  }
	  
	/**
	 * @brief method to generate DayTime start and endtime view
	 * @param tc TaskConstraintDaytime
	 * @return Array of size 2 with start and end view
	 */
	private LinearLayout[] getDayTimeConstraintLayout(TaskConstraintDayTime tc)
	  {
		LinearLayout[] ret = new LinearLayout[2];
		LinearLayout ll_start = new LinearLayout(this);
		LinearLayout ll_end = new LinearLayout(this);
		ll_start.setVisibility(1);
		ll_end.setVisibility(1);
		ll_start.setOrientation(0);
		ll_end.setOrientation(0);
		TextView tv_s = new TextView(this);
		tv_s.setText("Starttime:");
		tv_s.setWidth(label_length);
		TextView tv_e = new TextView(this);
		tv_e.setText("Endtime:");
		tv_e.setWidth(label_length);
		ll_start.addView(tv_s);
		ll_end.addView(tv_e);
		Date start = tc.getStartTime();
		Date end = tc.getEndTime();
		if (start != null && end != null)
		{
			TimePicker tp_start = new TimePicker(this);
			TimePicker tp_end = new TimePicker(this);
			tp_start.setIs24HourView(true);
			tp_end.setIs24HourView(true);
			tp_start.setCurrentHour(start.getHours());
			tp_start.setCurrentMinute(start.getMinutes());
			tp_end.setCurrentHour(end.getHours());
			tp_end.setCurrentMinute(end.getMinutes());
			ll_start.addView(tp_start);
			ll_end.addView(tp_end);
			tp_start.setId(generator.nextInt(Integer.MAX_VALUE));
			tp_end.setId(generator.nextInt(Integer.MAX_VALUE));
			active_views.add(buildEventMap(String.valueOf(tp_start.getId()), "timepicker", "starttime"));
			active_views.add(buildEventMap(String.valueOf(tp_end.getId()), "timepicker", "endtime"));
			ret[0] = ll_start;
			ret[1] = ll_end;
		}
		hasDayTimeConstraint = true;
		return ret;
	  }
	  
	  /**
	 * @param tc
	 * @return
	 */
	private LinearLayout[] getDateConstraintLayout(TaskConstraintDate tc)
	  {
		LinearLayout[] ret = new LinearLayout[2];
		LinearLayout ll_startdate = new LinearLayout(this);
		LinearLayout ll_enddate = new LinearLayout(this);
		ll_startdate.setVisibility(1);
		ll_enddate.setVisibility(1);
		ll_startdate.setOrientation(1);
		ll_enddate.setOrientation(1);
		TextView tv_start = new TextView(this);
		tv_start.setText("Startdate:");
		tv_start.setWidth(label_length);
		TextView tv_end = new TextView(this);
		tv_end.setText("Enddate:");
		tv_end.setWidth(label_length);
		ll_startdate.addView(tv_start);
		ll_enddate.addView(tv_end);
		// get constraint data
		Date startdate = tc.getStart();
		Date enddate = tc.getEnd();
		if (startdate != null && enddate != null)
		{
			DatePicker dp_start = new DatePicker(this);
			dp_start.updateDate(startdate.getYear() + 1900, startdate.getMonth(), startdate.getDay());
			dp_start.setId(generator.nextInt(Integer.MAX_VALUE));
			active_views.add(buildEventMap(String.valueOf(dp_start.getId()), "datepicker", "startdate"));
			ll_startdate.addView(dp_start);
			ret[0] = ll_startdate;
			
			DatePicker dp_end = new DatePicker(this);
			dp_end.updateDate(enddate.getYear() + 1900, enddate.getMonth(), enddate.getDay());
			dp_end.setId(generator.nextInt(Integer.MAX_VALUE));
			active_views.add(buildEventMap(String.valueOf(dp_end.getId()), "datepicker", "enddate"));
			ll_enddate.addView(dp_end);
			ret[1] = ll_enddate;
			
		}
		else if (startdate != null && enddate == null)
		{
			DatePicker dp_start = new DatePicker(this);
			dp_start.updateDate(startdate.getYear() + 1900, startdate.getMonth(), startdate.getDay());
			dp_start.setId(generator.nextInt(Integer.MAX_VALUE));
			active_views.add(buildEventMap(String.valueOf(dp_start.getId()), "datepicker", "startdate"));
			ll_startdate.addView(dp_start);
			ret[0] = ll_startdate;
		}
		else if (startdate == null && enddate != null)
		{						
			DatePicker dp_end = new DatePicker(this);
			dp_end.updateDate(enddate.getYear() + 1900, enddate.getMonth(), enddate.getDay());
			dp_end.setId(generator.nextInt(Integer.MAX_VALUE));
			active_views.add(buildEventMap(String.valueOf(dp_end.getId()), "datepicker", "enddate"));
			ll_enddate.addView(dp_end);
			ret[0] = ll_enddate;
		}
		hasDateConstraint = true;
		return ret;
	  }
	  
	  /**
	 * @param tc
	 * @return
	 */
	private LinearLayout getDurationConstraintLayout(TaskConstraintDuration tc)
	  {
		LinearLayout ll_duration = new LinearLayout(this);
		ll_duration.setVisibility(1);
		ll_duration.setOrientation(0);
		TextView tv_duration = new TextView(this);
		tv_duration.setText("Duration: ");
		tv_duration.setWidth(label_length);
		ll_duration.addView(tv_duration);
		int duration = tc.getDuration();
		EditText ed_duration = new EditText(this);
		ed_duration.setWidth(content_length);
		ed_duration.setText(Integer.toString(duration));
		ed_duration.setId(generator.nextInt(Integer.MAX_VALUE));
		active_views.add(buildEventMap(String.valueOf(ed_duration.getId()), "edittext", "duration"));
		ll_duration.addView(ed_duration);
		hasDurationConstraint = true;
		return ll_duration;  
	  }
	  
	/**
	 * @param tc
	 * @return
	 */
	private LinearLayout getLocationConstraintLayout(TaskConstraintLocation tc)
	  {
		p = tc.getPlace();
		LinearLayout ll_location = new LinearLayout(this);
		ll_location.setVisibility(1);
		ll_location.setOrientation(0);
		TextView tv_location = new TextView(this);
		tv_location.setText("Location: ");
		tv_location.setWidth(label_length);
		ll_location.addView(tv_location);
		actual_location = tc;
		Button ed_location = new Button(this);
		ed_location.setWidth(content_length);
		ed_location.setText(tc.getPlace().toString());
		ed_location.setId(generator.nextInt(Integer.MAX_VALUE));
		ed_location.setOnClickListener(LocationClickListener);
		ll_location.addView(ed_location);
		hasLocationConstraint = true;
		return ll_location;
	  }	  
	
	  /** menu methods */
	  /* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu)
	{
		  MenuInflater inflater = getMenuInflater();
		  inflater.inflate(R.menu.edittask_menu, menu);
		  return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		menu.clear();
		if (hasAmenityConstraint) menu.add(0, R.id.addAmenity, 0, "Remove Amenity"); 
		else menu.add(0, R.id.addAmenity, 0, "Add Amenity");
		if (hasDayTimeConstraint) menu.add(0, R.id.addDaytime, 0, "Remove Daytime"); 
		else menu.add(0, R.id.addDaytime, 0, "Add Daytime");
		if (hasDateConstraint) menu.add(0, R.id.addDate, 0, "Remove Date"); 
		else menu.add(0, R.id.addDate, 0, "Add Date");
		if (hasDurationConstraint) menu.add(0, R.id.addDuration, 0, "Remove Duration"); 
		else menu.add(0, R.id.addDuration, 0, "Add Duration");
		if (hasLocationConstraint) menu.add(0, R.id.addLocation, 0, "Remove Location"); 
		else menu.add(0, R.id.addLocation, 0, "Add Location");
		
		return super.onPrepareOptionsMenu(menu);
	}


	  /* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected (MenuItem item)
	{
		  updateResultData();
		  boolean ret = false;
		  switch (item.getItemId())
		  {
		  	case R.id.addAmenity:
		  		if (!hasAmenityConstraint)
		  		{
		  			TaskConstraintPOI tc1 = new TaskConstraintPOI(new POICode("shop#bakery"));
		  			t.addConstraint(tc1);
		  		}
		  		else
		  		{
			    	List<TaskConstraintInterface> l =  t.getConstraintsOfType("amenity");
			    	for (TaskConstraintInterface tci : l)
			    	{
			    		t.removeConstraint(tci);
			    	}
			    	hasAmenityConstraint = false;
		  		}
		  		ret = true;
		  		break;

		  	case R.id.addLocation:
		  		if (!hasLocationConstraint)
		  		{
		  			TaskConstraintLocation tc2 = new TaskConstraintLocation(new Place(0,0));
		  			t.addConstraint(tc2);
		  		}
		  		else
		  		{
			    	List<TaskConstraintInterface> l =  t.getConstraintsOfType("location");
			    	for (TaskConstraintInterface tci : l)
			    	{
			    		t.removeConstraint(tci);
			    	}
			    	hasLocationConstraint = false;
		  		}
		  		ret = true;
		  		break;

		  	case R.id.addDaytime:
		  		if (!hasDayTimeConstraint)
		  		{
			  		TaskConstraintDayTime tc3 = new TaskConstraintDayTime(new Date(), new Date());
			  		t.addConstraint(tc3);
		  		}
		  		else
		  		{
			    	List<TaskConstraintInterface> l =  t.getConstraintsOfType("daytime");
			    	for (TaskConstraintInterface tci : l)
			    	{
			    		t.removeConstraint(tci);
			    	}
			    	hasDayTimeConstraint = false;
		  		}
		  		ret = true;
		  		break;

		  	case R.id.addDate:
		  		if (!hasDateConstraint)
		  		{
			  		TaskConstraintDate tc4 = new TaskConstraintDate(new Date(), new Date());
			  		t.addConstraint(tc4);
		  		}
		  		else
		  		{
			    	List<TaskConstraintInterface> l =  t.getConstraintsOfType("date");
			    	for (TaskConstraintInterface tci : l)
			    	{
			    		t.removeConstraint(tci);
			    	}
			    	hasDateConstraint = false;
		  		}
		  		ret = true;
		  		break;

		  	case R.id.addDuration:
		  		if (!hasDurationConstraint)
		  		{
		  			TaskConstraintDuration tc5 = new TaskConstraintDuration(0);
		  			t.addConstraint(tc5);
		  		}
		  		else
		  		{
			    	List<TaskConstraintInterface> l =  t.getConstraintsOfType("duration");
			    	for (TaskConstraintInterface tci : l)
			    	{
			    		t.removeConstraint(tci);
			    	}
			    	hasDurationConstraint = false;
		  		}
		  		ret = true;
		  		break;
			default:
				System.out.println("ItemId: "+item.getItemId());
		  		ret = true;
		  		break;

		  }
		  load();
		  return ret;
	}
}
