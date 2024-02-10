package TestJavaClient.utils.Tasks;

import TestJavaClient.utils.EwrapImpl;
import TestJavaClient.utils.HistoryEnum;

import java.util.TimerTask;

public class HistoryMin5Task extends TimerTask {

    private EwrapImpl object;

    public HistoryMin5Task(EwrapImpl object) {
        this.object = object;
    }

    @Override
    public void run() {
        object.cronJob(HistoryEnum.MIN5);
    }

}
