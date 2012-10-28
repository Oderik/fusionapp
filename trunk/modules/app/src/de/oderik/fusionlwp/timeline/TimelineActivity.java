package de.oderik.fusionlwp.timeline;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 26.10.12
 */
public class TimelineActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.timeline);

    final View content = findViewById(R.id.content);
    content.setBackgroundDrawable(new StarfieldDrawable(this));

    final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
    final TimelineView timelineView = (TimelineView) findViewById(R.id.timeline);

    final View bigView = findViewById(R.id.big);

    scrollView.addOnScrollChangeListener(new OnScrollChangeListener() {
      @Override
      public void onScrollChanged(final FrameLayout scrollView, final int l, final int t, final int oldl, final int oldt) {
        final int bigViewHeight = bigView.getHeight();
        final int timelineViewHeight = timelineView.getHeight();
        if (bigViewHeight > timelineViewHeight) {
          final int level = RocketDrawable.MAX_LEVEL * t / (bigViewHeight - timelineViewHeight);
          timelineView.setLevel(level);
        } else {
          timelineView.setLevel(RocketDrawable.MAX_LEVEL);
        }
      }
    });
  }
}