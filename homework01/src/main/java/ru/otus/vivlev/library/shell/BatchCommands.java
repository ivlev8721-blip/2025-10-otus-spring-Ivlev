package ru.otus.vivlev.library.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Date;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class BatchCommands {

    private final JobLauncher jobLauncher;
    private final Job importAlbumsJob;
    private final JobExplorer jobExplorer;

    @ShellMethod(value = "Import albums from CSV file", key = {"batch:import", "import"})
    public String importAlbums(
            @ShellOption(value = "--file", defaultValue = "data/albums.csv") String fileName) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fileName", fileName)
                    .addDate("date", new Date())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(importAlbumsJob, jobParameters);

            return String.format("Batch job started with execution ID: %d, Status: %s",
                    execution.getId(), execution.getStatus());
        } catch (JobExecutionAlreadyRunningException e) {
            return "Job is already running!";
        } catch (JobRestartException e) {
            return "Job restart failed: " + e.getMessage();
        } catch (JobInstanceAlreadyCompleteException e) {
            return "Job instance already completed!";
        } catch (Exception e) {
            log.error("Error running batch job", e);
            return "Error: " + e.getMessage();
        }
    }

    @ShellMethod(value = "Show batch job status", key = {"batch:status", "status"})
    public String showJobStatus(@ShellOption(value = "--id") Long executionId) {
        JobExecution execution = jobExplorer.getJobExecution(executionId);
        
        if (execution == null) {
            return "Job execution not found with ID: " + executionId;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Job Execution ID: ").append(execution.getId()).append("\n");
        sb.append("Job Name: ").append(execution.getJobInstance().getJobName()).append("\n");
        sb.append("Status: ").append(execution.getStatus()).append("\n");
        sb.append("Start Time: ").append(execution.getStartTime()).append("\n");
        sb.append("End Time: ").append(execution.getEndTime()).append("\n");
        sb.append("Exit Status: ").append(execution.getExitStatus().getExitCode()).append("\n");
        
        if (!execution.getStepExecutions().isEmpty()) {
            sb.append("\nStep Executions:\n");
            for (StepExecution stepExecution : execution.getStepExecutions()) {
                sb.append("  - ").append(stepExecution.getStepName())
                  .append(": ").append(stepExecution.getStatus())
                  .append(" (Read: ").append(stepExecution.getReadCount())
                  .append(", Write: ").append(stepExecution.getWriteCount())
                  .append(", Skip: ").append(stepExecution.getSkipCount())
                  .append(")\n");
            }
        }

        return sb.toString();
    }

    @ShellMethod(value = "List recent batch jobs", key = {"batch:list", "list-jobs"})
    public String listJobs(@ShellOption(value = "--count", defaultValue = "10") int count) {
        StringBuilder sb = new StringBuilder("Recent batch jobs:\n");
        
        jobExplorer.getJobNames().forEach(jobName -> {
            sb.append("\nJob: ").append(jobName).append("\n");
            jobExplorer.getJobInstances(jobName, 0, count).forEach(instance -> {
                JobExecution lastExecution = jobExplorer.getLastJobExecution(instance);
                if (lastExecution != null) {
                    sb.append("  ID: ").append(lastExecution.getId())
                      .append(", Status: ").append(lastExecution.getStatus())
                      .append(", Start: ").append(lastExecution.getStartTime())
                      .append("\n");
                }
            });
        });

        return sb.toString();
    }
}
