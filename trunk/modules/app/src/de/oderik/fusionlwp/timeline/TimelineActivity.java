package de.oderik.fusionlwp.timeline;

import android.app.Activity;
import android.os.Bundle;
import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 26.10.12
 */
public class TimelineActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.timeline);
  }
}