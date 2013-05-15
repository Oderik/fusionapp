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
  private int surfaceWidth = 0;
  private int surfaceHeight = 0;

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

  public final void setSurfaceSize(final int width, final int height) {
    if (width != surfaceWidth || height != surfaceHeight) {
      surfaceWidth = width;
      surfaceHeight = height;

      onSurfaceSizeChanged(width, height);
    }
  }

  protected int getSurfaceHeight() {
    return surfaceHeight;
  }

  protected int getSurfaceWidth() {
    return surfaceWidth;
  }

  protected void onSurfaceSizeChanged(final int width, final int height) {
  }
}