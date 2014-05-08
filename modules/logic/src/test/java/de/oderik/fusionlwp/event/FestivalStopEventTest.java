package de.oderik.fusionlwp.event;

import java.util.Calendar;

import static java.util.Calendar.*;

/**
 * @author Maik.Riechel
 * @since 21.11.12
 */
public class FestivalStopEventTest extends EventTest {

  @Override
  protected Event createEvent(final TimeBase timeBase) {
    return new FestivalStopEvent(timeBase);
  }

  public void testGetTimestamp() throws Exception {
    configure(calendar, 2014, MAY, 7, 10);
    assertTimestamp(2014, JUNE, 30, 18, event.getTimestamp());

    configure(calendar, 2011, APRIL, 1, 12);
    assertTimestamp(2011, JULY, 4, 18, event.getTimestamp());

    configure(calendar, 2011, NOVEMBER, 1, 12);
    assertTimestamp(2012, JULY, 2, 18, event.getTimestamp());


  }

}
