package TestJavaClient.utils.Tasks;

import TestJavaClient.utils.EwrapImpl;
import TestJavaClient.utils.HistoryEnum;

import java.util.TimerTask;

public class HistoryDailyTask extends TimerTask {

    private EwrapImpl object;

    public HistoryDailyTask(EwrapImpl object) {
        this.object = object;
    }

    @Override
    public void run() {
        object.cronJob(HistoryEnum.DAILY);

    }
}
