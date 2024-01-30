/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package TestJavaClient;

import TestJavaClient.utils.EwrapImpl;
import TestJavaClient.utils.MyTask;
import TestJavaClient.utils.SampleCandlestick;
import org.jfree.ui.RefineryUtilities;

import java.awt.Component;
import java.util.Timer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    // This method is called to start the application
    public static void main (String args[]) {
        Timer t = new Timer();
        EwrapImpl startingPoint = new EwrapImpl();
        MyTask task = new MyTask(startingPoint);
        t.scheduleAtFixedRate(task, 60000, 60000);

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
