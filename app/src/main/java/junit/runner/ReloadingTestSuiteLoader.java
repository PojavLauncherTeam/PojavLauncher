package junit.runner;

/**
 * A TestSuite loader that can reload classes.
 */
public class ReloadingTestSuiteLoader implements TestSuiteLoader {
	
	public Class load(String suiteClassName) throws ClassNotFoundException {
		return createLoader().loadClass(suiteClassName, true);
	}
	
	public Class reload(Class aClass) throws ClassNotFoundException {
		return createLoader().loadClass(aClass.getName(), true);
	}
	
	protected TestCaseClassLoader createLoader() {
		return new TestCaseClassLoader();
	}
}
