package com.t3e.authorizationserver.repository;

import com.lib.common.repository.BaseSpecification;
import com.lib.common.repository.FilterMetadata;
import com.t3e.authorizationserver.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserSpecification implements BaseSpecification<UserEntity> {
    public FilterMetadata<String> getUsernameEqualFilter(String username) {
        return getAtomicFilter("username", username, FilterMetadata.FilterType.EQUAL);
    }
}
