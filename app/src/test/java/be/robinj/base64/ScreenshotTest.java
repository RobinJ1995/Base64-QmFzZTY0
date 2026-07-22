package be.robinj.base64;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowLooper;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Renders the real {@link MainActivity} and {@link About} screens on the JVM using
 * Robolectric's native graphics mode and writes PNG screenshots for the Play Store
 * listing. No emulator is required: the genuine inflated view hierarchy is measured,
 * laid out and rasterised exactly as it would appear on a device.
 *
 * <p>Guarded behind the {@code screenshots} system property (set by the Gradle
 * {@code generateScreenshots} task via {@code -Pscreenshots}) so a normal unit-test
 * run neither renders images nor fails when the native runtime is unavailable.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// A 1080x1920 (xxhdpi, 9:16) phone — well within the Play Store's 320-3840px / 2:1 limits.
@Config(qualifiers = "w360dp-h640dp-xxhdpi")
public class ScreenshotTest {

	/** Demo phrase requested for the store screenshots. */
	private static final String DEMO = "NOOT NOOT";

	private File outputDir;

	@Before
	public void onlyWhenRequested() {
		Assume.assumeTrue("run the generateScreenshots task (-Pscreenshots) to render images",
				Boolean.getBoolean("screenshots"));
		outputDir = new File(System.getProperty("screenshot.output.dir", "build/screenshots"));
		outputDir.mkdirs();
	}

	@Test
	public void captureMainAndAboutScreens() throws Exception {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup().visible()) {
			MainActivity activity = controller.get();
			activity.getSupportFragmentManager().executePendingTransactions();

			EditText txtPlain = activity.findViewById(R.id.txtPlain);

			// Type the demo phrase into the plaintext field; the TextWatcher
			// live-encodes it into the Base64 field below (NOOT NOOT -> Tk9PVCBOT09U).
			txtPlain.requestFocus();
			txtPlain.setText(DEMO);
			ShadowLooper.idleMainLooper();

			capture(activity, "1_en-GB_main.png");
		}

		try (ActivityController<About> controller =
				 Robolectric.buildActivity(About.class).setup().visible()) {
			About about = controller.get();
			about.getSupportFragmentManager().executePendingTransactions();
			ShadowLooper.idleMainLooper();

			capture(about, "2_en-GB_about.png");
		}
	}

	/** Measures, lays out and rasterises the activity's decor view to a PNG. */
	private void capture(Activity activity, String fileName) throws Exception {
		View decor = activity.getWindow().getDecorView();

		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		decor.measure(
				View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
		decor.layout(0, 0, width, height);

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE); // opaque background behind any transparent regions
		decor.draw(canvas);

		File out = new File(outputDir, fileName);
		try (FileOutputStream fos = new FileOutputStream(out)) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		}
		bitmap.recycle();

		System.out.println("Wrote screenshot: " + out.getAbsolutePath());
	}
}
