/**
 * 
 */
package com.kangaroo.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.kangaroo.R;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskConstraintPOI;

/**
 * @author mrtazz
 *
 */
public class ActivityEditTask extends Activity {
	
	  private final int label_length = 90;
	  private final int content_length = 200;
	  private Task t;
	  private ArrayList<String[]> active_views;
	  private Random generator;
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.edittask);
	        String s = (String)getIntent().getExtras().get("task");
	        t = Task.deserialize(s);
	        active_views = new ArrayList<String[]>();
	        generator = new Random();
	        
	        updateResultData();
	        
	        // set title view
	        EditText edit_title = (EditText)findViewById(R.id.edittitle);
	        edit_title.setText(t.getName());
	        active_views.add(buildEventMap(String.valueOf(edit_title.getId()),
	        								"edittext", "title"));
	        
	        // set description view
	        EditText edit_description = (EditText)findViewById(R.id.editdescription);
	        edit_description.setText(t.getDescription());
	        active_views.add(buildEventMap(String.valueOf(edit_description.getId()), "edittext", "description"));

			LinearLayout main = (LinearLayout)findViewById(R.id.mainedittasklayout);
			
	        TaskConstraintInterface[] constraints = t.getConstraints();
			for (TaskConstraintInterface tc : constraints)
			{
				String type = tc.getType();
				if (type.equals("amenity")) 
				{
				  TaskConstraintPOI ta = (TaskConstraintPOI)tc;
				  LinearLayout ll_amenity = new LinearLayout(this);
				  ll_amenity.setVisibility(1);
				  ll_amenity.setOrientation(0);
				  TextView tv_label = new TextView(this);
				  tv_label.setText("Amenity: ");
				  tv_label.setWidth(label_length);
				  ll_amenity.addView(tv_label);
				  EditText ev_content = new EditText(this);
				  ev_content.setWidth(content_length);
				  ev_content.setText(ta.getText().split("#")[1]);
				  ev_content.setId(generator.nextInt());
				  ll_amenity.addView(ev_content);
				  active_views.add(buildEventMap(String.valueOf(ev_content.getId()), "edittext", "amenity"));
				  
				  main.addView(ll_amenity);				  
				}
				else if (type.equals("daytime"))
				{
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
					TaskConstraintDayTime ta = (TaskConstraintDayTime)tc;
					Date start = ta.getStartTime();
					Date end = ta.getEndTime();
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
						main.addView(ll_start);
						main.addView(ll_end);
					}
					else if (start != null && end == null)
					{
						TimePicker tp_start = new TimePicker(this);
						tp_start.setIs24HourView(true);
						tp_start.setCurrentHour(start.getHours());
						tp_start.setCurrentMinute(start.getMinutes());
						tp_start.setId(generator.nextInt(Integer.MAX_VALUE));
						ll_start.addView(tp_start);
						active_views.add(buildEventMap(String.valueOf(tp_start.getId()), "timepicker", "starttime"));			
						main.addView(ll_start);
					}
					else if (start == null && end != null)
					{
						TimePicker tp_end = new TimePicker(this);
						tp_end.setIs24HourView(true);
						tp_end.setCurrentHour(end.getHours());
						tp_end.setCurrentMinute(end.getMinutes());
						tp_end.setId(generator.nextInt(Integer.MAX_VALUE));
						ll_end.addView(tp_end);
						active_views.add(buildEventMap(String.valueOf(tp_end.getId()), "timepicker", "endtime"));
						main.addView(ll_end);
					}
					
				else if (type.equals("date"))
				{
					LinearLayout ll_startdate = new LinearLayout(this);
					LinearLayout ll_enddate = new LinearLayout(this);
					ll_start.setVisibility(1);
					ll_end.setVisibility(1);
					ll_start.setOrientation(0);
					ll_end.setOrientation(0);
					TextView tv_start = new TextView(this);
					tv_start.setText("Starttime:");
					tv_start.setWidth(label_length);
					TextView tv_end = new TextView(this);
					tv_end.setText("Endtime:");
					tv_end.setWidth(label_length);
					ll_startdate.addView(tv_start);
					ll_enddate.addView(tv_end);
					// get constraint data
					TaskConstraintDate td = (TaskConstraintDate)tc;
					Date startdate = td.getStart();
					Date enddate = td.getEnd();
					if (startdate != null && enddate != null)
					{
						DatePicker dp_start = new DatePicker(this);
						dp_start.updateDate(dp_start.getYear(), dp_start.getMonth(), dp_start.getDayOfMonth());
						dp_start.setId(generator.nextInt(Integer.MAX_VALUE));
						ll_startdate.addView(dp_start);
						main.addView(ll_startdate);
						
						DatePicker dp_end = new DatePicker(this);
						dp_end.updateDate(dp_end.getYear(), dp_end.getMonth(), dp_end.getDayOfMonth());
						dp_end.setId(generator.nextInt(Integer.MAX_VALUE));
						ll_enddate.addView(dp_end);
						main.addView(ll_enddate);
						
					}
					else if (startdate != null && enddate == null)
					{
						
					}
					else if (startdate == null && enddate != null)
					{
						
					}
				
				}
			}	
		 }    
	  }
	  
	  
	  private void updateResultData()
	  {
		  	View v;
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
		  			else
		  			{
		  				
		  			}
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
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	  
	  private String[] buildEventMap(String id, String type, String content)
	  {
		  	String[] ret = new String[3];
	        ret[0] = id;
	        ret[1] = type;
	        ret[2] = content;
	        return ret;
	  }
	  

}
