/**
 * 
 */
package com.kangaroo.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.kangaroo.R;
import com.kangaroo.task.Task;

/**
 * @author mrtazz
 *
 */
public class ActivityEditTask extends Activity {
	
	
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.edittask);
	        String s = (String)getIntent().getExtras().get("task");
	        Task t = Task.deserialize(s);
	  }
	  

}
