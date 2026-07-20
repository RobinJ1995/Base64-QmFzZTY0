package be.robinj.base64;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Robolectric tests for the About screen: they inflate the real layout, verify
 * the version label is populated from the package metadata and that the
 * developer button fires a browser intent.
 */
@RunWith(RobolectricTestRunner.class)
public class AboutRobolectricTest {

	@Test
	public void launchInflatesLayoutAndAddsPlaceholderFragment() {
		try (ActivityController<About> controller =
				 Robolectric.buildActivity(About.class).setup()) {
			About activity = controller.get();
			assertNotNull(activity);

			activity.getSupportFragmentManager().executePendingTransactions();
			Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.container);
			assertNotNull("PlaceholderFragment should be added to the container", fragment);
			assertTrue(fragment instanceof About.PlaceholderFragment);
		}
	}

	@Test
	public void versionLabelIsResolvedFromPackageInfo() {
		try (ActivityController<About> controller =
				 Robolectric.buildActivity(About.class).setup()) {
			About activity = controller.get();
			activity.getSupportFragmentManager().executePendingTransactions();

			TextView version = activity.findViewById(R.id.version);
			String text = version.getText().toString();

			assertTrue("version should start with 'v' but was: " + text, text.startsWith("v"));
			assertTrue("package metadata should resolve without error: " + text,
					!text.startsWith("{"));
		}
	}

	@Test
	public void developerButtonOpensTheDeveloperWebsite() {
		try (ActivityController<About> controller =
				 Robolectric.buildActivity(About.class).setup()) {
			About activity = controller.get();

			View dev = activity.findViewById(R.id.dev);
			assertNotNull("developer button should be present", dev);
			activity.btnDev_clicked(dev);

			ShadowActivity shadow = Shadows.shadowOf(activity);
			Intent started = shadow.getNextStartedActivity();
			assertNotNull("clicking developer should start an activity", started);
			assertEquals(Intent.ACTION_VIEW, started.getAction());
			assertEquals(Uri.parse("http://www.robinj.be/"), started.getData());
		}
	}

	@Test
	public void onCreateOptionsMenuReturnsTrue() {
		try (ActivityController<About> controller =
				 Robolectric.buildActivity(About.class).setup()) {
			About activity = controller.get();
			assertTrue(activity.onCreateOptionsMenu(new RoboMenu(activity)));
		}
	}
}
