package com.t3e.authorizationserver.repository;

import com.lib.common.repository.BaseSpecification;
import com.lib.common.repository.FilterMetadata;
import com.t3e.authorizationserver.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserSpecification implements BaseSpecification<UserEntity> {
    public FilterMetadata<String> getEmailEqualFilter(String email) {
        return getAtomicFilter("email", email, FilterMetadata.FilterType.EQUAL);
    }
}
