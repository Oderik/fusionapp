package de.oderik.fusionlwp.wallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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

  public static final String PREFERENCE_POS_X = "posX";
  public static final String PREFERENCE_POS_Y = "posY";
  public static final String PREFERENCE_ENABLED = "enabled";

  public static final String SHARED_PREFERENCES_NAME = "countdownposition";

  private final Handler handler = new Handler();
  private final FusionEventTiming fusionEventTiming = new FusionEventTiming();

  private EventTheme theme = EventTheme.CURRENT;

  @Override
  public Engine onCreateEngine() {
    return new FusionEngine();
  }

  class FusionEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

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

    private float countdownPosX;
    private float countdownPosY;
    private boolean countdownEnabled;

    private SharedPreferences preferences;
    private final CountdownDrawable countdownDrawable;
    private LiveWallpaperDrawable backgroundDrawable;

    private float initialPosX;
    private float initialPosY;
    private Matrix matrix = null;

    private final float[] points = new float[2];


    FusionEngine() {
      countdownDrawable = new CountdownDrawable(FusionWallpaperService.this, fusionEventTiming, de.oderik.fusionlwp.theme.EventTheme.CURRENT);
      countdownDrawable.setTypeface(Typeface.createFromAsset(getAssets(), "Anton.ttf"));

      backgroundDrawable = new Background2012Drawable(getResources());
      backgroundDrawable.setCallback(drawableCallback);
      final WallpaperManager wallpaperManager = WallpaperManager.getInstance(FusionWallpaperService.this);
      final int desiredMinimumHeight = wallpaperManager.getDesiredMinimumHeight();
      final int desiredMinimumWidth = wallpaperManager.getDesiredMinimumWidth();
      setBackgroundBounds(desiredMinimumWidth, desiredMinimumHeight);
    }


    @Override
    public void onTouchEvent(final MotionEvent event) {
      if (isPreview()) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            final Rect countdownBounds = countdownDrawable.getBounds();
            if (countdownEnabled && countdownBounds.contains((int) event.getX(), (int) event.getY())) {
              initialPosX = countdownPosX;
              initialPosY = countdownPosY;

              matrix = new Matrix();
              final int intrinsicWidth = countdownDrawable.getIntrinsicWidth();
              final int intrinsicHeight = countdownDrawable.getIntrinsicHeight();
              matrix.postScale(1f / Math.max(surfaceWidth - intrinsicWidth, 1), 1f / Math.max(surfaceHeight - intrinsicHeight, 1));
              points[0] = event.getX();
              points[1] = event.getY();
              matrix.mapPoints(points);
              matrix.postTranslate(countdownPosX - points[0], countdownPosY - points[1]);
            }
            break;
          case MotionEvent.ACTION_MOVE:
            if (matrix != null) {
              points[0] = event.getX();
              points[1] = event.getY();
              matrix.mapPoints(points);
              countdownPosX = Math.max(0f, Math.min(1f, points[0]));
              countdownPosY = Math.max(0f, Math.min(1f, points[1]));
              updateCountdownBounds();
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
              countdownPosX = initialPosX;
              countdownPosY = initialPosY;
              drawEverything.run();
            }
            break;
        }
      }
    }

    private void savePreferences() {
      SharedPreferences.Editor editor = preferences.edit();
      editor.putFloat(PREFERENCE_POS_X, countdownPosX);
      editor.putFloat(PREFERENCE_POS_Y, countdownPosY);
      editor.putBoolean(PREFERENCE_ENABLED, countdownEnabled);
      editor.commit();
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
      preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
      loadPreferences();
      preferences.registerOnSharedPreferenceChangeListener(this);

      setTouchEventsEnabled(isPreview());
      if (isPreview() && countdownEnabled) {
        Toast.makeText(FusionWallpaperService.this, getString(R.string.hint_moveCountdown), Toast.LENGTH_LONG).show();
      }
    }

    private void loadPreferences() {
      countdownPosX = preferences.getFloat(PREFERENCE_POS_X, .5f);
      countdownPosY = preferences.getFloat(PREFERENCE_POS_Y, .5f);
      countdownEnabled = preferences.getBoolean(PREFERENCE_ENABLED, true);
    }

    @Override
    public void onDestroy() {
      handler.removeCallbacks(drawCountdown);
      if (preferences != null) {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
      }
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

      surfaceWidth = width;
      surfaceHeight = height;
      updateCountdownBounds();

      drawEverything.run();
    }

    private void updateCountdownBounds() {
      final int intrinsicWidth = countdownDrawable.getIntrinsicWidth();
      final int intrinsicHeight = countdownDrawable.getIntrinsicHeight();
      final int left = (int) ((surfaceWidth - intrinsicWidth) * countdownPosX);
      final int top = (int) ((surfaceHeight - intrinsicHeight) * countdownPosY);
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
      handler.removeCallbacks(drawCountdown);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset,
                                 float xStep, float yStep, int xPixels, int yPixels) {
      backgroundDrawable.offsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
      drawEverything.run();
    }


    void drawBackground(final Canvas canvas) {
      backgroundDrawable.draw(canvas);
    }

    @Override
    public void onDesiredSizeChanged(final int desiredWidth, final int desiredHeight) {
      super.onDesiredSizeChanged(desiredWidth, desiredHeight);
      setBackgroundBounds(desiredWidth, desiredHeight);
    }

    private void setBackgroundBounds(final int desiredWidth, final int desiredHeight) {
      backgroundDrawable.setBounds(0, 0, desiredWidth, desiredHeight);
    }

    void drawCountdown(Canvas c) {
      if (countdownEnabled) {
        fusionEventTiming.update();
        countdownDrawable.draw(c);
      }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
      loadPreferences();
      updateCountdownBounds();
    }

    abstract class DrawRunnable implements Runnable {
      @Override
      public void run() {
        long time;
        if (BuildConfig.DEBUG) {
          time = SystemClock.elapsedRealtime();
        }

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
          if (canvas != null) holder.unlockCanvasAndPost(canvas);
        }

        if (BuildConfig.DEBUG) {
          final long elapsed = SystemClock.elapsedRealtime() - time;
          Log.d(TAG, String.format("frame drawn in %dms (%ffps)", elapsed, 1000f/Math.max(elapsed, 1)));
        }

        // Reschedule the next redraw
        handler.removeCallbacks(drawCountdown);
        if (visible && countdownEnabled) {
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
