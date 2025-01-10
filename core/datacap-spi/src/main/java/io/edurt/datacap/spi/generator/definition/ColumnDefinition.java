package io.edurt.datacap.spi.generator.definition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.edurt.datacap.spi.generator.DataType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ColumnDefinition
        extends BaseDefinition
{
    private String name;
    private DataType type;
    private Integer length;
    private Integer precision;
    private Integer scale;
    private boolean nullable = true;
    private String defaultValue;
    private String comment;
    private String charset;
    private String collate;
    private boolean primaryKey;
    private boolean autoIncrement;
}
