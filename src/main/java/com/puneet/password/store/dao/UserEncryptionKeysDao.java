package com.puneet.password.store.dao;

import com.puneet.password.store.model.UserEncryptionKeys;
import com.puneet.password.store.model.UserDetailsVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserEncryptionKeysDao extends JpaRepository<UserEncryptionKeys,Long> {

    public UserEncryptionKeys findByUserName(UserDetailsVo userName);
}
