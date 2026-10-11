package com.mankind.mankindmatrixuserservice.mapper;


import com.mankind.api.user.dto.UpdateUserDTO;
import com.mankind.mankindmatrixuserservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserUpdateMapper {
    UpdateUserDTO toDto(User user);

    User toEntity(UpdateUserDTO dto);

}
