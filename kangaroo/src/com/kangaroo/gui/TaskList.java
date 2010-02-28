/**
 * 
 */
package com.kangaroo.gui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.SimpleExpandableListAdapter;

import com.android.kangaroo.R;
import com.kangaroo.task.Task;

/**
 * @author mrtazz
 * @brief Activity to show an expandable listview of tasks
 *
 */
public class TaskList extends ExpandableListActivity {

	private SimpleExpandableListAdapter la;
	private ArrayList<Task> taskslist;
	
	 @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tasklist);
	        
	        setUpTasks();
	        la = new SimpleExpandableListAdapter(this,
	        									buildGroupEntries(), 
	        									R.layout.taskfirstlevel,
	        									new String[]{"tasktitle"},
	        									new int[] {R.id.tasktitle},
	        									buildChildEntries(),
	        									R.layout.tasksecondlevel,
	        									new String[]{"tasklocation", "taskdescription"},
	        									new int[]{R.id.tasklocation, R.id.taskdescription});
	        setListAdapter(la);
	        
	  }
	 
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
	 
	 private ArrayList<ArrayList<HashMap<String, String>>> buildChildEntries()
	 {
		 ArrayList<ArrayList<HashMap<String, String>>> ret = 
			 new ArrayList<ArrayList<HashMap<String,String>>>();
		 for (int i=0; i < taskslist.size(); i++)
		 {
			 ArrayList<HashMap<String, String>> seclist =
				 				new ArrayList<HashMap<String,String>>();
			 HashMap<String, String> m = new HashMap<String, String>();
			 m.put("tasklocation","somelocation");
			 m.put("taskdescription", "somedescription");
			 seclist.add(m);
			 ret.add(seclist);
		 }
		 
		 return ret;
	 }
	 
	 
	 private void setUpTasks()
	 {
		 	taskslist = new ArrayList<Task>();
	        Task task1 = new Task();
	        task1.setName("Essen kaufen");
	        Task task2 = new Task();
	        task2.setName("Fische kaufen");
	        Task task3 = new Task();
	        task3.setName("DVDs leihen");
	        
	        taskslist.add(task1);
	        taskslist.add(task2);
	        taskslist.add(task3);
	 }
}
