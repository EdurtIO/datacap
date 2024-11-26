package io.edurt.datacap.plugin.mongo

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.edurt.datacap.spi.PluginService
import io.edurt.datacap.spi.adapter.JdbcAdapter
import io.edurt.datacap.spi.connection.JdbcConfigure
import io.edurt.datacap.spi.connection.JdbcConnection
import io.edurt.datacap.spi.model.Configure
import io.edurt.datacap.spi.model.Response
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.ObjectUtils.isNotEmpty
import org.slf4j.LoggerFactory.getLogger

@SuppressFBWarnings(value = ["EI_EXPOSE_REP"])
class MongoPlugin : _root_ide_package_.io.edurt.datacap.spi.PluginService
{
    private val log = getLogger(MongoPlugin::class.java)

    private var jdbcConfigure: JdbcConfigure? = null
    private var jdbcConnection: JdbcConnection? = null
    private var jdbcResponse: Response? = null

    override fun name(): String
    {
        return "MongoDB"
    }

    override fun validator(): String
    {
        return "db.version()"
    }

    override fun connect(configure: Configure?)
    {
        try
        {
            log.info("Connecting to mongodb community")
            jdbcResponse = Response()
            jdbcConfigure = JdbcConfigure()
            BeanUtils.copyProperties(jdbcConfigure, configure)
            jdbcConfigure !!.jdbcDriver = "com.dbschema.MongoJdbcDriver"
            jdbcConfigure !!.jdbcType = "mongodb"
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
        if (isNotEmpty(jdbcConnection))
        {
            log.info("Mongodb community connection established")
            jdbcResponse = jdbcConnection?.response
            val processor = JdbcAdapter(jdbcConnection)
            jdbcResponse = processor.handlerExecute(content)
            log.info("Mongodb community execution completed")
        }
        destroy()
        return jdbcResponse !!
    }

    override fun destroy()
    {
        if (isNotEmpty(jdbcConnection))
        {
            log.info("Mongodb community driver destroyed")
            jdbcConnection?.destroy()
            jdbcConnection = null
        }
    }
}