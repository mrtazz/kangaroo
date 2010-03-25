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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

import com.android.kangaroo.R;
import com.kangaroo.ActiveDayPlan;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapterAndroid;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintDuration;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskConstraintLocation;
import com.kangaroo.task.TaskConstraintPOI;
import com.kangaroo.task.TaskConstraintPendingTasks;

/**
 * @author mrtazz
 * @brief Activity to show an expandable listview of tasks
 *
 */
public class ActivityTaskList extends ExpandableListActivity {

	private SimpleExpandableListAdapter la;
	private com.kangaroo.ActiveDayPlan dp;
	private ArrayList<Task> taskslist;
	private long actual_task;
	private String[] childitems = new String[]{"tasklocation", "taskdescription",
											   "taskduration", "taskdate", "taskdaytime", 
												"taskpending", "taskpoi"};
	private int[] childlayout = new int[]{R.id.tasklocation, R.id.taskdescription,
										  R.id.taskduration, R.id.taskdate, R.id.taskdaytime,
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
	        
	        CalendarAccessAdapter caa = new CalendarAccessAdapterAndroid(this);
		 	caa.setContext(getApplicationContext());
		 	dp.setCalendarAccessAdapter(caa);

	        reload();
	        
	  }
	 
	 private void reload()
	 {
		 	
		 
		 	taskslist = (ArrayList<Task>)dp.getTasks();
		 	
		 	
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
						 m.put("taskdate", "Enddate: " + pad2(end.getDay()) + "/"
								 					   + pad2(end.getMonth()) + "/"
								 					   + (end.getYear()+1900));
					 }
					 else
					 {
						 m.put("taskdate","Startdate: "+ pad2(start.getDay()) + "/"
								 					   + pad2(start.getMonth()) + "/"
								 					   + (start.getYear()+1900) + "\n" +
								 		    "Endate: " + pad2(end.getDay()) + "/"
								 		    		   + pad2(end.getMonth()) + "/"
								 		    		   + (end.getYear()+1900));
					 }
				 }
				 else if (type.equals("daytime"))
				 {
					 TaskConstraintDayTime ta = (TaskConstraintDayTime)tc;
					 m.put("taskdaytime", "Daytime: " + pad2(ta.getStartTime().getHours()) + ":" + pad2(ta.getStartTime().getMinutes()) +
							 	   "->" + pad2(ta.getEndTime().getHours()) + ":" + pad2(ta.getEndTime().getMinutes()));
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
				 else if(type.equals("duration"))
				 {
					 TaskConstraintDuration td = (TaskConstraintDuration)tc;
					 m.put("taskduration", "Duration: " + td.getDuration() + " min");
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
		  actual_task = info.id;
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
			  Boolean ret = false;
			  switch (item.getItemId()) {
			    case MENU_DELETE:
			    	Task t = taskslist.get((int)actual_task);
			    	String s = t.getName();
			    	taskslist.remove(t);
			    	dp.setTasks(taskslist);
			    	
			    	toast = Toast.makeText(this,
				  					       "Task " + s + " deleted.",
					  				       Toast.LENGTH_SHORT);
			  	  	toast.show();
			  	  	ret = true;
			  	  	reload();
			  	  	break;
			    case MENU_EDIT:
				  // show the map
			      Task tt = taskslist.get((int)actual_task);
			      String ss = tt.serialize();
				  Intent intent = new Intent(this, ActivityEditTask.class);
				  intent.putExtra("task", ss);
				  intent.addCategory(Intent.CATEGORY_DEFAULT);
				  startActivityForResult(intent, 1);
				  
			      ret = true;
			      break;
			  }
			  return ret;
			}
		
		@Override
		  public void onActivityResult(int requestCode, int resultCode, Intent data) {
				if (data != null) {
					Task t = Task.deserialize((String)data.getExtras().get("task"));
					taskslist.remove((int)actual_task);
					taskslist.add((int)actual_task, t);
					dp.setTasks(taskslist);
				} else {
					Toast.makeText(this, "Did not get task back! resultCode = " + resultCode, Toast.LENGTH_SHORT).show();
				}
				reload();
			}
		
		  /** menu methods */
	  /* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu){

		  MenuInflater inflater = getMenuInflater();
		  inflater.inflate(R.menu.tasklist_menu, menu);
		  return true;

	  }

	  /* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected (MenuItem item){

		  switch (item.getItemId()){

		  	case R.id.addTask:
		  		newTask();
		  		return true;

			default:
				System.out.println("ItemId: "+item.getItemId());
				return true;

		  }

	}
		
		
		/**
		 * @brief method to pad time to a length of 2
		 * @param i time as int
		 * @return padded time as string
		 */
		private String pad2(int i)
		{
			String s = Integer.toString(i);
			s= (s.length() < 2) ? ("0"+s) : (s);
			return s;
		}
		
		private void newTask()
		{
			Task t = new Task();
	  		t.setName("New Task");
	  		t.setDescription("New Description");
	  		t.addConstraint(new TaskConstraintDuration(0));
	  		taskslist.add(t);
	  		dp.setTasks(taskslist);
	  		reload();
		}
}
