package cc.lixiaohui.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author lixiaohui
 * @date 2016年10月13日 下午2:26:36
 * 
 */
public class ExceptionUtils {
	
	public static String toDetail(Throwable e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(baos);
		e.printStackTrace(out);
		return baos.toString();
	}
	
}
