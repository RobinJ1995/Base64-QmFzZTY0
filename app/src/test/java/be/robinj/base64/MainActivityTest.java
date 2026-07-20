package be.robinj.base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

	// --- RFC 4648 §10 canonical test vectors -----------------------------

	@Test
	public void encode_rfc4648Vectors() {
		assertEquals("Zg==", MainActivity.encode("f"));
		assertEquals("Zm8=", MainActivity.encode("fo"));
		assertEquals("Zm9v", MainActivity.encode("foo"));
		assertEquals("Zm9vYg==", MainActivity.encode("foob"));
		assertEquals("Zm9vYmE=", MainActivity.encode("fooba"));
		assertEquals("Zm9vYmFy", MainActivity.encode("foobar"));
	}

	@Test
	public void decode_rfc4648Vectors() {
		assertEquals("f", MainActivity.decode("Zg=="));
		assertEquals("fo", MainActivity.decode("Zm8="));
		assertEquals("foo", MainActivity.decode("Zm9v"));
		assertEquals("foob", MainActivity.decode("Zm9vYg=="));
		assertEquals("fooba", MainActivity.decode("Zm9vYmE="));
		assertEquals("foobar", MainActivity.decode("Zm9vYmFy"));
	}

	// --- padding ---------------------------------------------------------

	@Test
	public void encode_producesCorrectPaddingForEachRemainder() {
		// 1 leftover byte -> "==", 2 leftover bytes -> "=", 0 -> none.
		assertTrue(MainActivity.encode("a").endsWith("=="));
		assertTrue(MainActivity.encode("ab").endsWith("="));
		assertFalse(MainActivity.encode("abc").endsWith("="));
	}

	// --- trimming behaviour ----------------------------------------------

	@Test
	public void encode_trimsSurroundingWhitespaceBeforeEncoding() {
		// encode() trims the input, so padding whitespace must not affect it.
		assertEquals(MainActivity.encode("Hello World"), MainActivity.encode("   Hello World   "));
		assertEquals(MainActivity.encode("Hello World"), MainActivity.encode("\n\tHello World\t\n"));
	}

	@Test
	public void encode_preservesInteriorWhitespace() {
		// Only the ends are trimmed; interior spaces are part of the payload.
		assertEquals("a b", MainActivity.decode(MainActivity.encode("a b")));
	}

	@Test
	public void decode_trimsSurroundingWhitespace() {
		assertEquals("Hello World", MainActivity.decode("  SGVsbG8gV29ybGQ=  "));
	}

	@Test
	public void decode_ignoresInteriorLineBreaks() {
		// The DEFAULT decoder skips line breaks, matching how wrapped Base64
		// (e.g. MIME) is laid out.
		assertEquals("Hello World", MainActivity.decode("SGVsbG8g\nV29ybGQ="));
	}

	// --- encoding of non-ASCII content -----------------------------------

	@Test
	public void roundtrip_multiByteUnicode() {
		String original = "日本語 🚀 Ω";
		assertEquals(original, MainActivity.decode(MainActivity.encode(original)));
	}

	@Test
	public void roundtrip_longText() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 500; i++) {
			sb.append("The quick brown fox 0123456789 ");
		}
		String original = sb.toString().trim();
		assertEquals(original, MainActivity.decode(MainActivity.encode(original)));
	}

	// --- invalid input ---------------------------------------------------

	@Test(expected = IllegalArgumentException.class)
	public void decode_rejectsBadLength() {
		// A five-character group (length % 4 == 1) can never be valid Base64.
		MainActivity.decode("AAAAA");
	}

	@Test(expected = IllegalArgumentException.class)
	public void decode_rejectsDataAfterPadding() {
		// Trailing data past the '=' padding is structurally invalid.
		MainActivity.decode("SGVsbG8=extra");
	}

	@Test(expected = IllegalArgumentException.class)
	public void decode_rejectsMisplacedPadding() {
		MainActivity.decode("AB=C");
	}
}
