package com.example.gogo.domain;

import lombok.Getter;

@Getter
public class Member {

    public enum Role {
        COMMON, ADMIN,
    }

    private String name;
    private String phoneNumber;
    private Role role;


    public Member(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = Role.COMMON;
    }

    public static void upgrade(Member member) {
        member.role = Role.ADMIN;
    }

}
