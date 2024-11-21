package io.edurt.datacap.plugin;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import io.edurt.datacap.plugin.service.ServiceBindings;
import io.edurt.datacap.plugin.service.ServiceNotFoundException;
import io.edurt.datacap.plugin.service.ServiceSpiLoader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;

@Slf4j
public abstract class Plugin
        extends AbstractModule
{
    private final Map<Class<? extends Service>, Boolean> binds = new HashMap<>();

    private volatile String cachedVersion;

    // 依赖注入器
    // Dependency injector
    @Setter
    @Getter
    private Injector injector;

    @Getter
    @Setter
    private String classLoader;

    /**
     * 类型安全的服务绑定方法
     * Type-safe service binding method
     *
     * @param service 服务接口类型
     * @param service service interface type
     * @param implementation 服务实现类型
     * @param implementation service implementation type
     */
    @SuppressWarnings("unchecked")
    private <T extends Service> void bindService(Class<? extends Service> service, Class<? extends Service> implementation, boolean multiple, String qualifier)
    {
        // 确保实现类是服务接口的子类
        // Ensure implementation class is a subclass of service interface
        if (!service.isAssignableFrom(implementation)) {
            log.warn("Implementation {} is not compatible with service {}", implementation.getName(), service.getName());
            return;
        }

        // 由于已经进行了类型检查，这里的转换是安全的
        // Type casting is safe here as we've done the type check
        Class<T> serviceType = (Class<T>) service;
        Class<? extends T> implementationType = (Class<? extends T>) implementation;

        if (multiple) {
            // 指定别名绑定服务
            // Bind service with alias
            bind(serviceType).annotatedWith(Names.named(qualifier))
                    .to(implementationType)
                    .in(Singleton.class);
        }
        else {
            // 绑定服务
            // Bind service
            bind(serviceType).to(implementationType).in(Singleton.class);
        }
    }

    private void validateInjector()
    {
        if (injector == null) {
            throw new IllegalStateException("Injector not set for plugin: " + getName());
        }
    }

    private String readVersionFromManifest()
            throws IOException
    {
        Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            try (InputStream is = resources.nextElement().openStream()) {
                Manifest manifest = new Manifest(is);
                String version = manifest.getMainAttributes().getValue("Implementation-Version");
                if (version != null) {
                    return version;
                }
            }
        }
        return null;
    }

    /**
     * 获取需要加载的服务类型列表
     * Get list of service types to load
     *
     * @return 服务类型列表
     * @return list of service types
     */
    public Set<Class<? extends Service>> getServiceTypes()
    {
        return Set.of(Service.class);
    }

    @Override
    protected void configure()
    {
        getServiceTypes().forEach(serviceType -> {
            ServiceBindings bindings = ServiceSpiLoader.loadServices(serviceType, this.getClass().getClassLoader());
            // 支持多个实现
            // Support multiple implementations
            bindings.getBindings().forEach((service, implementation) -> {
                // 使用命名绑定来支持多个实现
                // Use named binding to support multiple implementations
                String implName = implementation.getSimpleName();
                bindService(service, implementation, true, implName);

                // 默认绑定第一个实现
                // Default binding first implementation
                if (!binds.containsKey(service)) {
                    bindService(service, implementation, false, null);
                    binds.put(service, true);
                }
            });
        });

        // 调用子类的配置方法
        // Call subclass configuration method
        configurePlug();
    }

    protected void configurePlug() {}

    public String getName()
    {
        return StringUtils.remove(this.getClass().getSimpleName(), "Plugin");
    }

    public String getVersion()
    {
        if (cachedVersion != null) {
            return cachedVersion;
        }

        String version = this.getClass().getPackage().getImplementationVersion();
        if (version == null) {
            try {
                version = readVersionFromManifest();
            }
            catch (IOException e) {
                log.warn("Failed to read version from MANIFEST.MF", e);
                version = "unknown";
            }
        }

        cachedVersion = version;
        return version;
    }

    public PluginType getType()
    {
        return PluginType.CONNECTOR;
    }

    /**
     * 获取服务实例
     * Get service instance
     *
     * @param serviceClass 服务类型
     * @param serviceClass service class type
     * @return 服务实例
     * @return service instance
     */
    public <T extends Service> T getService(Class<T> serviceClass)
    {
        validateInjector();

        try {
            return injector.getInstance(serviceClass);
        }
        catch (ConfigurationException e) {
            throw new ServiceNotFoundException("Service not found for type: " + serviceClass.getName(), e);
        }
    }

    /**
     * 获取指定名称的服务实例
     * Get named service instance
     *
     * @param serviceClass 服务接口类型
     * @param serviceClass service interface type
     * @param name 服务名称
     * @param name service name
     * @return 服务实例
     * @return service instance
     */
    public <T extends Service> T getService(Class<T> serviceClass, String name)
    {
        validateInjector();

        try {
            return injector.getInstance(Key.get(serviceClass, Names.named(name)));
        }
        catch (ConfigurationException e) {
            throw new ServiceNotFoundException(
                    String.format("Named service not found - type: %s, name: %s",
                            serviceClass.getName(), name), e);
        }
    }

    // 获取所有服务
    // Get all services
    public <T extends Service> Set<T> getAllServices(Class<T> serviceClass)
    {
        validateInjector();

        Set<T> services = Sets.newHashSet();
        ServiceBindings bindings = ServiceSpiLoader.loadServices(serviceClass, this.getClass().getClassLoader());
        bindings.getBindings().get(serviceClass).forEach(impl -> {
            String name = impl.getSimpleName();
            services.add(getService(serviceClass, name));
        });

        return services;
    }
}
