package com.kangaroo;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.task.Task;

public interface DayPlan {

	
	public List<CalendarEvent> getEvents();
	
	
	public void setEvents(List<CalendarEvent> events);
	
	
	public CalendarEvent getNextEvent(Date now);
	
	
	public Collection<Task> getTasks();
	
	
	public void setTasks(Collection<Task> tasks);
	
	
}
