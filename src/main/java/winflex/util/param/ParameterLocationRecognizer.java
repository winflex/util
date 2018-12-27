package winflex.util.param;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月20日 上午9:46:01
 * @version 1.0
 *
 */
class ParameterLocationRecognizer implements Recognizer {

    private final SortedSet<Integer> ordinalParameterPositions = new TreeSet<>();
    
    private final SortedMap<Integer, String> namedParameterPositions = new TreeMap<>();

    private final Set<Integer> escapePositions = new HashSet<>();
    
    public SortedMap<Integer, String> getNamedParameterPositions() {
        return namedParameterPositions;
    }

    public SortedSet<Integer> getOrdinalParameterPositions() {
        return ordinalParameterPositions;
    }

    public Set<Integer> getEscapePositions() {
        return escapePositions;
    }

    @Override
    public void ordinalParameter(int sourcePosition) {
        ordinalParameterPositions.add(sourcePosition);
    }

    @Override
    public void namedParameter(String name, int position) {
        namedParameterPositions.put(position, name);
    }

    @Override
    public void other(int position) {
        // don't care
    }

    @Override
    public void escape(int position) {
        escapePositions.add(position);
    }
}
