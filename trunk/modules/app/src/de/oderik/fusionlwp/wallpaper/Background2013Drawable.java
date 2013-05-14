package de.oderik.fusionlwp.wallpaper;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import de.oderik.fusionlwp.R;

/**
 * @author maik.riechel
 * @since 14.05.13 20:25
 */
public class Background2013Drawable extends LiveBackgroundDrawable {

  private final Drawable backgroundDrawable;

  private final int[] paralaxLayerResourceIds;
  private final Bitmap[] paralaxLayers;

  private final Paint backgroundPaint = new Paint();

  private int xPixels;
  private int yPixels;

  public Background2013Drawable(final Resources resources) {
    super(resources);

    final TypedArray typedArray = resources.obtainTypedArray(R.array.paralax_layers_2013);
    paralaxLayerResourceIds = new int[typedArray.length()];
    for (int i = 0; i < typedArray.length(); i++) {
      paralaxLayerResourceIds[i] = typedArray.getResourceId(i, -1);
    }
    typedArray.recycle();

    paralaxLayers = new Bitmap[paralaxLayerResourceIds.length];

    backgroundDrawable = resources.getDrawable(R.drawable.bg_2013);
    if (backgroundDrawable instanceof BitmapDrawable) {
      backgroundPaint.setStyle(Paint.Style.FILL);
      backgroundPaint.setShader(new BitmapShader(((BitmapDrawable) backgroundDrawable).getBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
    }

  }

  @Override
  public void draw(final Canvas canvas) {
    canvas.save();
    canvas.translate(xPixels, yPixels);
    canvas.drawPaint(backgroundPaint);
    if (paralaxLayers != null) {
      for (Bitmap paralaxLayer : paralaxLayers) {
        canvas.drawBitmap(paralaxLayer, 0, 0, paint);
      }
    }
    canvas.restore();
  }

  @Override
  protected void onBoundsChange(final Rect bounds) {
    backgroundDrawable.setBounds(bounds);

    for (int i = 0; i < paralaxLayerResourceIds.length; i++) {
      final Drawable drawable = resources.getDrawable(paralaxLayerResourceIds[i]);
      if (drawable instanceof BitmapDrawable) {
        paralaxLayers[i] = ((BitmapDrawable) drawable).getBitmap();
      }
    }

  }

  private void setParalaxBounds(final Drawable paralaxLayer, final Rect bounds) {
    paralaxLayer.setBounds(bounds.right - paralaxLayer.getIntrinsicWidth(), bounds.top, bounds.right, bounds.top + paralaxLayer.getIntrinsicHeight());
  }

  @Override
  protected void onOffsetsChanged(final float xOffset, final float yOffset, final float xStep, final float yStep, final int xPixels, final int yPixels) {
    this.xPixels = xPixels;
    this.yPixels = yPixels;
    invalidateSelf();
  }
}
