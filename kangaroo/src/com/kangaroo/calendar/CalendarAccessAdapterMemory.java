package com.kangaroo.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.kangaroo.task.Task;

public class CalendarAccessAdapterMemory implements CalendarAccessAdapter {

	
	private List<CalendarEvent> events = null;
	
	
	private Collection<Task> tasks = null;
	
	
	public CalendarAccessAdapterMemory() {
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
