package de.oderik.fusionlwp.timeline;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 26.10.12
 */
public class TimelineActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    doStuff();
    //setContentView(R.layout.test);
  }

  private void doStuff() {
    setContentView(R.layout.timeline);

    final View content = findViewById(R.id.content);
    //content.setBackgroundDrawable(new StarfieldDrawable(this));

    final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
    final ImageView rocketView = (ImageView) findViewById(R.id.rocket);
    final RocketDrawable rocketDrawable = new RocketDrawable(this);
    rocketView.setImageDrawable(rocketDrawable);
    final ImageView calendarView = (ImageView) findViewById(R.id.calendar);
    calendarView.setImageDrawable(new CalendarDrawable(this));
    scrollView.addOnScrollChangeListener(new OnScrollChangeListener() {
      private final Matrix matrix = new Matrix();

      @Override
      public void onScrollChanged(final FrameLayout scrollView, final int l, final int t, final int oldl, final int oldt) {
        final int calendarViewHeight = calendarView.getHeight();
        final int rocketViewHeight = rocketView.getHeight();

        final int level;
        if (calendarViewHeight > rocketViewHeight) {
          level = RocketDrawable.MAX_LEVEL * t / (calendarViewHeight - rocketViewHeight);
        } else {
          level = RocketDrawable.MAX_LEVEL;
        }
        rocketDrawable.setLevel(level);
        rocketView.invalidateDrawable(rocketDrawable);
      }
    });
  }
}