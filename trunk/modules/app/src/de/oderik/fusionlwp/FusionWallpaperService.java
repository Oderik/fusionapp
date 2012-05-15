package de.oderik.fusionlwp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Maik.Riechel
 * @since 15.05.12 16:19
 */
public class FusionWallpaperService extends WallpaperService {
  private final Handler handler = new Handler();

  private final static Calendar FUSION_CALENDAR = GregorianCalendar.getInstance();
  {
    FUSION_CALENDAR.clear();
    //TODO timezone
    FUSION_CALENDAR.set(Calendar.YEAR, 2012);
    FUSION_CALENDAR.set(Calendar.MONTH, Calendar.JUNE);
    FUSION_CALENDAR.set(Calendar.DAY_OF_MONTH, 28);
    FUSION_CALENDAR.set(Calendar.HOUR_OF_DAY, 18);
    FUSION_CALENDAR.set(Calendar.MINUTE, 0);
  }

  @Override
  public Engine onCreateEngine() {
    return new FusionEngine();
  }

  class FusionEngine extends Engine {

    private final Paint paint = new Paint();
    private float mCenterX;
    private float mCenterY;

    private boolean visible;
    private Rect bounds = new Rect(0, 0, 1, 1);
    private int xPixels = 0;
    private int yPixels = 0;
    private Drawable background;

    FusionEngine() {
      // Create a Paint to draw the lines for our cube
      final Paint paint = this.paint;
      paint.setColor(0xffffffff);
      paint.setAntiAlias(true);
      paint.setStrokeWidth(2);
      paint.setStrokeCap(Paint.Cap.ROUND);
      paint.setStyle(Paint.Style.STROKE);
      paint.setTextAlign(Paint.Align.CENTER);
      paint.setTextSize(20);
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
      mCenterX = width / 2.0f;
      mCenterY = height / 2.0f;

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


    private long timeToNextSecond() {
      return 1000 - (System.currentTimeMillis() % 1000);
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
      c.save();
      c.translate(mCenterX, mCenterY);
      final String time = DateFormat.getTimeInstance().format(new Date());
      c.drawText(time, 0, 0, paint);
      c.restore();
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
        if (visible) {
          handler.postDelayed(drawCountdown, timeToNextSecond());
        }
      }

      abstract void draw(final Canvas canvas);
    }

  }
}
