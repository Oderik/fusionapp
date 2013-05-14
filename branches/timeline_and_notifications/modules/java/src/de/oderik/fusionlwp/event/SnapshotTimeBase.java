package de.oderik.fusionlwp.event;

/**
 * TimeBase that stores the timestamp of another TimeBase, thus allowing multiple clients to synchronize.
 *
 * @author Maik.Riechel
 * @since 15.11.12
 */
public class SnapshotTimeBase implements TimeBase {

  private final TimeBase timeBase;
  private long timestamp;

  /**
   * @param timeBase the source TimeBase
   */
  public SnapshotTimeBase(final TimeBase timeBase) {
    this.timeBase = timeBase;
    update();
  }

  @Override
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Take a new snapshot of the source TimeBase.
   */
  public void update() {
    timestamp = timeBase.getTimestamp();
  }
}
