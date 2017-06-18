package com.puneet.password.store.dao;


import com.puneet.password.store.model.UserDetailsVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserDetailsDao extends JpaRepository<UserDetailsVo,Long> {

    /*@PersistenceContext
    private EntityManager entityManager;


    public Optional<UserDetailsVo> findUserDetails(String userName){
        Query query = entityManager.createQuery("select userdetails from UserDetailsVo userdetails where userdetails.username = :userName");
        query.setParameter("userName",userName);
        List<UserDetailsVo> resultList = query.getResultList();
        if (CollectionUtils.isNotEmpty(resultList)) {
           return Optional.of(resultList.get(0)) ;
        }
        return Optional.empty();

    }

    @Transactional
    public void save(UserDetailsVo userDetails) {
        System.out.println("user detail is - "+userDetails.getId());
        if(userDetails.getId()== null){
            entityManager.persist(userDetails);
        }else{
            entityManager.merge(userDetails);
        }
    }*/

    public UserDetailsVo findByUsernameAndPassword(String userName, String password);

    public UserDetailsVo findByUsername(String username);
}
