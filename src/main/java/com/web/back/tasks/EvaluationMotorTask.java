package com.web.back.tasks;

import com.web.back.process.EvaluationAtLevelOneProcess;
import com.web.back.process.EvaluationAtLevelTwoProcess;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class EvaluationMotorTask {
    @Getter
    private volatile boolean running = false;
    private static final Logger logger = LoggerFactory.getLogger(EvaluationMotorTask.class);

    private final EvaluationAtLevelOneProcess evaluationAtLevelOneProcess;
    private final EvaluationAtLevelTwoProcess evaluationAtLevelTwoProcess;

    public EvaluationMotorTask(EvaluationAtLevelOneProcess evaluationAtLevelOneProcess, EvaluationAtLevelTwoProcess evaluationAtLevelTwoProcess) {
        this.evaluationAtLevelOneProcess = evaluationAtLevelOneProcess;
        this.evaluationAtLevelTwoProcess = evaluationAtLevelTwoProcess;
    }

    // Runs every day at 04:00 AM
    @Scheduled(cron = "0 0 4 * * ?")
    public void runDailyTask() {
        running = true;
        String runId = UUID.randomUUID().toString();
        logger.info("Scheduled task started. Run ID: {}", runId);
        try {
            Instant beginDate = Instant.now().minus(Duration.ofDays(15));
            Instant endDate = Instant.now();
            evaluationAtLevelOneProcess.generateEvaluations(beginDate, endDate);
            evaluationAtLevelTwoProcess.executeLevelTwoRules(beginDate, endDate);
        } finally {
            running = false;
            logger.info("Scheduled task finished. Run ID: {}", runId);
        }
    }
}
