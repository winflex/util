package cc.lixiaohui.util.param;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午5:35:48
 * @version 1.0
 *
 */
public class ParameterParser {

    public static final void parse(String text, Recognizer recognizer) {
        int stringLength = text.length();
        boolean inQuote = false;
        for (int index = 0; index < stringLength; index++) {
            char c = text.charAt(index);
            if (inQuote) {
                if ('\'' == c) {
                    inQuote = false;
                }
                recognizer.other(c);
            } else if ('\'' == c) {
                inQuote = true;
                recognizer.other(c);
            } else {
                if (c == ':') {
                    int right = Strings.firstIndexOfAnyChar(text, Strings.SEPERATORS, index + 1);
                    int chopLocation = right < 0 ? text.length() : right;
                    String param = text.substring(index + 1, chopLocation);
                    if (param.isEmpty()) {
                        throw new RuntimeException("Space is not allowed after parameter prefix ':'[" + text + "]");
                    }
                    recognizer.namedParameter(param, index);
                    index = chopLocation - 1;
                } else if (c == '?') {
                    recognizer.ordinalParameter(index);
                } else {
                    recognizer.other(c);
                }
            }
        }
    }

}
