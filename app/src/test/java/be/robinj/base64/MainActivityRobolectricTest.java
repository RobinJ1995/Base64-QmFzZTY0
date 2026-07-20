package be.robinj.base64;

import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Robolectric tests that drive the real MainActivity on the JVM: they inflate
 * the layout, run the fragment transaction and exercise the options-menu
 * navigation, covering behaviour the static encode/decode tests never touch.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityRobolectricTest {

	@Test
	public void launchInflatesLayoutAndAddsPlaceholderFragment() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();
			assertNotNull(activity);

			activity.getSupportFragmentManager().executePendingTransactions();
			Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.container);
			assertNotNull("PlaceholderFragment should be added to the container", fragment);
			assertTrue(fragment instanceof MainActivity.PlaceholderFragment);
		}
	}

	@Test
	public void aboutMenuItemLaunchesAboutActivity() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			MenuItem aboutItem = new RoboMenuItem(R.id.action_about);
			assertTrue(activity.onOptionsItemSelected(aboutItem));

			ShadowActivity shadow = Shadows.shadowOf(activity);
			Intent started = shadow.getNextStartedActivity();
			assertNotNull("selecting About should start an activity", started);
			assertEquals(About.class.getName(), started.getComponent().getClassName());
		}
	}
}
