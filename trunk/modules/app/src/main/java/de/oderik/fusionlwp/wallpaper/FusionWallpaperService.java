package de.oderik.fusionlwp.wallpaper;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

import de.oderik.fusionlwp.BuildConfig;
import de.oderik.fusionlwp.R;
import de.oderik.fusionlwp.countdown.CountdownDrawable;
import de.oderik.fusionlwp.countdown.FusionEventTiming;
import de.oderik.fusionlwp.theme.EventTheme;

/**
 * @author Maik.Riechel
 * @since 15.05.12 16:19
 */
public class FusionWallpaperService extends WallpaperService {
  public static final String TAG = FusionWallpaperService.class.getName();

  private final FusionEventTiming fusionEventTiming = new FusionEventTiming();

  @Override
  public Engine onCreateEngine() {
    return new FusionEngine();
  }

  class FusionEngine extends Engine implements Preferences.OnPreferencesChangedListener {

    private final Drawable.Callback drawableCallback = new Drawable.Callback() {
      @Override
      public void invalidateDrawable(final Drawable who) {
        if (who == backgroundDrawable) {
          handler.post(drawEverything);
        }
      }

      @Override
      public void scheduleDrawable(final Drawable who, final Runnable what, final long when) {
        if (who == backgroundDrawable) {
          handler.postAtTime(what, when);
        }

      }

      @Override
      public void unscheduleDrawable(final Drawable who, final Runnable what) {
        handler.removeCallbacks(what);
      }
    };

    private boolean visible;
    private int surfaceWidth;
    private int surfaceHeight;

    private final Preferences preferences;

    private CountdownDrawable countdownDrawable;
    private LiveWallpaperDrawable backgroundDrawable;

    private float initialPosX;
    private float initialPosY;
    private Matrix matrix = null;

    private final float[] points = new float[2];

    private Handler handler;
    private EventTheme eventTheme = null;

    private boolean hasSurface = false;


    FusionEngine() {
      preferences = new Preferences(FusionWallpaperService.this);
      preferences.setOnPreferencesChangedListener(this);

      new Thread(new Runnable() {
        @Override
        public void run() {
          Looper.prepare();
          handler = new Handler();
          Looper.loop();
        }
      }).start();
    }

    private void updateCountdownDrawable() {
      countdownDrawable = new CountdownDrawable(FusionWallpaperService.this, fusionEventTiming, EventTheme.noNull(preferences.getEventTheme()));
      countdownDrawable.setTypeface(Typeface.createFromAsset(getAssets(), "Anton.ttf"));
    }

    private void updateBackgroundDrawable() {
      if (backgroundDrawable != null) {
        backgroundDrawable.setCallback(null);
      }
      backgroundDrawable = EventTheme.noNull(preferences.getEventTheme()).wallpaperFactory.create(FusionWallpaperService.this);
      backgroundDrawable.setCallback(drawableCallback);
    }


