package com.kangaroo.task;

import java.util.Comparator;
import java.util.Date;

import com.mobiletsm.routing.Place;

public interface TaskPriorityComparator extends Comparator<Task> {
	
	public void setHere(Place here);
	
	public void setNow(Date now);

}
