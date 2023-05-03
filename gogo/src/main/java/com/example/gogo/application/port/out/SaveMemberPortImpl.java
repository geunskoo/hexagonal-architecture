package com.example.gogo.application.port.out;

import com.example.gogo.adaptor.infrastructure.MemberEntity;
import com.example.gogo.adaptor.infrastructure.MemberRepository;
import com.example.gogo.domain.Member;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveMemberPortImpl implements SaveMemberPort{

    private final MemberRepository memberRepository;

    @Override
    public Long joinMember(Member member) {
        MemberEntity memberEntity = MemberEntity.builder()
            .name(member.getName())
            .phoneNumber(member.getPhoneNumber())
            .role(member.getRole())
            .build();
        MemberEntity savedMemberEntity = memberRepository.save(memberEntity);
        return savedMemberEntity.getId();
    }
}
