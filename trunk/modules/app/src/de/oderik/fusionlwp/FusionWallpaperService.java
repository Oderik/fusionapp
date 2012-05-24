package de.oderik.fusionlwp;

import android.app.WallpaperManager;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * @author Maik.Riechel
 * @since 15.05.12 16:19
 */
public class FusionWallpaperService extends WallpaperService {
  public static final String TAG = FusionWallpaperService.class.getName();

  private final Handler handler = new Handler();

  @Override
  public Engine onCreateEngine() {
    return new FusionEngine();
  }

  class FusionEngine extends Engine {

    private boolean visible;
    private int  xPixels = 0;
    private int  yPixels = 0;
    private boolean fusionInFuture = true;
    final private CountDownDrawable countDownDrawable;
    private Bitmap backgroundBitmap;

    FusionEngine() {
      countDownDrawable = new CountDownDrawable();
      countDownDrawable.setColor(Color.WHITE);
      countDownDrawable.setTypeface(Typeface.createFromAsset(getAssets(), "Anton.ttf"));

      final WallpaperManager wallpaperManager = WallpaperManager.getInstance(FusionWallpaperService.this);
      final int desiredMinimumHeight = wallpaperManager.getDesiredMinimumHeight();
      final int desiredMinimumWidth = wallpaperManager.getDesiredMinimumWidth();
      updateBackgroundBitmap(desiredMinimumWidth, desiredMinimumHeight);
    }

    private void updateBackgroundBitmap(final int width, final int height) {
      final Drawable backgroundDrawable = getResources().getDrawable(R.drawable.background);
      backgroundDrawable.setBounds(0, 0, width - 1, height - 1);
      backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      final Canvas canvas = new Canvas(backgroundBitmap);
      backgroundDrawable.draw(canvas);
    }

    Runnable drawEverything = new DrawRunnable() {
      @Override
      void draw(final Canvas canvas) {
        drawBackground(canvas);
        drawCountdown(canvas);
      }
    };

    Runnable drawCountdown = new DrawRunnable() {
      @Override
      void draw(final Canvas canvas) {
        drawBackground(canvas); //TODO remove
        drawCountdown(canvas);
      }
    };


    @Override
    public void onDestroy() {
      super.onDestroy();
      handler.removeCallbacks(drawCountdown);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      this.visible = visible;
      if (visible) {
        drawEverything.run();
      } else {
        handler.removeCallbacks(drawCountdown);
      }
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      super.onSurfaceChanged(holder, format, width, height);

      countDownDrawable.setBounds(0, 0, width - 1, height - 1);

      drawEverything.run();
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
      super.onSurfaceCreated(holder);
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
      super.onSurfaceDestroyed(holder);
      visible = false;
      handler.removeCallbacks(drawCountdown);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xStep, float yStep, int xPixels, int yPixels) {
      this.xPixels = xPixels;
      this.yPixels = yPixels;
      drawEverything.run();
    }



    void drawBackground(final Canvas c) {
      c.drawBitmap(backgroundBitmap, xPixels, yPixels, new Paint());
    }

    @Override
    public void onDesiredSizeChanged(final int desiredWidth, final int desiredHeight) {
      super.onDesiredSizeChanged(desiredWidth, desiredHeight);
      updateBackgroundBitmap(desiredWidth, desiredHeight);
    }

    void drawCountdown(Canvas c) {
      countDownDrawable.draw(c);
    }

    abstract class DrawRunnable implements Runnable {
      @Override
      public void run() {
        final SurfaceHolder holder = getSurfaceHolder();

        Canvas canvas = null;
        try {
          canvas = holder.lockCanvas();
          if (canvas != null) {
            draw(canvas);
          }
        } finally {
          if (canvas != null) holder.unlockCanvasAndPost(canvas);
        }
        // Reschedule the next redraw
        handler.removeCallbacks(drawCountdown);
        if (visible && fusionInFuture) {
          handler.postDelayed(drawCountdown, FusionEventTiming.timeToNextSecond());
        }
      }

      abstract void draw(final Canvas canvas);
    }

  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    Log.w(TAG, "memory low");
  }
}
