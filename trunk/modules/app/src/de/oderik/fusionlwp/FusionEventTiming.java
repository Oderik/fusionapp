package de.oderik.fusionlwp;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created: 16.05.12
 *
 * @author Oderik
 */
public class FusionEventTiming {
  private final static Calendar FUSION_CALENDAR = GregorianCalendar.getInstance();

  static {
    FUSION_CALENDAR.clear();
    //TODO timezone
    FUSION_CALENDAR.set(Calendar.YEAR, 2012);
    FUSION_CALENDAR.set(Calendar.MONTH, Calendar.JUNE);
    FUSION_CALENDAR.set(Calendar.DAY_OF_MONTH, 28);
    FUSION_CALENDAR.set(Calendar.HOUR_OF_DAY, 18);
    FUSION_CALENDAR.set(Calendar.MINUTE, 0);
  }

  public final static long SECOND = 1000;
  public final static long MINUTE = 60 * SECOND;
  public final static long HOUR   = 60 * MINUTE;
  public final static long DAY    = 24 * HOUR;

  public static long timeToFusion() {
    final Calendar now = Calendar.getInstance();
    return FUSION_CALENDAR.getTimeInMillis() - now.getTimeInMillis();
  }


  public static long timeToNextSecond() {
    return SECOND - (System.currentTimeMillis() % SECOND);
  }

  public static long nextSecond() {
    return (System.currentTimeMillis() / SECOND + 1) * SECOND;
  }

  public static String format(final long time) {
    final long days = time / DAY;
    final long hours = time % DAY / HOUR;
    final long minutes = time % HOUR / MINUTE;
    final long seconds = time % MINUTE / SECOND;

    return String.format("%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
  }
}
