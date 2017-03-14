package com.larryhowell.xunta.bean;

import java.io.Serializable;

public class Person implements Serializable {
    private String nickname;
    private String telephone;
    private String portraitUrl;

    public Person() {
    }

    public Person(String nickname, String telephone, String portraitUrl) {
        this.nickname = nickname;
        this.telephone = telephone;
        this.portraitUrl = portraitUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }
}
