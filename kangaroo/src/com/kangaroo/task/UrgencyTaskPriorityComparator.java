package com.kangaroo.task;

import java.util.Comparator;
import java.util.Date;

public class UrgencyTaskPriorityComparator implements Comparator<Task> {

	@Override
	public int compare(Task task1, Task task2) {

		TaskConstraintHelper helper1 = new TaskConstraintHelper(task1);
		TaskConstraintHelper helper2 = new TaskConstraintHelper(task2);
		
		Date endDate1 = helper1.getEndDate();
		Date endDate2 = helper2.getEndDate();
		
		if (endDate1 != null && endDate2 != null) {
			if (endDate1.before(endDate2)) {
				return -1;
			} else if (endDate1.after(endDate2)) {
				return 1;
			}
		} else if (endDate1 != null && endDate2 == null) {
			return -1;
		} else if (endDate1 == null && endDate2 != null) {
			return 1;
		}
		
		Date endDaytime1 = helper1.getEndDaytime();
		Date endDaytime2 = helper2.getEndDaytime();
		
		if (endDaytime1 != null && endDaytime2 != null) {
			int compare = TaskConstraintHelper.compareDayTime(endDaytime1, endDaytime2);
			if (compare != 0) {
				return compare;
			}
		} else if (endDaytime1 != null && endDaytime2 == null) {
			return -1;
		} else if (endDaytime1 == null && endDaytime2 != null) {
			return 1;
		}
		
		return 0;
	}

}
