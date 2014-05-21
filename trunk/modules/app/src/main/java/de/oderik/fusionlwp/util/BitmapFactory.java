package de.oderik.fusionlwp.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import de.oderik.fusionlwp.BuildConfig;

import java.io.IOException;
import java.io.InputStream;

public class BitmapFactory extends android.graphics.BitmapFactory {
  private static final String TAG = BitmapFactory.class.getName();

  public static Bitmap load(final DecodeStrategy decodeStrategy, final int targetWidth, final int targetHeight) throws IOException {
    final Options options = new Options();
    options.inJustDecodeBounds = true;
    decodeStrategy.decode(options);
    options.inSampleSize = (int) Math.ceil(Math.max((float) options.outWidth / targetWidth, (float) options.outHeight / targetHeight));
    if (BuildConfig.DEBUG) {
      Log.v(TAG, String.format("Loading bitmap of size %dx%d to target size %dx%d resulting in a size of %dx%d.",
                               options.outWidth, options.outHeight,
                               targetWidth, targetHeight,
                               options.outWidth / options.inSampleSize, options.outHeight / options.inSampleSize));
    }
    options.inJustDecodeBounds = false;
    return decodeStrategy.decode(options);
  }

  /**
   * Uses {@link android.graphics.BitmapFactory#decodeStream(java.io.InputStream, android.graphics.Rect, android.graphics.BitmapFactory.Options)} to decode the bitmap.
   */
  public static class StreamDecodeStrategy implements DecodeStrategy {

    private final StreamProvider streamProvider;
    private final Rect outPadding;

    /**
     * @param provider Access to the input stream.
     * @param outPadding if not null it will contain the padding of the bitmap after the strategy was used to load one.
     *                   See {@link android.graphics.BitmapFactory#decodeStream(java.io.InputStream, android.graphics.Rect, android.graphics.BitmapFactory.Options)} for details.
     */
    public StreamDecodeStrategy(final StreamProvider provider, final Rect outPadding) {
      streamProvider = provider;
      this.outPadding = outPadding;
    }

    @Override
    public Bitmap decode(final Options options) throws IOException {
      final InputStream is = streamProvider.openInputStream();
      try {
        return decodeStream(is, outPadding, options);
      } finally {
        if (is != null) {
          is.close();
        }
      }
    }

    /**
     * Provides access to the input stream because it needs to be opened twice.
     */
    public interface StreamProvider {
      InputStream openInputStream() throws IOException;
    }
  }

  private interface DecodeStrategy {
    Bitmap decode(Options options) throws IOException;
  }

  /**
   * Uses {@link android.graphics.BitmapFactory#decodeResource(android.content.res.Resources, int, android.graphics.BitmapFactory.Options)} to decode the bitmap.
   */
  public static class ResourceDecodeStrategy implements DecodeStrategy {
    private final Resources resources;
    private final int id;

    public ResourceDecodeStrategy(final Resources resources, final int id) {
      this.resources = resources;
      this.id = id;
    }

    @Override
    public Bitmap decode(final Options options) throws IOException {
      return decodeResource(resources, id, options);
    }
  }

  public static Options decodeBounds(final DecodeStrategy decodeStrategy) {
    try {
      return decodeBounds(decodeStrategy, new Options());
    } catch (IOException e) {
      throw new RuntimeException("This was unexpected.", e);
    }
  }

  private static Options decodeBounds(DecodeStrategy decodeStrategy, Options options) throws IOException {
    options.inJustDecodeBounds = true;
    decodeStrategy.decode(options);
    return options;
  }

}
