package de.oderik.fusionlwp.theme;

import android.content.res.Resources;
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
          R.drawable.background_2012,
          R.string.label_theme_2012,
          new LiveWallpaperDrawableFactory() {
            @Override
            public LiveWallpaperDrawable create(final Resources resources) {
              return new Background2012Drawable(resources);
            }
          }),

  FUSION_2013(
          R.drawable.countdown_panel_2013,
          R.drawable.background_2013,
          R.string.label_theme_2013,
          new LiveWallpaperDrawableFactory() {
            @Override
            public LiveWallpaperDrawable create(final Resources resources) {
              return new Background2013Drawable(resources);
            }
          });

  public final int countdownPanel;
  public final int backgroundDrawable;
  public final int labelString;
  public final LiveWallpaperDrawableFactory wallpaperFactory;

  private EventTheme(final int countdownPanel, final int background, final int label, final LiveWallpaperDrawableFactory factory) {
    this.countdownPanel = countdownPanel;
    backgroundDrawable = background;
    labelString = label;
    wallpaperFactory = factory;
  }

  public interface LiveWallpaperDrawableFactory {
    LiveWallpaperDrawable create(final Resources resources);
  }

  public static EventTheme noNull(final EventTheme eventTheme) {
    return eventTheme == null ? FUSION_2013 : eventTheme;
  }
}
