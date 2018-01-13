package com.puneet.password.store.dao;

import com.puneet.password.store.model.VaultEntryVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaultEntryDao extends JpaRepository<VaultEntryVo,Long> {

  }
