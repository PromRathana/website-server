package com.cambofreelance.websiteservice.model.mapper.localized;

import com.cambofreelance.websiteservice.model.dto.common.LocalizedTextDto;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

@Component
public class LocalizedFieldMapper {

    public LocalizedTextDto toDto(Object source, LocalizedFieldConfig config) {
        if (source == null || config == null) {
            return null;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(source);

        return LocalizedTextDto.builder()
            .en(getString(beanWrapper, config.getEnField()))
            .km(getString(beanWrapper, config.getKmField()))
            .th(getString(beanWrapper, config.getThField()))
            .vn(getString(beanWrapper, config.getVnField()))
            .build();
    }

    public void toEntity(Object target, LocalizedTextDto dto, LocalizedFieldConfig config) {
        if (target == null || dto == null || config == null) {
            return;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(target);

        setValue(beanWrapper, config.getEnField(), dto.getEn());
        setValue(beanWrapper, config.getKmField(), dto.getKm());
        setValue(beanWrapper, config.getThField(), dto.getTh());
        setValue(beanWrapper, config.getVnField(), dto.getVn());
    }

    private String getString(BeanWrapper beanWrapper, String fieldName) {
        if (fieldName == null || !beanWrapper.isReadableProperty(fieldName)) {
            return null;
        }
        Object value = beanWrapper.getPropertyValue(fieldName);
        return value != null ? String.valueOf(value) : null;
    }

    private void setValue(BeanWrapper beanWrapper, String fieldName, Object value) {
        if (fieldName == null || !beanWrapper.isWritableProperty(fieldName)) {
            return;
        }
        beanWrapper.setPropertyValue(fieldName, value);
    }
}