package de.oderik.fusionlwp.notification;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.WelcomeActivity;
import de.oderik.fusionlwp.notification.FusionEvents.Event;
import de.oderik.fusionlwp.timeline.TimelineActivity;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String EXTRA_EVENT_NUMBER = "EventNumber";
	private int mId = 4711;
	
	@Override
	public void onReceive(Context context, Intent intent) {

		Toast.makeText(context, "FUSION-ALARM!!", Toast.LENGTH_LONG).show();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean alarmEnabled = prefs.getBoolean("notification_enabled", true);
		
		int evNum = intent.getIntExtra(EXTRA_EVENT_NUMBER, -1);
		
		if (alarmEnabled){
			
			if (evNum >= 0){
				Event event = FusionEvents.getEvents(context)[evNum];
				
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(context)
				        .setSmallIcon(R.drawable.icon)
				        .setContentTitle(event.title)
				        .setContentText(event.text);
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(context, TimelineActivity.class);
		
				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(WelcomeActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(mId, mBuilder.build());
			}else{
				Log.e(context.getString(R.string.app_name), "getIntExtra(EXTRA_EVENT_NUMBER) == -1");
			}
		}
		
		// register next alarm
		evNum++;
		FusionEvents.Event ev = FusionEvents.getEvent(context, evNum);
		if (ev != null){
			registerAlarms(context, evNum);
		}

	}
	
	/**
	 * Register all alarms that are written in {@link FusionEvents}
	 * @param context
	 */
	public static void registerAlarms(Context context){
		registerAlarms(context, 0);
	}
	
	/**
	 * Register all alarms that are written in {@link FusionEvents}
	 * @param context
	 */
	public static void registerAlarms(Context context, int eventCount){

		// Get the AlarmManager service
	   	AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	   	
	   	FusionEvents.Event event = FusionEvents.getEvent(context, eventCount);
		
	   	if (event != null){
			// Intent for this BroadCastReceiver
			Intent intent = new Intent(context, AlarmReceiver.class);
			// put number of event list as extra
			intent.putExtra(EXTRA_EVENT_NUMBER, eventCount);
			
			PendingIntent pIntend = PendingIntent.getBroadcast(context, -1, intent, PendingIntent.FLAG_ONE_SHOT);
			
		   	// Set the alarms
		   	Calendar now = Calendar.getInstance();
		   	Calendar when = event.when;
		   	if (when.before(now)){
		   		when = now;
		   		when.add(Calendar.SECOND, 2);
		   	}
		   	am.set(AlarmManager.RTC, when.getTimeInMillis(), pIntend);
	   	}
	}
	
	

}
