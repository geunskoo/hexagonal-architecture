package com.example.gogo.adaptor.web;

import com.example.gogo.application.port.in.JoinMemberUseCase;
import com.example.gogo.application.port.in.MemberRequest;
import com.example.gogo.application.service.JoinMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinMemberAdaptor {

    private final JoinMemberUseCase joinMemberUseCase;

    @PostMapping("/member")
    public ResponseEntity joinClub(@RequestBody MemberRequest memberRequest) {
        Long savedId = joinMemberUseCase.joinMember(memberRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(savedId);
    }
}
