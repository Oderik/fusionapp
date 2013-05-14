package de.oderik.fusionlwp.wallpaper;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.Log;
import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 14.05.13 19:38
 */
public class Background2012Drawable extends LiveBackgroundDrawable {
  private static final String TAG = Background2012Drawable.class.getName();

  private Bitmap backgroundBitmap;
  private int backgroundBitmapScale = 1;

  private int xPixels = 0;
  private int yPixels = 0;

  public Background2012Drawable(final Resources resources) {
    super(resources);
  }

  @Override
  public void draw(final Canvas canvas) {
    if (backgroundBitmapScale > 0) {
      canvas.save();
      canvas.scale(backgroundBitmapScale, backgroundBitmapScale);
      canvas.drawBitmap(backgroundBitmap, 1f * xPixels / backgroundBitmapScale, 1f * yPixels / backgroundBitmapScale, paint);
      canvas.restore();
    } else {
      canvas.drawColor(Color.BLACK);
    }
  }

  private void updateBackgroundBitmap(final int width, final int height) {
    if (width > 0 && height > 0) {
      final Drawable backgroundDrawable = resources.getDrawable(R.drawable.background_2012);
      backgroundDrawable.setBounds(0, 0, width - 1, height - 1);
      for (backgroundBitmapScale = 1; backgroundBitmapScale <= 4; backgroundBitmapScale++) {
        if (backgroundBitmap != null) {
          backgroundBitmap.recycle();
        }
        try {
          backgroundBitmap = Bitmap.createBitmap(width / backgroundBitmapScale, height / backgroundBitmapScale, Bitmap.Config.RGB_565);
          break;
        } catch (Throwable e) {
          Log.i(TAG, String.format("Error creating background bitmap: %s", e.getMessage()));
        }
      }
      if (backgroundBitmap != null) {
        final Canvas canvas = new Canvas(backgroundBitmap);
        canvas.scale(1f / backgroundBitmapScale, 1f / backgroundBitmapScale);
        backgroundDrawable.draw(canvas);
      } else {
        Log.i(TAG, String.format("Too many errors creating background image, using plain black instead."));
        backgroundBitmapScale = 0;
      }
    }
  }

  @Override
  protected void onBoundsChange(final Rect bounds) {
    super.onBoundsChange(bounds);
    updateBackgroundBitmap(bounds.width(), bounds.height());
  }

  @Override
  protected void onOffsetsChanged(final float xOffset, final float yOffset, final float xStep, final float yStep, final int xPixels, final int yPixels) {
    if (this.xPixels != xPixels || this.yPixels != yPixels) {
      this.xPixels = xPixels;
      this.yPixels = yPixels;
      invalidateSelf();
    }
  }
}
