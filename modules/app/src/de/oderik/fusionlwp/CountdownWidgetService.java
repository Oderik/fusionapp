package de.oderik.fusionlwp;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.IBinder;
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

  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(final Intent intent, final int flags, final int startId) {
    final int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
    Log.d(TAG, String.format("start intent: %s flags: %d startId: %d widgetIds: %s", intent, flags, startId, Arrays.toString(appWidgetIds)));
    updateWidget(appWidgetIds);
    stopSelf();
    return START_NOT_STICKY;
  }

  private void updateWidget(final int[] appWidgetIds) {
    final Typeface antonTypeface = Typeface.createFromAsset(getAssets(), "Anton.ttf");
    final CountDownDrawable countDownDrawable = new CountDownDrawable(this);
    countDownDrawable.setTypeface(antonTypeface);
    countDownDrawable.setBounds(0, 0, countDownDrawable.getIntrinsicWidth() - 1, countDownDrawable.getIntrinsicHeight() - 1);
    final Bitmap bitmap = Bitmap.createBitmap(countDownDrawable.getIntrinsicWidth(), countDownDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(bitmap);
    countDownDrawable.draw(canvas);

    final RemoteViews views = new RemoteViews(getPackageName(), R.layout.countdown_widget);
    views.setImageViewBitmap(R.id.countdown, bitmap);
    AppWidgetManager.getInstance(this).updateAppWidget(appWidgetIds, views);
  }
}
