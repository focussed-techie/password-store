package com.puneet.password.store.model;


import javax.persistence.*;

@Entity

@Table(name = "PASSWORD_STORAGE_DETAILS")
public class EntryDetailVo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SITE_NAME")
    private String siteName;

    @Column(name = "USERNAME")
    private String username;


    @Column(name="SITE_URL")
    private String siteUrl;

    @Column(name = "DESCRIPTION")
    private String description;


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = new Long(id);
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
