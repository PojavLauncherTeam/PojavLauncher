package junit.extensions;

import junit.framework.*;

/**
 * A TestCase that expects an Exception of class fExpected to be thrown.
 * The other way to check that an expected exception is thrown is:
 * <pre>
 * try {
 *   shouldThrow();
 * }
 * catch (SpecialException e) {
 *   return;
 * }
 * fail("Expected SpecialException");
 * </pre>
 *
 * To use ExceptionTestCase, create a TestCase like:
 * <pre>
 * new ExceptionTestCase("testShouldThrow", SpecialException.class);
 * </pre>
 */
public class ExceptionTestCase extends TestCase {
	Class<?> fExpected;

	public ExceptionTestCase(String name, Class exception) {
		super(name);
		fExpected= exception;
	}
	/**
	 * Execute the test method expecting that an Exception of
	 * class fExpected or one of its subclasses will be thrown
	 */
	protected void runTest() throws Throwable {
		try {
			super.runTest();
		}
		catch (Exception e) {
			if (fExpected.isAssignableFrom(e.getClass()))
				return;
			else
				throw e;
		}
		fail("Expected exception " + fExpected);
	}
}
