package de.oderik.fusionlwp.event;

import java.util.Calendar;

/**
 * @author Maik.Riechel
 * @since 15.11.12
 */
public class FestivalStopEvent extends BaseEvent {

  private final FusionEventCalculation fusionEventCalculation;

  public FestivalStopEvent(final TimeBase timeBase) {
    super(timeBase);
    fusionEventCalculation = new FusionEventCalculation(calendar);
  }

  protected FestivalStopEvent(final TimeBase timeBase, final Calendar calendar) {
    super(timeBase, calendar);
    fusionEventCalculation = new FusionEventCalculation(this.calendar);
  }

  @Override
  protected long calculateTimestamp(final long timebase, final int iteration) {
    calendar.setTimeInMillis(timebase);
    final int month = calendar.get(Calendar.MONTH);
    final int year = calendar.get(Calendar.YEAR);
    if (month < Calendar.JUNE) {
      return getFusionCalendar(year + iteration).getTimeInMillis();
    } else if (month > Calendar.JULY) {
      return getFusionCalendar(year + iteration + 1).getTimeInMillis();
    } else {
      final long fusionTimeInMillis = getFusionCalendar(year).getTimeInMillis();
      if (fusionTimeInMillis > timebase) {
        return iteration == 0 ? fusionTimeInMillis : getFusionCalendar(year + iteration).getTimeInMillis();
      } else {
        return iteration == -1 ? fusionTimeInMillis : getFusionCalendar(year + iteration + 1).getTimeInMillis();
      }
    }
  }

  private Calendar getFusionCalendar(final int year) {
    final Calendar fusionCalendarOfYear = fusionEventCalculation.getFusionCalendarOfYear(year);
    fusionCalendarOfYear.roll(Calendar.DAY_OF_YEAR, 4);
    return fusionCalendarOfYear;
  }
}
