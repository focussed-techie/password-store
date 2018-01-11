package com.puneet.password.store.dao;

import com.puneet.password.store.model.SaltAssocation;
import com.puneet.password.store.model.UserDetailsVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SaltAssociationDao extends JpaRepository<SaltAssocation,Long> {

    public SaltAssocation findByUserName(UserDetailsVo userName);
}
