package de.oderik.fusionlwp.wallpaper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 14.05.13 19:38
 */
public class Background2012Drawable extends LiveBackgroundDrawable {
  private static final String TAG = Background2012Drawable.class.getName();
  private final PackageManager packageManager;
  private Bitmap backgroundBitmap;
  private int backgroundBitmapScale = 1;
  private int xPixels = 0;
  private int yPixels = 0;

  public Background2012Drawable(final Context context) {
    super(context.getResources());
    packageManager = context.getPackageManager();
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
      @SuppressWarnings("ResourceType") Drawable backgroundDrawable = packageManager.getDrawable("de.oderik.fusionlwp.ladymode", 2130837504, null);
      if (backgroundDrawable == null) {
        backgroundDrawable = resources.getDrawable(R.drawable.background_2012);
      }
      backgroundDrawable.setBounds(0, 0, width, height);
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
  protected void onVirtualSizeChanged(final int width, final int height) {
    super.onVirtualSizeChanged(width, height);
    updateBackgroundBitmap(width, height);
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
