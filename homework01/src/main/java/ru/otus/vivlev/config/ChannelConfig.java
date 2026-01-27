package ru.otus.vivlev.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@Configuration
public class ChannelConfig {

    @Bean
    public PollableChannel itemsChannel() {
        return MessageChannels.queue(10).get();
    }

    @Bean
    public SubscribableChannel centralChannel() {
        SubscribableChannel channel = MessageChannels.publishSubscribe().get();

        channel.subscribe(msg -> {
            log.info("[МАТРИЦА] Анализ локации: {}", msg.getPayload());
        });

        return channel;
    }

    @Bean
    public SubscribableChannel reportChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2).get();
    }

    @Bean
    public IntegrationFlow matrixFlow() {
        return IntegrationFlows.from(itemsChannel())
                .split()

                // SUB-FLOW: Определение дивизиона для локации
                // Важно: gateway требует, чтобы sub-flow возвращал результат
                .gateway(divisionAssignmentSubFlow())

                .channel(centralChannel())
                .handle("centralServiceImpl", "sendPurpose")
                .aggregate()
                .channel(reportChannel())
                .get();
    }

    // SUB-FLOW: Определение ответственного дивизиона
    @Bean
    public IntegrationFlow divisionAssignmentSubFlow() {
        return IntegrationFlows
                .from("divisionInputChannel")
                .<String>handle((location, headers) -> {
                    String division;

                    if (location.contains("Небоскрёб")) {
                        division = "Операция 'Красная пилюля'";
                    } else if (location.contains("Зион")) {
                        division = "Хранители Зиона";
                    } else if (location.contains("Меровингена")) {
                        division = "Дипломатический корпус";
                    } else {
                        division = "Общая разведка";
                    }

                    log.info("[НАЗНАЧЕНИЕ] Локация '{}' -> Дивизион: {}", location, division);

                    // Возвращаем обогащенное сообщение
                    return MessageBuilder
                            .withPayload(location)
                            .copyHeaders(headers)
                            .setHeader("assignedDivision", division)
                            .setHeader("assignmentTime", System.currentTimeMillis())
                            .build();
                })
                // Не указываем .channel() в конце - gateway сам управляет выходным каналом
                // Просто заканчиваем flow
                .get();
    }
}