package com.project.durumoongsil.teutoo.member.service;

import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileService fileService;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입")
    void signUp() {
        //given
        MemberJoinDto memberJoinDto = MemberJoinDto.builder()
                .name("변주환")
                .email("test@naver.com")
                .password("1111")
                .address("경기도 성남시 분당구")
                .sortRole(true)
                .build();

        Member member = Member.toEntity(memberJoinDto);
        String encoded = passwordEncoder.encode(member.getPassword());
        member.setPassword(encoded);

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);

        // when
        memberService.signUp(memberJoinDto);


        // then
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member captorValue = memberArgumentCaptor.getValue();
        assertThat(captorValue.getRole()).isEqualTo(Role.TRAINER);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원수정 성공 로직")
    void update() {
        //given
        String userId = "test@naver.com";
        Member originalMember = Member.builder()
                .address("경기도 성남시 분당구")
                .build();

        MemberUpdateDto updateMember = MemberUpdateDto.builder()
                .address("서울특별시 강남구")
                .role(true)
                .build();

        when(memberRepository.findMemberByEmail(userId)).thenReturn(Optional.of(originalMember));

        // when
        memberService.updateInfo(userId, updateMember);

        // then
        verify(memberRepository).findMemberByEmail(userId);
        verify(memberRepository, times(1)).findMemberByEmail(userId);
        assertThat(originalMember.getAddress()).isEqualTo(updateMember.getAddress());
    }

    @Test
    @DisplayName("회원수정시 사용자를 찾지 못했을때 예외")
    void failFindUser() {
        //given
        when(memberRepository.findMemberByEmail("test@naver.com")).thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> {
            memberService.updateInfo("test@naver.com", new MemberUpdateDto());
        }).isInstanceOf(NotFoundUserException.class).hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("회원 조회 (단건)")
    void findMember() {
        Member member = Member.builder()
                .name("변주환")
                .email("test@naver.com")
                .password("1111")
                .address("경기도 성남시 분당구")
                .role(Role.USER)
                .build();

        //given
        when(memberRepository.findMemberByEmail("test@naver.com")).thenReturn(Optional.of(member));

        //when
        Member finedMember = memberService.findMember("test@naver.com");

        // then
        assertThat(finedMember.getName()).isEqualTo("변주환");
        assertThat(finedMember.getAddress()).isEqualTo("경기도 성남시 분당구");
    }
}