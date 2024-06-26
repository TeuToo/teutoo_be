package com.project.durumoongsil.teutoo.estimate.service;

import com.project.durumoongsil.teutoo.common.LoginEmail;
import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.domain.TrainerEstimate;
import com.project.durumoongsil.teutoo.estimate.dto.user.CreateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.dto.user.UpdateEstimateDto;
import com.project.durumoongsil.teutoo.estimate.repository.EstimateRepository;
import com.project.durumoongsil.teutoo.exception.DuplicateEstimateException;
import com.project.durumoongsil.teutoo.exception.UnauthorizedActionException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EstimateService {

    private final EstimateRepository estimateRepository;
    private final MemberRepository memberRepository;

    /**
     *  유저 입장에서는 트레이너 견적서 목록이 no-offset 으로 나와야함
     */
    public List<TrainerEstimate> searchAllTrainerEstimates(Long cursorId, int size) {
        return estimateRepository.findTrainerEstimateNoOffset(cursorId, size);
    }

    /**
     * 내가 작성한 견적서 Id 를 리턴한다
     * @return 견적서 Id / null
     */
    public Long getMyEstimateId() {
        return estimateRepository.getMyEstimateId(LoginEmail.getLoginUserEmail());
    }

    /**
     * 우선 기획상 한사람당 한개의 견적서만 할 수 있다.
     */
    public void createEstimate(CreateEstimateDto createEstimateDto, String loginUserEmail) {
        Member member = getMember(loginUserEmail);
        isEstimateAvailable(member);
        estimateRepository.save(createEstimateEntity(createEstimateDto, member));
    }


    /**
     * 견적서 단건 조회
     */
    @Transactional(readOnly = true)
    public Estimate searchEstimate(Long estimateId) {
         return estimateRepository.findEstimateWithMemberName(estimateId);
    }

    /**
     * 견적서 수정
     */
    public Estimate updateEstimate(Long estimateId, UpdateEstimateDto updateEstimateDto, String currentLoginId) {
        Estimate estimate = hasAuthority(estimateId, currentLoginId);
        estimate.setPrice(updateEstimateDto.getPrice());
        estimate.setPtAddress(updateEstimateDto.getPtAddress());
        return estimate;
    }

    /**
     * 견적서 삭제 -> 자가가 작성한 건지 확인 후 삭제
     */
    public void deleteEstimate(Long estimateId, String currentLoginId) {
        Estimate estimate = hasAuthority(estimateId, currentLoginId);
        estimateRepository.delete(estimate);
    }

    private Estimate hasAuthority(Long estimateId, String currentLoginId) {
        Estimate estimateWithMember = estimateRepository.findEstimateWithMemberName(estimateId);

        if(!estimateWithMember.getMember().getEmail().equals(currentLoginId))
            throw new UnauthorizedActionException("권한이 없습니다");

        return estimateWithMember;
    }

    private Estimate createEstimateEntity(CreateEstimateDto createEstimateDto, Member member) {
        return Estimate.builder()
                .price(createEstimateDto.getPrice())
                .ptAddress(createEstimateDto.getPtAddress())
                .member(member)
                .build();
    }

    private Member getMember(String loginUserEmail) {
        log.info("loinEmail ={}", loginUserEmail);
        return memberRepository.findMemberByEmail(loginUserEmail).get();
    }

    private void isEstimateAvailable(Member member) {
        if (estimateRepository.countEstimateByMember(member) > 0) {
            throw new DuplicateEstimateException("이미 작성한 견적서가 있습니다.");
        }
    }
}
