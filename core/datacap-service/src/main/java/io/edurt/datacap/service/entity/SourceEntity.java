package io.edurt.datacap.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.common.utils.JsonUtils;
import io.edurt.datacap.common.view.EntityView;
import io.edurt.datacap.plugin.Plugin;
import io.edurt.datacap.plugin.PluginManager;
import io.edurt.datacap.service.configure.IConfigure;
import io.edurt.datacap.service.configure.IConfigureExecutor;
import io.edurt.datacap.spi.model.Configure;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "datacap_source")
@JsonIgnoreProperties(value = {"configure", "pluginAudits"})
@EntityListeners(AuditingEntityListener.class)
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC", "EQ_DOESNT_OVERRIDE_EQUALS"},
        justification = "I prefer to suppress these FindBugs warnings")
public class SourceEntity
        extends BaseEntity
{
    @Column(name = "description")
    private String description;

    @Column(name = "_type", nullable = false)
    @JsonView(value = {EntityView.UserView.class, EntityView.AdminView.class})
    private String type;

    @Column(name = "protocol", unique = true, nullable = false, columnDefinition = "varchar default 'HTTP'")
    @NotNull(message = "The passed protocol cannot by empty")
    private String protocol;

    @Column(name = "host", unique = true, nullable = false)
    @NotEmpty(message = "The passed host cannot by empty")
    private String host;

    @Column(name = "port", unique = true, nullable = false)
    @NotNull(message = "The passed port cannot by empty")
    private Integer port = 0;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "_catalog")
    private String catalog;

    @Column(name = "_database")
    private String database;

    // Add from 1.1.0.20221115
    @Column(name = "_ssl", columnDefinition = "boolean default false")
    private Boolean ssl;

    @OneToMany(mappedBy = "source", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<PluginAuditEntity> pluginAudits;

    // Add from 1.1.0.20221115
    @Column(name = "public", columnDefinition = "boolean default false")
    @JsonProperty(value = "public")
    private Boolean publish; // Public use or not

    @Column(name = "configure")
    @JsonProperty(value = "configure")
    private String configure;

    @Column(name = "used_config")
    private boolean usedConfig;

    @Column(name = "version")
    private String version;

    @Column(name = "available")
    private Boolean available;

    @Column(name = "message")
    private String message;

    @Transient
    private Map<String, Object> configures;

    @Transient
    private IConfigure schema;

    @Transient
    private List<IConfigureExecutor> pipelines;

    @Transient
    private String home;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIncludeProperties(value = {"id", "username"})
    private UserEntity user;

    @OneToMany(mappedBy = "source", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<DatabaseEntity> databases;

    @OneToMany(mappedBy = "source", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ScheduledHistoryEntity> historys;

    public void setConfigure(String configure)
    {
        this.configure = configure;
        if (StringUtils.isNotEmpty(configure)) {
            this.setConfigures(JsonUtils.toMap(this.configure));
        }
    }

    public Map<String, Object> getConfigures()
    {
        if (StringUtils.isNotEmpty(this.configure)) {
            this.setConfigures(JsonUtils.toMap(this.configure));
        }
        return configures;
    }

    public Configure toConfigure(PluginManager pluginManager, Plugin plugin)
    {
        return toConfigure("JsonConvert", pluginManager, plugin);
    }

    /**
     * Converts the current object to a Configure object.
     *
     * @return The Configure object created from the current object.
     */
    public Configure toConfigure(String format, PluginManager pluginManager, Plugin plugin)
    {
        return Configure.builder()
                .host(this.getHost())
                .port(this.getPort())
                .username(Optional.ofNullable(this.getUsername()))
                .password(Optional.ofNullable(this.getPassword()))
                .database(StringUtils.isNotEmpty(this.getDatabase()) ? Optional.ofNullable(this.getDatabase()) : Optional.empty())
                .ssl(Optional.ofNullable(this.getSsl()))
                .env(Optional.ofNullable(this.getConfigures()))
                .format(format)
                .usedConfig(this.isUsedConfig())
                .pluginManager(pluginManager)
                .plugin(plugin)
                .id(String.valueOf(this.getId()))
                .home(this.getHome())
                .build();
    }
}
