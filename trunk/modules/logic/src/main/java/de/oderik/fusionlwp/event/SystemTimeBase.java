package de.oderik.fusionlwp.event;

/**
 * TimeBase that just returns the current system time.
 *
 * @author Maik.Riechel
 * @since 15.11.12
 */
public class SystemTimeBase implements TimeBase {

  @Override
  public long getTimestamp() {
    return System.currentTimeMillis();
  }
}
