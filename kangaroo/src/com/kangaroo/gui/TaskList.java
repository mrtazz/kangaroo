/**
 * 
 */
package com.kangaroo.gui;

import android.app.ExpandableListActivity;
import android.os.Bundle;

import com.android.kangaroo.R;

/**
 * @author mrtazz
 * @brief Activity to show an expandable listview of tasks
 *
 */
public class TaskList extends ExpandableListActivity {

	
	 @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dayplan);
	  }
}
