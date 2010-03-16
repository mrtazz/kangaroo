package com.kangaroo.task;

import java.util.Date;
import java.util.List;

import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintDuration;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskConstraintLocation;
import com.kangaroo.task.TaskConstraintPOI;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.routing.GeoConstraints;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RoutingEngine;



/**
 * This class provides methods to analyze task constraints.
 * @author andreaswalz
 *
 */
public class TaskConstraintHelper {

	
	private final int UNDEFINED = 0;
	
	
	private final int BLACK_LIST = 1;
	
	
	private final int WHITE_LIST = 2;	
	
	
	private Task task;
	
	
	private RoutingEngine routingEngine = null;
	
	
	public void setRoutingEngine(RoutingEngine routingEngine) {
		this.routingEngine = routingEngine;
	}
	
	
	public TaskConstraintHelper(Task task) {
		super();
		this.task = task;
	}
	
	
	/**
	 * returns the duration of this task in minutes, 0 if not specified.
	 * The task may specify more than one duration constraint, and if so
	 * the maximum of all will be returned
	 * @return the duration of this task in minutes, 0 if not specified
	 */
	public int getDuration() {
		
		int taskDuration = 0;

		List<TaskConstraintInterface> durationConstraints = 
			task.getConstraintsOfType(TaskConstraintInterface.TYPE_DURATION);
		
		if (durationConstraints.size() == 0) {
			/* no duration constraints associated with this task */
			return 0;
		}
		
		/* iterate over all duration constraints and find the maximum */
		for (TaskConstraintInterface constraint : durationConstraints) {
			TaskConstraintDuration durationConstraint = 
				(TaskConstraintDuration)constraint;
			if (taskDuration < durationConstraint.getDuration()) {
				taskDuration = durationConstraint.getDuration();
			}
		}
	
		return taskDuration;		
	}
	
	
	/**
	 * returns true if Date now is consistent with task date and daytime constraints 
	 * @param now
	 * @return true if Date now is consistent with task date and daytime constraints
	 */
	public boolean isAllowed(Date now) {
		
		boolean isAllowedByDateConstraints = false;
		boolean dateConstraintVeto = false;
		int dateConstraintType = UNDEFINED;
		
		List<TaskConstraintInterface> dateConstraints = 
			task.getConstraintsOfType(TaskConstraintInterface.TYPE_DATE);		

		for (TaskConstraintInterface constraint : dateConstraints) {
			
			TaskConstraintDate dateConstraint = (TaskConstraintDate)constraint;
			
			if (dateConstraint.getStart() != null) {

				if (dateConstraint.getEnd() != null) {
				
					/* do not allow both black and white list in one task */
					if (dateConstraintType == BLACK_LIST) {
						throw new RuntimeException("TaskConstraintHelper.isAllowed(): " +
								"Task specifies date constraints of both date span and end date");
					}					
					
					/* this task has a date constraint white list */
					dateConstraintType = WHITE_LIST;
					if (!now.before(dateConstraint.getStart()) && !now.after(dateConstraint.getEnd())) {
						isAllowedByDateConstraints = true;
					}
				
				} else {
					
					// TODO: also check for constraints only specifying a start date
					
				}
				
			} else {
				
				/* do not allow task specifying neither start date nor end date */
				if (dateConstraint.getEnd() == null) {
					throw new RuntimeException("TaskConstraintHelper.isAllowed(): " +
							"Task specifies date constraint with neither start date nor end date");
				}				
				
				/* do not allow both black and white list in one task */
				if (dateConstraintType == WHITE_LIST) {
					throw new RuntimeException("TaskConstraintHelper.isAllowed(): " +
							"Task specifies date constraints of both date span and end date");
				}
				
				/* this task has a date constraint black list */
				dateConstraintType = BLACK_LIST;
				if (now.after(dateConstraint.getEnd())) {
					dateConstraintVeto = true;
				}
			}
		}
		
		if (dateConstraintType == UNDEFINED) {
			isAllowedByDateConstraints = true;
		} else if (dateConstraintType == BLACK_LIST) {
			isAllowedByDateConstraints = !dateConstraintVeto;
		} 
		
					
		boolean isAllowedByDaytimeConstraints = false;
		boolean daytimeConstraintVeto = false;
		int daytimeConstraintType = UNDEFINED;
		
		List<TaskConstraintInterface> daytimeConstraints = 
			task.getConstraintsOfType(TaskConstraintInterface.TYPE_DAYTIME);		
		
		for (TaskConstraintInterface constraint : daytimeConstraints) {
			
			TaskConstraintDayTime daytimeConstraint = (TaskConstraintDayTime)constraint;
			
			if (daytimeConstraint.getStartTime() != null) {
				
				if (daytimeConstraint.getEndTime() != null) {
					
					/* do not allow both black and white list in one task */
					if (daytimeConstraintType == BLACK_LIST) {
						throw new RuntimeException(
								"TaskConstraintHelper.isAllowed(): "
										+ "Task specifies daytime constraints of both daytime span and end daytime");
					}
					/* this task has a daytime constraint white list */
					daytimeConstraintType = WHITE_LIST;
					if (compareDayTime(now, daytimeConstraint.getStartTime()) >= 0
							&& compareDayTime(daytimeConstraint.getEndTime(), now) >= 0) {
						isAllowedByDaytimeConstraints = true;
					}
					
				} else {
					
					// TODO: also check for constraints only specifying a start date
					
				}
				
			} else {
				
				/* do not allow task specifying neither start daytime nor end daytime */
				if (daytimeConstraint.getEndTime() == null) {
					throw new RuntimeException("TaskConstraintHelper.isAllowed(): " +
							"Task specifies daytime constraint with neither start daytime nor end daytime");
				}
				
				/* do not allow both black and white list in one task */
				if (daytimeConstraintType == WHITE_LIST) {
					throw new RuntimeException("TaskConstraintHelper.isAllowed(): " +
							"Task specifies daytime constraints of both daytime span and end daytime");
				}
				
				/* this task has a date constraint black list */
				dateConstraintType = BLACK_LIST;
				if (compareDayTime(now, daytimeConstraint.getEndTime()) > 0) {
					daytimeConstraintVeto = true;
				}
				
			}
		}
				
		if (daytimeConstraintType == UNDEFINED) {
			isAllowedByDaytimeConstraints = true;
		} else if (daytimeConstraintType == BLACK_LIST) {
			isAllowedByDaytimeConstraints = !daytimeConstraintVeto;
		} 
		
		
		return isAllowedByDateConstraints && isAllowedByDaytimeConstraints;
	}
	
	
	
