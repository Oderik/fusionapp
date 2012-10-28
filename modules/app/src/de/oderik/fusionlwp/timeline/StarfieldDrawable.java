package de.oderik.fusionlwp.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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
    super(new Paint());

    starfield = context.getResources().getDrawable(R.drawable.starfield);
    starfield.setBounds(0, 0, starfield.getIntrinsicWidth(), starfield.getIntrinsicHeight());

    paint.setColor(0xffffffff);
  }

  @Override
  public void draw(final Canvas canvas) {
    canvas.save();

    final Matrix canvasMatrix = canvas.getMatrix();
    canvasMatrix.postRotate((System.currentTimeMillis() % 3600L) / 10f, starfield.getIntrinsicWidth() / 2, starfield.getIntrinsicHeight() / 2);
    canvasMatrix.postConcat(matrix);
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

    final float horizontalScale = (1f * width) / starfieldWidth;
    final float verticalScale = (2f * height) / starfieldHeight;
    final float scale = Math.max(verticalScale, horizontalScale);
    matrix.setScale(scale, scale, starfieldWidth / 2, starfieldHeight / 4);
    matrix.preTranslate(Math.min((width - height) / 2, 0), Math.min(height - width, 0));
  }
}
