package de.oderik.fusionlwp.event;

import java.util.Calendar;

import static java.util.Calendar.*;

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
    final int currentYear = calendar.get(YEAR);
    return getFusionCalendarOfYear(currentYear);
  }

  public Calendar getFusionCalendarOfYear(final int year) {
    calendar.clear();
    calendar.set(YEAR, year);
    calendar.set(MONTH, JUNE);
    calendar.set(DAY_OF_WEEK, THURSDAY);
    calendar.set(HOUR_OF_DAY, 18);
    calendar.set(DAY_OF_WEEK_IN_MONTH, -1);
    return calendar;
  }
}
