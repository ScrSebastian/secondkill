package com.xxxx.secondkill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "seckillQueue.v2";
    public static final String EXCHANGE = "seckillExchange";
    public static final String ROUTING_KEY = "seckill.message";
    public static final String DEAD_QUEUE = "seckillQueue.dead";
    public static final String DEAD_EXCHANGE = "seckillDeadExchange";
    public static final String DEAD_ROUTING_KEY = "seckill.dead";

    @Bean
    public Queue seckillQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange(DEAD_EXCHANGE)
                .deadLetterRoutingKey(DEAD_ROUTING_KEY)
                .build();
    }

    @Bean
    public Exchange seckillExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding seckillBinding() {
        return BindingBuilder.bind(seckillQueue()).to(seckillExchange()).with("seckill.#").noargs();
    }

    @Bean
    public Queue seckillDeadQueue() {
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }

    @Bean
    public DirectExchange seckillDeadExchange() {
        return new DirectExchange(DEAD_EXCHANGE);
    }

    @Bean
    public Binding seckillDeadBinding() {
        return BindingBuilder.bind(seckillDeadQueue())
                .to(seckillDeadExchange())
                .with(DEAD_ROUTING_KEY);
    }
}
