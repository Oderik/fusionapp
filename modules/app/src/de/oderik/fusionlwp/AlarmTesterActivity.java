package de.oderik.fusionlwp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created: 25.05.12
 *
 * @author Oderik
 */
public class AlarmTesterActivity extends Activity implements View.OnClickListener {

  private Intent intent;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.alarm_tester);

    intent = new Intent(this, CountdownWidgetService.class);

  }

  @Override
  public void onClick(final View v) {
    switch (v.getId()) {
      case R.id.button1: {
        final AlarmManager alarmManager = getAlarmManager();
        final PendingIntent pendingIntent = getPendingIntent();
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000, 1000, pendingIntent);
      }
      break;
      case R.id.button2: {
        final AlarmManager alarmManager = getAlarmManager();
        alarmManager.cancel(getPendingIntent());
      }
      break;
    }
  }

  private AlarmManager getAlarmManager() {
    return (AlarmManager) getSystemService(Context.ALARM_SERVICE);
  }

  private PendingIntent getPendingIntent() {
    return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
  }
}