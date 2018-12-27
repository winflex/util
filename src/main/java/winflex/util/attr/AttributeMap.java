package winflex.util.attr;

/**
 * holds attributes
 *
 * @author lixiaohui
 * @date 2017年3月28日 下午4:13:31
 * @version 1.0
 */
public interface AttributeMap {
    
    <T> Attribute<T> attr(AttributeKey<T> key);
    
}
