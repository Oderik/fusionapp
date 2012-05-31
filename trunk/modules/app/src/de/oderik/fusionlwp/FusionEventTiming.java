package de.oderik.fusionlwp;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class FusionEventTiming {

  private static FusionEventTiming INSTANCE = new FusionEventTiming();

  public static FusionEventTiming get() {
    return INSTANCE;
  }

  public final static long SECOND = 1000;
  public final static long MINUTE = 60 * SECOND;
  public final static long HOUR   = 60 * MINUTE;
  public final static long DAY    = 24 * HOUR;

  public enum Phase {
    BEFORE (SECOND),
    DURING (MINUTE);

    private final long interval;

    Phase(final long interval) {
      this.interval = interval;
    }

    public long getInterval() {
      return interval;
    }
  }

  private static String[] TIMETABLE_DAY_OF_WEEK_NAMES = {"", "SO", "MO", "DI", "MI", "DO", "FR", "SA"};

  private Phase phase = Phase.BEFORE;

  private FusionEventTiming() {
  }

  public long timeToNextSecond() {
    return SECOND - (System.currentTimeMillis() % SECOND);
  }

  public long nextSecond() {
    return (System.currentTimeMillis() / SECOND + 1) * SECOND;
  }

  public static String format(final long time) {
    final long days = time / DAY;
    final long hours = time % DAY / HOUR;
    final long minutes = time % HOUR / MINUTE;
    final long seconds = time % MINUTE / SECOND;

    return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
  }

  private Calendar getFusionCalendar() {
    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
    final int currentYear = calendar.get(Calendar.YEAR);
    calendar.clear();
    calendar.set(Calendar.YEAR, currentYear);
    calendar.set(Calendar.MONTH, Calendar.JUNE);
    calendar.set(Calendar.HOUR_OF_DAY, 18);
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
    calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, -Calendar.THURSDAY);
    return calendar;
  }

  private void ensureInFuture(final Calendar calendar) {
    while (calendar.getTimeInMillis() < System.currentTimeMillis()) {
      calendar.roll(Calendar.YEAR, true);
    }
  }

  private long nextFusionStart() {
    final Calendar calendar = getFusionCalendar();
    ensureInFuture(calendar);
    return calendar.getTimeInMillis();
  }

  private long nextFusionEnd() {
    final Calendar calendar = getFusionCalendar();
    calendar.roll(Calendar.DAY_OF_YEAR, 5);
    ensureInFuture(calendar);
    return calendar.getTimeInMillis();
  }

  private Phase getCurrentPhase() {
    return nextFusionStart() < nextFusionEnd() ? Phase.BEFORE : Phase.DURING;
  }
}
