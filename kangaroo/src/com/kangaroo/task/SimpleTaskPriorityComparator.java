package com.kangaroo.task;

import java.util.Comparator;
import java.util.List;


public class SimpleTaskPriorityComparator implements Comparator<Task> {

	@Override
	public int compare(Task task1, Task task2) {
		
		int result = 0;
		
		/* TODO: account for priority constraints */
		
		List<TaskConstraintInterface> dateConstraints1 =
			task1.getConstraintsOfType(TaskConstraintInterface.TYPE_DATE);
		List<TaskConstraintInterface> dateConstraints2 =
			task2.getConstraintsOfType(TaskConstraintInterface.TYPE_DATE);
		List<TaskConstraintInterface> daytimeConstraints1 =
			task1.getConstraintsOfType(TaskConstraintInterface.TYPE_DAYTIME);
		List<TaskConstraintInterface> daytimeConstraints2 =
			task2.getConstraintsOfType(TaskConstraintInterface.TYPE_DAYTIME);
		

		if (dateConstraints1.size() > 0 || dateConstraints2.size() > 0 ||
				daytimeConstraints1.size() > 0 || daytimeConstraints2.size() > 0) {
	
			if (dateConstraints1.size() > 0 && dateConstraints2.size() == 0) {
				result = 1;
			} else if (dateConstraints1.size() == 0 && dateConstraints2.size() > 0) {
				result = -1;
			} else {
				if (daytimeConstraints1.size() > 0 && daytimeConstraints2.size() == 0) {
					result = 1;
				} else {
					result = -1;
				}
			}
			
		}		
		
		return result * (-1);
	}

}
