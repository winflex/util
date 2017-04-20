package cc.lixiaohui.util.param;


/**
 * 命名参数描述
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午6:14:04
 * @version 1.0
 *
 */
class NamedParameterDescriptor {

    private final String name;

    private final int sourcePosition;

    private final int ordinal;

    public NamedParameterDescriptor(String name, int sourcePosition, int ordinal) {
        super();
        this.name = name;
        this.sourcePosition = sourcePosition;
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public int getSourcePosition() {
        return sourcePosition;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
