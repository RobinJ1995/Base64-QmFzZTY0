package be.robinj.base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Well-known Base64 edge cases: the canonical Wikipedia / RFC 4648 samples, the
 * two "awkward" alphabet characters ('+' and '/'), MIME line wrapping and case
 * sensitivity.
 */
@RunWith(RobolectricTestRunner.class)
public class Base64EdgeCasesTest {

	// --- the canonical Wikipedia examples --------------------------------

	@Test
	public void wikipediaManExamplesShowEachPaddingLength() {
		// The textbook illustration of how padding grows as the final group
		// shrinks: "Man" (no pad), "Ma" (one '='), "M" (two '=').
		assertEquals("TWFu", MainActivity.encode("Man"));
		assertEquals("TWE=", MainActivity.encode("Ma"));
		assertEquals("TQ==", MainActivity.encode("M"));

		assertEquals("Man", MainActivity.decode("TWFu"));
		assertEquals("Ma", MainActivity.decode("TWE="));
		assertEquals("M", MainActivity.decode("TQ=="));
	}

	@Test
	public void wikipediaManyHandsSentence() {
		String plain = "Many hands make light work.";
		String encoded = "TWFueSBoYW5kcyBtYWtlIGxpZ2h0IHdvcmsu";
		assertEquals(encoded, MainActivity.encode(plain));
		assertEquals(plain, MainActivity.decode(encoded));
	}

	// --- the '+' and '/' alphabet characters -----------------------------

	@Test
	public void exercisesThePlusAndSlashAlphabetCharacters() {
		// Indices 62 and 63 of the standard alphabet are '+' and '/'; these
		// inputs are the smallest ASCII strings that produce each.
		assertEquals("Pj4+", MainActivity.encode(">>>"));   // ends with '+'
		assertEquals("Pz8/", MainActivity.encode("???"));   // ends with '/'

		assertEquals(">>>", MainActivity.decode("Pj4+"));
		assertEquals("???", MainActivity.decode("Pz8/"));
	}

	@Test
	public void usesStandardAlphabetNotUrlSafe() {
		// Standard Base64 (RFC 4648 §4) uses '+' and '/', never the URL-safe
		// '-' and '_' (§5).
		String encoded = MainActivity.encode(">>>???");
		assertTrue("expected a '+' or '/' in " + encoded,
				encoded.indexOf('+') >= 0 || encoded.indexOf('/') >= 0);
		assertFalse("URL-safe '-' must not appear", encoded.indexOf('-') >= 0);
		assertFalse("URL-safe '_' must not appear", encoded.indexOf('_') >= 0);
	}

	// --- MIME line wrapping ----------------------------------------------

	@Test
	public void longOutputIsWrappedAtMimeLineLengthAndStillDecodes() {
		// android.util.Base64.DEFAULT emits MIME-style output wrapped at 76
		// characters, so a sufficiently long payload contains a line break —
		// which decode() transparently ignores on the way back.
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			sb.append('A');
		}
		String plain = sb.toString();

		String encoded = MainActivity.encode(plain);
		assertTrue("a >76 char encoding should be line-wrapped: " + encoded,
				encoded.contains("\n"));
		assertEquals(plain, MainActivity.decode(encoded));
	}

	// --- case sensitivity ------------------------------------------------

	@Test
	public void decodingIsCaseSensitive() {
		// Base64 letters are case-significant: swapping case selects different
		// alphabet indices and therefore different bytes.
		assertEquals("Man", MainActivity.decode("TWFu"));
		assertNotEquals("Man", MainActivity.decode("twfu"));
	}
}
