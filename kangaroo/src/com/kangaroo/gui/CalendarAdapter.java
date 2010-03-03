/**
 * 
 */
package com.kangaroo.gui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.kangaroo.R;
import com.kangaroo.calendar.CalendarEvent;

/**
 * @author mrtazz
 * @brief adapter for filling a row with calendar data
 *
 */
public class CalendarAdapter extends ArrayAdapter<CalendarEvent>{
	
		private ArrayList<CalendarEvent> events;
		private Context ctx;
		
		public CalendarAdapter(Context ctx, int textViewRessourceId,
								ArrayList<CalendarEvent> events)
		{
			super(ctx, textViewRessourceId, events);
			this.ctx = ctx;
			this.events = events;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View v = convertView;
			if (v == null)
			{
				LayoutInflater vi = 
					(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			CalendarEvent ce = events.get(position);
			if (ce != null)
			{
				/* get views */
				TextView start = (TextView) v.findViewById(R.id.eventstarttime);
				TextView end = (TextView) v.findViewById(R.id.eventendtime);
				TextView title = (TextView) v.findViewById(R.id.eventtitle);
				TextView location = (TextView) v.findViewById(R.id.eventlocation);
				
				/* fill views */
				String starttime = "All";
				String endtime = "Day";
				if (ce.getAllDay() != true)
				{
					starttime = ce.getStartDate().getHours()+":"
					  		  + ce.getStartDate().getMinutes();
					endtime = ce.getEndDate().getHours()+":"
							+ ce.getEndDate().getMinutes();
				}
				start.setText(starttime);
				end.setText(endtime);
				title.setText(ce.getTitle());
				location.setText(ce.getLocation());
			}
			/* return the view */
			return v;
		}

}
