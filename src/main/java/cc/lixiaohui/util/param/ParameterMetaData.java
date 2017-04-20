package cc.lixiaohui.util.param;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * 参数描述, 包含下标参数和命名参数
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午6:13:06
 * @version 1.0
 */
class ParameterMetaData {

    public static ParameterMetaData buildFromParameterLocationRecognizer(ParameterLocationRecognizer recognizer) {
        SortedSet<Integer> ordinalPositions = recognizer.getOrdinalParameterPositions();
        SortedMap<Integer, OrdinalParameterDescriptor> pos2OrdinalDescriptors = new TreeMap<>();
        SortedMap<Integer, OrdinalParameterDescriptor> ordinal2OrdinalDescriptors = new TreeMap<>();
        int ordinal = 1;
        for (Integer position : ordinalPositions) {
            OrdinalParameterDescriptor d = new OrdinalParameterDescriptor(ordinal, position);
            pos2OrdinalDescriptors.put(position, d);
            ordinal2OrdinalDescriptors.put(ordinal, d);
            ++ordinal;
        }

        SortedMap<Integer, NamedParameterDescriptor> pos2NamedDescriptors = new TreeMap<>();
        ordinal = 1;
        for (Map.Entry<Integer, String> entry : recognizer.getNamedParameterPositions().entrySet()) {
            int sourcePosition = entry.getKey();
            NamedParameterDescriptor d = new NamedParameterDescriptor(entry.getValue(), sourcePosition, ordinal);
            pos2NamedDescriptors.put(sourcePosition, d);
        }
        return new ParameterMetaData(pos2OrdinalDescriptors, pos2NamedDescriptors, ordinal2OrdinalDescriptors, recognizer.getEscapePositions());
    }

    private final SortedMap<Integer, OrdinalParameterDescriptor> pos2OrdinalDescriptors = new TreeMap<>();
    private final SortedMap<Integer, OrdinalParameterDescriptor> ordinal2OrdinalDescriptors = new TreeMap<>();
    
    private final SortedMap<Integer, NamedParameterDescriptor> pos2NamedDescriptors = new TreeMap<>();
    private final Set<String> parameterNames = new HashSet<>();
    
    private final Set<Integer> escapePositions = new HashSet<>();
    
    public ParameterMetaData(SortedMap<Integer, OrdinalParameterDescriptor> pos2OrdinalDescriptors,
            SortedMap<Integer, NamedParameterDescriptor> pos2NamedDescriptors,
            SortedMap<Integer, OrdinalParameterDescriptor> ordinal2OrdinalDescriptors,
            Set<Integer> escapePositions) {
        super();
        this.pos2OrdinalDescriptors.putAll(pos2OrdinalDescriptors);
        this.pos2NamedDescriptors.putAll(pos2NamedDescriptors);
        this.ordinal2OrdinalDescriptors.putAll(ordinal2OrdinalDescriptors);
        this.escapePositions.addAll(escapePositions);
        
        for (NamedParameterDescriptor d : pos2NamedDescriptors.values()) {
            parameterNames.add(d.getName());
        }
    }


    public int getOrdinalParameterCount() {
        return pos2OrdinalDescriptors.size();
    }

    public int getNamedParameterCount() {
        return parameterNames.size();
    }

    public SortedMap<Integer, OrdinalParameterDescriptor> getOrdinalDescriptors() {
        return pos2OrdinalDescriptors;
    }

    public SortedMap<Integer, NamedParameterDescriptor> getNamedDescriptorMap() {
        return pos2NamedDescriptors;
    }

    public OrdinalParameterDescriptor getOrdinalParameterDescriptor(int position) {
        return pos2OrdinalDescriptors.get(position);
    }

    public NamedParameterDescriptor getNamedParameterDescriptor(int position) {
        return pos2NamedDescriptors.get(position);
    }

    public boolean isOrdinalParameterPosition(int position) {
        return pos2OrdinalDescriptors.containsKey(position);
    }

    public boolean isNamedParameterPosition(int position) {
        return pos2NamedDescriptors.containsKey(position);
    }
    
    public boolean isOrdinalExist(int ordinal) {
        return ordinal2OrdinalDescriptors.containsKey(ordinal);
    }
    
    public boolean isParameterNameExist(String name) {
        return parameterNames.contains(name);
    }
    
    public boolean isEscapePosition(int position) {
        return escapePositions.contains(position);
    }
}
