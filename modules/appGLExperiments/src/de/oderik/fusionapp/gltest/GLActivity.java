package de.oderik.fusionapp.gltest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES11;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class GLActivity extends Activity {

  private GLSurfaceView glSurfaceView;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    glSurfaceView = new GLSurfaceView(this);
    setContentView(glSurfaceView);

    glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    glSurfaceView.setRenderer(new WallpaperRenderer());
  }

  private static class WallpaperRenderer implements GLSurfaceView.Renderer {
    public static final int TEXTURE_COUNT = 5;

    private WallpaperRenderer(final Context context) {
      loadTextures(context);
    }

    private void loadTextures(final Context context) {
      final int[] textures = new int[TEXTURE_COUNT];
      GLES11.glGenTextures(TEXTURE_COUNT, textures, 0);

      loadTexture(context, R.drawable.bg_2013);

    }

    private void loadTexture(final Context context, final int resourceId) {
      final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);

      final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
      byteBuffer.order(ByteOrder.BIG_ENDIAN);
      final IntBuffer intBuffer = byteBuffer.asIntBuffer();
      for (int y = bitmap.getHeight() - 1; y >= 0; y--) {
        for (int x = 0; x < bitmap.getHeight(); x++) {
          final int color = bitmap.getPixel(x, y);
          intBuffer.put(Color.argb(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color)));
        }
      }
    }

    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {

    }

    @Override
    public void onDrawFrame(final GL10 gl) {

    }
  }
}
