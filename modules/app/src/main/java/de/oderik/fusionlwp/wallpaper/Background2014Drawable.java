package de.oderik.fusionlwp.wallpaper;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import de.oderik.fusionlwp.R;

import static de.oderik.fusionlwp.util.BitmapFactory.*;
import static java.lang.Math.*;

/**
 * Created by Oderik on 21.05.2014.
 */
public class Background2014Drawable extends LiveBackgroundDrawable {

  private Bitmap backgroundBitmap;
  private Bitmap foregroundBitmap;

  public Background2014Drawable(Resources resources) {
    super(resources);
  }

  @Override
  public void draw(Canvas canvas) {

  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    final int width = bounds.width();
    final int height = bounds.height();

    if (width > 0 && height > 0) {
      backgroundBitmap = loadBitmap(R.drawable.bg_2014, width, height);
      foregroundBitmap = loadBitmap(R.drawable.lead_2014, width, height);
    }
  }

  private Bitmap loadBitmap(int resourceId, int width, int height) {
    final Options options = decodeBounds(new ResourceDecodeStrategy(resources, resourceId));
    options.inSampleSize = max(1, min(options.outHeight / height, options.outWidth / width));
    return decodeResource(resources, resourceId, options);
  }
}
