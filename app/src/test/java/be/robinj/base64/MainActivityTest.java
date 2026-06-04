package be.robinj.base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MainActivityTest {

	@Test
	public void encode_simpleString() {
		assertEquals("SGVsbG8gV29ybGQ=", MainActivity.encode("Hello World"));
	}

	@Test
	public void decode_validBase64() {
		assertEquals("Hello World", MainActivity.decode("SGVsbG8gV29ybGQ="));
	}

	@Test
	public void roundtrip_preservesContent() {
		String original = "The quick brown fox jumps over the lazy dog";
		assertEquals(original, MainActivity.decode(MainActivity.encode(original)));
	}

	@Test
	public void encode_emptyString() {
		assertTrue(MainActivity.encode("").isEmpty());
	}

	@Test
	public void decode_emptyString() {
		assertTrue(MainActivity.decode("").isEmpty());
	}

	@Test
	public void encode_specialCharacters() {
		String original = "café & résumé";
		assertEquals(original, MainActivity.decode(MainActivity.encode(original)));
	}

	@Test
	public void encode_numbersAndSymbols() {
		String original = "1234567890!@#$%^&*()";
		assertEquals(original, MainActivity.decode(MainActivity.encode(original)));
	}
}
