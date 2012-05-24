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

  public static final int DEFAULT_TEXT_SIZE = 25;
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
