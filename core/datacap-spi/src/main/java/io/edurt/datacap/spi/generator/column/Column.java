package io.edurt.datacap.spi.generator.column;

import io.edurt.datacap.spi.generator.DataType;

public interface Column
{
    String getName();

    DataType getType();

    String build();
}
