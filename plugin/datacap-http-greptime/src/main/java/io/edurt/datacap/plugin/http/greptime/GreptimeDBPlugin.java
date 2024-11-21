package io.edurt.datacap.plugin.http.greptime;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.spi.PluginService;
import io.edurt.datacap.spi.PluginType;
import io.edurt.datacap.spi.connection.HttpConfigure;
import io.edurt.datacap.spi.connection.HttpConnection;
import io.edurt.datacap.spi.model.Configure;
import io.edurt.datacap.spi.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
@SuppressFBWarnings(value = {"EI_EXPOSE_REP"})
public class GreptimeDBPlugin
        implements PluginService
{
    private HttpConfigure configure;
    private HttpConnection connection;
    private Response response;

    @Override
    public String validator()
    {
        return "SELECT 1";
    }

    @Override
    public String name()
    {
        return "GreptimeDB";
    }

    @Override
    public String description()
    {
        return "Integrate GreptimeDB data sources";
    }

    @Override
    public PluginType type()
    {
        return PluginType.HTTP;
    }

    @Override
    public void connect(Configure configure)
    {
        try {
            this.response = new Response();
            this.configure = new HttpConfigure();
            BeanUtils.copyProperties(this.configure, configure);
            this.connection = new HttpConnection(this.configure, this.response);
        }
        catch (Exception ex) {
            this.response.setIsConnected(Boolean.FALSE);
            this.response.setMessage(ex.getMessage());
        }
    }

    @Override
    public Response execute(String content)
    {
        if (ObjectUtils.isNotEmpty(this.connection)) {
            log.info("Execute greptimedb plugin logic started");
            this.response = this.connection.getResponse();
            GreptimeDBAdapter processor = new GreptimeDBAdapter(this.connection);
            this.response = processor.handlerExecute(content);
            log.info("Execute greptimedb plugin logic end");
        }
        this.destroy();
        return this.response;
    }

    @Override
    public void destroy()
    {
        if (ObjectUtils.isNotEmpty(this.connection)) {
            this.connection.destroy();
        }
    }
}
