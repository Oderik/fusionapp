package de.oderik.fusionlwp.timeline;

import android.widget.FrameLayout;

/**
* @author Maik.Riechel
* @since 28.10.12
*/
public interface OnScrollChangeListener {
  void onScrollChanged(FrameLayout scrollView, int l, int t, int oldl, int oldt);
}
