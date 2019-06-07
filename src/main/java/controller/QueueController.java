package controller;

import java.time.LocalTime;

public class QueueController extends AbstractScheduledController {
    private static final int SECOND = 1000;
    private static final int HOUR = 60 * 60 * SECOND;
    private static final int DAY = 24 * HOUR;
    private String dataQueue = "";
    private String commandQueue = "";

    QueueController(LocalTime startTime, long cycleInterval) {
        super(startTime, cycleInterval);
    }


    @Override
    protected void cycle() {

    }

    public void checkCommandQueue() {
    }
}
