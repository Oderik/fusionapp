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

    scrollView.addOnScrollChangeListener(new OnScrollChangeListener() {
      @Override
      public void onScrollChanged(final FrameLayout scrollView, final int l, final int t, final int oldl, final int oldt) {
        final int level = 10000 * t / content.getHeight();
        timelineView.setLevel(level);
      }
    });
  }
}