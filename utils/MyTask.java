package TestJavaClient.utils;

import TestJavaClient.SampleFrame;

import java.util.TimerTask;

public class MyTask extends TimerTask {

    private EwrapImpl object;

    public MyTask(EwrapImpl object) {
        this.object = object;
    }

    @Override
    public void run() {
        object.cronJob();
    }
}
