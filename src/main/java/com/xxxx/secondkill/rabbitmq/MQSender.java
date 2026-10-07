package com.xxxx.secondkill.rabbitmq;

import com.xxxx.secondkill.config.RabbitMQConfig;
import com.xxxx.secondkill.pojo.SeckillMessage;
import com.xxxx.secondkill.service.SeckillAdmissionService;
import com.xxxx.secondkill.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {

    private final RabbitTemplate rabbitTemplate;
    private final SeckillAdmissionService admissionService;

    public MQSender(RabbitTemplate rabbitTemplate, SeckillAdmissionService admissionService) {
        this.rabbitTemplate = rabbitTemplate;
        this.admissionService = admissionService;
    }

    public boolean sendSeckillMessage(SeckillMessage message) {
        String payload = JSONUtil.object2JsonStr(message);
        CorrelationData correlationData = new CorrelationData(message.getRequestId());
        correlationData.getFuture().addCallback(confirm -> {
            if (confirm == null || !confirm.isAck() || correlationData.getReturned() != null) {
                log.error("秒杀消息未被 RabbitMQ 接收, requestId={}, reason={}",
                        message.getRequestId(), confirm == null ? "no confirm" : confirm.getReason());
                admissionService.failAndReleaseReservation(message.getUserId(), message.getGoodsId());
            }
        }, exception -> {
            log.error("秒杀消息确认异常, requestId={}", message.getRequestId(), exception);
            admissionService.failAndReleaseReservation(message.getUserId(), message.getGoodsId());
        });

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    payload,
                    correlationData);
            log.debug("秒杀消息已发送, requestId={}", message.getRequestId());
            return true;
        } catch (AmqpException exception) {
            log.error("秒杀消息发送失败, requestId={}", message.getRequestId(), exception);
            return false;
        }
    }
}
