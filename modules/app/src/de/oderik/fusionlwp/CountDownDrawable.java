package de.oderik.fusionlwp;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class CountDownDrawable extends Drawable {
  private static final String SAMPLE = "00:00:00:00";

  private final Paint paint;

  public CountDownDrawable() {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint paint = this.paint;
    paint.setColor(Color.BLACK);
    paint.setAntiAlias(true);
    paint.setStrokeWidth(2);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStyle(Paint.Style.STROKE);
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(20);
  }

  public void setTypeface(final Typeface typeface) {
    paint.setTypeface(typeface);
  }

  public void setTextSize(final float textSize) {
    paint.setTextSize(textSize);
  }

  public void setColor(final int color) {
    paint.setColor(color);
  }

  @Override
  public void draw(final Canvas canvas) {
    final Rect bounds = getBounds();
    final float centerX = (bounds.right - bounds.left) / 2;
    final float centerY = (bounds.bottom - bounds.top) / 2;

    canvas.save();
    canvas.translate(centerX, centerY);

    final int timeLeft = getLevel();

    final String time;
    if (timeLeft > 0) {
      time = FusionEventTiming.format(timeLeft);
    } else {
      time = "Takeoff!";
    }
    canvas.drawText(time, 0, 0, paint);
    canvas.restore();
  }

  @Override
  public void setAlpha(final int i) {
  }

  @Override
  public void setColorFilter(final ColorFilter colorFilter) {
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }
}
