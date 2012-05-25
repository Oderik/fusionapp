package de.oderik.fusionlwp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class CountdownWidgetProvider extends AppWidgetProvider {
  private static final String TAG = CountdownWidgetProvider.class.getName();

  @Override
  public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
    context.startService(CountdownWidgetService.createUpdateIntent(context, appWidgetIds));
  }

  @Override
  public void onDeleted(final Context context, final int[] appWidgetIds) {
    context.startService(CountdownWidgetService.createDeletedIntent(context, appWidgetIds));
  }

  @Override
  public void onDisabled(final Context context) {
    context.startService(CountdownWidgetService.createDisabledIntent(context));
  }
}
