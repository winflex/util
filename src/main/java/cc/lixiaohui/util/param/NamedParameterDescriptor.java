package cc.lixiaohui.util.param;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午6:14:04
 * @version 1.0
 *
 */
public class NamedParameterDescriptor {
    
    private final String name;
    
    private final int[] sourceLocations;

    
    public NamedParameterDescriptor(String name, int[] sourceLocations) {
        super();
        this.name = name;
        this.sourceLocations = sourceLocations;
    }

    public String getName() {
        return name;
    }

    public int[] getSourceLocations() {
        return sourceLocations;
    }

}
