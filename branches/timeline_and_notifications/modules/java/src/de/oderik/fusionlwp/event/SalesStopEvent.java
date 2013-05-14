package de.oderik.fusionlwp.event;

import java.util.Calendar;

/**
 * @author Maik.Riechel
 * @since 15.11.12
 */
public class SalesStopEvent extends BaseEvent {
  public SalesStopEvent(final TimeBase timeBase) {
    super(timeBase);
  }

  public SalesStopEvent(final TimeBase timeBase, final Calendar calendar) {
    super(timeBase, calendar);
  }

  @Override
  protected long calculateTimestamp(final long timebase, final int iteration) {
    calendar.setTimeInMillis(timebase);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.MONTH, Calendar.MARCH);
    calendar.roll(Calendar.DAY_OF_MONTH, false);
    while (timebase > calendar.getTimeInMillis()) {
      calendar.roll(Calendar.YEAR, true);
    }
    calendar.roll(Calendar.YEAR, getIteration());
    return calendar.getTimeInMillis();
  }
}
