package de.oderik.fusionlwp;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created: 16.05.12
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
  private Calendar calendar;

  public String getCountdownString() {
    return format(nextFusionStart - now);
  }

  private static String[] TIMETABLE_DAY_OF_WEEK_NAMES_SHORT = {"", "SO", "MO", "DI", "MI", "DO", "FR", "SA"};
  private static String[] TIMETABLE_DAY_OF_WEEK_NAMES = {"", "SONNTAG", "MONTAG", "DIENSTAG", "MITTWOCH", "DONNERSTAG", "FREITAG", "SONNABEND"};

  public FusionEventTiming() {
    calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
    update();
  }

  public boolean update() {
    now = System.currentTimeMillis();
    updateNextFusionStart();
    updateNextFusionEnd();
    if (during == nextFusionEnd < nextFusionStart) {
      return false;
    } else {
      during = !during;
      return true;
    }
  }

  public long timeToNextTick() {
    final long interval = getInterval();
    return interval - (System.currentTimeMillis() % interval);
  }

  public long nextTick() {
    final long interval = getInterval();
    return (System.currentTimeMillis() / interval + 1) * interval;
  }

  public static String format(final long time) {
    final long days = time / DAY;
    final long hours = time % DAY / HOUR;
    final long minutes = time % HOUR / MINUTE;
    final long seconds = time % MINUTE / SECOND;

    return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
  }

  private Calendar getFusionCalendar() {
    calendar.setTimeInMillis(now);
    final int currentYear = calendar.get(Calendar.YEAR);
    calendar.clear();
    calendar.set(Calendar.YEAR, currentYear);
    calendar.set(Calendar.MONTH, Calendar.JULY);
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

  private void updateNextFusionStart() {
    final Calendar calendar = getFusionCalendar();
    ensureInFuture(calendar);
    nextFusionStart = calendar.getTimeInMillis();
  }

  private void updateNextFusionEnd() {
    final Calendar calendar = getFusionCalendar();
    calendar.roll(Calendar.DAY_OF_YEAR, 4);
    ensureInFuture(calendar);
    nextFusionEnd = calendar.getTimeInMillis();
  }

  public long getInterval() {
    return during ? MINUTE : SECOND;
  }

  public boolean isDuring() {
    return during;
  }
}
