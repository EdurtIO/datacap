package io.edurt.datacap.plugin.jdbc.trino;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.spi.PluginService;
import io.edurt.datacap.spi.PluginType;
import io.edurt.datacap.spi.adapter.JdbcAdapter;
import io.edurt.datacap.spi.connection.JdbcConfigure;
import io.edurt.datacap.spi.model.Configure;
import io.edurt.datacap.spi.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;

@Slf4j
@SuppressFBWarnings(value = {"EI_EXPOSE_REP"})
public class TrinoPlugin
        implements PluginService
{
    private JdbcConfigure jdbcConfigure;
    private TrinoConnection trinoConnection;
    private Response response;

    @Override
    public String name()
    {
        return "Trino";
    }

    @Override
    public String description()
    {
        return "Integrate Trino data sources";
    }

    @Override
    public PluginType type()
    {
        return PluginType.JDBC;
    }

    @Override
    public void connect(Configure configure)
    {
        try {
            this.response = new Response();
            this.jdbcConfigure = new JdbcConfigure();
            BeanUtils.copyProperties(this.jdbcConfigure, configure);
            this.jdbcConfigure.setJdbcDriver("io.trino.jdbc.TrinoDriver");
            this.jdbcConfigure.setJdbcType("trino");
            this.trinoConnection = new TrinoConnection(this.jdbcConfigure, this.response);
        }
        catch (Exception ex) {
            this.response.setIsConnected(Boolean.FALSE);
            this.response.setMessage(ex.getMessage());
        }
    }

    @Override
    public Response execute(String content)
    {
        if (ObjectUtils.isNotEmpty(this.trinoConnection)) {
            log.info("Execute trino plugin logic started");
            this.response = this.trinoConnection.getResponse();
            JdbcAdapter processor = new TrinoAdapter(this.trinoConnection);
            this.response = processor.handlerExecute(content);
            log.info("Execute trino plugin logic end");
        }
        return this.response;
    }

    @Override
    public void destroy()
    {
        if (ObjectUtils.isNotEmpty(this.trinoConnection)) {
            this.trinoConnection.destroy();
        }
    }
}
