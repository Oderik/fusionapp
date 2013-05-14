package de.oderik.fusionlwp.wallpaper;

import android.content.res.Resources;

/**
 * @author maik.riechel
 * @since 14.05.13 20:27
 */
public abstract class LiveBackgroundDrawable extends LiveWallpaperDrawable {
  protected final Resources resources;

  public LiveBackgroundDrawable(final Resources resources) {
    this.resources = resources;
  }
}
