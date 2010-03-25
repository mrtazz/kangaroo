package com.kangaroo.gui;

import com.android.kangaroo.R;
import com.kangaroo.ActiveDayPlan;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapterAndroid;

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
}
