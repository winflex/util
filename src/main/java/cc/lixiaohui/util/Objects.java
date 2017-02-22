package cc.lixiaohui.util;

public class Objects {

	public static void requiredNonNull(Object... objs) {
		if (objs.length % 2 != 0) {
			throw new IllegalArgumentException("objs");
		}

		for (int i = 0; i < objs.length; i += 2) {
			if (objs[i] == null) {
				throw new NullPointerException(objs[i + 1].toString());
			}
		}
	}

	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}

	public static <T> T requireNonNull(T obj, String message) {
		if (obj == null)
			throw new NullPointerException(message);
		return obj;
	}
	
}