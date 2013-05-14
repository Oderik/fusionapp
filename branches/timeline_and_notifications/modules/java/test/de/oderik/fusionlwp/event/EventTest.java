package de.oderik.fusionlwp.event;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Maik.Riechel
 * @since 21.11.12
 */
public abstract class EventTest extends TestCase {

  protected Event    event;
  protected Calendar calendar;

  protected Calendar createCalendar() {
    return Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
  }

  protected Calendar configure(final Calendar calendar, int year, int month, int day, int hour) {
    calendar.clear();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    return calendar;
  }

  protected void assertTimestamp(final int year, final int month, final int day, final int hour, final long timestamp) {
    final Calendar calendar = createCalendar();
    calendar.setTimeInMillis(timestamp);

    assertEquals("year", year, calendar.get(Calendar.YEAR));
    assertEquals("month", month, calendar.get(Calendar.MONTH));
    assertEquals("day", day, calendar.get(Calendar.DAY_OF_MONTH));
    assertEquals("hour", hour, calendar.get(Calendar.HOUR_OF_DAY));
  }

  @Override
  public void setUp() throws Exception {
    calendar = createCalendar();
    event = createEvent(new CalendarTimeBase(calendar));
  }

  protected abstract Event createEvent(TimeBase timeBase);
}
