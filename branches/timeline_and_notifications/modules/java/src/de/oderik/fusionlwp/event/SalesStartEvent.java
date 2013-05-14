package de.oderik.fusionlwp.event;

import java.util.Calendar;

/**
 * @author Maik.Riechel
 * @since 15.11.12
 */
public class SalesStartEvent extends BaseEvent {
  public SalesStartEvent(final TimeBase timeBase) {
    super(timeBase);
  }

  public SalesStartEvent(final TimeBase timeBase, final Calendar calendar) {
    super(timeBase, calendar);
  }

  @Override
  protected long calculateTimestamp(final long timebase, final int iteration) {
    calendar.setTimeInMillis(timebase);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
    while (timebase > calendar.getTimeInMillis()) {
      calendar.add(Calendar.YEAR, 1);
    }
    calendar.roll(Calendar.YEAR, getIteration());
    return calendar.getTimeInMillis();
  }
}
