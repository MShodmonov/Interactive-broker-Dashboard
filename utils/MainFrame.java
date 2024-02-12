package TestJavaClient.utils;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public SampleCandlestick minChart;

    public SampleCandlestick min5Chart;

    public SampleCandlestick hourlyChart;

    public SampleCandlestick dailyChart;

    private JMenu menuList = null;

    private JPanel panel;

    private HistoryEnum currentEnum;

    public MainFrame(SampleCandlestick minChart) {
        this.minChart = minChart;
        setSize(new Dimension(1920, 1080));

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

        panel = new JPanel(new GridLayout(2, 2));
        getContentPane().add(panel);
        panel.add(minChart.chartPanel);
//        JButton someButton = new JButton("Connect");
//        someButton.setPreferredSize(new Dimension(20, 100));

        minItem.addActionListener(e -> menuOptionChooseAction(HistoryEnum.valueOf(e.getActionCommand())));
        min5Item.addActionListener(e -> menuOptionChooseAction(HistoryEnum.valueOf(e.getActionCommand())));
        hourlyItem.addActionListener(e -> menuOptionChooseAction(HistoryEnum.valueOf(e.getActionCommand())));
        dailyItem.addActionListener(e -> menuOptionChooseAction(HistoryEnum.valueOf(e.getActionCommand())));
//        panel.add(someButton);
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
        if (historyEnum.equals(currentEnum)) {
            menuOptionChooseAction(historyEnum);
        }
        panel.revalidate();
    }

    public void menuOptionChooseAction(HistoryEnum historyEnum) {
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
        panel.repaint();
        menuList.setText(historyEnum.toString());
        currentEnum = historyEnum;
    }

    public void setMinChart(SampleCandlestick minChart) {
        this.minChart = minChart;

    }

    public void setMin5Chart(SampleCandlestick min5Chart) {
        this.min5Chart = min5Chart;
    }

    public void setHourlyChart(SampleCandlestick hourlyChart) {
        this.hourlyChart = hourlyChart;
    }

    public void setDailyChart(SampleCandlestick dailyChart) {
        this.dailyChart = dailyChart;
    }
}
