package com.stmarys.library;

public class Member {
    private int memberId;
    private String name;
    private String email;
    private String membershipStatus;

    public Member(int memberId, String name, String email, String membershipStatus) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.membershipStatus = membershipStatus;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }
}