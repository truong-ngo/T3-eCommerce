package com.t3e.authorizationserver.service.mapper;

import com.lib.common.mapper.EntityMapper;
import com.t3e.authorizationserver.entity.UserEntity;
import com.t3e.authorizationserver.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserEntity, User> {

    @Override
    @Mapping(target = "roles", source = "roles", qualifiedByName = "listToString")
    UserEntity toEntity(User dto);

    @Override
    @Mapping(target = "roles", source = "roles", qualifiedByName = "stringToList")
    User toDto(UserEntity entity);

    @Named("listToString")
    default String toString(List<String> list) {
        return String.join(",", list);
    }

    @Named("stringToList")
    default List<String> toList(String string) {
        return new ArrayList<>(List.of(string.split(",")));
    }
}
