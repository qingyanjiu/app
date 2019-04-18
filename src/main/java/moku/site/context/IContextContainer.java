package moku.site.context;

import java.util.Map;

public interface IContextContainer {

    Map getProperties();
    Map getBeans();
}