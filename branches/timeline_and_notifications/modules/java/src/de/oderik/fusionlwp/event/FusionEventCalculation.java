package de.oderik.fusionlwp.event;

import java.util.Calendar;

/**
 * @author maik.riechel
 * @since 19.11.12
 */
public class FusionEventCalculation {
  private final Calendar calendar;

  public FusionEventCalculation(final Calendar calendar) {
    this.calendar = calendar;
  }

  public Calendar getFusionCalendar(final long now) {
    calendar.setTimeInMillis(now);
    final int currentYear = calendar.get(Calendar.YEAR);
    return getFusionCalendarOfYear(currentYear);
  }

  public Calendar getFusionCalendarOfYear(final int year) {
    calendar.clear();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, Calendar.JULY);
    calendar.set(Calendar.HOUR_OF_DAY, 18);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
    calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, -Calendar.THURSDAY);
    return calendar;
  }
}
