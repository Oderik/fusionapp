package de.oderik.fusionlwp;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created: 25.05.12
 *
 * @author Oderik
 */
public class ChooseWallpaperForwarderActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
    startActivity(intent);

    finish();
  }
}