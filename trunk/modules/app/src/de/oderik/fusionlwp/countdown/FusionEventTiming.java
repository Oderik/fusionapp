package de.oderik.fusionlwp.countdown;

import de.oderik.fusionlwp.event.FusionEventCalculation;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Handles time calculations for the countdown.
 *
 * @author Oderik
 */
public class FusionEventTiming {

  private long now;
  private long nextFusionStart;
  private long nextFusionEnd;
  private boolean during;

  public final static long SECOND = 1000;
  public final static long MINUTE = 60 * SECOND;
  public final static long HOUR   = 60 * MINUTE;
  public final static long DAY    = 24 * HOUR;

  // Do all calculations in fusion festival time zone
  private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
  private final FusionEventCalculation fusionEventCalculation = new FusionEventCalculation(calendar);

  private static String[] TIMETABLE_DAY_OF_WEEK_NAMES_SHORT = {"EI", "SO", "MO", "DI", "MI", "DO", "FR", "SA"};
  private static String[] TIMETABLE_DAY_OF_WEEK_NAMES = {"KAPUTT", "SONNTAG", "MONTAG", "DIENSTAG", "MITTWOCH", "DONNERSTAG", "FREITAG", "SAMSTAG"};

  public FusionEventTiming() {
    update();
  }

  /**
   * Update values based on the current system time.
   *
   * @return <code>true</code> if the festival started or ended since last call
   * (and thus the countdown needs to switch between countdown and festival modes)
   */
  public boolean update() {
    now = System.currentTimeMillis();
    //TODO only recalculate if event is in the past or more than one year in the future
    updateNextFusionStart();
    updateNextFusionEnd();
    if (during == nextFusionEnd < nextFusionStart) {
      return false;
    } else {
      during = !during;
      return true;
    }
  }

  /**
   * @return the milliseconds left until to the next tick.
   * A tick occurs every second in countdown mode and every minute in festival mode.
   */
  public long timeToNextTick() {
    final long interval = getInterval();
    return interval - (System.currentTimeMillis() % interval);
  }

  /**
   * @return the timestamp of the next tick.
   * A tick occurs every second in countdown mode and every minute in festival mode.
   */
  public long nextTick() {
    final long interval = getInterval();
    return (System.currentTimeMillis() / interval + 1) * interval;
  }

  private void ensureInFuture(final Calendar calendar) {
    while (calendar.getTimeInMillis() < System.currentTimeMillis()) {
      calendar.roll(Calendar.YEAR, true);
    }
  }

  private void updateNextFusionStart() {
    final Calendar calendar = fusionEventCalculation.getFusionCalendar(now);
    ensureInFuture(calendar);
    nextFusionStart = calendar.getTimeInMillis();
  }

  private void updateNextFusionEnd() {
    final Calendar calendar = fusionEventCalculation.getFusionCalendar(now);
    calendar.roll(Calendar.DAY_OF_YEAR, 4);
    ensureInFuture(calendar);
    nextFusionEnd = calendar.getTimeInMillis();
  }

  /**
   * @return in milliseconds the length of a second or minute, depending on if we are currently in countdown or festival mode.
   */
  public long getInterval() {
    return during ? MINUTE : SECOND;
  }

  /**
   * @return <code>true</code> if we are in festival mode, <code>false</code> otherwise.
   */
  public boolean isDuring() {
    return during;
  }

  /**
   * @return a String representing the current day (during festival mode).
   */
  public String getFestivalDayString() {
    calendar.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
    calendar.setTimeInMillis(now);

    return TIMETABLE_DAY_OF_WEEK_NAMES[calendar.get(Calendar.DAY_OF_WEEK)];
  }

  /**
   * @return a String representing the current hour of the day (during festival mode).
   */
  public String getFestivalHourString() {
    calendar.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
    calendar.setTimeInMillis(now);

    return String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
  }

  /**
   * @return a String representing the current countdown (in countdown mode).
   */
  public String getCountdownString() {
    final long days = (nextFusionStart - now) / DAY;
    final long hours = (nextFusionStart - now) % DAY / HOUR;
    final long minutes = (nextFusionStart - now) % HOUR / MINUTE;
    final long seconds = (nextFusionStart - now) % MINUTE / SECOND;

    return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
  }

  /**
   * @return a value between 0 and 1 representing the progress of the current hour of the day (in festival mode).
   */
  public float getHourFraction() {
    calendar.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
    calendar.setTimeInMillis(now);

    return calendar.get(Calendar.MINUTE) / 60f;
  }
}
