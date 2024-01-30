package TestJavaClient.utils;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame{

//    public SampleCandlestick minChart;
//
//    public SampleCandlestick min5Chart;
//
//    public SampleCandlestick hourlyChart;
//
//    public SampleCandlestick dailyChart;

    public MainFrame(SampleCandlestick minChart, SampleCandlestick min5Chart, SampleCandlestick hourlyChart, SampleCandlestick dailyChart) {
//        this.minChart = minChart;
//        this.min5Chart = min5Chart;
//        this.hourlyChart = hourlyChart;
//        this.dailyChart = dailyChart;

        setLayout(new FlowLayout() );
        getContentPane().add(minChart.chartPanel);
        getContentPane().add(min5Chart.chartPanel);
        getContentPane().add(hourlyChart.chartPanel);
        getContentPane().add(dailyChart.chartPanel);
        pack();
        setVisible(true);




//        JPanel panel= new JPanel(new GridLayout(3, 0));
//        panel.add(minChart, BorderLayout.CENTER);
//        panel.add(min5Chart, BorderLayout.CENTER);
//        panel.add(hourlyChart, BorderLayout.CENTER);
//        panel.add(dailyChart, BorderLayout.CENTER);
//        panel.validate();

    }
}
