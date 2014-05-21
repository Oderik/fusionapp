package de.oderik.fusionlwp.wallpaper;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;

import de.oderik.fusionlwp.BuildConfig;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.util.BitmapFactory;
import de.oderik.fusionlwp.util.OffsetsHolder;

import static android.graphics.BitmapFactory.Options;
import static de.oderik.fusionlwp.util.BitmapFactory.*;

/**
 * @author maik.riechel
 * @since 14.05.13 20:25
 */
public class Background2013Drawable extends LiveBackgroundDrawable {
  protected static final float MAX_PARALLAX_SCROLL = .3f;
  private static final String TAG = Background2013Drawable.class.getName();
  private final int[] parallaxLayerResourceIds;
  private final Bitmap[] parallaxLayers;
  private final Paint backgroundPaint = new Paint();
  private final OffsetsHolder offsetsHolder = new OffsetsHolder();
  private final OffsetsHolder offsetsHolderSnapshot = new OffsetsHolder();
  private final int intrinsicParallaxWidth;
  private final int intrinsicParallaxHeight;

  public Background2013Drawable(final Context context) {
    super(context.getResources());

    final Resources resources = context.getResources();
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

    final Options options = decodeBounds(new ResourceDecodeStrategy(resources, R.drawable.rakete_2013));
    intrinsicParallaxWidth = options.outWidth;
    intrinsicParallaxHeight = options.outHeight;
  }

  @Override
  public void draw(final Canvas canvas) {
    synchronized (offsetsHolder) {
      offsetsHolderSnapshot.set(offsetsHolder);
    }
    canvas.save();
    canvas.translate(offsetsHolder.getxPixels(), offsetsHolder.getyPixels());
    canvas.drawPaint(backgroundPaint);
    canvas.restore();

    final Rect bounds = getBounds();
    if (!bounds.isEmpty()) {
      for (int i = 0; i < parallaxLayers.length; i++) {
        final Bitmap layer = parallaxLayers[i];
        if (!layer.isRecycled()) {
          final float left = calculateLayerPosition(bounds.width(), getVirtualWidth(), layer.getWidth(), offsetsHolder.getHorizontals(), parallaxLayers.length, i);
          final float top = calculateLayerPosition(bounds.height(), getVirtualHeight(), layer.getHeight(), offsetsHolder.getVerticals(), parallaxLayers.length, i);
          canvas.drawBitmap(layer, left, top, paint);
        }
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
    final int width = bounds.width();
    final int height = bounds.height();

    if (width > 0 && height > 0) {
      final Options options = new Options();
      options.inSampleSize = Math.max(intrinsicParallaxWidth / width, intrinsicParallaxHeight / height);

      final float scale = Math.min((float) options.inSampleSize * width / intrinsicParallaxWidth, (float) options.inSampleSize * height / intrinsicParallaxHeight);

      if (BuildConfig.DEBUG) {
        Log.d(TAG, "Reloading resources with sample size of " + options.inSampleSize + ", scaling " + scale);
      }

      final Matrix matrix = new Matrix();
      matrix.setScale(scale, scale);

      for (int i = 0; i < parallaxLayerResourceIds.length; i++) {
        if (parallaxLayers[i] != null) {
          parallaxLayers[i].recycle();
        }
        parallaxLayers[i] = decodeResource(resources, parallaxLayerResourceIds[i], options);

        if (scale < 1) {
          final int layerWidth = (int) (parallaxLayers[i].getWidth() * scale);
          final int layerHeight = (int) (parallaxLayers[i].getHeight() * scale);
          if (layerWidth > 0 && layerHeight > 0) {
            final Bitmap scaledLayer = Bitmap.createBitmap(layerWidth, layerHeight, Bitmap.Config.ARGB_8888);
            if (!parallaxLayers[i].isRecycled()) {
              new Canvas(scaledLayer).drawBitmap(parallaxLayers[i], matrix, new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG));
              parallaxLayers[i].recycle();
              parallaxLayers[i] = scaledLayer;
            }
          }
        }
      }
    }
  }

  @Override
  protected void onOffsetsChanged(final float xOffset, final float yOffset, final float xStep, final float yStep, final int xPixels, final int yPixels) {
    synchronized (offsetsHolder) {
      offsetsHolder.set(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
    }
    invalidateSelf();
  }
}
