package com.erp.zup.api.config.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Component;

/**
 * Util class for Mapper Model operations.
 */
@Component("mapperUtil")
public final class MapperUtil {

    /**
     * Model mapper.
     */
    protected final ModelMapper modelMapper;

    /**
     * Default Constructor.
     */
    public MapperUtil() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    /**
     * Maps the source to destination class.
     *
     * @param source    Source.
     * @param destClass Destination class.
     * @return Instance of destination class.
     */
    public <S, D> D map(S source, Class<D> destClass) {
        return this.modelMapper.map(source, destClass);
    }
}
