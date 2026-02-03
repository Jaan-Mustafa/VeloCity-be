package com.velocity.notification.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.velocity.notification.model.dto.NotificationDto;
import com.velocity.notification.model.entity.Notification;

/**
 * MapStruct mapper for Notification entity.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    
    /**
     * Convert Notification entity to DTO
     */
    NotificationDto toDto(Notification notification);
}
