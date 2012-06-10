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
  public static final int SPACE              = 5;

  private final Paint    textPaint;
  private final Drawable backgroundDrawable;
  private       int      intrinsicWidth;
  private       int      intrinsicHeight;

  private final FusionEventTiming fusionEventTiming;
  private final Paint             boxPaint;
  private       float             boxSize;

  public CountdownDrawable(final Context context, final FusionEventTiming fusionEventTiming) {
    this.fusionEventTiming = fusionEventTiming;
    Resources resources = context.getResources();
    intrinsicWidth = resources.getDimensionPixelOffset(R.dimen.countdownWidth);
    intrinsicHeight = resources.getDimensionPixelOffset(R.dimen.countdownHeight);

    backgroundDrawable = resources.getDrawable(R.drawable.countdown_panel);

    textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textPaint.setColor(DEFAULT_TEXT_COLOR);
    textPaint.setStyle(Paint.Style.FILL);

    textPaint.setTextSize(resources.getDimension(R.dimen.countdownTextSize));

    boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    boxPaint.setColor(DEFAULT_TEXT_COLOR);
    boxPaint.setStyle(Paint.Style.STROKE);

    updateBoxSize();
  }

  private void updateBoxSize() {
    boxSize = 2 * -textPaint.ascent();
  }

  public void setTypeface(final Typeface typeface) {
    textPaint.setTypeface(typeface);
    updateBoxSize();
  }

  public void setTextSize(final float textSize) {
    textPaint.setTextSize(textSize);
  }

  public void setColor(final int color) {
    textPaint.setColor(color);
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


    if (fusionEventTiming.isDuring()) {
      final String festivalDay = fusionEventTiming.getFestivalDayString();
      final String festivalHour = fusionEventTiming.getFestivalHourString();
      textPaint.setTextAlign(Paint.Align.RIGHT);
      final float boxMargin = (bounds.height() - boxSize) / 2;
      final float horizontalTextAnchor = bounds.right - boxSize - boxMargin - SPACE;

      canvas.drawText(festivalDay, horizontalTextAnchor, bounds.exactCenterY() - (textPaint.descent() / 2), textPaint);
      canvas.drawText(festivalHour, horizontalTextAnchor, bounds.exactCenterY() - textPaint.ascent() - (textPaint.descent() / 2), textPaint);

      canvas.drawRect(bounds.right - boxMargin - boxSize,
                      bounds.top + boxMargin,
                      bounds.right - boxMargin,
                      bounds.bottom - boxMargin,
                      boxPaint);
      final float hourFraction = fusionEventTiming.getHourFraction();
      if (hourFraction != 0) {
        canvas.drawRect(bounds.right - boxMargin - boxSize + 1,
                        bounds.top + boxMargin + 1,
                        bounds.right - boxMargin - ((boxSize - 2) * (1 - hourFraction)) - 1,
                        bounds.bottom - boxMargin - 1,
                        textPaint);
      }
    } else {
      final String countdown = fusionEventTiming.getCountdownString();
      textPaint.setTextAlign(Paint.Align.CENTER);
      canvas.drawText(countdown, bounds.exactCenterX(), bounds.exactCenterY() - (textPaint.ascent() + textPaint.descent())/ 2, textPaint);
    }

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
