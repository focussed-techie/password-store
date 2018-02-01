package com.puneet.password.store.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER_DETAILS")
public class UserDetailsVo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name="USERNAME")
    private String username;

    @Column(name="PASSWORD")
    private String password;



    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = new Long(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
