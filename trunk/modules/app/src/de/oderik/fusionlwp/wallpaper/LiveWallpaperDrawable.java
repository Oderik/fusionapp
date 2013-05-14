package de.oderik.fusionlwp.wallpaper;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * @author maik.riechel
 * @since 14.05.13 19:32
 */
public abstract class LiveWallpaperDrawable extends Drawable {

  protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  @Override
  public void setAlpha(final int alpha) {
    paint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(final ColorFilter cf) {
    paint.setColorFilter(cf);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.OPAQUE;
  }

  public final void offsetsChanged(float xOffset, float yOffset,
                                   float xStep, float yStep,
                                   int xPixels, int yPixels) {
    onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
  }


  /**
   * @see android.service.wallpaper.WallpaperService.Engine#onOffsetsChanged(float, float, float, float, int, int)
   */
  protected void onOffsetsChanged(float xOffset, float yOffset,
                                  float xStep, float yStep,
                                  int xPixels, int yPixels) {

  }
}