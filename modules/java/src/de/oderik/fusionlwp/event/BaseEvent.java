package de.oderik.fusionlwp.event;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Maik.Riechel
 * @since 15.11.12
 */
public abstract class BaseEvent implements Event {

  protected final TimeBase timeBase;
  protected final Calendar calendar;
  private int iteration = 0;

  protected BaseEvent(final TimeBase timeBase) {
    this(timeBase, Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin")));
  }

  protected BaseEvent(final TimeBase timeBase, final Calendar calendar) {
    this.timeBase = timeBase;
    this.calendar = calendar;
  }

  @Override
  public void setIteration(final int iteration) {
    this.iteration = iteration;
  }

  protected int getIteration() {
    return iteration;
  }
}
