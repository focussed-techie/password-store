package com.puneet.password.store.dao;


import com.puneet.password.store.model.UserDetailsVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserDetailsDao extends JpaRepository<UserDetailsVo,Long> {

   public UserDetailsVo findByUsernameAndPassword(String userName, String password);

    public UserDetailsVo findByUsername(String username);
}
