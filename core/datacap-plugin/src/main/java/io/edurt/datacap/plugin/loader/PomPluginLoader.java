package io.edurt.datacap.plugin.loader;

import io.edurt.datacap.plugin.Plugin;
import io.edurt.datacap.plugin.SpiType;
import io.edurt.datacap.plugin.utils.PluginClassLoaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class PomPluginLoader
        implements PluginLoader
{
    @Override
    public SpiType getType()
    {
        return SpiType.POM;
    }

    @Override
    public List<Plugin> load(Path path)
    {
        try {
            Path pomFile = path.resolve("pom.xml");
            if (!Files.exists(pomFile)) {
                return List.of();
            }

            // 读取 POM 信息
            // Read POM information
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(pomFile.toFile()));

            String mainClass = model.getProperties().getProperty("plugin.class");
            if (mainClass == null) {
                return List.of();
            }

            // 获取插件版本
            // Get plugin version
            String version = model.getVersion();
            if (version == null) {
                version = "1.0.0";
            }

            // 创建插件专用类加载器
            // Create plugin-specific class loader
            PluginClassLoader classLoader = PluginClassLoaderUtils.createClassLoader(
                    path,
                    model.getArtifactId(),
                    version
            );

            Class<?> pluginClass = classLoader.loadClass(mainClass);

            if (!Plugin.class.isAssignableFrom(pluginClass)) {
                log.error("Class {} does not implement PluginModule", mainClass);
                return List.of();
            }

            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            plugin.setPluginClassLoader(classLoader);

            return List.of(plugin);
        }
        catch (Exception e) {
            log.error("Failed to load plugins using POM from: {}", path, e);
            return List.of();
        }
    }
}
