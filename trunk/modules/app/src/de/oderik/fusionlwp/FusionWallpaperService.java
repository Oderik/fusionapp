package de.oderik.fusionlwp;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

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

  @Override
  public Engine onCreateEngine() {
    return new FusionEngine();
  }

  class FusionEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener {

    private boolean visible;
    private int surfaceWidth;
    private int surfaceHeight;
    private int xPixels = 0;
    private int yPixels = 0;

    private float countdownPosX;
    private float countdownPosY;
    private boolean countdownEnabled;

    private boolean fusionInFuture = true;

    private SharedPreferences preferences;
    final private CountdownDrawable countdownDrawable;
    private Bitmap backgroundBitmap;

    private float initialPosX;
    private float initialPosY;
    private Matrix matrix = null;

    private float[] points = new float[2];


    FusionEngine() {
      countdownDrawable = new CountdownDrawable(FusionWallpaperService.this);
      countdownDrawable.setTypeface(Typeface.createFromAsset(getAssets(), "Anton.ttf"));

      final WallpaperManager wallpaperManager = WallpaperManager.getInstance(FusionWallpaperService.this);
      final int desiredMinimumHeight = wallpaperManager.getDesiredMinimumHeight();
      final int desiredMinimumWidth = wallpaperManager.getDesiredMinimumWidth();
      updateBackgroundBitmap(desiredMinimumWidth, desiredMinimumHeight);
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
      editor.apply();
    }

    private void updateBackgroundBitmap(final int width, final int height) {
      final Drawable backgroundDrawable = getResources().getDrawable(R.drawable.background);
      backgroundDrawable.setBounds(0, 0, width - 1, height - 1);
      backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      final Canvas canvas = new Canvas(backgroundBitmap);
      backgroundDrawable.draw(canvas);
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
      if (countdownEnabled) {
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
        if (BuildConfig.DEBUG) {
          Log.v(TAG, "about to draw something");
        }

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
        if (visible && fusionInFuture && countdownEnabled) {
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
