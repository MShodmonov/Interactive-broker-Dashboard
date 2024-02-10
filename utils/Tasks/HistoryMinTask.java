package TestJavaClient.utils.Tasks;

import TestJavaClient.utils.EwrapImpl;
import TestJavaClient.utils.HistoryEnum;

import java.util.TimerTask;

public class HistoryMinTask extends TimerTask {

    private EwrapImpl object;

    public HistoryMinTask(EwrapImpl object) {
        this.object = object;
    }

    @Override
    public void run() {
        object.cronJob(HistoryEnum.MIN);

    }
}
