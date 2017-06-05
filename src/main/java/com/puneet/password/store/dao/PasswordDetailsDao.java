package com.puneet.password.store.dao;

import com.puneet.password.store.model.SiteDetailVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordDetailsDao extends JpaRepository<SiteDetailVo,Long> {

  }
