package com.athena.mis.application.utility;

/**
 * Created by Mirza-Ehsan on 07-Dec-2014.
 */
public class TimeLogger {
    private long startTime = 0L;
    private long endTime = 0L;
    private long cumulativeTime = 0L;
    boolean isInterval;

    public TimeLogger(boolean isInterval) {
        this.isInterval = isInterval;
    }

    public TimeLogger() {
        this.isInterval = false;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    private void stop() {
        endTime = System.currentTimeMillis();
    }

    public void logInterval() {
        if (!isInterval) return;
        stop();
        cumulativeTime = cumulativeTime + (endTime - startTime);
    }

    public long calculate() {
        if (!isInterval) return calc();
        long tmp = cumulativeTime;
        cumulativeTime = 0L;
        return tmp;
    }

    private long calc() {
        stop();
        return endTime - startTime;
    }
}
