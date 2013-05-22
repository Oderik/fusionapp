package de.oderik.fusionlwp.util;

/**
 * @author maik.riechel
 * @since 15.05.13 21:05
 */
public class OffsetsHolder {

  private float xOffset;
  private float yOffset;
  private float xStep;
  private float yStep;
  private int xPixels;
  private int yPixels;

  public interface DimensionValues {
    float getOffset();

    float getStep();

    int getPixels();
  }

  public void set(final float xOffset, final float yOffset, final float xStep, final float yStep, final int xPixels, final int yPixels) {
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.xStep = xStep;
    this.yStep = yStep;
    this.xPixels = xPixels;
    this.yPixels = yPixels;
  }

  public void set(final OffsetsHolder source) {
    this.xOffset = source.xOffset;
    this.yOffset = source.yOffset;
    this.xStep = source.xStep;
    this.yStep = source.yStep;
    this.xPixels = source.xPixels;
    this.yPixels = source.yPixels;
  }

  public DimensionValues getHorizontals() {
    return new HorizontalDimensionValues();
  }

  public DimensionValues getVerticals() {
    return new VerticalDimensionValues();
  }

  private class HorizontalDimensionValues implements DimensionValues {
    @Override
    public float getOffset() {
      return xOffset;
    }

    @Override
    public float getStep() {
      return xStep;
    }

    @Override
    public int getPixels() {
      return xPixels;
    }
  }

  private class VerticalDimensionValues implements DimensionValues {
    @Override
    public float getOffset() {
      return yOffset;
    }

    @Override
    public float getStep() {
      return yStep;
    }

    @Override
    public int getPixels() {
      return yPixels;
    }
  }

  public float getxOffset() {
    return xOffset;
  }

  public int getxPixels() {
    return xPixels;
  }

  public float getxStep() {
    return xStep;
  }

  public float getyOffset() {
    return yOffset;
  }

  public int getyPixels() {
    return yPixels;
  }

  public float getyStep() {
    return yStep;
  }
}
