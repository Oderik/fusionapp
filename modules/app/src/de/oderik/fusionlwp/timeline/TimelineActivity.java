package de.oderik.fusionlwp.timeline;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 26.10.12
 */
public class TimelineActivity extends Activity {

  private CalendarView calendarView;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    doStuff();
    //setContentView(R.layout.test);
  }

  private void doStuff() {
    setContentView(R.layout.timeline);

    final View content = findViewById(R.id.content);
    //content.setBackgroundDrawable(new StarfieldDrawable(this));

    final ImageView rocketView = (ImageView) findViewById(R.id.rocket);
    final RocketDrawable rocketDrawable = new RocketDrawable(this);
    rocketView.setImageDrawable(rocketDrawable);
    calendarView = (CalendarView) findViewById(R.id.calendar);
    calendarView.setOnScrollChangeListener(new CalendarView.OnScrollChangeListener() {
      private final Matrix matrix = new Matrix();

      @Override
      public void onScrollChanged(final CalendarView calendarView, final float relativeScroll) {
        final int level = (int) (RocketDrawable.MAX_LEVEL * relativeScroll);
        rocketDrawable.setLevel(level);
        rocketView.invalidateDrawable(rocketDrawable);
      }
    });
  }

}