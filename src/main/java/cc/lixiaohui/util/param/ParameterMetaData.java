package cc.lixiaohui.util.param;

import java.util.Set;

/**
 *
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午6:13:06
 * @version 1.0
 *
 */
public class ParameterMetaData {

    public int getOrdinalParameterCount() {
        return 0;
    }

    public OrdinalParameterDescriptor getOrdinalParameterDescriptor(int position) {
        return null;

    }

    public Set<String> getNamedParameterNames() {
        return null;
    }

    public NamedParameterDescriptor getNamedParameterDescriptor(String name) {
        return null;
    }

    public int[] getNamedParameterSourceLocations(String name) {
        return null;
    }
}
