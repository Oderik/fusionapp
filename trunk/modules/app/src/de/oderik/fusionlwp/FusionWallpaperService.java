package de.oderik.fusionlwp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/**
 * @author Maik.Riechel
 * @since 15.05.12 16:19
 */
public class FusionWallpaperService extends WallpaperService {
  private final Handler mHandler = new Handler();


  @Override
  public Engine onCreateEngine() {
    return new FusionEngine();
  }

  class FusionEngine extends Engine {
    private final Paint mPaint = new Paint();
    private float mCenterX;
    private float mCenterY;

    private final Runnable mDrawCube = new Runnable() {
      public void run() {
        drawFrame();
      }
    };
    private boolean mVisible;

    FusionEngine() {
      // Create a Paint to draw the lines for our cube
      final Paint paint = mPaint;
      paint.setColor(0xffffffff);
      paint.setAntiAlias(true);
      paint.setStrokeWidth(2);
      paint.setStrokeCap(Paint.Cap.ROUND);
      paint.setStyle(Paint.Style.STROKE);

      SystemClock.elapsedRealtime();
    }

    @Override
    public void onCreate(SurfaceHolder surfaceHolder) {
      super.onCreate(surfaceHolder);

      // By default we don't get touch events, so enable them.
      setTouchEventsEnabled(true);
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      mHandler.removeCallbacks(mDrawCube);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      mVisible = visible;
      if (visible) {
        drawFrame();
      } else {
        mHandler.removeCallbacks(mDrawCube);
      }
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      super.onSurfaceChanged(holder, format, width, height);
      // store the center of the surface, so we can draw the cube in the right spot
      mCenterX = width / 2.0f;
      mCenterY = height / 2.0f;
      drawFrame();
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
      super.onSurfaceCreated(holder);
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
      super.onSurfaceDestroyed(holder);
      mVisible = false;
      mHandler.removeCallbacks(mDrawCube);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xStep, float yStep, int xPixels, int yPixels) {
      drawFrame();
    }

    /*
    * Store the position of the touch event so we can use it for drawing later
    */
    @Override
    public void onTouchEvent(MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_MOVE) {
      } else {
      }
      super.onTouchEvent(event);
    }

    /*
    * Draw one frame of the animation. This method gets called repeatedly
    * by posting a delayed Runnable. You can do any drawing you want in
    * here. This example draws a wireframe cube.
    */
    void drawFrame() {
      final SurfaceHolder holder = getSurfaceHolder();

      Canvas c = null;
      try {
        c = holder.lockCanvas();
        if (c != null) {
          // draw something

          drawCountdown(c);
        }
      } finally {
        if (c != null) holder.unlockCanvasAndPost(c);
      }

      // Reschedule the next redraw
      mHandler.removeCallbacks(mDrawCube);
      if (mVisible) {
        mHandler.postDelayed(mDrawCube, 1000 / 25);
      }
    }

    void drawCountdown(Canvas c) {
      c.save();
      c.translate(mCenterX, mCenterY);
      c.drawColor(0xff000000);

      c.drawText("Hello LWP", 0, 0, mPaint);
      //todo: draw!
      c.restore();
    }



  }
}
