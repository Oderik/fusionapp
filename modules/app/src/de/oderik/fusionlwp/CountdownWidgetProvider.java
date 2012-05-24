package de.oderik.fusionlwp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class CountdownWidgetProvider extends AppWidgetProvider {
  private static final String TAG = CountdownWidgetProvider.class.getName();

  @Override
  public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
    final AlarmManager alarmManager = getAlarmManager(context);

    final Intent intent = new Intent(context, CountdownWidgetService.class);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

    final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

    alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() / 1000 * 1000 + 1000, 1000, pendingIntent);
    if (BuildConfig.DEBUG) {
      Log.d(TAG, String.format("onUpdate (%s)", pendingIntent));
    }
  }

  @Override
  public void onDeleted(final Context context, final int[] appWidgetIds) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "onDeleted");
    }
    super.onDeleted(context, appWidgetIds);
  }

  private AlarmManager getAlarmManager(final Context context) {
    return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
  }

  @Override
  public void onDisabled(final Context context) {
    super.onDisabled(context);
    final AlarmManager alarmManager = getAlarmManager(context);
    final Intent intent = new Intent(context, CountdownWidgetService.class);
    final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    alarmManager.cancel(pendingIntent);
    if (BuildConfig.DEBUG) {
      Log.d(TAG, String.format("onDisabled (%s)", pendingIntent));
    }
  }
}
