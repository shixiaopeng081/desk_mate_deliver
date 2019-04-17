package com.sunlands.deskmate.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author liude
 */
public interface BeanPropertiesUtil {

    /**
     * 复制对象的非空属性
     * 例如(例子中的对象是bean, 不是JSONObject, 对象无需是同一个class, 只要有共同的属性名称就可以):
     * source={"name":"a","address":null, "age":30},
     * target={"name":"b", "address"="address", "note"="note"}
     * <p>
     * <p>
     * 执行copyNonNullProperties(source,target)之后,
     * <p>
     * target={"name":"a", "address"="address", "note"="note"}
     *
     * @param source source
     * @param target target
     */
    @SuppressWarnings("Duplicates")
    default void copyNonNullProperties(Object source, Object target) {

        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);

        String[] nullPropertyNames = Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);

        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }

    /**
     *  static
     * @param source source
     * @param target target
     */
    @SuppressWarnings("Duplicates")
    static void copyNonNullPropertiesStatic(Object source, Object target) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);

        String[] nullPropertyNames = Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);

        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }

    /**
     * 检查pojo中某些属性是否为空, 如果为空则抛出IllegalArgumentException
     *
     * @param obj               pojo
     * @param nonNullProperties 需要检查非空的属性名
     */
    default void requireNonNullProperties(Object obj, String[] nonNullProperties) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(obj);

        Optional<String> nullProperty = Stream.of(nonNullProperties)
                .filter(s -> Objects.isNull(wrappedSource.getPropertyValue(s)))
                .findFirst();

        if (nullProperty.isPresent()) {
            throw new IllegalArgumentException(nullProperty.get() + "不能为空!");
        }
    }
}
