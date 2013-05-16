package de.oderik.fusionlwp.wallpaper;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.util.BitmapFactory;
import de.oderik.fusionlwp.util.OffsetsHolder;

/**
 * @author maik.riechel
 * @since 14.05.13 20:25
 */
public class Background2013Drawable extends LiveBackgroundDrawable {
  private static final String TAG = Background2013Drawable.class.getName();

  protected static final float MAX_PARALLAX_SCROLL = .3f;

  private final int[] parallaxLayerResourceIds;
  private final Bitmap[] parallaxLayers;

  private final Paint backgroundPaint = new Paint();

  private final OffsetsHolder offsetsHolder = new OffsetsHolder();

  private final int intrinsicParallaxWidth;
  private final int intrinsicParallaxHeight;

  public Background2013Drawable(final Resources resources) {
    super(resources);

    final TypedArray typedArray = resources.obtainTypedArray(R.array.parallax_layers_2013);
    assert typedArray != null;
    parallaxLayerResourceIds = new int[typedArray.length()];
    for (int i = 0; i < typedArray.length(); i++) {
      parallaxLayerResourceIds[i] = typedArray.getResourceId(i, -1);
    }
    typedArray.recycle();

    parallaxLayers = new Bitmap[parallaxLayerResourceIds.length];

    final Drawable backgroundDrawable = resources.getDrawable(R.drawable.bg_2013);
    if (backgroundDrawable instanceof BitmapDrawable) {
      backgroundPaint.setStyle(Paint.Style.FILL);
      backgroundPaint.setShader(new BitmapShader(((BitmapDrawable) backgroundDrawable).getBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
    }

    final android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(resources, R.drawable.rakete_2013, options);
    intrinsicParallaxWidth = options.outWidth;
    intrinsicParallaxHeight = options.outHeight;
  }

  @Override
  public void draw(final Canvas canvas) {
    canvas.save();
    canvas.translate(offsetsHolder.getxPixels(), offsetsHolder.getyPixels());
    canvas.drawPaint(backgroundPaint);
    canvas.restore();

    if (parallaxLayers != null) {
      for (int i = 0; i < parallaxLayers.length; i++) {
        final Bitmap layer = parallaxLayers[i];
        final float left = calculateLayerPosition(getBounds().width(), getVirtualWidth(), layer.getWidth(), offsetsHolder.getHorizontals(), parallaxLayers.length, i);
        final float top = calculateLayerPosition(getBounds().height(), getVirtualHeight(), layer.getHeight(), offsetsHolder.getVerticals(), parallaxLayers.length, i);
        canvas.drawBitmap(layer, left, top, paint);
      }
    }
  }

  private float calculateLayerPosition(final int size, final int virtualSize, final int layerSize,
                                       final OffsetsHolder.DimensionValues dimensionValues,
                                       final int layerCount, final int i) {
    final float centerOffset = (size - layerSize) / 2f;
    if (dimensionValues.getStep() > 0 && dimensionValues.getStep() < 1) {
      return centerOffset + (.5f - dimensionValues.getOffset()) * (virtualSize - layerSize) * (layerCount - i) / (layerCount) * MAX_PARALLAX_SCROLL;
    } else {
      return centerOffset;
    }
  }

  @Override
  protected void onBoundsChange(final Rect bounds) {
    final android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
    options.inSampleSize = Math.max(intrinsicParallaxWidth / bounds.width(), intrinsicParallaxHeight / bounds.height());

    for (int i = 0; i < parallaxLayerResourceIds.length; i++) {
      parallaxLayers[i] = BitmapFactory.decodeResource(resources, parallaxLayerResourceIds[i], options);
    }
  }

  @Override
  protected void onOffsetsChanged(final float xOffset, final float yOffset, final float xStep, final float yStep, final int xPixels, final int yPixels) {
    offsetsHolder.set(xOffset, yOffset, xStep, yStep, xPixels, yPixels);

    invalidateSelf();
  }
}
