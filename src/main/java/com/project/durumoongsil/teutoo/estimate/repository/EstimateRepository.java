package com.project.durumoongsil.teutoo.estimate.repository;

import com.project.durumoongsil.teutoo.estimate.domain.Estimate;
import com.project.durumoongsil.teutoo.estimate.repository.query.user.EstimateQueryRepository;
import com.project.durumoongsil.teutoo.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Long>, EstimateQueryRepository {
    int countEstimateByMember(Member member);
}