	public int compareDayTime(Date daytime1, Date daytime2) {		
		int hours = daytime1.getHours() - daytime2.getHours();		
		if (hours != 0) {
			return hours;
		}
		
		int minutes = daytime1.getMinutes() - daytime2.getMinutes();		
		if (minutes != 0) {
			return minutes;
		}
		
		int seconds = daytime1.getSeconds() - daytime2.getSeconds();
		return seconds;
	}
	
	
	
	/**
	 * return the nearest location from Place here that is consistent with
	 * constraints of type TaskConstraintLocation and TaskConstraintPOI
	 * @param here
	 * @param geoConstraints TODO
	 * @return
	 */
	public Place getLocation(Place here, GeoConstraints geoConstraints) {
		
		Place minPlace = null;
		double minDist = Double.MAX_VALUE;

		List<TaskConstraintInterface> locationConstraints = 
			task.getConstraintsOfType(TaskConstraintInterface.TYPE_LOCATION);
		
		if (locationConstraints.size() > 0) {
			
			for (TaskConstraintInterface constraint : locationConstraints) {
				TaskConstraintLocation locationConstraint = (TaskConstraintLocation)constraint;
				
				/* update minPlace and minDist */
				Place place = locationConstraint.getPlace();
				if (place != null) {
					if (minPlace == null || minPlace.distanceTo(place) < minDist) {
						minPlace = place;
						minDist = minPlace.distanceTo(place);
					}
				}
			}
			
		}
		
				
		List<TaskConstraintInterface> poiConstraints = 
			task.getConstraintsOfType(TaskConstraintInterface.TYPE_POI);
		
		if (poiConstraints.size() > 0) {
			
			if (routingEngine == null || !routingEngine.initialized()) {
				throw new RuntimeException("TaskConstraintHelper.getLocation(): " +
						"no routing engine defined or routing engine not ready");
			}
			
			for (TaskConstraintInterface constraint : poiConstraints) {
				TaskConstraintPOI poiConstraint = (TaskConstraintPOI)constraint;				
				Place place = routingEngine.getNearestPOINode(here, 
						new POINodeSelector(poiConstraint.getId()), geoConstraints);
				
				/* update minPlace and minDist */
				if (place != null) {
					if (minPlace == null || minPlace.distanceTo(place) < minDist) {
						minPlace = place;
						minDist = minPlace.distanceTo(place);
					}
				}
				
			}
		}
		
		return minPlace;
		
	}
	
}
