package cc.lixiaohui.util.param;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午6:14:20
 * @version 1.0
 *
 */
public class OrdinalParameterDescriptor {
    
    private final int ordinalPosition;
    
    private final int sourceLocation;

    
    public OrdinalParameterDescriptor(int ordinalPosition, int sourceLocation) {
        super();
        this.ordinalPosition = ordinalPosition;
        this.sourceLocation = sourceLocation;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public int getSourceLocation() {
        return sourceLocation;
    }

}
