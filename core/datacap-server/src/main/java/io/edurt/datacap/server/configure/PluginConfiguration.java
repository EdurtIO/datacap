package io.edurt.datacap.server.configure;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.edurt.datacap.common.utils.EnvironmentUtils;
import io.edurt.datacap.plugin.PluginManager;
import io.edurt.datacap.plugin.utils.PluginPathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.nio.file.Path;

@Slf4j
@Configuration
public class PluginConfiguration
{
    private final Environment environment;

    public PluginConfiguration(Environment environment)
    {
        this.environment = environment;
    }

    @Bean
    public PluginManager pluginManager()
    {
        EnvironmentUtils.printEnvironmentInfo();

        String root = environment.getProperty("spring.config.location");
        Path projectRoot = PluginPathUtils.findProjectRoot();
        io.edurt.datacap.plugin.PluginConfigure config = io.edurt.datacap.plugin.PluginConfigure.builder()
                .pluginsDir(PluginPathUtils.appendPath("plugins"))
                .autoCleanup(true)
                .build();

        // 开发模式下生效，如果涉及到插件的安装卸载，请注释掉这部分代码
        // In development mode, it is effective, if there is a plugin installation and uninstallation, please comment out this code
        if (EnvironmentUtils.isIdeEnvironment()) {
            log.info("Development mode is development mode");
            config.setPluginsDir(projectRoot.resolve(Path.of(String.join("/", root, "plugins.properties"))));
        }

        log.info("Plugins directory: {}", config.getPluginsDir());
        PluginManager pluginManager = new PluginManager(config);
        pluginManager.start();

        return pluginManager;
    }

    @Bean
    public Injector injector()
    {
        return Guice.createInjector();
    }
}
