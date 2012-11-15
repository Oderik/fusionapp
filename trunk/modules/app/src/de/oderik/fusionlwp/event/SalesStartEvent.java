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
  public long getTimestamp() {
    final long timestamp = timeBase.getTimestamp();
    calendar.setTimeInMillis(timestamp);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
    while (timestamp > calendar.getTimeInMillis()) {
      calendar.roll(Calendar.YEAR, true);
    }
    calendar.roll(Calendar.YEAR, getIteration());
    return calendar.getTimeInMillis();
  }
}
