package com.project.durumoongsil.teutoo.chat.repository.custom;

import com.project.durumoongsil.teutoo.chat.domain.Chat;
import com.project.durumoongsil.teutoo.chat.domain.QChat;
import com.project.durumoongsil.teutoo.chat.domain.QChatMsg;
import com.project.durumoongsil.teutoo.chat.dto.query.*;
import com.project.durumoongsil.teutoo.member.domain.QMember;
import com.project.durumoongsil.teutoo.trainer.info.domain.QTrainerInfo;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.QPtReservation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChatCustomRepositoryImpl implements ChatCustomRepository {

    private final JPAQueryFactory queryFactory;

    QChat qChat = QChat.chat;
    QMember qMember = QMember.member;
    QMember qTrainer = new QMember("trainer");
    QMember QaMember = new QMember("a_member");
    QMember QbMember = new QMember("b_member");
    QChatMsg qChatMsg = QChatMsg.chatMsg;
    QPtReservation qPtReservation = QPtReservation.ptReservation;
    QPtProgram qPtProgram = QPtProgram.ptProgram;
    QTrainerInfo qTrainerInfo = QTrainerInfo.trainerInfo;

    @Override
    public Chat findBySenderIdAndReceiverId(Long senderId, Long receiverId) {

        Long aMemberId = Math.min(senderId, receiverId);
        Long bMemberId = Math.max(senderId, receiverId);

        return queryFactory
                .selectFrom(qChat)
                .where(
                        qChat.aMember.id.eq(aMemberId),
                        qChat.bMember.id.eq(bMemberId)
                )
                .fetchFirst();
    }

    @Override
    public Long findSenderIdByRoomIdAndMemberEmail(String roomId, String memberEmail) {

        ChatMemberIdsQueryDto memberIdsQueryDto = queryFactory
                .select(new QChatMemberIdsQueryDto(qChat.aMember.id, qChat.bMember.id))
                .from(qChat)
                .where(
                        qChat.roomId.eq(roomId)
                                .and(qChat.aMember.email.eq(memberEmail)
                                        .or(qChat.bMember.email.eq(memberEmail))
                                ))
                .innerJoin(qChat.aMember, QaMember)
                .innerJoin(qChat.bMember, QbMember)
                .fetchFirst();

        if (memberIdsQueryDto.getAMemberId() == null)
            return memberIdsQueryDto.getBMemberId();

        return memberIdsQueryDto.getAMemberId();
    }

    @Override
    public Optional<Chat> findByRoomIdWithAMemberAndBMember(String roomId) {

        Chat chat = queryFactory
                .selectFrom(qChat)
                .innerJoin(qChat.aMember, QaMember).fetchJoin()
                .innerJoin(qChat.bMember, QbMember).fetchJoin()
                .where(qChat.roomId.eq(roomId))
                .fetchFirst();

        return Optional.ofNullable(chat);
    }

    @Override
    public List<ChatPreviewQueryDto> findChatMembersByEmailAndName(String memberEmail, String searchMemberName) {

        // 최신 메시지를 찾기 위한 subQuery
        JPQLQuery<Long> subQuery = JPAExpressions
                .select(qChatMsg.id.max())
                .from(qChatMsg)
                .groupBy(qChatMsg.chat.id);

        return queryFactory
                    .select(
                            new QChatPreviewQueryDto(
                                    QaMember.id,
                                    QbMember.id,
                                    QaMember.name,
                                    QbMember.name,
                                    QaMember.profileImageName,
                                    QbMember.profileImageName,
                                    qChat.aMsgIdx,
                                    qChat.bMsgIdx,
                                    new QChatMsgQueryDto(
                                            qChatMsg.id,
                                            qChatMsg.sender.id,
                                            qChatMsg.sender.name,
                                            qChatMsg.msgType,
                                            qChatMsg.createdAt,
                                            qChatMsg.textContent,
                                            qChatMsg.imgPath,
                                            qChatMsg.imgName,
                                            qPtProgram.id,
                                            qPtProgram.title,
                                            qChatMsg.ptProgramPrice,
                                            qChatMsg.gymAddress,
                                            qPtReservation.startDateTime,
                                            qPtReservation.endDateTime,
                                            qPtReservation.status,
                                            qMember.id,
                                            qMember.name,
                                            qTrainer.id,
                                            qTrainer.name,
                                            qPtReservation.id,
                                            qChatMsg.ptProgramName
                                    )
                            )
                    )
                    .from(qChat)
                    .innerJoin(qChat.aMember, QaMember)
                    .innerJoin(qChat.bMember, QbMember)
                    .innerJoin(qChat.chatMsgList, qChatMsg)
                    .leftJoin(qChatMsg.ptReservation, qPtReservation)
                    .leftJoin(qPtReservation.ptProgram, qPtProgram)
                    .leftJoin(qPtReservation.member, qMember)
                    .leftJoin(qPtProgram.trainerInfo, qTrainerInfo)
                    .leftJoin(qTrainerInfo.member, qTrainer)
                    .where(
                            qChatMsg.id.in(subQuery),
                            eqEmailAndLikeSearchMemberName(memberEmail, searchMemberName)
                    )
                    .fetch();
    }

    private BooleanBuilder eqEmailAndLikeSearchMemberName(String memberEmail, String searchMemberName) {
        BooleanBuilder builder = new BooleanBuilder();

        // A member을 대상으로 memberEmail 같은지, B member을 대상으로 이름을 포함하고 있는지,
        BooleanBuilder searchCondition1 = new BooleanBuilder();
        searchCondition1.and(QaMember.email.eq(memberEmail));
        searchCondition1.and(QbMember.name.like("%" + searchMemberName + "%"));

        // B member을 대상으로 memberEmail 같은지, A member을 대상으로 이름을 포함하고 있는지,
        BooleanBuilder searchCondition2 = new BooleanBuilder();
        searchCondition2.and(QbMember.email.eq(memberEmail));
        searchCondition2.and(QaMember.name.like("%" + searchMemberName + "%"));

        // 조건 그룹을 OR 연산자로 결합
        builder.or(searchCondition1);
        builder.or(searchCondition2);

        return builder;
    }
}
