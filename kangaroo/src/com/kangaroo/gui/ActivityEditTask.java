/**
 * 
 */
package com.kangaroo.gui;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.kangaroo.R;
import com.kangaroo.task.Task;
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
	
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.edittask);
	        String s = (String)getIntent().getExtras().get("task");
	        Task t = Task.deserialize(s);
	        
	        EditText edit_title = (EditText)findViewById(R.id.edittitle);
	        EditText edit_description = (EditText)findViewById(R.id.editdescription);
	        edit_title.setText(t.getName());
	        edit_description.setText(t.getDescription());

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
				  ev_content.setText(ta.getText());
				  ev_content.setWidth(content_length);
				  ll_amenity.addView(ev_content);
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
						main.addView(ll_start);
						main.addView(ll_end);
					}
					else if (start != null && end == null)
					{
						TimePicker tp_start = new TimePicker(this);
						tp_start.setIs24HourView(true);
						tp_start.setCurrentHour(start.getHours());
						tp_start.setCurrentMinute(start.getMinutes());
						ll_start.addView(tp_start);			
						main.addView(ll_start);
					}
					else if (start == null && end != null)
					{
						TimePicker tp_end = new TimePicker(this);
						tp_end.setIs24HourView(true);
						tp_end.setCurrentHour(end.getHours());
						tp_end.setCurrentMinute(end.getMinutes());
						ll_end.addView(tp_end);
						main.addView(ll_end);
					}
					else
					{
						
					}
				}
			}
	        
	  }
	  

}
