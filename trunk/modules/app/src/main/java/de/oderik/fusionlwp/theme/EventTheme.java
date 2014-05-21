package de.oderik.fusionlwp.theme;

import android.content.Context;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.wallpaper.Background2012Drawable;
import de.oderik.fusionlwp.wallpaper.Background2013Drawable;
import de.oderik.fusionlwp.wallpaper.LiveWallpaperDrawable;

/**
 * @author maik.riechel
 * @since 14.05.13 18:38
 */
public enum EventTheme {
  FUSION_2012(
          R.drawable.countdown_panel_2012,
          R.string.label_theme_2012,
          R.color.countdown_text_color_2012,
          new LiveWallpaperDrawableFactory() {
            @Override
            public LiveWallpaperDrawable create(final Context context) {
              return new Background2012Drawable(context);
            }
          }),

  FUSION_2013(
          R.drawable.countdown_panel_2013,
          R.string.label_theme_2013,
          R.color.countdown_text_color_2013,
          new LiveWallpaperDrawableFactory() {
            @Override
            public LiveWallpaperDrawable create(final Context context) {
              return new Background2013Drawable(context);
            }
          }),

  FUSION_2014(
          R.drawable.countdown_panel_2014,
          R.string.label_theme_2014,
          R.color.countdown_text_color_2014,
          new LiveWallpaperDrawableFactory() {
            @Override
            public LiveWallpaperDrawable create(final Context context) {
              return new Background2013Drawable(context);
            }
          });
  public final int countdownPanel;
  public final int labelString;
  public final int countdownColor;
  public final LiveWallpaperDrawableFactory wallpaperFactory;

  private EventTheme(final int countdownPanel, final int label, final int countdownColor, final LiveWallpaperDrawableFactory factory) {
    this.countdownPanel = countdownPanel;
    labelString = label;
    this.countdownColor = countdownColor;
    wallpaperFactory = factory;
  }

  public static EventTheme noNull(final EventTheme eventTheme) {
    return eventTheme == null ? FUSION_2014 : eventTheme;
  }

  public interface LiveWallpaperDrawableFactory {
    LiveWallpaperDrawable create(final Context context);
  }
}
