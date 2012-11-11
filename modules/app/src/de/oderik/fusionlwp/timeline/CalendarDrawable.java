package de.oderik.fusionlwp.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.util.Tag;

import java.util.Calendar;

/**
 * @author maik.riechel
 * @since 29.10.12
 */
public class CalendarDrawable extends PaintDrawable {
  private static final String TAG = Tag.of(CalendarDrawable.class);

  private static final int MAX_COLOR_COMPONENT     = 0xFF;
  private static final int AMBIENT_COLOR_COMPONENT = 0x3F;
  private static final int ACTIVE_COLOR_COMPONENT  = MAX_COLOR_COMPONENT - AMBIENT_COLOR_COMPONENT;

  private final String[] monthNames;

  private final Calendar calendar;

  private Rect clipBounds = new Rect();
  private final int                  dayCountOfYear;
  private final Calendar             today;

  private final int   dayHeight;
  private final float lineSpace;

  public CalendarDrawable(final Context context) {
    today = Calendar.getInstance();
    calendar = Calendar.getInstance();
    calendar.clear();
    calendar.set(Calendar.YEAR, 2012);

    dayCountOfYear = getDayCountOfYear();

    paint.setColor(0xffffffff);
    paint.setAntiAlias(true);
    paint.setTextSize(context.getResources().getDimension(R.dimen.calendar_text_size));

    monthNames = buildMonthNames();

    final float fontHeight = paint.getFontMetrics(null);
    lineSpace = fontHeight / 10;
    dayHeight = (int) (fontHeight + lineSpace);
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


  @Override
  public void draw(final Canvas canvas) {
    final boolean hasClipBounds = canvas.getClipBounds(clipBounds);
    if (!hasClipBounds) {
      Log.w(TAG, String.format("no clipbounds"));
    }
    final Rect bounds = getBounds();

    final int left = hasClipBounds ? clipBounds.left : bounds.left;
    final int right = hasClipBounds ? clipBounds.right : bounds.right;

    final int start = hasClipBounds ? clipBounds.top / dayHeight : 0;
    final int end = hasClipBounds ? Math.min(1 + clipBounds.bottom / dayHeight, dayCountOfYear) : dayCountOfYear;

    calendar.set(Calendar.YEAR, 2012);

    calendar.set(Calendar.DAY_OF_YEAR, start + 1);
    final int startMonth = calendar.get(Calendar.MONTH);
    calendar.set(Calendar.DAY_OF_YEAR, end);
    final int endMonth = calendar.get(Calendar.MONTH);
    canvas.save();
    canvas.translate(left, clipBounds.top);
    canvas.rotate(270);
    if (startMonth == endMonth) {
      paint.setTextAlign(Paint.Align.CENTER);
      canvas.drawText(monthNames[startMonth], -clipBounds.height() / 2, clipBounds.width() - paint.descent(), paint);
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
    paint.setTextAlign(Paint.Align.RIGHT);
    for (int day = start; day < end; day++) {
      final int dayY = day * dayHeight;

      calendar.set(Calendar.DAY_OF_YEAR, day + 1);

      final boolean today = isToday(calendar);
      final String text;
      if (today) {
        text = "heute";
      } else {
        text = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
      }
      if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
        canvas.drawLine(left, dayY, right, dayY, paint);

      }
      canvas.drawText(text, clipBounds.width() - dayHeight, dayY - paint.ascent() + lineSpace, paint);
    }
  }

  private int getDayColor(final int day, final int daysInYear) {
    return Color.rgb(
            red(day, daysInYear),
            green(day, daysInYear),
            blue(day, daysInYear)
                    );
  }

  private int blue(final int day, final int daysInYear) {
    final int halfOfDaysInYear = daysInYear / 2;
    if (day > halfOfDaysInYear) {
      return AMBIENT_COLOR_COMPONENT;
    }
    return (halfOfDaysInYear - day) * ACTIVE_COLOR_COMPONENT / halfOfDaysInYear + AMBIENT_COLOR_COMPONENT;
  }

  private int green(final int day, final int daysInYear) {
    final int halfOfDaysInYear = daysInYear / 2;
    return (halfOfDaysInYear - Math.abs(halfOfDaysInYear - day)) * ACTIVE_COLOR_COMPONENT / halfOfDaysInYear + AMBIENT_COLOR_COMPONENT;
  }

  private int red(final int day, final int daysInYear) {
    final int halfOfDaysInYear = daysInYear / 2;
    if (day < halfOfDaysInYear) {
      return AMBIENT_COLOR_COMPONENT;
    }
    return (day - halfOfDaysInYear) * ACTIVE_COLOR_COMPONENT / halfOfDaysInYear + AMBIENT_COLOR_COMPONENT;
  }

  private boolean isToday(final Calendar calendar) {
    return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
           && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR);
  }

  @Override
  public int getIntrinsicHeight() {
    return getDayCountOfYear() * dayHeight;
  }

  private int getDayCountOfYear() {
    return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
  }
}
