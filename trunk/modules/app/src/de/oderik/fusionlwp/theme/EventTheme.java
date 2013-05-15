package de.oderik.fusionlwp.theme;

import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 14.05.13 18:38
 */
public enum EventTheme {
  CURRENT(
          R.drawable.countdown_panel_2013,
          R.drawable.background_2013),

  FUSION_2012(
          R.drawable.countdown_panel_2012,
          R.drawable.background_2012);

  public final int countdownPanel;
  public final int backgroundDrawable;

  private EventTheme(final int countdownPanel, final int background) {
    this.countdownPanel = countdownPanel;
    backgroundDrawable = background;
  }
}
