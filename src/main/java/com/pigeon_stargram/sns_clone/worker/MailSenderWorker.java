package com.pigeon_stargram.sns_clone.worker;

import com.pigeon_stargram.sns_clone.dto.login.internal.MailTask;
import com.pigeon_stargram.sns_clone.exception.login.EmailNotSentException;
import com.pigeon_stargram.sns_clone.service.redis.RedisService;
import io.lettuce.core.RedisConnectionException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pigeon_stargram.sns_clone.constant.RedisQueueConstants.MAIL_QUEUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailSenderWorker {

    private final RedisService redisService;
    private final JavaMailSender mailSender;

    // 각 스레드가 Redis 작업큐에서 task를 가져와 처리
    public void processTasks() {
        while (true) {
            try {
                log.info("Redis 큐에서 메일 전송 작업을 대기 중입니다...");
                // Redis 작업큐에서 대기시간을 무한으로 Blocking Pop 방식으로 가져옴
                MailTask task = (MailTask) redisService.popTask(MAIL_QUEUE);
                if (task != null) {
                    log.info("메일 전송 작업을 가져왔습니다. 수신자: {}", task.getEmail());
                    // 가져온 작업이 유효하다면 메일을 전송
                    sendEmail(task);
                }
            } catch (QueryTimeoutException e) {
                // Lettuce 클라이언트는 기본적으로 1분후에 타임아웃 시킴
                // 서버의 안전성을 위해 작업큐에 task가 없다면
                // 1분(기본값)후에 연결을 재시도한 후 다시 블로킹
                log.info("[MAIL BLOCKING POP 재설정] MAIL 작업큐에 1분동안 작업이없어서 다시 연결합니다");
            } catch (RedisConnectionException e) {
                log.error("Redis 서버와의 연결이 끊어졌습니다. 다시 연결 시도 중...", e);
            } catch (Exception e) {
                log.error("메일 전송 작업 처리 중 예외가 발생했습니다.", e);
            }
        }
    }



    // 메일 전송 작업을 처리
    private void sendEmail(MailTask task) {
        try {
            log.info("메일을 전송합니다. 수신자: {}, 제목: {}", task.getEmail(), task.getSubject());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // 메시지 설정
            helper.setTo(task.getEmail());
            helper.setSubject(task.getSubject());
            helper.setText(task.getBody(), true);

            // 메일을 전송
            mailSender.send(mimeMessage);
            log.info("메일 전송 완료. 수신자: {}", task.getEmail());
        } catch (MessagingException | MailException e) {
            throw new EmailNotSentException("메일 전송 실패", e);
        }
    }
}
