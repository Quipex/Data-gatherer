package controller;

import notification.EmailCenter;
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

        try {
            while (!Thread.interrupted()) {
                cycle();
                ThreadUtils.sleep(cycleInterval);
            }
        } catch (Exception e) {
            EmailCenter.sendError(e.getMessage());
            throw e;
        }
    }

    /**
     * A method that will be scheduled to run in cycle with specific time interval.
     */
    protected abstract void cycle();
}
