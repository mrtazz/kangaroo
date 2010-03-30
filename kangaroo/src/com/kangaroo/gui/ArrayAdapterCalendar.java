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
public class ArrayAdapterCalendar extends ArrayAdapter<CalendarEvent>{
	
		private ArrayList<CalendarEvent> events;
		private Context ctx;
		
		public ArrayAdapterCalendar(Context ctx, int textViewRessourceId,
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
				v.setTag(R.id.row, ce);
				
				/* fill views */
				String starttime = "All";
				String endtime = "Day";
				if (ce.getAllDay() != true)
				{
					starttime = pad2(ce.getStartDate().getHours()) + ":"
					  		  + pad2(ce.getStartDate().getMinutes());
					endtime = pad2(ce.getEndDate().getHours()) + ":"
							+ pad2(ce.getEndDate().getMinutes());
				}
				start.setText(starttime);
				end.setText(endtime);
				title.setText(ce.getTitle());
				location.setText(ce.getLocation());
			}
			/* return the view */
			return v;
		}
		
		/**
		 * @brief method to pad time to a length of 2
		 * @param i time as int
		 * @return padded time as string
		 */
		private String pad2(int i)
		{
			String s = Integer.toString(i);
			s= (s.length() < 2) ? ("0"+s) : (s);
			return s;
		}

}
