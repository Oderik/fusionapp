package de.oderik.fusionlwp.wallpaper;

import android.content.Context;
import android.content.SharedPreferences;
import de.oderik.fusionlwp.theme.EventTheme;

/**
 * @author maik.riechel
 * @since 16.05.13 06:41
 */
public class Preferences implements SharedPreferences.OnSharedPreferenceChangeListener {
  private static final String PREFERENCES_NAME = "countdownposition";

  private static final String POS_X = "posX";
  private static final String POS_Y = "posY";
  private static final String ENABLED = "enabled";
  private static final String THEME = "theme";

  private final SharedPreferences preferences;

  private OnPreferencesChangedListener onPreferencesChangedListener;

  private float countdownPosX;
  private float countdownPosY;
  private boolean countdownEnabled;
  private EventTheme eventTheme;

  public Preferences(final Context context) {
    preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
    load();
  }

  public void load() {
    countdownPosX = preferences.getFloat(Preferences.POS_X, .5f);
    countdownPosY = preferences.getFloat(Preferences.POS_Y, .5f);
    countdownEnabled = preferences.getBoolean(Preferences.ENABLED, true);
    final int themeOrdinal = preferences.getInt(Preferences.THEME, -1);
    final EventTheme[] eventThemes = EventTheme.values();
    if (themeOrdinal >= 0 && themeOrdinal < eventThemes.length) {
      eventTheme = eventThemes[themeOrdinal];
    } else {
      eventTheme = null;
    }
  }

  public void save() {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putFloat(Preferences.POS_X, countdownPosX);
    editor.putFloat(Preferences.POS_Y, countdownPosY);
    editor.putBoolean(Preferences.ENABLED, countdownEnabled);
    editor.putInt(Preferences.THEME, eventTheme != null ? eventTheme.ordinal() : -1);
    editor.commit();
  }

  @Override
  public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
    load();
    notifyChangeListener();
  }

  private void notifyChangeListener() {
    if (onPreferencesChangedListener != null) {
      onPreferencesChangedListener.onPreferencesChanged(this);
    }
  }

  /**
   * Notify the listener when the underlying SharedPreferences change.
   * Don't forget to set to <code>null</code> when you're done with this!
   */
  public void setOnPreferencesChangedListener(final OnPreferencesChangedListener onPreferencesChangedListener) {
    this.onPreferencesChangedListener = onPreferencesChangedListener;

    if (onPreferencesChangedListener != null) {
      preferences.registerOnSharedPreferenceChangeListener(this);
    } else {
      preferences.unregisterOnSharedPreferenceChangeListener(this);
    }
  }

  public boolean isCountdownEnabled() {
    return countdownEnabled;
  }

  public float getCountdownPosX() {
    return countdownPosX;
  }

  public float getCountdownPosY() {
    return countdownPosY;
  }

  public void setCountdownPosX(final float countdownPosX) {
    this.countdownPosX = countdownPosX;
  }

  public void setCountdownPosY(final float countdownPosY) {
    this.countdownPosY = countdownPosY;
  }

  public EventTheme getEventTheme() {
    return eventTheme;
  }

  public void setEventTheme(final EventTheme eventTheme) {
    this.eventTheme = eventTheme;
  }

  public void setCountdownEnabled(final boolean countdownEnabled) {
    this.countdownEnabled = countdownEnabled;
  }

  interface OnPreferencesChangedListener {
    void onPreferencesChanged(Preferences preferences);
  }
}
