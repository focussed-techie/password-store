package com.puneet.password.store.model;


import javax.persistence.*;

@Entity
@Table(name = "USER_ENCRYPTION_KEYS")
public class UserEncryptionKeys {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


@Column(name = "SALT")
private String salt;

 @Column(name = "INIT_VECTOR")
 private String initVector;




    @OneToOne
@JoinColumn(name = "USERNAME")
private UserDetailsVo userName;


    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInitVector() {
        return initVector;
    }

    public void setInitVector(String initVector) {
        this.initVector = initVector;
    }

    public UserDetailsVo getUserName() {
        return userName;
    }

    public void setUserName(UserDetailsVo userName) {
        this.userName = userName;
    }
}



