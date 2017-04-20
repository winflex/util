package cc.lixiaohui.util.param;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月20日 上午9:40:19
 * @version 1.0
 *
 */
public class ParameteredString {

    private final String originalString;
    private final ParameterMetaData parameterMetaData;

    private final Map<Integer, Value> ordinalValues = new HashMap<>();
    private final Map<String, Value> namedValues = new HashMap<>();

    private final boolean allowParameterUnset;

    public ParameteredString(String string) {
        this(string, false);
    }

    public ParameteredString(String string, boolean allowParameterUnset) {
        this.originalString = string;
        this.allowParameterUnset = allowParameterUnset;
        this.parameterMetaData = buildParameterMetaData(string);
    }

    private ParameterMetaData buildParameterMetaData(String string) {
        ParameterLocationRecognizer recognizer = new ParameterLocationRecognizer();
        ParameterParser.parse(string, recognizer);
        return ParameterMetaData.buildFromParameterLocationRecognizer(recognizer);
    }

    public ParameteredString setParameter(String name, Object v) {
        return setParameter(name, v, null);
    }

    public ParameteredString setParameter(String name, Object v, CharSequence encloseChars) {
        if (!parameterMetaData.isParameterNameExist(name)) {
            throw new RuntimeException("No such parameter named " + name);
        }
        
        Value value = namedValues.get(name);
        if (value == null) {
            value = new Value(v, encloseChars);
            namedValues.put(name, value);
        }
        return this;
    }

    public ParameteredString setParameter(int ordinal, Object v) {
        return setParameter(ordinal, v, null);
    }

    public ParameteredString setParameter(int ordinal, Object v, CharSequence enclosingChars) {
        if (!parameterMetaData.isOrdinalExist(ordinal)) {
            throw new RuntimeException("No such parameter located by ordinal " + ordinal);
        }
        
        Value value = ordinalValues.get(ordinal);
        if (value == null) {
            value = new Value(v, enclosingChars);
            ordinalValues.put(ordinal, value);
        }
        return this;
    }

    public String toNormalizedString() {
        validateParameters();

        final char[] originalChars = originalString.toCharArray();
        final StringBuilder buf = new StringBuilder();
        final ParameterMetaData parameterMetaData = this.parameterMetaData;
        for (int currPosition = 0; currPosition < originalChars.length; currPosition++) {
            if (parameterMetaData.isOrdinalParameterPosition(currPosition)) {
                OrdinalParameterDescriptor descriptor = parameterMetaData.getOrdinalParameterDescriptor(currPosition);
                Value value = ordinalValues.get(descriptor.getOrdinalPosition());
                buf.append(value);
            } else if (parameterMetaData.isNamedParameterPosition(currPosition)) {
                NamedParameterDescriptor descriptor = parameterMetaData.getNamedParameterDescriptor(currPosition);
                Value value = namedValues.get(descriptor.getName());
                buf.append(value);
                currPosition += descriptor.getName().length();
            } else if (parameterMetaData.isEscapePosition(currPosition)) {
                // skip escape char 
            } else {
                buf.append(originalChars[currPosition]);
            }
        }
        return buf.toString();
    }

    private void validateParameters() {
        if (!allowParameterUnset) {
            if (namedValues.size() != parameterMetaData.getNamedParameterCount()) {
                throw new RuntimeException("Not all named parameters are set");
            }

            if (ordinalValues.size() != parameterMetaData.getOrdinalParameterCount()) {
                throw new RuntimeException("Not all ordinal parameters are set");
            }
        }
    }

    @Override
    public String toString() {
        return toNormalizedString();
    }

    static class Value {
        Object v;
        CharSequence enclosingChars;

        Value(Object v) {
            this.v = v;
        }

        Value(Object v, CharSequence encloseString) {
            this.v = v;
            this.enclosingChars = encloseString;
        }

        @Override
        public String toString() {
            if (enclosingChars != null && enclosingChars.length() > 0) {
                return enclosingChars + v.toString() + enclosingChars;
            } else {
                return v.toString();
            }
        }
    }
    
    public static void main(String[] args) {
        String s = "select ? f\\\\?rom user";
        ParameteredString ps = new ParameteredString(s);
        ps.setParameter(1, "name");
        System.out.println(s);
        System.out.println(ps.toNormalizedString());
    }
}
