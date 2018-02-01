package com.puneet.password.store.dao;

import com.puneet.password.store.model.UserDetailsVo;
import com.puneet.password.store.model.VaultEntryVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaultEntryDao extends JpaRepository<VaultEntryVo,Long> {

   List<VaultEntryVo> findByUser_Id(long id);

}
