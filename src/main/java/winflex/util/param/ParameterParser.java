package winflex.util.param;

import winflex.util.Strings;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午5:35:48
 * @version 1.0
 */
class ParameterParser {

    public static final void parse(String text, Recognizer recognizer) {
        int stringLength = text.length();
        boolean inQuote = false;
        char lastChar = Character.MAX_VALUE;
        char lastButOneChar = Character.MAX_VALUE;
        for (int index = 0; index < stringLength; index++) {
            char c = text.charAt(index);
            if (inQuote) {
                if ('\'' == c) {
                    inQuote = false;
                }
                recognizer.other(index);
            } else if ('\'' == c) {
                inQuote = true;
                recognizer.other(index);
            } else {
                if (c == ':') {
                    if (lastChar == '\\' && lastButOneChar != '\\') { // escape
                        recognizer.escape(index - 1);
                        recognizer.other(index);
                        continue;
                    }
                    
                    int right = Strings.firstIndexOfAnyChar(text, Strings.SEPERATE_CHARS, index + 1);
                    int chopLocation = right < 0 ? text.length() : right;
                    String param = text.substring(index + 1, chopLocation);
                    if (param.isEmpty()) {
                        throw new RuntimeException("Space is not allowed after parameter prefix ':'[" + text + "]");
                    }
                    recognizer.namedParameter(param, index);
                    index = chopLocation - 1;
                } else if (c == '?') {
                    if (lastChar == '\\' && lastButOneChar != '\\') { // escape
                        recognizer.escape(index - 1);
                        recognizer.other(index);
                        continue;
                    }
                    recognizer.ordinalParameter(index);
                } else {
                    recognizer.other(index);
                }
            }
            lastButOneChar = lastChar;
            lastChar = c;
        }
    }
}
