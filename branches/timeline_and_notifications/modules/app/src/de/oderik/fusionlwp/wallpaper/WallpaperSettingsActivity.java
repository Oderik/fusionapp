package de.oderik.fusionlwp.wallpaper;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import de.oderik.fusionlwp.R;

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