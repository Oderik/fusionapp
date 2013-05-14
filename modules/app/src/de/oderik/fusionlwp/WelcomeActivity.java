package de.oderik.fusionlwp;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

/**
 * Created: 25.05.12
 *
 * @author Oderik
 */
public class WelcomeActivity extends Activity implements View.OnClickListener {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.welcome);
    final TextView welcomeText = (TextView) findViewById(R.id.welcomeText);
    if (welcomeText != null) {
      welcomeText.setTypeface(Typeface.createFromAsset(getAssets(), "Anton.ttf"));
      welcomeText.setText(Html.fromHtml(getString(R.string.welcome)));
    }
  }

  private void startWallpaperChooser() {
    final Intent intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
    startActivity(intent);
  }

  @Override
  public void onClick(final View v) {
    startWallpaperChooser();
  }
}