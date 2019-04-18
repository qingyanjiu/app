package moku.site.context;

import java.util.HashMap;
import java.util.Map;

public class ContextContainer implements IContextContainer,IConfigurableContextContainer {

    private static final ContextContainer contextContainer = new ContextContainer();
    public static final String KEY_PROPERTIES = "properties";
    public static final String KEY_BEANS = "beans";

    private Map properties = new HashMap();
    private Map beans = new HashMap();

    private ContextContainer(){
    }

    public static ContextContainer getInstance(){
        return contextContainer;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public Map getBeans() {
        return beans;
    }

    public void setBeans(Map beans) {
        this.beans = beans;
    }
}
