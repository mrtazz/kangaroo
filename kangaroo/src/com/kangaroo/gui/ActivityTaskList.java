/**
 * 
 */
package com.kangaroo.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.android.kangaroo.R;
import com.kangaroo.ActiveDayPlan;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskConstraintLocation;
import com.kangaroo.task.TaskConstraintPOI;
import com.kangaroo.task.TaskConstraintPendingTasks;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.routing.Place;

/**
 * @author mrtazz
 * @brief Activity to show an expandable listview of tasks
 *
 */
public class ActivityTaskList extends ExpandableListActivity {

	private SimpleExpandableListAdapter la;
	private com.kangaroo.DayPlan dp;
	private ArrayList<Task> taskslist;
	private String[] childitems = new String[]{"tasklocation", "taskdescription",
												"taskdate", "taskdaytime", 
												"taskpending", "taskpoi"};
	private int[] childlayout = new int[]{R.id.tasklocation, R.id.taskdescription,
									      R.id.taskdate, R.id.taskdaytime,
									      R.id.taskpending, R.id.taskpoi};

	  // menu item ids
	  private final int MENU_DELETE = 0;
	  private final int MENU_EDIT = 1;
	
	 @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tasklist);
	        registerForContextMenu(getExpandableListView());
	        dp = new ActiveDayPlan();
	        //setUpTasks();
	        reload();
	        
	  }
	 
	 private void reload()
	 {
		 	
	        la = new SimpleExpandableListAdapter(this,
	        									buildGroupEntries(), 
	        									R.layout.taskfirstlevel,
	        									new String[]{"tasktitle"},
	        									new int[] {R.id.tasktitle},
	        									buildChildEntries(),
	        									R.layout.tasksecondlevel,
	        									childitems,
	        									childlayout);
	        setListAdapter(la);
	 }
	 
	 /**
	  * @brief method to build the entries for the first level
	  * in the expandable list
	  * @return return ArrayList of entries
	  */
	private ArrayList<HashMap<String, String>> buildGroupEntries()
	 {
		 ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String,String>>();
		 for ( int i=0; i < taskslist.size(); i++)
		 {
			HashMap<String, String> m = new HashMap<String, String>();
			m.put("tasktitle", taskslist.get(i).getName());
			ret.add(m);
		 }
		 return ret;
	 }
	
	/**
	 * @brief method to build the entries for the child level
	 * in the expandable list
	 * @return return ArrayList of ArrayList of entries
	 */
	 private ArrayList<ArrayList<HashMap<String, String>>> buildChildEntries()
	 {
		 /** array list of list of map to return */
		 ArrayList<ArrayList<HashMap<String, String>>> ret = 
			 new ArrayList<ArrayList<HashMap<String,String>>>();
		 /** get constraints for every task */
		 for (int i=0; i < taskslist.size(); i++)
		 {
			 /** list of maps for child items */
			 ArrayList<HashMap<String, String>> seclist =
				 				new ArrayList<HashMap<String,String>>();
			 /** hashmap containing the actual values to show */
			 HashMap<String, String> m = new HashMap<String, String>();
			 // preset hashmap
			 for (String s: childitems)
			 {
				m.put(s,""); 
			 }
			 Task t = taskslist.get(i);
			 TaskConstraintInterface[] constraints = t.getConstraints();
			 for (TaskConstraintInterface tc : constraints)
			 {
				 String type = tc.getType();
				 if (type.equals("amenity")) 
				 {
					 TaskConstraintPOI ta = (TaskConstraintPOI)tc;
					 m.put("taskpoi",ta.getText());
				 }
				 else if (type.equals("date"))
				 {
					 TaskConstraintDate ta = (TaskConstraintDate)tc;
					 Date start = ta.getStart();
					 Date end = ta.getEnd();
					 if (start == null && end == null)
					 {
						 m.put("taskdate", "no time constraints given.");
					 }
					 else if (start == null)
					 {
						 m.put("taskdate", "Enddate: " + end.toLocaleString());
					 }
					 else
					 {
						 m.put("taskdate","Startdate: "+ start.toLocaleString() + 
								 		  "Endate: " + end.toLocaleString());
					 }
				 }
				 else if (type.equals("daytime"))
				 {
					 TaskConstraintDayTime ta = (TaskConstraintDayTime)tc;
					 m.put("taskdaytime", ta.getStartTime().toLocaleString() + "->" + ta.getEndTime().toLocaleString());						
				 }
				 else if (type.equals("location"))
				 {
					 TaskConstraintLocation ta = (TaskConstraintLocation)tc;
					 m.put("tasklocation",ta.getPlace().toString());
				 }
				 else if (type.equals("pending"))
				 {
					 TaskConstraintPendingTasks ta = (TaskConstraintPendingTasks)tc;
					 m.put("taskpoi",ta.getTaskName());
				 }
				 else
				 {
					 
				 }
			 }
			 m.put("taskdescription", t.getDescription());
			 seclist.add(m);
			 ret.add(seclist);
		 }
		 
		 return ret;
	 }
	 
	 
	 private void setUpTasks()
	 {
		 	taskslist = new ArrayList<Task>();
		 	Task myTask1 = new Task();
        	myTask1.setName("Essen kaufen");
        	myTask1.setDescription("essen halt");
        	myTask1.addConstraint(new TaskConstraintLocation(new Place(2,3)));
        	myTask1.addConstraint(new TaskConstraintPOI(new POICode(POICode.AMENITY_ARCHITECT_OFFICE)));
        	myTask1.addConstraint(new TaskConstraintDate(new Date(110,2,17)));
        	Task myTask2 = new Task();
        	myTask2.setName("Geld holen");
        	myTask2.setDescription("fuer mehr essen");
        	myTask2.addConstraint(new TaskConstraintLocation(new Place(3,4)));
        	myTask2.addConstraint(new TaskConstraintPOI(new POICode(POICode.AMENITY_ARTS_CENTRE)));
        	myTask2.addConstraint(new TaskConstraintDate(new Date(110,2,12)));
        	Task myTask3 = new Task();
        	myTask3.setName("Kleider kaufen");
        	myTask3.setDescription("alte kleider zu klein");
        	myTask3.addConstraint(new TaskConstraintLocation(new Place(4,5)));
        	myTask3.addConstraint(new TaskConstraintPOI(new POICode(POICode.AMENITY_ATM)));
        	myTask3.addConstraint(new TaskConstraintDate(new Date(110,2,23)));
	        
	        taskslist.add(myTask1);
	        taskslist.add(myTask2);
	        taskslist.add(myTask3);
	 }
	 
	 // context menu methods
	 /* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	 public void onCreateContextMenu(ContextMenu menu,
			  						  View v,
			  						  ContextMenuInfo menuInfo)
	 {
		  ExpandableListContextMenuInfo info;
		  try {
		      info = (ExpandableListContextMenuInfo) menuInfo;
		  } catch (ClassCastException e) {
		      //Log.e(TAG, "bad menuInfo", e);
		      return;
		  }
		  // add menu items
		  menu.add(0, MENU_DELETE, 0, R.string.delete);
		  menu.add(0, MENU_EDIT, 0, R.string.edit_task);
	  }
	  /** when press-hold option selected */
	  @Override
	  public boolean onContextItemSelected(MenuItem item) {
	    return applyMenuChoice(item) || super.onContextItemSelected(item);
	  }
	  
	  /**
	   * @brief method to distinguish context menu choices 
	   * @param item MenuItem
	   * @return true if choice was found, false otherwise
	   */
		private boolean applyMenuChoice(MenuItem item) {
			  Toast toast;
			  switch (item.getItemId()) {
			    case MENU_DELETE:
				  toast = Toast.makeText(this,
				  					     "Menu item DELETE clicked",
					  				     Toast.LENGTH_SHORT);
			  	  toast.show();
			      return true;
			    case MENU_EDIT:
				  // show the map
				  Intent intent = new Intent(this, ActivityEditTask.class);
				  intent.addCategory(Intent.CATEGORY_DEFAULT);
				  startActivityForResult(intent, 1);
				  
			      return true;
			  }
			  return false;
			}
}
