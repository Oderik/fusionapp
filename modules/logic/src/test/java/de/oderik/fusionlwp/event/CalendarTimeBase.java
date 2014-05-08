package de.oderik.fusionlwp.event;

import java.util.Calendar;

/**
 * @author Maik.Riechel
 * @since 21.11.12
 */
public class CalendarTimeBase implements TimeBase {

  private final Calendar calendar;

  public CalendarTimeBase(final Calendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public long getTimestamp() {
    return calendar.getTimeInMillis();
  }

  public Calendar getCalendar() {
    return calendar;
  }
}
