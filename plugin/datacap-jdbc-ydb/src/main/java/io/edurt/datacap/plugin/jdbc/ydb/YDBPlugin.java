package io.edurt.datacap.plugin.jdbc.ydb;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.spi.PluginService;
import io.edurt.datacap.spi.PluginType;
import io.edurt.datacap.spi.adapter.JdbcAdapter;
import io.edurt.datacap.spi.connection.JdbcConfigure;
import io.edurt.datacap.spi.connection.JdbcConnection;
import io.edurt.datacap.spi.model.Configure;
import io.edurt.datacap.spi.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
@SuppressFBWarnings(value = {"EI_EXPOSE_REP"})
public class YDBPlugin
        implements PluginService
{
    private JdbcConfigure jdbcConfigure;
    private JdbcConnection connection;
    private Response response;

    @Override
    public String name()
    {
        return "YDB";
    }

    @Override
    public String description()
    {
        return "Integrate Yandex Database data sources";
    }

    @Override
    public PluginType type()
    {
        return PluginType.JDBC;
    }

    @Override
    public String validator()
    {
        return "SELECT 'ydb'";
    }

    @Override
    public void connect(Configure configure)
    {
        try {
            this.response = new Response();
            this.jdbcConfigure = new JdbcConfigure();
            BeanUtils.copyProperties(this.jdbcConfigure, configure);
            this.jdbcConfigure.setJdbcDriver("com.yandex.ydb.jdbc.YdbDriver");
            this.jdbcConfigure.setJdbcType("ydb");
            this.connection = new YDBConnection(this.jdbcConfigure, this.response);
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
            log.info("Execute ydb plugin logic started");
            this.response = this.connection.getResponse();
            JdbcAdapter processor = new YDBAdapter(this.connection);
            this.response = processor.handlerExecute(content);
            log.info("Execute ydb plugin logic end");
        }
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
