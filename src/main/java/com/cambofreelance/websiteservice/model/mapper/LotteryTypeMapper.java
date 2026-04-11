/*
package com.cambofreelance.websiteservice.model.mapper;

import com.cambofreelance.websiteservice.model.mapper.localized.LocalizedFieldConfig;
import com.cambofreelance.websiteservice.model.mapper.localized.LocalizedFieldMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LotteryTypeMapper {

    private static final LocalizedFieldConfig NAME_CONFIG = LocalizedFieldConfig.builder()
        .enField("name")
        .kmField("nameKm")
        .thField("nameTh")
        .vnField("nameVn")
        .build();
    private final LocalizedFieldMapper localizedFieldMapper;

    public LotteryTypeDtoResponse toDto(LotteryType entity) {
        if (entity == null) {
            return null;
        }

        LotteryTypeDtoResponse dto = new LotteryTypeDtoResponse();
        dto.setId(entity.getId());
        dto.setName(localizedFieldMapper.toDto(entity, NAME_CONFIG));
        dto.setKeyName(entity.getKeyName());
        dto.setColor(entity.getColor());
        dto.setBgColor(entity.getBgColor());
        dto.setImageUrl(entity.getImageUrl());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    public LotteryType toEntity(LotteryTypeDtoResponse dto) {
        if (dto == null) {
            return null;
        }

        LotteryType entity = new LotteryType();
        entity.setId(dto.getId());
        localizedFieldMapper.toEntity(entity, dto.getName(), NAME_CONFIG);
        entity.setKeyName(dto.getKeyName());
        entity.setColor(dto.getColor());
        entity.setBgColor(dto.getBgColor());
        entity.setImageUrl(dto.getImageUrl());
        entity.setStatus(dto.getStatus());

        return entity;
    }

    public void updateEntity(LotteryTypeDtoResponse dto, LotteryType entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            localizedFieldMapper.toEntity(entity, dto.getName(), NAME_CONFIG);
        }
        if (dto.getKeyName() != null) {
            entity.setKeyName(dto.getKeyName());
        }
        if (dto.getColor() != null) {
            entity.setColor(dto.getColor());
        }
        if (dto.getBgColor() != null) {
            entity.setBgColor(dto.getBgColor());
        }
        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }
}*/
