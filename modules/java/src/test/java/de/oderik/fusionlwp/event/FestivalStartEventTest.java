package de.oderik.fusionlwp.event;

import static java.util.Calendar.*;

/**
 * @author Maik.Riechel
 * @since 21.11.12
 */
public class FestivalStartEventTest extends EventTest {

  @Override
  protected Event createEvent(final TimeBase timeBase) {
    return new FestivalStartEvent(timeBase);
  }

  public void testGetTimestamp() throws Exception {
    configure(calendar, 2014, MAY, 7, 7);
    assertTimestamp(2014, JUNE, 26, 18, event.getTimestamp());

    configure(calendar, 2014, JULY, 7, 7);
    assertTimestamp(2015, JUNE, 25, 18, event.getTimestamp());

    configure(calendar, 2016, MAY, 7, 7);
    assertTimestamp(2016, JUNE, 30, 18, event.getTimestamp());

    configure(calendar, 2011, APRIL, 1, 12);
    assertTimestamp(2011, JUNE, 30, 18, event.getTimestamp());

    configure(calendar, 2011, JULY, 1, 12);
    assertTimestamp(2012, JUNE, 28, 18, event.getTimestamp());
  }

}