    @Override
    public void onTouchEvent(final MotionEvent event) {
      if (isPreview()) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            final Rect countdownBounds = countdownDrawable.getBounds();
            if (preferences.isCountdownEnabled() && countdownBounds.contains((int) event.getX(), (int) event.getY())) {
              initialPosX = preferences.getCountdownPosX();
              initialPosY = preferences.getCountdownPosY();

              matrix = new Matrix();
              final int intrinsicWidth = countdownDrawable.getIntrinsicWidth();
              final int intrinsicHeight = countdownDrawable.getIntrinsicHeight();
              matrix.postScale(1f / Math.max(surfaceWidth - intrinsicWidth, 1), 1f / Math.max(surfaceHeight - intrinsicHeight, 1));
              points[0] = event.getX();
              points[1] = event.getY();
              matrix.mapPoints(points);
              matrix.postTranslate(preferences.getCountdownPosX() - points[0], preferences.getCountdownPosY() - points[1]);
            }
            break;
          case MotionEvent.ACTION_MOVE:
            if (matrix != null) {
              points[0] = event.getX();
              points[1] = event.getY();
              matrix.mapPoints(points);
              preferences.setCountdownPosX(Math.max(0f, Math.min(1f, points[0])));
              preferences.setCountdownPosY(Math.max(0f, Math.min(1f, points[1])));
              updateCountdownBounds(preferences.getCountdownPosX(), preferences.getCountdownPosY());
              drawEverything.run();
            }
            break;
          case MotionEvent.ACTION_UP:
            matrix = null;
            savePreferences();
            break;
          default:
            if (matrix != null) {
              matrix = null;
              preferences.setCountdownPosX(initialPosX);
              preferences.setCountdownPosY(initialPosY);
              drawEverything.run();
            }
            break;
        }
      }
    }

    private void savePreferences() {
      preferences.save();
    }


    final Runnable drawEverything = new DrawRunnable() {
      @Override
      void draw(final Canvas canvas) {
        drawBackground(canvas);
        drawCountdown(canvas);
      }
    };

    final Runnable drawCountdown = new DrawRunnable() {
      @Override
      void draw(final Canvas canvas) {
        drawBackground(canvas); // it is not save to rely on the wallpaper being properly prepared
        drawCountdown(canvas);
      }
    };

    @Override
    public void onCreate(final SurfaceHolder surfaceHolder) {
      super.onCreate(surfaceHolder);

      updateCountdownDrawable();
      updateBackgroundDrawable();
      updateVirtualSizes();

      setTouchEventsEnabled(isPreview());
      if (isPreview() && preferences.isCountdownEnabled()) {
        Toast.makeText(FusionWallpaperService.this, getString(R.string.hint_moveCountdown), Toast.LENGTH_LONG).show();
      }
    }

    private void updateVirtualSizes() {
      if (backgroundDrawable != null) {
        backgroundDrawable.setVirtualSize(getDesiredMinimumWidth(), getDesiredMinimumHeight());
      }
    }


    @Override
    public void onDestroy() {
      handler.removeCallbacks(drawCountdown);
      handler.getLooper().quit();
      preferences.setOnPreferencesChangedListener(null);
      if (backgroundDrawable != null) {
        backgroundDrawable.setCallback(null);
      }
      super.onDestroy();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      this.visible = visible;
      if (visible) {
        drawEverything.run();
      } else {
        handler.removeCallbacks(drawCountdown);
      }
      if (BuildConfig.DEBUG) {
        Log.d(TAG, String.format("visibility is %B", visible));
      }
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      super.onSurfaceChanged(holder, format, width, height);

      hasSurface = true;
      surfaceWidth = width;
      surfaceHeight = height;

      updateBounds();

      drawEverything.run();
    }

    private void updateBounds() {
      if (hasSurface && backgroundDrawable != null) {
        backgroundDrawable.setBounds(0, 0, surfaceWidth, surfaceHeight);
      }
      updateCountdownBounds(preferences.getCountdownPosX(), preferences.getCountdownPosY());
    }

    private void updateCountdownBounds(final float posX, final float posY) {
      final int intrinsicWidth = countdownDrawable.getIntrinsicWidth();
      final int intrinsicHeight = countdownDrawable.getIntrinsicHeight();
      final int left = (int) ((surfaceWidth - intrinsicWidth) * posX);
      final int top = (int) ((surfaceHeight - intrinsicHeight) * posY);
      countdownDrawable.setBounds(left, top, left + intrinsicWidth, top + intrinsicHeight);
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
      super.onSurfaceCreated(holder);
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
      super.onSurfaceDestroyed(holder);
      visible = false;
      hasSurface = false;
      handler.removeCallbacks(drawCountdown);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xStep, float yStep, int xPixels, int yPixels) {
      backgroundDrawable.offsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
    }


    void drawBackground(final Canvas canvas) {
      backgroundDrawable.draw(canvas);
    }

    @Override
    public void onDesiredSizeChanged(final int desiredWidth, final int desiredHeight) {
      super.onDesiredSizeChanged(desiredWidth, desiredHeight);
      updateVirtualSizes();
    }

    private void drawCountdown(Canvas c) {
      if (preferences.isCountdownEnabled()) {
        fusionEventTiming.update();
        countdownDrawable.draw(c);
      }
    }

    @Override
    public void onPreferencesChanged(final Preferences preferences) {
      updateCountdownBounds(preferences.getCountdownPosX(), preferences.getCountdownPosY());

      setEventTheme(EventTheme.noNull(preferences.getEventTheme()));
    }

    private void setEventTheme(final EventTheme eventTheme) {
      if (this.eventTheme != eventTheme) {
        this.eventTheme = eventTheme;
        updateBackgroundDrawable();
        updateCountdownDrawable();
        updateVirtualSizes();
        updateBounds();
      }
    }

    abstract class DrawRunnable implements Runnable {
      @Override
      public void run() {
        final long time = SystemClock.elapsedRealtime();

        final SurfaceHolder holder = getSurfaceHolder();

        Canvas canvas = null;
        try {
          canvas = holder.lockCanvas();
          if (canvas != null) {
            draw(canvas);
          }
        } catch (NullPointerException ignored) {
          ignored.printStackTrace();
        } finally {
            try {
          if (canvas != null) holder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                Log.w(TAG, "Could not update canvas", e);
            }
        }

        if (BuildConfig.DEBUG) {
          final long elapsed = SystemClock.elapsedRealtime() - time;
          Log.d(TAG, String.format("frame drawn in %dms (%ffps)", elapsed, 1000f / Math.max(elapsed, 1)));
        }

        // Reschedule the next redraw
        handler.removeCallbacks(drawCountdown);
        if (visible && preferences.isCountdownEnabled()) {
          handler.postDelayed(drawCountdown, fusionEventTiming.timeToNextTick());
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
