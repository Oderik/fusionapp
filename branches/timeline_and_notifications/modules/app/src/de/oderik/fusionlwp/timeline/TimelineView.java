package de.oderik.fusionlwp.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import de.oderik.fusionlwp.util.Tag;

/**
 * @author Maik.Riechel
 * @since 28.10.12
 */
public class TimelineView extends View {
  private static final String TAG = Tag.of(TimelineView.class);

  private RocketDrawable rocketDrawable;

  public TimelineView(Context context) {
    super(context);
    init(context);
  }

  public TimelineView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
    init(context);
  }

  public TimelineView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private void init(final Context context) {
    rocketDrawable = new RocketDrawable(context);
  }

  @Override
  protected void onDraw(final Canvas canvas) {
    rocketDrawable.draw(canvas);
  }

  @Override
  protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
    rocketDrawable.setBounds(0, 0, w - 1, h - 1);
  }

  /**
   * @deprecated this is just a temporary hack to interact with a ScrollView
   */
  @Deprecated
  public void setLevel(final int level) {
    rocketDrawable.setLevel(level);
  }
}
