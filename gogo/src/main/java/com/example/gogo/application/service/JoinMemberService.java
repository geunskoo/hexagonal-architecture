package com.example.gogo.application.service;

import com.example.gogo.application.port.in.JoinMemberUseCase;
import com.example.gogo.application.port.in.MemberRequest;
import com.example.gogo.application.port.out.SaveMemberPort;
import com.example.gogo.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinMemberService implements JoinMemberUseCase {

    private final SaveMemberPort saveMemberPort;

    @Override
    public Long joinMember(MemberRequest memberRequest) {
        Member member = new Member(memberRequest.getName(), memberRequest.getPhoneNumber());
        Member.upgrade(member);
        Long savedId = saveMemberPort.joinMember(member);
        return savedId;
    }
}
