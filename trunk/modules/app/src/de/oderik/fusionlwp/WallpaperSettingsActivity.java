package de.oderik.fusionlwp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created: 25.05.12
 *
 * @author Oderik
 */
public class WallpaperSettingsActivity extends PreferenceActivity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getPreferenceManager().setSharedPreferencesName(FusionWallpaperService.SHARED_PREFERENCES_NAME);
    addPreferencesFromResource(R.xml.wallpaper_preferences);
  }
}