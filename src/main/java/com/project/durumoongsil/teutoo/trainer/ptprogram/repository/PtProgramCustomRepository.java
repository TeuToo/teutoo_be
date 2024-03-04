package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;

import java.util.List;
import java.util.Optional;

public interface PtProgramCustomRepository {

    Optional<PtProgram> findByPtProgramByIdAndMemberEmail(Long ptProgramId, String email);
    List<PtProgram> findByMemberEmailWithPtImg(String email);
}