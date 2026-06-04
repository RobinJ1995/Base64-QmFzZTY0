package be.robinj.base64;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

	@Test
	public void useAppContext() {
		Context appContext = ApplicationProvider.getApplicationContext();
		assertEquals("be.robinj.base64", appContext.getPackageName());
	}
}
