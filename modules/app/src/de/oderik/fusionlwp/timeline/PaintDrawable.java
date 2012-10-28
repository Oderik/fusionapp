package de.oderik.fusionlwp.timeline;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * @author Maik.Riechel
 * @since 28.10.12
 */
public abstract class PaintDrawable extends Drawable {
  protected final Paint paint;

  public PaintDrawable(final Paint paint) {
    this.paint = paint;
  }

  @Override
  public void setAlpha(final int i) {
    paint.setAlpha(i);
  }

  @Override
  public void setColorFilter(final ColorFilter filter) {
    paint.setColorFilter(filter);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }
}
