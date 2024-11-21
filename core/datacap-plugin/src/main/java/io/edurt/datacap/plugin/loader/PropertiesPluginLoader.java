package io.edurt.datacap.plugin.loader;

import io.edurt.datacap.plugin.Plugin;
import io.edurt.datacap.plugin.SpiType;
import io.edurt.datacap.plugin.utils.PluginPathUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
public class PropertiesPluginLoader
        implements PluginLoader
{
    @Override
    public SpiType getType()
    {
        return SpiType.PROPERTIES;
    }

    @Override
    public List<Plugin> load(Path path)
    {
        List<Plugin> plugins = new ArrayList<>();
        try {
            Path propertiesPath;
            if (path.toString().endsWith("pom.xml")) {
                propertiesPath = path.getParent().resolve("plugin.properties");
            }
            else {
                propertiesPath = path;
            }

            if (!propertiesPath.toFile().exists() || propertiesPath.toFile().isDirectory()) {
                log.debug("Properties file not found: {}", propertiesPath);
                return plugins;
            }

            Properties props = new Properties();
            props.load(new FileInputStream(propertiesPath.toFile()));
            Path baseDir = propertiesPath.getParent();

            for (String value : props.stringPropertyNames()) {
                String pluginPath = props.getProperty(value);
                if (pluginPath == null || pluginPath.trim().isEmpty()) {
                    continue;
                }

                Path other = Paths.get(pluginPath);
                Path resolvedPath = baseDir.resolve(other);
                log.info("Loading plugin from [ {} ]", resolvedPath);

                if (!resolvedPath.toFile().exists()) {
                    log.info("Plugin file [ {} ] does not exist", resolvedPath);
                    log.info("Retrying with module root as base directory");
                    resolvedPath = PluginPathUtils.findProjectRoot()
                            .resolve(other)
                            .getParent();
                    log.info("Retrying module root as base directory [ {} ]", resolvedPath);
                }

                // 使用 SpiPluginLoader 加载
                // Use SpiPluginLoader to load
                SpiPluginLoader compiledLoader = new SpiPluginLoader();
                List<Plugin> loadedPlugins = compiledLoader.load(resolvedPath);
                if (!loadedPlugins.isEmpty()) {
                    plugins.addAll(loadedPlugins);
                }
                else {
                    loadedPlugins = PluginLoaderFactory.loadPlugins(resolvedPath);
                    plugins.addAll(loadedPlugins);
                }
            }
        }
        catch (Exception e) {
            log.error("Failed to load plugins from properties file: {}", path, e);
        }
        return plugins;
    }
}
