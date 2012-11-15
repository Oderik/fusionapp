package de.oderik.fusionlwp.event;

/**
 * @author Maik.Riechel
 * @since 15.11.12
 */
public class FestivalStartEvent extends BaseEvent {
  public FestivalStartEvent(final TimeBase timeBase) {
    super(timeBase);
  }

  @Override
  public long getTimestamp() {
    //TODO
    return timeBase.getTimestamp();
  }
}
