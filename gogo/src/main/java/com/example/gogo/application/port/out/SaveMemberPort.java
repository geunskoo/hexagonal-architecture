package com.example.gogo.application.port.out;

import com.example.gogo.domain.Member;

public interface SaveMemberPort {

    Long joinMember(Member member);
}
