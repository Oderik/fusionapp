package de.oderik.fusionlwp.timeline;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maik.Riechel
 * @since 28.10.12
 */
public class ScrollView extends android.widget.ScrollView {

  private final List<OnScrollChangeListener> onScrollChangeListeners = new ArrayList<OnScrollChangeListener>();
  
  public ScrollView(Context context) {
    super(context);
  }

  public ScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ScrollView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
    for (OnScrollChangeListener onScrollChangeListener : onScrollChangeListeners) {
      onScrollChangeListener.onScrollChanged(this, l, t, oldl, oldt);
    }
  }

  public void addOnScrollChangeListener(final OnScrollChangeListener onScrollChangeListener) {
    onScrollChangeListeners.add(onScrollChangeListener);
  }

  public boolean removeOnScrollChangeListener(final OnScrollChangeListener onScrollChangeListener) {
    return onScrollChangeListeners.remove(onScrollChangeListener);
  }
}
