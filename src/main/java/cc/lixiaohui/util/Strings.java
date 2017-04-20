package cc.lixiaohui.util;

/**
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午5:42:34
 * @version 1.0
 */
public class Strings {

    private static final String ALL_SEPERATOR = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\";
    public static final char[] SEPERATE_CHARS = new char[ALL_SEPERATOR.length()];
    static {
        for (int i = 0; i < ALL_SEPERATOR.length(); i++) {
            SEPERATE_CHARS[i] = ALL_SEPERATOR.charAt(i);
        }
    }
    
    public static int firstIndexOfAnyChar(String string, char[] chars, int startIndex) {
        int matchAt = -1;
        for (int i = 0; i < chars.length; i++) {
            int curMatch = string.indexOf(chars[i], startIndex);
            if (curMatch >= 0) {
                if (matchAt == -1) { // first time we find match!
                    matchAt = curMatch;
                } else {
                    matchAt = Math.min(matchAt, curMatch);
                }
            }
        }
        return matchAt;
    }

    public static void main(String[] args) {
        String s = "adsa a";
        System.out.println(firstIndexOfAnyChar(s, SEPERATE_CHARS, 0));
    }
}
