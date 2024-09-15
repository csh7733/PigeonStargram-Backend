package com.pigeon_stargram.sns_clone.worker.mail;

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

/**
 * Redis 작업 큐에서 메일 전송 작업을 처리하는 워커 클래스입니다.
 *
 * 이 클래스는 Redis에서 메일 전송 작업을 가져와 해당 메일을 전송하며,
 * 블로킹 큐를 사용하여 작업이 있을 때까지 CPU를 소모하지 않으며 대기합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MailSenderWorker {

    private final RedisService redisService;
    private final JavaMailSender mailSender;

    /**
     * Redis 큐에서 메일 전송 작업을 지속적으로 처리하는 메서드입니다.
     *
     * 이 메서드는 블로킹팝을 사용해 Redis 작업 큐에서 메일 전송 작업을 가져오고,
     * 작업이 있으면 메일을 전송합니다. Redis 연결 문제 또는 타임아웃 발생 시 적절히 재시도합니다.
     */
    public void processTasks() {
        while (true) {
            try {
                // Redis 큐에서 블로킹 방식으로 메일 전송 작업을 가져옴
                MailTask task = (MailTask) redisService.popTask(MAIL_QUEUE);
                if (task != null) {
                    // 가져온 작업이 유효하다면 메일을 전송
                    sendEmail(task);
                }
            } catch (QueryTimeoutException e) {
                // Lettuce 클라이언트의 기본 타임아웃(1분)에 도달하면 재연결 시도
            } catch (RedisConnectionException e) {
                log.error("Redis 서버와의 연결이 끊어졌습니다. 다시 연결 시도 중...", e);
            } catch (Exception e) {
                log.error("메일 전송 작업 처리 중 예외가 발생했습니다.", e);
            }
        }
    }

    /**
     * 주어진 메일 전송 작업을 처리하는 메서드입니다.
     */
    private void sendEmail(MailTask task) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            // 메시지 설정
            helper.setTo(task.getEmail());
            helper.setSubject(task.getSubject());
            helper.setText(task.getBody(), true);

            // 메일을 전송
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            throw new EmailNotSentException("메일 전송 실패", e);
        }
    }
}
