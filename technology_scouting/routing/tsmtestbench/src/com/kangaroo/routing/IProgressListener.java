/**
 * 
 */
package com.kangaroo.routing;

/**
 * @author andreaswalz
 *
 */
public interface IProgressListener {

	public void onProgressMade(String status);
	
	public void onDone(String status);
	
}
