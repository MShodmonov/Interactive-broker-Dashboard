/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package TestJavaClient;

import TestJavaClient.utils.EwrapImpl;
import TestJavaClient.utils.HistoryEnum;
import TestJavaClient.utils.Tasks.HistoryDailyTask;
import TestJavaClient.utils.Tasks.HistoryHourlyTask;
import TestJavaClient.utils.Tasks.HistoryMin5Task;
import TestJavaClient.utils.Tasks.HistoryMinTask;

import java.awt.Component;
import java.util.Timer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    // This method is called to start the application
    public static void main (String args[]) {
        Timer t = new Timer();
        EwrapImpl apiService = new EwrapImpl();
        HistoryMinTask minTask = new HistoryMinTask(apiService);
        HistoryMin5Task min5Task = new HistoryMin5Task(apiService);
        HistoryHourlyTask hourlyTask = new HistoryHourlyTask(apiService);
        HistoryDailyTask dailyTask = new HistoryDailyTask(apiService);
//        t.scheduleAtFixedRate(minTask, 60_000, 60_000);
//        t.scheduleAtFixedRate(min5Task, 300_000, 300_000);
//        t.scheduleAtFixedRate(hourlyTask, 3600_000, 3600_000);
//        t.scheduleAtFixedRate(dailyTask, 86400_000, 86400_000);

//        SampleFrame sampleFrame = new SampleFrame();


//        final SampleCandlestick demo = new SampleCandlestick("Candlestick Demo");
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
    }

    public static void inform( final Component parent, final String str) {
        if( SwingUtilities.isEventDispatchThread() ) {
        	showMsg( parent, str, JOptionPane.INFORMATION_MESSAGE);
        } else {
            SwingUtilities.invokeLater(() -> showMsg( parent, str, JOptionPane.INFORMATION_MESSAGE));
        }
    }

    private static void showMsg( Component parent, String str, int type) {
        // this function pops up a dlg box displaying a message
        JOptionPane.showMessageDialog( parent, str, "IB Java Test Client", type);
    }
}
