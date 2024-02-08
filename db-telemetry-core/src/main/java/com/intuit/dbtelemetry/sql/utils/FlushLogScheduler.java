package com.intuit.dbtelemetry.sql.utils;

import com.intuit.dbtelemetry.sql.logs.LogFlush;
import com.intuit.dbtelemetry.sql.logs.TelemetryFlushException;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FlushLogScheduler {

    private final ScheduledExecutorService scheduler;
    private final Map<ScheduleType, ScheduledFuture<?>> scheduledTasks;
    private final LogFlush logFlush;
    private final Map<ScheduleType, ScheduleConfig> scheduleConfigMap;
    /**
     * Constructor for creating a FlushLogScheduler object.
     *
     * @param logFlush Object in charge of flushing logs.
     * @param scheduler A scheduled executor service to run tasks.
     * @param scheduleConfigMap A map defining config schedules for different types.
     */
    public FlushLogScheduler(LogFlush logFlush, ScheduledExecutorService scheduler,
                             Map<ScheduleType, ScheduleConfig> scheduleConfigMap) {
        this.logFlush = logFlush;
        this.scheduler = scheduler;
        this.scheduleConfigMap = scheduleConfigMap;
        this.scheduledTasks = new EnumMap<>(ScheduleType.class);
    }

    /**
     * Schedules a task based on the type provided.
     *
     * @param scheduleType The type of scheduling to be performed.
     */
    public void scheduleTask(ScheduleType scheduleType) {
        validateSchedulerState("ScheduledExecutorService is null in DefaultDBTelemetryConfig schedule failed", scheduleType);
        ScheduleConfig scheduleConfig = scheduleConfigMap.get(scheduleType);
        Runnable task = getRunnableTask(scheduleType, scheduleConfig.getLoggerSleepTime());

        ScheduledFuture<?> scheduledTask = this.scheduler.scheduleAtFixedRate(
                task,
                scheduleConfig.getInterval(),
                scheduleConfig.getInterval(),
                TimeUnit.SECONDS
        );

        scheduledTasks.put(scheduleType, scheduledTask);
    }

    /**
     * Returns a Runnable task depending on the schedule type.
     *
     * @param scheduleType The type of scheduling to be performed.
     * @param sleepTime The time in milliseconds that the task thread should sleep for.
     * @return a Runnable task
     */
    private Runnable getRunnableTask(ScheduleType scheduleType, long sleepTime) {
        switch (scheduleType) {
            case Telemetry:
                return () -> {
                    try {
                        logFlush.flushLogs(sleepTime);
                    } catch (TelemetryFlushException e) {
                        // log.error(e);
                    }
                };
            case StackTrace:
                return () -> {
                    try {
                        logFlush.flushStackTrace(sleepTime);
                    } catch (TelemetryFlushException e) {
                        // log.error(e);
                    }
                };
            default:
                throw new IllegalArgumentException("Invalid ScheduleType");
        }
    }
    /**
     * Cancels and reschedules a task based on the type provided.
     *
     * @param scheduleType The type of scheduling to be cancelled and rescheduled.
     */
    public void reSchedule(ScheduleType scheduleType) {
        unSchedule(scheduleType);
        scheduleTask(scheduleType);
    }
    /**
     * Un-schedules a task based on the type provided.
     *
     * @param scheduleType The type of scheduling to be cancelled.
     */
    public void unSchedule(ScheduleType scheduleType) {
        validateSchedulerState("ScheduledExecutorService is null in DefaultDBTelemetryConfig unSchedule failed", scheduleType);
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(scheduleType);

        if (Objects.nonNull(scheduledTask)) {
            ScheduleConfig scheduleConfig = scheduleConfigMap.get(scheduleType);

            scheduler.schedule(
                    () -> scheduledTask.cancel(true),
                    scheduleConfig.getInterval(), TimeUnit.SECONDS
            );
        }
    }
    /**
     * Validates current state of scheduler and provided schedule type before scheduling or un-scheduling.
     *
     * @param s Error message to show when exception is thrown due to invalid state.
     * @param scheduleType The type of scheduling to be validated.
     */
    private void validateSchedulerState(String s, ScheduleType scheduleType) {
        if (Objects.isNull(this.scheduler)) {
            throw new IllegalStateException(s);
        }
        if (Objects.isNull(scheduleType)
                || Objects.isNull(scheduleConfigMap)
                || Objects.isNull(scheduleConfigMap.get(scheduleType))) {
            throw new IllegalStateException("ScheduleConfig is null for the schedule type " + scheduleType);
        }
    }

    public enum ScheduleType {
        StackTrace,
        Telemetry
    }

    public static class ScheduleConfig {
        long interval = 0;
        long loggerSleepTime = 0;

        public ScheduleConfig() {
        }

        public ScheduleConfig(long interval, long loggerSleepTime) {
            this.interval = interval;
            this.loggerSleepTime = loggerSleepTime;
        }

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public long getLoggerSleepTime() {
            return loggerSleepTime;
        }

        public void setLoggerSleepTime(long loggerSleepTime) {
            this.loggerSleepTime = loggerSleepTime;
        }
    }

}
