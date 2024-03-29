package com.project.durumoongsil.teutoo.trainer.ptprogram.repository.custom;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;

import java.util.List;
import java.util.Optional;

public interface PtProgramCustomRepository {
    List<PtProgram> findByMemberEmailWithPtImg(String email);
    Optional<PtProgram> findByIdAndMemberEmailWithPtImgAndFile(Long ptProgramId, String email);
    Optional<PtProgram> findByIdWithPtImgAndFile(Long ptProgramId);
    Optional<Long> findTrainerIdById(Long ptProgramId);
    List<PtProgram> findByTrainerIdWithPtReservation(Long trainerId);
}
