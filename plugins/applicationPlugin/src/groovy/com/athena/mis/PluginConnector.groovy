package com.athena.mis

import javax.annotation.PostConstruct

public abstract class PluginConnector {
    public static final String PLUGIN_NAME = "Application";
    public static final int PLUGIN_ID = 1
    public static final PLUGIN_PREFIX = "APP"

    private static Map<String, Object> plugins = new HashMap<String, Object>();

    @PostConstruct
    public abstract boolean initialize();

    public abstract String getName();

    public abstract int getId();

    public abstract String getPrefix();

    public final static boolean isPluginInstalled(String key) {
        return plugins.containsKey(key);
    }

    public final static void setPlugin(String key, Object pluginConnector) {
        plugins.put(key, pluginConnector);
    }

    public final static Object getPlugin(String key) {
        return (Object) plugins.get(key);
    }

    public final static Collection<Object> getAllPlugins() {
        return plugins.values();
    }
}