/**
 * 
 */
package com.kangaroo.gui;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.android.kangaroo.R;

/**
 * @author mrtazz
 *
 */
public class ActivityMainWindow extends TabActivity {
	
	 private TabHost myTabHost;

	 /** Called when the activity is first created.*/
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mainwindow);
	    myTabHost = getTabHost();
	    // get dayplan tab
	    TabSpec tabDayPlan = myTabHost.newTabSpec("Dayplan");
	    tabDayPlan.setIndicator("Dayplan");
	    Context ctx = this.getApplicationContext();
	    Intent intentDayplan = new Intent(ctx, ActivityDayPlan.class);
	    tabDayPlan.setContent(intentDayplan);
	    myTabHost.addTab(tabDayPlan);
	    // get tasklist tab
	    TabSpec tabTasklist = myTabHost.newTabSpec("Tasklist");
	    tabTasklist.setIndicator("Tasklist");
	    Intent intentTasklist = new Intent(ctx, ActivityTaskList.class);
	    tabTasklist.setContent(intentTasklist);
	    myTabHost.addTab(tabTasklist);
	    // get debug tab
	    // get tasklist tab
	    TabSpec tabDebug = myTabHost.newTabSpec("Debug");
	    tabDebug.setIndicator("Debug");
	    Intent intentDebug = new Intent(ctx, ActivityBuildPlan.class);
	    tabDebug.setContent(intentDebug);
	    myTabHost.addTab(tabDebug);
	    
	    // set tab view
	    myTabHost.setCurrentTab(0);
	 }

}
