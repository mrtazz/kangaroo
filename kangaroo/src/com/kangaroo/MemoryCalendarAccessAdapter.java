package com.kangaroo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.task.Task;

public class MemoryCalendarAccessAdapter implements CalendarAccessAdapter {

	
	private List<CalendarEvent> events = null;
	
	
	private Collection<Task> tasks = null;
	
	
	public MemoryCalendarAccessAdapter() {
		super();
		events = new ArrayList<CalendarEvent>();
		tasks = new ArrayList<Task>();
	}
	
	
	@Override
	public List<CalendarEvent> loadEvents() {
		return events;
	}

	
	@Override
	public Collection<Task> loadTasks() {
		return tasks;
	}


	@Override
	public void saveEvents(List<CalendarEvent> events) {
		this.events.clear();
		this.events.addAll(events);
	}

	
	@Override
	public void saveTasks(Collection<Task> tasks) {
		this.tasks.clear();
		this.tasks.addAll(tasks);
	}

	
	@Override
	public void setContext(Object context) {
		/* don't need to do anything */
	}

}
