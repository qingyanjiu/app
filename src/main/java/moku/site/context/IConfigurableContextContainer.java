package moku.site.context;

import java.util.Map;

public interface IConfigurableContextContainer extends IContextContainer {

    void setProperties(Map properties);
    void setBeans(Map beans);
}
