package de.oderik.fusionlwp.wallpaper;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.theme.EventTheme;

/**
 * Created: 25.05.12
 *
 * @author Oderik
 */
public class WallpaperSettingsActivity extends FragmentActivity {

  private Preferences preferences;
  private RadioGroup themesRadioGroup;
  private CompoundButton enableCountdownButton;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    preferences = new Preferences(this);

    setContentView(R.layout.wallpaper_settings);

    enableCountdownButton = (CompoundButton) findViewById(R.id.enable_countdown);
    enableCountdownButton.setChecked(preferences.isCountdownEnabled());
    enableCountdownButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        preferences.setCountdownEnabled(isChecked);
      }
    });

    themesRadioGroup = (RadioGroup) findViewById(R.id.themes);
    assert themesRadioGroup != null;

    final EventTheme currentTheme = preferences.getEventTheme();
    final EventTheme[] eventThemes = EventTheme.values();
    for (EventTheme eventTheme : eventThemes) {
      final RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.theme_radio_button, themesRadioGroup, false);
      assert radioButton != null;
      radioButton.setText(eventTheme.labelString);
      radioButton.setTag(eventTheme);
      themesRadioGroup.addView(radioButton);
      if (eventTheme == currentTheme) {
        radioButton.setChecked(true);
      }
    }
    themesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(final RadioGroup group, final int checkedId) {
        preferences.setEventTheme((EventTheme) group.findViewById(checkedId).getTag());
      }
    });

  }

  @Override
  protected void onPause() {
    super.onPause();

    preferences.save();
  }
}