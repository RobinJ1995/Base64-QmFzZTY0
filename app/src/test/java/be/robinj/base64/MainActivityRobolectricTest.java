package be.robinj.base64;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

	@Test
	public void onCreateOptionsMenuInflatesTheAboutItem() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			RoboMenu menu = new RoboMenu(activity);
			assertTrue(activity.onCreateOptionsMenu(menu));
			assertNotNull("About item should be inflated into the menu",
					menu.findItem(R.id.action_about));
		}
	}

	@Test
	public void unhandledMenuItemIsDelegatedToSuper() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();
			assertFalse(activity.onOptionsItemSelected(new RoboMenuItem(0)));
		}
	}

	@Test
	public void typingPlaintextLiveEncodesIntoTheBase64Field() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			EditText txtPlain = activity.findViewById(R.id.txtPlain);
			EditText txtBase64 = activity.findViewById(R.id.txtBase64);

			assertTrue("plaintext field should take focus", txtPlain.requestFocus());
			txtPlain.setText("Hello World");

			assertEquals("SGVsbG8gV29ybGQ=", txtBase64.getText().toString());
		}
	}

	@Test
	public void typingBase64LiveDecodesIntoThePlaintextField() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			EditText txtPlain = activity.findViewById(R.id.txtPlain);
			EditText txtBase64 = activity.findViewById(R.id.txtBase64);

			assertTrue("base64 field should take focus", txtBase64.requestFocus());
			txtBase64.setText("SGVsbG8gV29ybGQ=");

			assertEquals("Hello World", txtPlain.getText().toString());
		}
	}

	@Test
	public void whitespaceTypedIntoBase64FieldIsStrippedInPlace() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			EditText txtPlain = activity.findViewById(R.id.txtPlain);
			EditText txtBase64 = activity.findViewById(R.id.txtBase64);

			assertTrue(txtBase64.requestFocus());
			// A space pasted into the (multiline) Base64 box is removed so the
			// value stays a single decodable token.
			txtBase64.setText("SGVsbG8g V29ybGQ=");

			assertEquals("SGVsbG8gV29ybGQ=", txtBase64.getText().toString());
			assertEquals("Hello World", txtPlain.getText().toString());
		}
	}

	@Test
	public void invalidBase64ShowsAnErrorAndLeavesPlaintextUntouched() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			EditText txtPlain = activity.findViewById(R.id.txtPlain);
			EditText txtBase64 = activity.findViewById(R.id.txtBase64);

			String plainBefore = txtPlain.getText().toString();
			assertTrue(txtBase64.requestFocus());
			// A five-character group cannot be valid Base64, so decode() throws
			// and the fragment flags the error instead of updating plaintext.
			txtBase64.setText("AAAAA");

			assertNotNull("an error should be flagged on the base64 field", txtBase64.getError());
			assertEquals("plaintext should be left untouched on a decode error",
					plainBefore, txtPlain.getText().toString());
		}
	}

	@Test
	public void editingAnUnfocusedFieldDoesNotOverwriteTheOther() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			EditText txtPlain = activity.findViewById(R.id.txtPlain);
			EditText txtBase64 = activity.findViewById(R.id.txtBase64);

			assertTrue(txtBase64.requestFocus());
			txtBase64.setText("Zm9v");
			txtPlain.setText("ignored");

			assertEquals("Zm9v", txtBase64.getText().toString());
		}
	}

	@Test
	public void editingClearsAnyErrorMarkersOnBothFields() {
		try (ActivityController<MainActivity> controller =
				 Robolectric.buildActivity(MainActivity.class).setup()) {
			MainActivity activity = controller.get();

			EditText txtPlain = activity.findViewById(R.id.txtPlain);
			EditText txtBase64 = activity.findViewById(R.id.txtBase64);

			txtPlain.setError("boom");
			txtBase64.setError("boom");

			assertTrue(txtPlain.requestFocus());
			txtPlain.setText("reset");

			assertNull(txtPlain.getError());
			assertNull(txtBase64.getError());
		}
	}
}
