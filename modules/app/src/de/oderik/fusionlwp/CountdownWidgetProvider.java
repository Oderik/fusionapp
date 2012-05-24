package de.oderik.fusionlwp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class CountdownWidgetProvider extends AppWidgetProvider {
  private static final String TAG = CountdownWidgetProvider.class.getName();

  @Override
  public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "onUpdate");
    }
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    final Typeface antonTypeface = Typeface.createFromAsset(context.getAssets(), "Anton.ttf");
    final CountDownDrawable countDownDrawable = new CountDownDrawable(context);
    countDownDrawable.setTypeface(antonTypeface);
    countDownDrawable.setBounds(0, 0, 299, 79);
    final Bitmap bitmap = Bitmap.createBitmap(300, 80, Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(bitmap);
    countDownDrawable.draw(canvas);

    final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_widget);
    views.setImageViewBitmap(R.id.countdown, bitmap);

    appWidgetManager.updateAppWidget(appWidgetIds, views);
  }

  @Override
  public void onReceive(final Context context, final Intent intent) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, String.format("onReceive: %s", intent.getAction()));
    }
    super.onReceive(context, intent);
  }

  @Override
  public void onDeleted(final Context context, final int[] appWidgetIds) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "onDeleted");
    }
    super.onDeleted(context, appWidgetIds);
  }

  @Override
  public void onEnabled(final Context context) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "onEnabled");
    }
    super.onEnabled(context);
  }

  @Override
  public void onDisabled(final Context context) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "onDisabled");
    }
    super.onDisabled(context);
  }
}
