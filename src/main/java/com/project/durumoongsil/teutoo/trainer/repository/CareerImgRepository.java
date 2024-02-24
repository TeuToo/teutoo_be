package com.project.durumoongsil.teutoo.trainer.repository;

import com.project.durumoongsil.teutoo.common.domain.QFile;
import com.project.durumoongsil.teutoo.trainer.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.domain.QCareerImg;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CareerImgRepository {

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void save(CareerImg careerImg) {
        em.merge(careerImg);
    }

    @Transactional(readOnly = true)
    public List<CareerImg> findByTrainerIdWithFile(Long trainerId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        QCareerImg qCareerImg = QCareerImg.careerImg;
        QFile qFile = QFile.file;

        return queryFactory.selectFrom(qCareerImg)
                    .join(qCareerImg.file, qFile).fetchJoin()
                    .where(qCareerImg.trainerInfo.id.eq(trainerId))
                    .fetch();
    }
}