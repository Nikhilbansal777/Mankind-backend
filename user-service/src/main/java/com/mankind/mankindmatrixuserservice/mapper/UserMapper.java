package com.mankind.mankindmatrixuserservice.mapper;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.mankindmatrixuserservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);

    User toEntity(UserDTO dto);
}
