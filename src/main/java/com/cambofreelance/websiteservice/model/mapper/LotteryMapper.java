/*
package com.cambofreelance.websiteservice.model.mapper;

import org.springframework.stereotype.Component;

@Component
public class LotteryMapper {

    public LotteryDtoResponse toDto(Lottery entity) {
        if (entity == null) {
            return null;
        }

        LotteryDtoResponse dto = new LotteryDtoResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setTime(entity.getTime());
        dto.setCloseTime(entity.getCloseTime());
        dto.setType(entity.getType());
        dto.setIndex(entity.getIndex());
        dto.setRequestDeleteMinuteValue(entity.getRequestDeleteMinuteValue());
        dto.setSpecial(entity.getSpecial());
        dto.setGeneratePostLo(entity.getGeneratePostLo());

        return dto;
    }

    public Lottery toEntity(LotteryDtoResponse dto) {
        if (dto == null) {
            return null;
        }

        Lottery entity = new Lottery();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setTime(dto.getTime());
        entity.setCloseTime(dto.getCloseTime());
        entity.setType(dto.getType());
        entity.setIndex(dto.getIndex());
        entity.setRequestDeleteMinuteValue(dto.getRequestDeleteMinuteValue());
        entity.setSpecial(dto.getSpecial());
        entity.setGeneratePostLo(dto.getGeneratePostLo());

        return entity;
    }

    public void updateEntity(LotteryDtoResponse dto, Lottery entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getTime() != null) {
            entity.setTime(dto.getTime());
        }
        if (dto.getCloseTime() != null) {
            entity.setCloseTime(dto.getCloseTime());
        }
        if (dto.getType() != null) {
            entity.setType(dto.getType());
        }
        if (dto.getIndex() != null) {
            entity.setIndex(dto.getIndex());
        }
        if (dto.getRequestDeleteMinuteValue() != null) {
            entity.setRequestDeleteMinuteValue(dto.getRequestDeleteMinuteValue());
        }
        if (dto.getSpecial() != null) {
            entity.setSpecial(dto.getSpecial());
        }
        if (dto.getGeneratePostLo() != null) {
            entity.setGeneratePostLo(dto.getGeneratePostLo());
        }
    }
}*/
