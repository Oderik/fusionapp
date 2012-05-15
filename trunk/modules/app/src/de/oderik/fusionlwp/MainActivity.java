package de.oderik.fusionlwp;

import android.app.Activity;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

  private FusionWallpaperService  fusionWallpaperService;
  private WallpaperService.Engine engine;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    fusionWallpaperService = new FusionWallpaperService();
    fusionWallpaperService.onCreate();

    this.engine = fusionWallpaperService.onCreateEngine();

    final SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface);
    surfaceView.getHolder().addCallback(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    engine.onVisibilityChanged(true);
  }

  @Override
  protected void onStop() {
    super.onStop();
    engine.onVisibilityChanged(false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    engine.onDestroy();
    fusionWallpaperService.onDestroy();
  }

  public void surfaceCreated(final SurfaceHolder holder) {
    engine.onSurfaceCreated(holder);
  }

  public void surfaceChanged(final SurfaceHolder holder, final int i, final int i1, final int i2) {
    engine.onSurfaceChanged(holder, i, i1, i2);
  }

  public void surfaceDestroyed(final SurfaceHolder holder) {
    engine.onSurfaceDestroyed(holder);
  }
}
