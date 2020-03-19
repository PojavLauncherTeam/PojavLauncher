package junit.extensions;

import junit.framework.*;

/**
 * A Decorator that runs a test repeatedly.
 *
 */
public class RepeatedTest extends  TestDecorator {
	private int fTimesRepeat;

	public RepeatedTest(Test test, int repeat) {
		super(test);
		if (repeat < 0)
			throw new IllegalArgumentException("Repetition count must be > 0");
		fTimesRepeat= repeat;
	}
	public int countTestCases() {
		return super.countTestCases()*fTimesRepeat;
	}
	public void run(TestResult result) {
		for (int i= 0; i < fTimesRepeat; i++) {
			if (result.shouldStop())
				break;
			super.run(result);
		}
	}
	public String toString() {
		return super.toString()+"(repeated)";
	}
}
