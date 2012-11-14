package de.oderik.fusionlwp.timeline;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import de.oderik.fusionlwp.BuildConfig;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.util.Tag;

import java.util.Calendar;

/**
 * @author maik.riechel
 * @since 13.11.12
 */
public class CalendarView extends View {
  private static final String TAG = Tag.of(CalendarView.class);

  private static final int MAX_COLOR_COMPONENT     = 0xFF;
  private static final int AMBIENT_COLOR_COMPONENT = 0x3F;
  private static final int ACTIVE_COLOR_COMPONENT  = MAX_COLOR_COMPONENT - AMBIENT_COLOR_COMPONENT;

  private final String[] monthNames;

  private final Calendar calendar;

  private Rect clipBounds = new Rect();
  private final int      dayCountOfYear;
  private final Calendar today;

  private final int   dayHeight;
  private final float dayWidth;
  private final float lineSpace;
  private final Rect  salesPhaseRect;
  private final Rect clippedSalesPhaseRect = new Rect();

  private final Paint paint;
  private final Paint eraserPaint;

  private int maxScrollY;

  private final GestureDetector gestureDetector;
  private final Scroller        scroller;
  private final Runnable scrollRunnable = new Runnable() {
    @Override
    public void run() {
      if (scroller.computeScrollOffset()) {
        scrollTo(0, scroller.getCurrY());
        post(this);
      }
    }
  };

  private long    debugTimestamp = SystemClock.uptimeMillis();
  private boolean initialized    = false;

  public CalendarView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CalendarView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    today = Calendar.getInstance();
    calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(Calendar.YEAR, 2012);

    dayCountOfYear = getDayCountOfYear();

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(0xffffffff);
    paint.setTextSize(context.getResources().getDimension(R.dimen.calendar_text_size));

    monthNames = buildMonthNames();

    final float fontHeight = paint.getFontMetrics(null);
    lineSpace = fontHeight / 10;
    dayHeight = (int) (fontHeight + lineSpace);

    float tempDayWidth = 0;
    for (int i = 10; i <= 31; i++) {
      tempDayWidth = Math.max(paint.measureText(Integer.toString(i)), tempDayWidth);
    }
    dayWidth = tempDayWidth;

    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
    final int top = (calendar.get(Calendar.DAY_OF_YEAR) - 1) * dayHeight;

    calendar.set(Calendar.MONTH, Calendar.APRIL);
    final int bottom = (calendar.get(Calendar.DAY_OF_YEAR) - 1) * dayHeight - 1;
    salesPhaseRect = new Rect(0, top, 1, bottom);

    eraserPaint = new Paint(paint);
    eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    eraserPaint.setColor(0x00000000);
    eraserPaint.setTextAlign(Paint.Align.RIGHT);

    gestureDetector = new GestureDetector(context, new OnScrollGestureListener());
    scroller = new Scroller(context);

