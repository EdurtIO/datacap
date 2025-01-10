package io.edurt.datacap.spi.generator.column;

import io.edurt.datacap.spi.generator.DataType;

public class CreateColumn
        extends AbstractColumn
{
    private boolean autoIncrement;

    private CreateColumn(String name, DataType type)
    {
        super(name, type);
    }

    public static CreateColumn create(String name, DataType type)
    {
        return new CreateColumn(name, type);
    }

    public CreateColumn autoIncrement()
    {
        this.autoIncrement = true;
        return this;
    }

    @Override
    public String build()
    {
        StringBuilder sql = buildBasicSQL();

        if (autoIncrement) {
            sql.append(" AUTO_INCREMENT");
        }

        if (comment != null) {
            sql.append(" COMMENT '").append(comment).append("'");
        }

        return sql.toString();
    }
}
