package de.oderik.fusionlwp.timeline;

import android.opengl.GLSurfaceView;
import android.util.Log;
import de.oderik.fusionlwp.util.Tag;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
* @author maik.riechel
* @since 31.10.12
*/
public class TimeLineRenderer implements GLSurfaceView.Renderer {
  private static final String TAG = Tag.of(TimeLineRenderer.class);
  private int width;
  private int height;

  private final FloatBuffer vertexes;

  public TimeLineRenderer() {
    final float[] rawCoords = {
            -1, -1,
            1,  1,
            0, .5f,
            1, 0,
            0, -1
    };
    final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(rawCoords.length * 4);
    byteBuffer.order(ByteOrder.nativeOrder());
    vertexes = byteBuffer.asFloatBuffer();
    vertexes.put(rawCoords);
    vertexes.position(0);
  }

  @Override
  public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
    Log.i(TAG, String.format("Surface Created: %s, %s", gl, config));
    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

    gl.glClearColor(0f, 0f, 0f, 1);
    gl.glShadeModel(GL10.GL_FLAT);
    gl.glDisable(GL10.GL_DEPTH_TEST);
    gl.glEnable(GL10.GL_TEXTURE_2D);

    gl.glDisable(GL10.GL_DITHER);
    gl.glDisable(GL10.GL_LIGHTING);

    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
  }

  @Override
  public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
    Log.i(TAG, String.format("Surface Changed: %s, %d, %d", gl, width, height));

    this.width = width;
    this.height = height;

    gl.glMatrixMode(GL10.GL_PROJECTION);
    gl.glLoadIdentity();
  }

  @Override
  public void onDrawFrame(final GL10 gl) {
    gl.glLineWidth(2f);
    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexes);
    gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 5);

    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
  }
}
