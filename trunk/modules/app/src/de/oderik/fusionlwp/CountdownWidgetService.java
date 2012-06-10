package de.oderik.fusionlwp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Arrays;

/**
 * Created: 24.05.12
 *
 * @author Oderik
 */
public class CountdownWidgetService extends Service {
  public static final String TAG = CountdownWidgetService.class.getName();

  public static final String ACTION_UPDATE = AppWidgetManager.ACTION_APPWIDGET_UPDATE;
  public static final String ACTION_DELETED = AppWidgetManager.ACTION_APPWIDGET_DELETED;
  public static final String ACTION_DISABLED = AppWidgetManager.ACTION_APPWIDGET_DISABLED;
  public static final String ACTION_TICK = "de.oderik.fusionlwp.TICK";

  public static final String EXTRA_APPWIDGET_IDS = AppWidgetManager.EXTRA_APPWIDGET_IDS;

  private CountdownDrawable countdownDrawable;
  private Bitmap bitmap;
  private Canvas canvas;
  private Intent intent;
  private boolean tickerRunning = false;
  private FusionEventTiming fusionEventTiming = new FusionEventTiming();

  public IBinder onBind(Intent intent) {
    return null;
  }

  private void updateWidgets() {
    if (BuildConfig.DEBUG) {
      Log.v(TAG, "updateWidgets()");
    }

    final AppWidgetManager appWidgetManager = getAppWidgetManager();
    final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CountdownWidgetProvider.class));
    if (appWidgetIds != null && appWidgetIds.length > 0) {
      if (BuildConfig.DEBUG) {
        Log.d(TAG, String.format("refreshing app widgets: %s", Arrays.toString(appWidgetIds)));
      }
      final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
      if (powerManager.isScreenOn()) {
        countdownDrawable.draw(canvas);
        final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.countdown_widget);
        remoteViews.setImageViewBitmap(R.id.countdown, bitmap);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
      } else if (BuildConfig.DEBUG) {
        Log.d(TAG, "Screen is off, skipping refresh of widget.");
      }
      startTicker();
    } else {
      stopService();
    }
  }

  private void startTicker() {
    if (!tickerRunning) {
      tickerRunning = true;
      if (BuildConfig.DEBUG) {
        Log.d(TAG, "starting ticker");
      }
      final AlarmManager alarmManager = getAlarmManager();
      final PendingIntent pendingIntent = getPendingIntent();
      alarmManager.setRepeating(AlarmManager.RTC, fusionEventTiming.nextTick(), FusionEventTiming.SECOND, pendingIntent);
    }
  }

  private void stopService() {
    if (BuildConfig.DEBUG) {
      Log.v(TAG, "stopService()");
    }
    final AlarmManager alarmManager = getAlarmManager();
    alarmManager.cancel(getPendingIntent());
    tickerRunning = false;
    stopSelf();
  }

  private AlarmManager getAlarmManager() {
    return (AlarmManager) getSystemService(Context.ALARM_SERVICE);
  }

  private PendingIntent getPendingIntent() {
    return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    final String action = intent.getAction();

    if (ACTION_TICK.equals(action) || ACTION_UPDATE.equals(action) || ACTION_DELETED.equals(action)) {
      updateWidgets();
    } else if (ACTION_DISABLED.equals(action)) {
      stopService();
    } else {
      Log.i(TAG, String.format("Don't know how to perform ACTION '%s'.", action));
    }

    return START_NOT_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    final Typeface antonTypeface = Typeface.createFromAsset(getAssets(), "Anton.ttf");
    countdownDrawable = new CountdownDrawable(this, fusionEventTiming);
    countdownDrawable.setTypeface(antonTypeface);
    countdownDrawable.setBounds(0, 0, countdownDrawable.getIntrinsicWidth() - 1, countdownDrawable.getIntrinsicHeight() - 1);
    bitmap = Bitmap.createBitmap(countdownDrawable.getIntrinsicWidth(), countdownDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    canvas = new Canvas(bitmap);
    intent = createTickIntent(this);
  }

  private AppWidgetManager getAppWidgetManager() {
    return AppWidgetManager.getInstance(this);
  }

  public static Intent createTickIntent(final Context context) {
    final Intent intent = new Intent(context, CountdownWidgetService.class);
    intent.setAction(ACTION_TICK);
    return intent;
  }

  public static Intent createUpdateIntent(final Context context, final int... appWidgetIds) {
    final Intent intent = new Intent(context, CountdownWidgetService.class);

    intent.setAction(ACTION_UPDATE);

    if (appWidgetIds != null && appWidgetIds.length > 0) {
      intent.putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds);
    }

    return intent;
  }

  public static Intent createDeletedIntent(final Context context, final int... appWidgetIds) {
    final Intent intent = new Intent(context, CountdownWidgetService.class);

    intent.setAction(ACTION_DELETED);

    if (appWidgetIds != null && appWidgetIds.length > 0) {
      intent.putExtra(EXTRA_APPWIDGET_IDS, appWidgetIds);
    }

    return intent;
  }

  public static Intent createDisabledIntent(final Context context) {
    final Intent intent = new Intent(context, CountdownWidgetService.class);

    intent.setAction(ACTION_DISABLED);

    return intent;
  }
}
