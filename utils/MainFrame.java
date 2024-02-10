package TestJavaClient.utils;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MainFrame extends JFrame {

    public SampleCandlestick minChart;

    public SampleCandlestick min5Chart;

    public SampleCandlestick hourlyChart;

    public SampleCandlestick dailyChart;

    private JMenu menuList = null;

    public MainFrame(SampleCandlestick minChart, SampleCandlestick min5Chart, SampleCandlestick hourlyChart, SampleCandlestick dailyChart) {
        this.minChart = minChart;
        this.min5Chart = min5Chart;
        this.hourlyChart = hourlyChart;
        this.dailyChart = dailyChart;

        JMenuBar menu = new JMenuBar();
        menuList = new JMenu("Historical Data");
        JMenuItem min5Item, hourlyItem, dailyItem, minItem;
        minItem = new JMenuItem(HistoryEnum.MIN.toString());

        min5Item = new JMenuItem(HistoryEnum.MIN5.toString());
        hourlyItem = new JMenuItem(HistoryEnum.HOURLY.toString());
        dailyItem = new JMenuItem(HistoryEnum.DAILY.toString());
        menuList.add(minItem);
        menuList.add(min5Item);
        menuList.add(hourlyItem);
        menuList.add(dailyItem);
        menu.add(menuList);
        setJMenuBar(menu);

        JPanel panel = new JPanel(new GridLayout(2, 2));
//        getJMenuBar().add()
        getContentPane().add(panel);
        JButton someButton = new JButton("Connect");
        someButton.setPreferredSize(new Dimension(20, 100));
        menuOptionChooseAction(panel, HistoryEnum.MIN);
//        butConnect.addActionListener(e -> onConnect());

        minItem.addActionListener(e -> menuOptionChooseAction(panel, HistoryEnum.valueOf(e.getActionCommand())));
        min5Item.addActionListener(e -> menuOptionChooseAction(panel, HistoryEnum.valueOf(e.getActionCommand())));
        hourlyItem.addActionListener(e -> menuOptionChooseAction(panel, HistoryEnum.valueOf(e.getActionCommand())));
        dailyItem.addActionListener(e -> menuOptionChooseAction(panel, HistoryEnum.valueOf(e.getActionCommand())));
        panel.add(someButton);
        panel.validate();
        panel.setVisible(true);
        pack();
        setVisible(true);
    }

    public void updateChart(SampleCandlestick chart, HistoryEnum historyEnum) {
        switch (historyEnum) {
            case MIN -> minChart = chart;
            case MIN5 -> min5Chart = chart;
            case DAILY -> dailyChart = chart;
            case HOURLY -> hourlyChart = chart;
        }
    }

    public void menuOptionChooseAction(JPanel panel, HistoryEnum historyEnum) {
        switch (historyEnum) {
            case MIN -> {
                panel.add(minChart.chartPanel);
                panel.remove(min5Chart.chartPanel);
                panel.remove(hourlyChart.chartPanel);
                panel.remove(dailyChart.chartPanel);
            }
            case MIN5 -> {
                panel.add(min5Chart.chartPanel);
                panel.remove(minChart.chartPanel);
                panel.remove(hourlyChart.chartPanel);
                panel.remove(dailyChart.chartPanel);
            }
            case HOURLY -> {
                panel.add(hourlyChart.chartPanel);
                panel.remove(min5Chart.chartPanel);
                panel.remove(minChart.chartPanel);
                panel.remove(dailyChart.chartPanel);
            }
            case DAILY -> {
                panel.add(dailyChart.chartPanel);
                panel.remove(min5Chart.chartPanel);
                panel.remove(hourlyChart.chartPanel);
                panel.remove(minChart.chartPanel);
            }
        }
        panel.revalidate();
        menuList.setText(historyEnum.toString());
    }
}
