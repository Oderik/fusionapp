package de.oderik.fusionapp.gltest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11.*;
import static android.opengl.GLES11Ext.GL_TEXTURE_CROP_RECT_OES;
import static android.opengl.GLES11Ext.glDrawTexfOES;

public class GLActivity extends Activity {
  private static final String TAG = GLActivity.class.getName();
  private GLSurfaceView glSurfaceView;
  private GestureDetector gestureDetector;
  private WallpaperRenderer renderer;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    glSurfaceView = new GLSurfaceView(this);
    setContentView(glSurfaceView);
    glSurfaceView.setEGLConfigChooser(false);
    renderer = new WallpaperRenderer(this);
    glSurfaceView.setRenderer(renderer);
    glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
      @Override
      public boolean onDown(final MotionEvent e) {
        return true;
      }

      @Override
      public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
        renderer.offset(distanceX, distanceY);
        Log.d(TAG, "Requesting render...");
        glSurfaceView.requestRender();
        return true;
      }
    });
  }

  @Override
  public boolean onTouchEvent(final MotionEvent event) {
    return gestureDetector.onTouchEvent(event);
  }

  private static class WallpaperRenderer implements GLSurfaceView.Renderer {
    public static final int TEXTURE_COUNT = 5;
    private final Context context;
    private final PointF position = new PointF();
    private int[] textures;
    private int width;
    private int height;


    private WallpaperRenderer(final Context context) {
      this.context = context;
    }

    private void loadTextures(final Context context) {
      final long start = SystemClock.elapsedRealtime();

      glEnable(GL_TEXTURE_2D);
      glEnable(GL_BLEND);
      glEnable(GL_REPEAT);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
      textures = new int[TEXTURE_COUNT];
      glGenTextures(TEXTURE_COUNT, textures, 0);

      //glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

      loadTexture(context, R.drawable.bg_2013, textures[0], GL_REPEAT);
      final int edge = GL_REPEAT;
      loadTexture(context, R.drawable.b_2013, textures[1], edge);
      loadTexture(context, R.drawable.o_2013, textures[2], edge);
      loadTexture(context, R.drawable.s_2013, textures[3], edge);
      loadTexture(context, R.drawable.rakete_2013, textures[4], edge);

      Log.d(TAG, "GL environment set up in " + (SystemClock.elapsedRealtime() - start) + "ms");

    }

    private void loadTexture(final Context context, final int resourceId, final int texture, final int wrap) {
      final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
      final int width = bitmap.getWidth();
      final int height = bitmap.getHeight();
      //final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(width * height * 4);
      //byteBuffer.order(ByteOrder.BIG_ENDIAN);
      //final IntBuffer intBuffer = byteBuffer.asIntBuffer();
      //for (int y = 0; y < height; y++) {
      //  for (int x = 0; x < width; x++) {
      //    final int color = bitmap.getPixel(x, y);
      //    intBuffer.put(Color.argb(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color))); // ARGB -> RGBA
      //  }
      //}
      //byteBuffer.position(0);
      glBindTexture(GL_TEXTURE_2D, texture);
      glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
      glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
      glTexParameterx(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);
      int crop[] = new int[]{0, height, width, -height};
      glTexParameteriv(GL_TEXTURE_2D, GL_TEXTURE_CROP_RECT_OES, crop, 0);
      //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
      GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);


      //GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, 0);

      Log.i(TAG, "GL Error: " + glGetError());
    }

    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
      glClearColor(0f, 1f, 0f, 1f);
      loadTextures(context);
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
      glViewport(0, 0, width, height);
      this.width = width;
      this.height = height;
      synchronized (position) {
        position.set(0, 0);
      }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
      Log.d(TAG, "Drawing frame...");
      glClear(GL_COLOR_BUFFER_BIT);
      synchronized (position) {
        glBindTexture(GL_TEXTURE_2D, textures[0]);
        glDrawTexfOES(position.x % width - width, position.y % height - height, 0, 3 * width, 3 * height);

        for (int i = 1; i < 5; i++) {
          glBindTexture(GL_TEXTURE_2D, textures[i]);
          glDrawTexfOES(position.x * (1 + i / 4f), position.y * (1 + i / 4f), i, width, height);
        }
      }

    }

    public void offset(final float x, final float y) {
      synchronized (position) {
        position.offset(-x, y);
      }
    }
  }
}
