package io.edurt.datacap.plugin.jdbc.starrocks

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.edurt.datacap.spi.PluginService
import io.edurt.datacap.spi.PluginType
import io.edurt.datacap.spi.adapter.JdbcAdapter
import io.edurt.datacap.spi.connection.JdbcConfigure
import io.edurt.datacap.spi.connection.JdbcConnection
import io.edurt.datacap.spi.model.Configure
import io.edurt.datacap.spi.model.Response
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.ObjectUtils
import org.slf4j.LoggerFactory.getLogger

@SuppressFBWarnings(value = ["EI_EXPOSE_REP"])
class StarRocksPlugin : _root_ide_package_.io.edurt.datacap.spi.PluginService
{
    private val log = getLogger(StarRocksPlugin::class.java)

    private var jdbcConfigure: JdbcConfigure? = null
    private var jdbcConnection: JdbcConnection? = null
    private var jdbcResponse: Response? = null

    override fun name(): String
    {
        return "StarRocks"
    }

    override fun description(): String
    {
        return "Integrate StarRocks data sources"
    }

    override fun type(): PluginType
    {
        return PluginType.JDBC
    }

    override fun connect(configure: Configure?)
    {
        try
        {
            log.info("Connecting to StarRocks")
            jdbcResponse = Response()
            jdbcConfigure = JdbcConfigure()
            BeanUtils.copyProperties(jdbcConfigure, configure)
            jdbcConfigure !!.jdbcDriver = "com.mysql.cj.jdbc.Driver"
            jdbcConfigure !!.jdbcType = "mysql"
            jdbcConnection = object : JdbcConnection(jdbcConfigure, jdbcResponse)
            {}
        }
        catch (ex: Exception)
        {
            jdbcResponse !!.isConnected = false
            jdbcResponse !!.message = ex.message
        }
    }

    override fun execute(content: String?): Response
    {
        if (ObjectUtils.isNotEmpty(jdbcConnection))
        {
            log.info("Execute starrocks plugin logic started")
            jdbcResponse = jdbcConnection?.response
            val processor = JdbcAdapter(jdbcConnection)
            jdbcResponse = processor.handlerExecute(content)
            log.info("Execute starrocks plugin logic end")
        }
        destroy()
        return jdbcResponse !!
    }

    override fun destroy()
    {
        if (ObjectUtils.isNotEmpty(jdbcConnection))
        {
            jdbcConnection?.destroy()
            jdbcConnection = null
        }
    }
}