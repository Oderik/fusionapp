package de.oderik.fusionlwp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class CountdownDrawable extends Drawable {
  public static final int DEFAULT_TEXT_COLOR = Color.BLACK;

  private final Paint paint;
  private final Drawable backgroundDrawable;
  private int intrinsicWidth;
  private int intrinsicHeight;

  public CountdownDrawable(final Context context) {
    Resources resources = context.getResources();
    intrinsicWidth = resources.getDimensionPixelOffset(R.dimen.countdownWidth);
    intrinsicHeight = resources.getDimensionPixelOffset(R.dimen.countdownHeight);

    backgroundDrawable = resources.getDrawable(R.drawable.countdown_panel);

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint paint = this.paint;
    paint.setColor(DEFAULT_TEXT_COLOR);
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
    paint.setTextAlign(Paint.Align.CENTER);

    paint.setTextSize(resources.getDimension(R.dimen.countdownTextSize));
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
    drawBackground(canvas);
    drawCountdown(canvas);
  }



  private void drawBackground(final Canvas canvas) {
    canvas.save();
    backgroundDrawable.setBounds(getBounds());
    backgroundDrawable.draw(canvas);
    canvas.restore();
  }

  private void drawCountdown(final Canvas canvas) {
    final Rect bounds = getBounds();
    if (bounds.width() < 1) {
      bounds.right = bounds.left + getMinimumWidth();
    }
    if (bounds.height() < 1) {
      bounds.bottom = bounds.top + getMinimumHeight();
    }
    final float xOffset = bounds.left + (bounds.width()) / 2;
    final float yOffset = bounds.top + (bounds.height() - paint.ascent()) / 2;

    final long timeLeft = FusionEventTiming.timeToFusion();

    final String time;
    if (timeLeft > 0) {
      time = FusionEventTiming.format(timeLeft);
    } else {
      time = "Takeoff!";
    }
    canvas.drawText(time, xOffset, yOffset, paint);
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

  @Override
  public int getIntrinsicWidth() {
    return intrinsicWidth;
  }

  @Override
  public int getIntrinsicHeight() {
    return intrinsicHeight;
  }

  @Override
  public int getMinimumWidth() {
    return getIntrinsicWidth();
  }

  @Override
  public int getMinimumHeight() {
    return getIntrinsicHeight();
  }
}
