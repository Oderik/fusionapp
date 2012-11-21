package de.oderik.fusionlwp.event;

import java.util.Calendar;

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
    configure(calendar, 2011, Calendar.APRIL, 1, 12);
    assertTimestamp(2011, Calendar.JUNE, 30, 18, event.getTimestamp());

    configure(calendar, 2011, Calendar.JULY, 1, 12);
    assertTimestamp(2012, Calendar.JUNE, 28, 18, event.getTimestamp());


  }

}
