package winflex.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author lixiaohui
 * @date 2016年10月13日 下午2:26:36
 * 
 */
public class ThrowableUtils {

	public static String toDetailString(Throwable e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PrintStream out = new PrintStream(baos)) {
			e.printStackTrace(out);
			out.flush();
			return baos.toString();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends Throwable> void throwException(Throwable t) throws E {
        throw (E) t;
    }
}