    setMinimumWidth((int) (2 * dayHeight + dayWidth));
    setMinimumHeight(dayHeight * dayCountOfYear);
  }

  private static String[] buildMonthNames() {
    final String[] monthNames = new String[12];
    final Calendar calendar = Calendar.getInstance();
    for (int month = 0; month < 12; month++) {
      calendar.set(Calendar.MONTH, month);
      monthNames[month] = String.format("%tB", calendar);
    }
    return monthNames;
  }

  private int getDayCountOfYear() {
    return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
  }

  @Override
  protected void onDraw(final Canvas canvas) {
    if (BuildConfig.DEBUG) {
      final long newTimestamp = SystemClock.uptimeMillis();
      final long elapsed = newTimestamp - debugTimestamp;
      debugTimestamp = newTimestamp;
      Log.v(TAG, elapsed + "ms since last frame, that's " + 1000 / elapsed + "fps.");
    }

    final boolean hasClipBounds = canvas.getClipBounds(clipBounds);
    if (!hasClipBounds) {
      Log.w(TAG, String.format("no clipbounds"));
      clipBounds.set(getLeft(), getTop(), getRight(), getBottom());
    }

    final int start = hasClipBounds ? clipBounds.top / dayHeight : 0;
    final int end = hasClipBounds ? Math.min(1 + clipBounds.bottom / dayHeight, dayCountOfYear) : dayCountOfYear;

    drawYear(canvas, start, end);

    drawEvents(canvas, start, end);
  }

  private void drawEvents(final Canvas canvas, final int start, final int end) {
    if (clippedSalesPhaseRect.setIntersect(clipBounds, salesPhaseRect)) {

      canvas.saveLayer(clippedSalesPhaseRect.left, clippedSalesPhaseRect.top, clippedSalesPhaseRect.right, clippedSalesPhaseRect.bottom, null, Canvas.ALL_SAVE_FLAG);
      canvas.drawRect(clippedSalesPhaseRect, paint);
      canvas.rotate(270);
      final String ticketanmeldung = "Ticketanmeldung";
      final float width = eraserPaint.measureText(ticketanmeldung);
      canvas.drawText(ticketanmeldung, -clippedSalesPhaseRect.top - width, clippedSalesPhaseRect.left - eraserPaint.ascent(), eraserPaint);

      //canvas.drawCircle(salesPhaseRect.centerX(), salesPhaseRect.centerY(), salesPhaseRect.width(), eraserPaint);
      canvas.restore();
    }
  }

  @Override
  protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);

  }

  private void drawYear(final Canvas canvas, final int start, final int end) {

    calendar.set(Calendar.YEAR, 2012);

    drawDays(canvas, start, end);

    drawMonths(canvas, start, end);
  }

  private void drawDays(final Canvas canvas, final int start, final int end) {
    paint.setTextAlign(Paint.Align.RIGHT);
    for (int day = start; day < end; day++) {
      final int dayY = day * dayHeight;

      calendar.set(Calendar.DAY_OF_YEAR, day + 1);

      if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
        canvas.drawLine(getLeft(), dayY, getRight(), dayY, paint);
      }

      final boolean today = isToday(calendar);
      final String text;
      if (today) {
        canvas.saveLayer(getLeft(), dayY, getRight() - dayHeight, dayY + dayHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(getLeft(), dayY, getRight() - dayHeight, dayY + dayHeight, paint);
      }
      text = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

      canvas.drawText(text, clipBounds.width() - dayHeight, dayY - paint.ascent() + lineSpace, today ? eraserPaint : paint);

      if (today) {
        canvas.restore();
      }
    }
  }

  private void drawMonths(final Canvas canvas, final int start, final int end) {
    calendar.set(Calendar.DAY_OF_YEAR, start + 1);
    final int startMonth = calendar.get(Calendar.MONTH);
    calendar.set(Calendar.DAY_OF_YEAR, end);
    final int endMonth = calendar.get(Calendar.MONTH);
    canvas.save();
    canvas.translate(getLeft(), clipBounds.top);
    canvas.rotate(270);
    if (startMonth == endMonth) {
      paint.setTextAlign(Paint.Align.CENTER);
      canvas.drawText(monthNames[startMonth], -clipBounds.height() / 2, getWidth() - paint.descent(), paint);
    } else {
      int daysInMonth;
      int dayOfMonth;
      int space;
      float textWidth;
      int offset = 0;

      calendar.set(Calendar.DAY_OF_YEAR, start + 1);
      daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
      space = (daysInMonth - dayOfMonth + 1) * dayHeight - clipBounds.top % dayHeight;
      textWidth = paint.measureText(monthNames[startMonth]);
      if (textWidth > space) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(monthNames[startMonth], -space, clipBounds.width() - paint.descent(), paint);
      } else {
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(monthNames[startMonth], -space / 2, clipBounds.width() - paint.descent(), paint);
      }

      for (int month = startMonth + 1; month < endMonth; month++) {
        offset += space;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month);
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        space = daysInMonth * dayHeight;
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(monthNames[month], -space / 2 - offset, clipBounds.width() - paint.descent(), paint);
      }

      calendar.set(Calendar.DAY_OF_YEAR, end);
      dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) - 1;
      space = dayOfMonth * dayHeight + clipBounds.bottom % dayHeight;
      textWidth = paint.measureText(monthNames[endMonth]);
      if (textWidth > space) {
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(monthNames[endMonth], -clipBounds.height() + space, clipBounds.width() - paint.descent(), paint);
      } else {
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(monthNames[endMonth], -clipBounds.height() + space / 2, clipBounds.width() - paint.descent(), paint);
      }
    }
    canvas.restore();
  }

  private boolean isToday(final Calendar calendar) {
    return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
           && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
  }

  @Override
  protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    if (changed) {
      salesPhaseRect.right = (int) (right - dayHeight - dayWidth);
      salesPhaseRect.left = (int) (right - 2 * dayHeight - dayWidth);

      maxScrollY = dayCountOfYear * dayHeight - getHeight();
    }
  }

  @Override
  public boolean onTouchEvent(final MotionEvent event) {
    return
            gestureDetector.onTouchEvent(event) ||
            super.onTouchEvent(event);
  }

  private class OnScrollGestureListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onDown(final MotionEvent e) {
      scroller.abortAnimation();
      return true;
    }

    @Override
    public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
      scrollBy(0, (int) distanceY);
      final int scrollY = getScrollY();
      if (scrollY < 0) {
        scrollTo(0, 0);
      } else if (scrollY > maxScrollY) {
        scrollTo(0, maxScrollY);
      }
      return true;
    }

    @Override
    public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
      scroller.fling(0, getScrollY(), 0, (int) -velocityY, 0, 0, 0, maxScrollY);
      post(scrollRunnable);
      return true;
    }
  }

}