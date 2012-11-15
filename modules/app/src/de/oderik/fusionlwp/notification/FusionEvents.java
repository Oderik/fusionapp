package de.oderik.fusionlwp.notification;

import android.content.Context;
import de.oderik.fusionlwp.R;

import java.util.Calendar;

/**
 * Data holder for all fusion events that should generate a notification
 * @author monsterkind
 *
 */
public class FusionEvents {
	
	
	/**
	 * Declares one event
	 * @author monsterkind
	 *
	 */
	public static class Event{
		
		public String title; 
		public String text;
		public Calendar when;
		
		public Event(int day, int month, int year, int hourOfDay, int minute, String title, String text){
			this.title = title;
			this.text = text;
			when = Calendar.getInstance();
			when.set(year, month, day, hourOfDay, minute);
		}
	}
	
	private static Event[] events = null;
	
	public static Event[] getEvents(Context context){
		
		if (events == null){
			events = new Event[]{new Event(1,10,2012,0,0, // naja ab 1.10.2012 schonmal langsam vorbereiten...
										context.getString(R.string.event_title_prepare_to_register),
										context.getString(R.string.event_text_prepare_to_register)),
								new Event(1,12,2012,0,0, // am 01.12.2012 kann man sich registrieren
										context.getString(R.string.event_title_register),
										context.getString(R.string.event_text_register)),
								new Event(27,06,2013,0,0, // am 27.06.2013 GEHTS LOS!!!
										context.getString(R.string.event_title_festival_start),
										context.getString(R.string.event_text_festival_start))
			};
		}
		return events;

	}
	
	public static Event getEvent(Context context, int number){
		Event[] events = getEvents(context);
		if (events != null && number >= 0 && events.length > number){
			return events[number];
		}else{
			return null;
		}
	}
	
	

}
