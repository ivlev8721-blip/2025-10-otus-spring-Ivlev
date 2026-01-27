package ru.otus.vivlev;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import ru.otus.vivlev.integration.Headquarters;

import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(Application.class);

        Headquarters headquarters = ctx.getBean(Headquarters.class);

        headquarters.sendPurpose(List.of(
                "Небоскрёб",
                "Подземелье Зион",
                "Логово Меровингена"
        ));
    }
}
