package de.oderik.fusionlwp.event;

/**
 * Next occurrence of a periodic (probably yearly) event. A TimeBase should be used to determine which occurrence is actually the next.
 *
 * @author Maik.Riechel
 * @since 15.11.12
 */
public interface Event extends TimeBase {

  /**
   * @param iteration the occurrence offset of the event. A value of 0 (default) means the next occurrence of the event.
   *                  Negative values represent past events (e.g. a value of <code>-1</code> represents the previous occurrence),
   *                  positive values represent occurrences that are even further in the future than the next.
   */
  void setIteration(int iteration);
}
