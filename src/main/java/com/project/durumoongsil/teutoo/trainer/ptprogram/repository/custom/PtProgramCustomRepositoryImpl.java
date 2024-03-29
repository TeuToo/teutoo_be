package com.project.durumoongsil.teutoo.trainer.ptprogram.repository.custom;

import com.project.durumoongsil.teutoo.common.domain.QFile;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.constants.ReservationStatus;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtReservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PtProgramCustomRepositoryImpl implements PtProgramCustomRepository {

    private final JPAQueryFactory queryFactory;

    QMember qMember = QMember.member;
    QPtProgram qPtProgram = QPtProgram.ptProgram;
    QTrainerInfo qTrainerInfo = QTrainerInfo.trainerInfo;
    QPtImg qPtImg = QPtImg.ptImg;
    QFile qFile = QFile.file;
    QPtReservation qPtReservation = QPtReservation.ptReservation;

    @Override
    public List<PtProgram> findByMemberEmailWithPtImg(String email) {

        return queryFactory
                .selectFrom(qPtProgram)
                .innerJoin(qPtProgram.trainerInfo, qTrainerInfo)
                .innerJoin(qTrainerInfo.member, qMember)
                .leftJoin(qPtProgram.ptImgList, qPtImg).fetchJoin()
                .leftJoin(qPtImg.file, qFile).fetchJoin()
                .where(qMember.email.eq(email))
                .fetch();
    }

    @Override
    public Optional<PtProgram> findByIdAndMemberEmailWithPtImgAndFile(Long ptProgramId, String email) {
        PtProgram ptProgram = queryFactory
                .selectFrom(qPtProgram)
                .innerJoin(qPtProgram.trainerInfo, qTrainerInfo)
                .innerJoin(qTrainerInfo.member, qMember)
                .leftJoin(qPtProgram.ptImgList, qPtImg).fetchJoin()
                .leftJoin(qPtImg.file, qFile).fetchJoin()
                .where(
                        qMember.email.eq(email),
                        qPtProgram.id.eq(ptProgramId)
                )
                .fetchFirst();

        return Optional.ofNullable(ptProgram);
    }

    @Override
    public Optional<PtProgram> findByIdWithPtImgAndFile(Long ptProgramId) {
        PtProgram ptProgram = queryFactory
                .selectFrom(qPtProgram)
                .leftJoin(qPtProgram.ptImgList, qPtImg).fetchJoin()
                .leftJoin(qPtImg.file, qFile).fetchJoin()
                .where(
                        qPtProgram.id.eq(ptProgramId)
                )
                .fetchFirst();

        return Optional.ofNullable(ptProgram);
    }

    @Override
    public Optional<Long> findTrainerIdById(Long ptProgramId) {
        Long trainerId =  queryFactory
                .select(qMember.id)
                .from(qPtProgram)
                .innerJoin(qPtProgram.trainerInfo, qTrainerInfo)
                .innerJoin(qTrainerInfo.member, qMember)
                .where(
                        qPtProgram.id.eq(ptProgramId)
                ).fetchFirst();

        return Optional.ofNullable(trainerId);
    }

    @Override
    public List<PtProgram> findByTrainerIdWithPtReservation(Long trainerId) {
        return queryFactory
                .selectFrom(qPtProgram)
                .innerJoin(qPtProgram.trainerInfo, qTrainerInfo)
                .leftJoin(qPtProgram.ptReservationList, qPtReservation).fetchJoin()
                .leftJoin(qPtReservation.member, qMember).fetchJoin()
                .where(qTrainerInfo.id.eq(trainerId)
                        .and(qPtReservation.status.eq(ReservationStatus.RESERVED))
                )
                .fetch();
    }

}
