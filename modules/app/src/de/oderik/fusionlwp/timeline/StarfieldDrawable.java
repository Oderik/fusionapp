package de.oderik.fusionlwp.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import de.oderik.fusionlwp.R;

/**
 * @author Maik.Riechel
 * @since 28.10.12
 */
public class StarfieldDrawable extends PaintDrawable {

  private final Drawable starfield;
  private Matrix   matrix = new Matrix();
  private Runnable what   = new Runnable() {
    @Override
    public void run() {
      invalidateSelf();
    }
  };

  public StarfieldDrawable(final Context context) {
    super();

    starfield = context.getResources().getDrawable(R.drawable.starfield);
    starfield.setBounds(0, 0, starfield.getIntrinsicWidth(), starfield.getIntrinsicHeight());

    paint.setColor(0xffffffff);
  }

  @Override
  public void draw(final Canvas canvas) {
    canvas.save();

    final Matrix canvasMatrix = canvas.getMatrix();
    canvasMatrix.preConcat(matrix);
    canvasMatrix.preRotate((System.currentTimeMillis() % 360000L) / 1000f, starfield.getIntrinsicWidth() / 2, starfield.getIntrinsicHeight() / 2);
    canvas.setMatrix(canvasMatrix);

    starfield.draw(canvas);
    canvas.restore();

    scheduleSelf(what, 50);
  }

  @Override
  protected void onBoundsChange(final Rect bounds) {
    super.onBoundsChange(bounds);

    final int width = bounds.width();
    final int height = bounds.height();
    final int starfieldWidth = starfield.getIntrinsicWidth();
    final int starfieldHeight = starfield.getIntrinsicHeight();

    final float radius = (float) Math.sqrt(width * width / 4f + height * height);

    final float scale = 2 * radius / Math.min(starfieldWidth, starfieldHeight);
    matrix.setTranslate(-starfieldWidth / 2f, 0);
    matrix.postScale(scale, scale);
    matrix.postTranslate(bounds.exactCenterX(), height - radius);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.OPAQUE;
  }
}
