package controller;

import utils.ThreadUtils;

import java.time.LocalTime;

abstract class AbstractScheduledController implements Runnable {
    private final LocalTime startTime;
    private final long cycleInterval;

    AbstractScheduledController(LocalTime startTime, long cycleInterval) {
        this.startTime = startTime;
        this.cycleInterval = cycleInterval;
    }

    @Override
    public final void run() {
        ThreadUtils.waitUntil(startTime);

        while (!Thread.interrupted()) {
            cycle();
            ThreadUtils.sleep(cycleInterval);
        }
    }

    /**
     * A method that will be scheduled to run in cycle with specific time interval.
     */
    protected abstract void cycle();
}
