package de.oderik.fusionlwp.event;

import java.util.Calendar;

/**
 * @author Maik.Riechel
 * @since 15.11.12
 */
public class FestivalStartEvent extends BaseEvent {

  private final FusionEventCalculation fusionEventCalculation;

  public FestivalStartEvent(final TimeBase timeBase) {
    super(timeBase);
    fusionEventCalculation = new FusionEventCalculation(calendar);
  }

  protected FestivalStartEvent(final TimeBase timeBase, final Calendar calendar) {
    super(timeBase, calendar);
    fusionEventCalculation = new FusionEventCalculation(this.calendar);
  }

  @Override
  public long getTimestamp() {
    final long now = timeBase.getTimestamp();
    final int iteration = getIteration();
    calendar.setTimeInMillis(now);
    final int month = calendar.get(Calendar.MONTH);
    final int year = calendar.get(Calendar.YEAR);
    if (month < Calendar.JUNE) {
      return fusionEventCalculation.getFusionCalendarOfYear(year + iteration).getTimeInMillis();
    } else if (month > Calendar.JULY) {
      return fusionEventCalculation.getFusionCalendarOfYear(year + iteration + 1).getTimeInMillis();
    } else {
      final long fusionTimeInMillis = fusionEventCalculation.getFusionCalendarOfYear(year).getTimeInMillis();
      if (fusionTimeInMillis > now) {
        return iteration == 0 ? fusionTimeInMillis : fusionEventCalculation.getFusionCalendarOfYear(year + iteration).getTimeInMillis();
      } else {
        return iteration == -1 ? fusionTimeInMillis : fusionEventCalculation.getFusionCalendarOfYear(year + iteration + 1).getTimeInMillis();
      }
    }
  }
}
