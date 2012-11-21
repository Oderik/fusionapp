package de.oderik.fusionlwp.event;

/**
 * @author maik.riechel
 * @since 19.11.12
 */
public class ProxyEvent implements Event {

  private final Event delegate;

  public ProxyEvent(final Event delegate) {
    this.delegate = delegate;
  }

  @Override
  public void setIteration(final int iteration) {
    delegate.setIteration(iteration);
  }

  @Override
  public long getTimestamp() {
    return delegate.getTimestamp();
  }
}
