package de.oderik.fusionlwp.event;

/**
 * A TimeBase provides a timestamp as a basis for further calculations, especially {@link Event} calculations.
 * The main purpose of this interface is to allow synchronicity across multiple events.
 *
 * @author Maik.Riechel
 * @since 15.11.12
 */
public interface TimeBase {

  /**
   * @return the current timestamp based on {@link System#currentTimeMillis()}.
   */
  long getTimestamp();
}
