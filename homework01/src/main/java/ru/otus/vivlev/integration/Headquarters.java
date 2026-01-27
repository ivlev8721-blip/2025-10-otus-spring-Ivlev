package ru.otus.vivlev.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.vivlev.domain.Report;

import java.util.Collection;

@MessagingGateway
public interface Headquarters {

    @Gateway(requestChannel = "itemsChannel", replyChannel = "reportChannel")
    Collection<Report> sendPurpose(Collection<String> purposes);
}
