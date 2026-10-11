package com.mankind.mankindmatrixuserservice.mapper;

import com.mankind.api.user.dto.UserRegistrationDTO;
import com.mankind.mankindmatrixuserservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "keycloakId", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "profilePictureUrl", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    User toEntity(UserRegistrationDTO dto);
} 