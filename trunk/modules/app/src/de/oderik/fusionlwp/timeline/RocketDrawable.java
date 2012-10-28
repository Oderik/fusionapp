package de.oderik.fusionlwp.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import de.oderik.fusionlwp.BuildConfig;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.util.Tag;

/**
 * @author Maik.Riechel
 * @since 28.10.12
 */
public class RocketDrawable extends PaintDrawable {
  private final static String TAG = Tag.of(RocketDrawable.class);

  public static final int MAX_LEVEL = 10000;

  private final Bitmap rocketBitmap;
  private int top;
  private int left;

  public RocketDrawable(final Context context) {
    final Drawable drawable = context.getResources().getDrawable(R.drawable.icon);

    final int intrinsicWidth = drawable.getIntrinsicWidth();
    final int intrinsicHeight = drawable.getIntrinsicHeight();
    final int width;
    final int height;
    if (intrinsicWidth > 0 && intrinsicHeight > 0) {
      width = intrinsicWidth;
      height = intrinsicHeight;
    } else {
      width = 128;
      height = 128;
    }
    rocketBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    drawable.setBounds(0, 0, width, height);
    drawable.draw(new Canvas(rocketBitmap));
  }

  @Override
  public void draw(final Canvas canvas) {
    canvas.drawBitmap(rocketBitmap, left, top, paint);
  }

  @Override
  protected void onBoundsChange(final Rect bounds) {
    super.onBoundsChange(bounds);
    updateTransformation();
  }

  private boolean updateTransformation() {
    final int oldLeft = left;
    final int oldTop = top;

    final Rect bounds = getBounds();
    final int level = getLevel();

    left = (bounds.width() - rocketBitmap.getWidth()) >> 1;
    top = bounds.height() - rocketBitmap.getHeight() * level / MAX_LEVEL;

    final boolean changed = oldTop != top || oldLeft != left;
    if (BuildConfig.DEBUG) {
      if (changed) {
        Log.v(TAG, String.format("(%d, %d) -> (%d, %d)", oldLeft, oldTop, left, top));
      }
    }
    return changed;
  }

  @Override
  protected boolean onLevelChange(final int level) {
    if (BuildConfig.DEBUG) {
      Log.v(TAG, String.format("level changed to %d", level));
    }
    return updateTransformation();
  }

  @Override
  public int getIntrinsicWidth() {
    // we don't want any adjustment based on our size
    return -1;
  }

  @Override
  public int getIntrinsicHeight() {
    // we don't want any adjustment based on our size
    return -1;
  }

}
