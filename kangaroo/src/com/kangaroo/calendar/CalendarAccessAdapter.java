package com.kangaroo.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.kangaroo.task.Task;
import com.kangaroo.task.TaskManager;

public interface CalendarAccessAdapter {
	

	public Collection<Task> loadTasks();
	
	
	public void saveTasks(Collection<Task> tasks);
		
	
	public List<CalendarEvent> loadEvents();
	
	
	public void saveEvents(List<CalendarEvent> events);	


	public void setContext(Context context);
	
	
}
