package ru.otus.vivlev.service;

import org.springframework.stereotype.Service;
import ru.otus.vivlev.domain.Report;
import ru.otus.vivlev.domain.ReportStatus;


@Service
public class CentralServiceImpl implements CentralService{

    @Override
    public Report sendPurpose(String purpose) {
        if (purpose.equals("Небоскрёб")) {
            return new Report("Обнаружены агенты Смита! Эвакуировать Нео!",
                    ReportStatus.FAIL, "Операция 'Красная пилюля'");
        } else if (purpose.equals("Подземелье Зион")) {
            return new Report("Локация чиста. Повстанцы в безопасности",
                    ReportStatus.SUCCESS, "Хранители Зиона");
        } else if (purpose.equals("Логово Меровингена")) {
            return new Report("Требуется переговоры с эксиленсами. Опасно!",
                    ReportStatus.IN_PROCESS, "Дипломатический корпус");
        } else {
            return new Report("Локация не найдена в реестре Матрицы",
                    ReportStatus.FAIL, "Системные администраторы");
        }
    }
}
