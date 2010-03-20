package com.kangaroo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.kangaroo.calendar.conflicts.CalendarEventConflict;

public class DayPlanConsistency {

	/**
	 * set of calendar conflicts
	 */
	private Set<CalendarEventConflict> conflicts;
	
	
	public DayPlanConsistency() {
		super();
		this.conflicts = new HashSet<CalendarEventConflict>();
	}
	
	
	/**
	 * returns true if no conflicts are known, false otherwise
	 * @return true if no conflicts are known, false otherwise
	 */
	public boolean hasNoConflicts() {
		return (conflicts.size() == 0);
	}
	
	
	/**
	 * returns the set of calendar conflicts
	 * @return set of calendar conflicts
	 */
	public Set<CalendarEventConflict> getConflicts() {
		return conflicts;
	}
	
	
	/**
	 * adds a new calendar conflict to the set
	 * @param event
	 * @param predecessor
	 * @param timeLeft
	 */
	public void addConflict(CalendarEventConflict conflict) {
		conflicts.add(conflict);
	}
	
	
	@Override
	public String toString() {
		if (hasNoConflicts()) {
			return "DayPlanConsistency: {no conflicts}";
		} else {
			StringBuffer buf = new StringBuffer("DayPlanConsistency: {");
			buf.append(conflicts.size() + " conflicts: ");
			Iterator<CalendarEventConflict> itr = conflicts.iterator();
			while (itr.hasNext()) {
				CalendarEventConflict conflict = itr.next();
				buf.append(conflict.toString());
				if (itr.hasNext()) {
					buf.append(", ");
				} else {
					buf.append("}");
				}
			}
			return buf.toString();
		}
	}
	
}
