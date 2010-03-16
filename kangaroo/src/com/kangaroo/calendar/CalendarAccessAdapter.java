package com.kangaroo.calendar;

import java.util.Collection;
import java.util.List;

import com.kangaroo.task.Task;


public interface CalendarAccessAdapter {
	

	public Collection<Task> loadTasks();
	
	
	public void saveTasks(Collection<Task> tasks);
		
	
	public List<CalendarEvent> loadEvents();
	
	
	public void saveEvents(List<CalendarEvent> events);	


	public void setContext(Object context);
	
	
}
