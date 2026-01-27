package ru.otus.vivlev.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Report {

    private String text;

    private ReportStatus status;

    private String division;
}
