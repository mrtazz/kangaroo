package com.kangaroo.gui;

import java.util.Date;

import com.android.kangaroo.R;
import com.kangaroo.ActiveDayPlan;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapterAndroid;
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintDuration;
import com.kangaroo.task.TaskConstraintPOI;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.routing.AllStreetVehicle;
import com.mobiletsm.routing.MobileTSMRoutingEngine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityConfiguration extends Activity
{

	private SharedPreferences prefsPrivate = null;
	private String preferencesName = "kangaroo_config";
	
	private CheckBox checkEnable;
	private EditText editBackgroundTime;
	private EditText editBackgroundDistance;
	private EditText editBackgroundDistanceTime;
	private EditText editCalendar;
	private EditText editOptimizer;
	private EditText editMap;
	private Button buttonSafe;
	private Button buttonReset;
	
	private Button eventButton;
	private Button taskButton;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.configuration);
	        
	        //get key/value store
	        prefsPrivate = getSharedPreferences(preferencesName, MODE_PRIVATE);
	        
	        //get instances for all the elements here
	        checkEnable = (CheckBox)findViewById(R.id.checkEnable);
	        editBackgroundTime = (EditText)findViewById(R.id.editBackgroundTime);
	        editBackgroundDistance = (EditText)findViewById(R.id.editBackgroundDistance);
	        editBackgroundDistanceTime = (EditText)findViewById(R.id.editBackgroundDistanceTime);
	        editCalendar = (EditText)findViewById(R.id.editCalendar);
	        editOptimizer = (EditText)findViewById(R.id.editOptimizer);
	        editMap = (EditText)findViewById(R.id.editMap);
	        buttonSafe = (Button)findViewById(R.id.buttonSafe);
	        buttonReset = (Button)findViewById(R.id.buttonReset);
	        
	        buttonSafe.setOnClickListener(SafeClickListener);
	        buttonReset.setOnClickListener(ResetClickListener);
	        
	        eventButton = (Button)findViewById(R.id.buttonEvent);
	        eventButton.setOnClickListener(EventClickListener);
	        taskButton = (Button)findViewById(R.id.buttonTask);
	        taskButton.setOnClickListener(TaskClickListener);
	        
	        //load values
	        load();
	        
	  }
	
	  
	  private void load()
	  {
		  System.out.println("ActivityConfiguration.load() called");
		  checkEnable.setChecked(prefsPrivate.getBoolean("background_call_enable" , true));
		  editBackgroundTime.setText(String.valueOf(prefsPrivate.getInt("background_call_intervall", 60)));
		  editBackgroundDistance.setText(String.valueOf(prefsPrivate.getInt("background_call_position", 100)));
		  editBackgroundDistanceTime.setText(String.valueOf(prefsPrivate.getInt("background_call_time_difference", 60)));
		  editCalendar.setText(prefsPrivate.getString("calendar_in_use", "kangaroo@lordofhosts.de"));
		  editOptimizer.setText(prefsPrivate.getString("optimizer_in_use", "---"));
		  editMap.setText(prefsPrivate.getString("tsm_file_path", "/sdcard/map-fr.db"));
	  }
	  
	  private void safe()
	  {
		  System.out.println("ActivityConfiguration.safe() called");
		  Editor prefsPrivateEditor = prefsPrivate.edit();
		  prefsPrivateEditor.putBoolean("background_call_enable", checkEnable.isChecked());
		  prefsPrivateEditor.putInt("background_call_intervall", Integer.parseInt(editBackgroundTime.getText().toString()));
		  prefsPrivateEditor.putInt("background_call_position", Integer.parseInt(editBackgroundDistance.getText().toString()));
		  prefsPrivateEditor.putInt("background_call_time_difference", Integer.parseInt(editBackgroundDistanceTime.getText().toString()));
		  prefsPrivateEditor.putString("calendar_in_use", editCalendar.getText().toString());
		  prefsPrivateEditor.putString("optimizer_in_use", editOptimizer.getText().toString());
		  prefsPrivateEditor.putString("tsm_file_path", editMap.getText().toString());
		  
		  prefsPrivateEditor.commit();
		  
	  }
	  
	    private OnClickListener SafeClickListener = new OnClickListener() 
	    {
	        public void onClick(View v) 
	        {
	        	safe();
	        }
	    };
	    
	    private OnClickListener ResetClickListener = new OnClickListener() 
	    {
	        public void onClick(View v) 
	        {
	        	load();
	        }
	    };
	    
	    private OnClickListener EventClickListener = new OnClickListener() 
	    {
	        public void onClick(View v) 
	        {
	        	fill_events();
	        }
	    };
	    
	    private OnClickListener TaskClickListener = new OnClickListener() 
	    {
	        public void onClick(View v) 
	        {
	        	fill_tasks();
	        }
	    };
	    
		private void fill_events()
		{
			Date cd = new Date();
			ActiveDayPlan currentDayPlan = new ActiveDayPlan();
	    	currentDayPlan.setRoutingEngine(MobileTSMRoutingEngine.getInstance(this));
	    	
	        CalendarAccessAdapter caa = new CalendarAccessAdapterAndroid(this);
	    	//CalendarAccessAdapter caa = new CalendarAccessAdapterMemory();
			currentDayPlan.setCalendarAccessAdapter(caa);
	        CalendarEvent event1 = new CalendarEvent();
	        event1.setStartDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 19, 20));
	        event1.setEndDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 19, 30));
	        event1.setLocationLatitude(48.00);
	        event1.setLocationLongitude(7.852);
	        event1.setTitle("BAM Title1");

	        CalendarEvent event2 = new CalendarEvent();
	        event2.setStartDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 20, 45));
	        event2.setEndDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 21, 00));
	        event2.setLocationLatitude(48.000);
	        event2.setLocationLongitude(7.852);
	        event2.setTitle("BAM Title2");
	        
	        CalendarEvent event3 = new CalendarEvent();
	        event3.setStartDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 21, 20));
	        event3.setEndDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 21, 40));
	        event3.setLocationLatitude(47.987);
	        event3.setLocationLongitude(7.852);
	        event3.setTitle("BAM Title3");
	        
	        CalendarEvent event4 = new CalendarEvent();
	        event4.setStartDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 21, 45));
	        event4.setEndDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 21, 50));
	        event4.setLocationLatitude(47.987);
	        event4.setLocationLongitude(7.852);        
	        event4.setTitle("BAM Title4");
	        
	        CalendarEvent event5 = new CalendarEvent();
	        event5.setStartDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 22, 0));
	        event5.setEndDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 22, 40));
	        event5.setLocationLatitude(47.983);
	        event5.setLocationLongitude(7.852);        
	        event5.setTitle("BAM Title5");
	        
	        CalendarEvent event6 = new CalendarEvent();
	        event6.setStartDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 23, 0));
	        event6.setEndDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 23, 40));
	        event6.setLocationLatitude(48.983);
	        event6.setLocationLongitude(7.852);  
	        event6.setTitle("BAM Title6");
	        
	        CalendarEvent event7 = new CalendarEvent();
	        event7.setStartDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 23, 45));
	        event7.setEndDate(new Date(cd.getYear(), cd.getMonth(), cd.getDate(), 23, 50));
	        event7.setLocationLatitude(47.983);
	        event7.setLocationLongitude(7.852); 
	        event7.setTitle("BAM Title7");
	        
	        currentDayPlan.addEvent(event1);
	        currentDayPlan.addEvent(event2);
	        currentDayPlan.addEvent(event3);
	        currentDayPlan.addEvent(event4);
	        currentDayPlan.addEvent(event5);
	        currentDayPlan.addEvent(event6);
	        currentDayPlan.addEvent(event7); 
	        
	        System.out.println("add Events called!");
		}
		
		private void fill_tasks()
		{
	        /* add and create some tasks */
			Date currentDate = new Date();
			ActiveDayPlan currentDayPlan = new ActiveDayPlan();
	    	currentDayPlan.setRoutingEngine(MobileTSMRoutingEngine.getInstance(this));
	    	
	        CalendarAccessAdapter caa = new CalendarAccessAdapterAndroid(this);
	    	//CalendarAccessAdapter caa = new CalendarAccessAdapterMemory();
			currentDayPlan.setCalendarAccessAdapter(caa);
			
			Task task1 = new Task();
			task1.setName("Schnell was essen");
			task1.addConstraint(new TaskConstraintDuration(5));
			task1.addConstraint(new TaskConstraintPOI(new POICode(POICode.AMENITY_FAST_FOOD)));
			task1.addConstraint(new TaskConstraintDayTime(new Date(0, 0, 0, 19, 00), new Date(0, 0, 0, 20, 01)));
			
			Task task2 = new Task();
			task2.setName("Frisšr");
			task2.addConstraint(new TaskConstraintDuration(3));
			task2.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_HAIRDRESSER)));
			task2.addConstraint(new TaskConstraintDayTime(18, 00, 23, 00));
			
			Task task3 = new Task();
			task3.setName("Oma anrufen");
			task3.addConstraint(new TaskConstraintDuration(3));
			task3.addConstraint(new TaskConstraintDate(new Date(currentDate.getYear(), currentDate.getMonth(), currentDate.getDate()+3)));
			
			Task task4 = new Task();
			task4.setName("Brštchen kaufen");
			task4.addConstraint(new TaskConstraintDuration(3));
			task4.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_BAKERY)));		
			//task4.addConstraint(new TaskConstraintDayTime(18, 00, 19, 10));
			
			Task task5 = new Task();
			task5.setName("Blumen kaufen");
			task5.addConstraint(new TaskConstraintDuration(30));
			task5.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_FLORIST)));	
			task5.addConstraint(new TaskConstraintDayTime(18, 00, 23, 00));

			Task task6 = new Task();
			task6.setName("Buch kaufen");
			task6.addConstraint(new TaskConstraintDuration(30));
			task6.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_BOOKS)));	
			task6.addConstraint(new TaskConstraintDayTime(18, 00, 23, 00));
			
			currentDayPlan.addTask(task1);
			currentDayPlan.addTask(task2);
			currentDayPlan.addTask(task3);
			currentDayPlan.addTask(task4);        
			currentDayPlan.addTask(task5);		
			currentDayPlan.addTask(task6);	
			
			System.out.println("add Tasks called!");
		}
}
