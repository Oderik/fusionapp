package de.oderik.fusionlwp;

import android.graphics.Canvas;
import android.graphics.Rect;
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
  private final Handler handler = new Handler();

  @Override
  public Engine onCreateEngine() {
    return new FusionEngine();
  }

  class FusionEngine extends Engine {

    private boolean visible;
    private Rect bounds  = new Rect(0, 0, 1, 1);
    private int  xPixels = 0;
    private int  yPixels = 0;
    private Drawable background;
    private boolean fusionInFuture = true;
    final private CountDownDrawable countDownDrawable;

    FusionEngine() {
      countDownDrawable = new CountDownDrawable();
      //todo setbounds und so
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
      // store the center of the surface, so we can draw the cube in the right spot

      bounds.right = width;
      bounds.bottom = height;

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
      this.background = getResources().getDrawable(R.drawable.background);
      this.background.setBounds(xPixels, yPixels, getWallpaperDesiredMinimumWidth() + xPixels, getWallpaperDesiredMinimumHeight() + yPixels);

      this.background.draw(c);
    }

    @Override
    public void onDesiredSizeChanged(final int desiredWidth, final int desiredHeight) {
      super.onDesiredSizeChanged(desiredWidth, desiredHeight);
      Log.d("Fusionsize", String.format("width=%d height=%d", desiredWidth, desiredHeight));
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
}
