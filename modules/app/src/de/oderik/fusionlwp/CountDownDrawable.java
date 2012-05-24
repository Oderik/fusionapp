package de.oderik.fusionlwp;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class CountDownDrawable extends Drawable {
  private static final String SAMPLE = "00:00:00:00";
  public static final int DEFAULT_TEXT_SIZE = 20;
  public static final int DEFAULT_TEXT_COLOR = Color.BLACK;

  private final Paint paint;
  private final Drawable backgroundDrawable;
  private static final int INTRINSIC_WIDTH = 300;
  private static final int INTRINSIC_HEIGHT = 100;

  public CountDownDrawable(final Context context) {
    backgroundDrawable = context.getResources().getDrawable(R.drawable.countdown_panel);

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint paint = this.paint;
    paint.setColor(DEFAULT_TEXT_COLOR);
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.FILL);
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(DEFAULT_TEXT_SIZE);
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
    final float centerX = (bounds.right - bounds.left) / 2 + bounds.left;
    final float centerY = (bounds.bottom - bounds.top) / 2 + bounds.top;

    final long timeLeft = FusionEventTiming.timeToFusion();

    final String time;
    if (timeLeft > 0) {
      time = FusionEventTiming.format(timeLeft);
    } else {
      time = "Takeoff!";
    }
    canvas.drawText(time, centerX, centerY, paint);
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
    return INTRINSIC_WIDTH;
  }

  @Override
  public int getIntrinsicHeight() {
    return INTRINSIC_HEIGHT;
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
