package cc.lixiaohui.util;


/**
 * 随机数/字符生成工具类
 * 
 * @author lixiaohui
 * @date 2016年10月12日 上午11:21:09
 * 
 */
public class Randoms {
	
	private static final char ASCII_A = 65;
	private static final char ASCII_Z = 90;
	private static final char ASCII_a = 97;
	private static final char ASCII_z = 122;

	/**
	 * 生成随机long值N, min <= N <= max
	 * 
	 * @param min 下界
	 * @param max 上界
	 * @return 随机值N
	 */
	public static long randomLong(long min, long max) {
		checkNumber(min, max);
		if (max - min > 0) {
			return min + (long) (Math.random() * (max - min + 1));
		} else {
			double range = (double) max - (double) min;
			return (long) ((double)min + (Math.random() * range));
		}
	}

	/**
	 * 生成随机long值N, Long.MIN_VALUE <= N <= Long.MAX_VALUE
	 * 
	 * @return 随机值N
	 */
	public static long randomLong() {
		return randomLong(Long.MIN_VALUE, Long.MAX_VALUE);
	}
	
	/**
	 * 生成随机long值N, 1 <= N <= max
	 * 
	 * @param max 上界
	 * @return 随机值N
	 */
	public static long randomPositiveLong(long max) {
		return randomLong(1, max);
	}
	
	/**
	 * 生成随机long值N, 1 <= N <= Long.MAX_VALUE
	 * 
	 * @param max 上界
	 * @return 随机值N
	 */
	public static long randomPositiveLong() {
		return randomLong(1, Long.MAX_VALUE);
	}

	public static long randomNaturalLong(long max) {
		return randomLong(0, max);
	}
	
	public static long randomNaturalLong() {
		return randomLong(0, Long.MAX_VALUE);
	}

	/**
	 * 生成随机boolean
	 * 
	 * @return 随机值N
	 */
	public static boolean randomBoolean() {
		int n = (int) (Math.random() * 2);
		return n == 1;
	}

	/**
	 * 生成int随机值N, min <= N <= max
	 * 
	 * @param min
	 *            下界
	 * @param max
	 *            上界
	 * @return 随机值
	 */
	public static int randomInt(int min, int max) {
		checkNumber(min, max);
		/*if (max - min > 0) {
			return (int) (Math.random() * (max - min + 1)) - (max - min - 1);
		} else {
			long range = (long) max - (long) min;
			return Math.random() * range
		}*/
		return (int) randomLong(min, max);
	}

	/**
	 * 生成int随机值N, Integer.MIN_VALUE <= N <= Integer.MAX_VALUE
	 * 
	 * @return 随机值
	 */
	public static int randomInt() {
		return randomInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * 生成int随机值N, 1 <= N <= max
	 * 
	 * @param max
	 *            上界
	 * @return 随机值
	 */
	public static int randomPositiveInt(int max) {
		return randomInt(1, max);
	}

	/**
	 * 生成int随机值N, 1 <= N <= Integer.MAX_VALUE
	 * 
	 * @return 随机值
	 */
	public static int randomPositiveInt() {
		return randomInt(1, Integer.MAX_VALUE);
	}
	
	public static int randomNaturalInt(int max) {
		return randomInt(0, max);
	}
	
	public static int randomNaturalInt() {
		return randomInt(0, Integer.MAX_VALUE);
	}

	/**
	 * 生成随机short N, min <= N <= max
	 * 
	 * @param min
	 *            下界
	 * @param max
	 *            上界
	 * @return 随机值
	 */
	public static short randomShort(short min, short max) {
		return (short) randomLong(min, max);
	}

	/**
	 * 生成随机short N, Short.MIN_VALUE <= N <= Short.MAX_VALUE
	 * 
	 * @param min
	 *            下界
	 * @param max
	 *            上界
	 * @return 随机值
	 */
	public static short randomShort() {
		return randomShort(Short.MIN_VALUE, Short.MAX_VALUE);
	}
	
	/**
	 * 生成随机short N, 1 <= N <= max
	 * 
	 * @param min
	 *            下界
	 * @param max
	 *            上界
	 * @return 随机值
	 */
	public static short randomPositiveShort(short max) {
		return randomShort((short) 1, max);
	}
	
	/**
	 * 生成随机short N, 1 <= N <= Short.MAX_VALUE
	 * 
	 * @param min
	 *            下界
	 * @param max
	 *            上界
	 * @return 随机值
	 */
	public static short randomPositiveShort() {
		return randomShort((short) 1, Short.MAX_VALUE);
	}
	
	
	public static short randomNaturalShort(short max) {
		return randomShort((short) 0, max);
	}
	
	public static short randomNaturalShort() {
		return randomShort((short) 0, Short.MAX_VALUE);
	}
	
	public static byte randomByte(byte min, byte max) {
		return (byte) randomLong(min, max);
	}
	
	public static byte randomByte() {
		return randomByte(Byte.MIN_VALUE, Byte.MAX_VALUE);
	}
	
	public static byte randomPositiveByte(byte max) {
		return randomByte((byte) 1, max);
	}
	
	public static byte randomPositiveByte() {
		return randomByte((byte) 1, Byte.MAX_VALUE);
	}
	
	public static byte randomNaturalByte(byte max) {
		return randomByte((byte) 0, max);
	}
	
	public static byte randomNaturalByte() {
		return randomByte((byte) 0, Byte.MAX_VALUE);
	}
	
	public static char randomChar(char min, char max) {
		return (char) randomLong(min, max);
	}
	
	public static char randomChar() {
		return randomChar(Character.MIN_VALUE, Character.MAX_VALUE);
	}
	
	public static char randomAscii() {
		return randomChar((char) 0, (char) Byte.MAX_VALUE);
	}
	
	public static char randomUpperCharacter() {
		return randomChar(ASCII_A, ASCII_Z);
	}
	
	public static char randomLowerCharacter() {
		return randomChar(ASCII_a, ASCII_z);
	}
	
	 
	// ----------------- util methods -----------------------
	
	private static void checkNumber(long min, long max) {
		if (min > max) {
			throw new IllegalArgumentException("min cannot be greater than max");
		}
	}
}
