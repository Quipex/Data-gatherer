package controller;

import java.time.LocalTime;

public class QueueController extends AbstractScheduledController{

    public QueueController(LocalTime startTime, long cycleInterval) {
        super(startTime, cycleInterval);
    }

    @Override
    protected void cycle() {

    }

    public void checkCommandQueue() {

    }
}
