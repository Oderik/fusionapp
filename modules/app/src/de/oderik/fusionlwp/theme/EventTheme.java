package de.oderik.fusionlwp.theme;

import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 14.05.13 18:38
 */
public enum EventTheme {
  FUSION_2012(
          R.drawable.countdown_panel_2012,
          R.drawable.background_2012,
          R.string.label_theme_2012),

  FUSION_2013(
          R.drawable.countdown_panel_2013,
          R.drawable.background_2013,
          R.string.label_theme_2013);

  public final int countdownPanel;
  public final int backgroundDrawable;
  public final int labelString;

  private EventTheme(final int countdownPanel, final int background, final int label) {
    this.countdownPanel = countdownPanel;
    backgroundDrawable = background;
    labelString = label;
  }

}
