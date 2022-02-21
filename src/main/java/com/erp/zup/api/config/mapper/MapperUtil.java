package com.erp.zup.api.config.mapper;

import com.erp.zup.api.dto.PaginationDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Util class for Mapper Model operations.
 */
@Component("mapperUtil")
public class MapperUtil {

    /**
     * Model mapper.
     */
    protected ModelMapper modelMapper;

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

    public <S, D> PaginationDTO<D> mapToGenericPagination(S source,Type type) {
        return this.modelMapper.map(source, type);
    }

    /**
     * <p>Note: outClass object must have default constructor with no arguments</p>
     *
     * @param entityList list of entities that needs to be mapped
     * @param outCLass   class of result list element
     * @param <D>        type of objects in result list
     * @param <T>        type of entity in <code>entityList</code>
     * @return list of mapped object with <code><D></code> type.
     */
    public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }
}
