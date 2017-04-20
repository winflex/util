package cc.lixiaohui.util.param;

/**
 *
 * @author lixiaohui
 * @date 2017年4月19日 下午5:54:11
 * @version 1.0
 */
interface Recognizer {
    
    /**
     * reconized by '?'
     */
    void ordinalParameter(int position);

    /**
     * reconized by ':'
     */
    void namedParameter(String name, int position);
    
    void escape(int position);

    void other(int position);
}