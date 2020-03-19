package sun.misc;

public final class FakeUnsafe
{
	private FakeUnsafe() {}
	
	public static FakeUnsafe getUnsafe() {
		return new FakeUnsafe();
	}
	
	public void ensureClassInitialized(Class<?> unused) {
		
	}
}
