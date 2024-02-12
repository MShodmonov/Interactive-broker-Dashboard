package TestJavaClient.utils;

import com.ib.client.Bar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SampleCandlestick {

    public Comparable seriesKey;

    Date[] date;
    double[] high;
    double[] low;
    double[] open;
    double[] close;
    double[] volume;

    AtomicReference<Double> max = new AtomicReference<>(0D);
    AtomicReference<Double> min = new AtomicReference<>(10000D);
    AtomicReference<Double> average = new AtomicReference<>(0D);

    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    DefaultHighLowDataset dataset;

    public ChartPanel chartPanel;


    public SampleCandlestick(final String title, List<Bar> barList, HistoryEnum historyEnum) {
        drawCandleSticks(barList);
        final JFreeChart chart = createChart(dataset);
        chart.setTitle(title);
        chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
        XYPlot plot = chart.getXYPlot();
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(false);
//        if (historyEnum.equals(HistoryEnum.MIN) || historyEnum.equals(HistoryEnum.MIN5)) {
//            rangeAxis.setRange(min.get(), min.get() + (max.get() - min.get()) / 30);
//        } else
            rangeAxis.setRange(min.get(), max.get());
        ValueAxis domainAxis = plot.getDomainAxis();
        if (historyEnum.equals(HistoryEnum.MIN) || historyEnum.equals(HistoryEnum.MIN5) || historyEnum.equals(HistoryEnum.HOURLY)) {
            ((DateAxis) domainAxis).setTimeline(SegmentedTimeline.newFifteenMinuteTimeline());
        }


        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMaximumDrawHeight(1000);
        chartPanel.setMaximumDrawWidth(2000);
        chartPanel.setPreferredSize(new java.awt.Dimension(2000, 1000));
        this.chartPanel = chartPanel;
    }

    private JFreeChart createChart(final DefaultHighLowDataset dataset) {

        final JFreeChart chart = ChartFactory.createCandlestickChart(
                "Stock Market ES 500 FUT",
                "Time",
                "Value",
                dataset,
                true
        );
        return chart;
    }

    public void drawCandleSticks(List<Bar> barList) {

        dataset = new DefaultHighLowDataset("ES FUT", barList.stream().map(bar -> {
            StringBuilder date = new StringBuilder(bar.time());
            if (date.length() > 18) {
                date.substring(0, 17);
            } else if (date.length() == 8) {
                date.append(" 00:00:00");
            }
            try {
                return simpleFormat.parse(date.toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).toArray(Date[]::new),
                barList.stream().mapToDouble(bar -> {
                    if (bar.high() > max.get()) {
                        max.set(bar.high());
                    }
                    average.set(average.get() + bar.high());
                    if (bar.high() < min.get()) {
                        min.set(bar.high());
                    }
                    return bar.high();
                }).toArray(),
                barList.stream().mapToDouble(bar -> {
                    if (bar.low() > max.get()) {
                        max.set(bar.low());
                    }
                    if (bar.low() < min.get()) {
                        min.set(bar.low());
                    }
                    return bar.low();
                }).toArray(),
                barList.stream().mapToDouble(bar -> {
                    if (bar.open() > max.get()) {
                        max.set(bar.open());
                    }
                    if (bar.open() < min.get()) {
                        min.set(bar.open());
                    }
                    return bar.open();
                }).toArray(),
                barList.stream().mapToDouble(bar -> {
                    if (bar.close() > max.get()) {
                        max.set(bar.close());
                    }
                    if (bar.close() < min.get()) {
                        min.set(bar.close());
                    }
                    return bar.close();
                }).toArray(),
                barList.stream().mapToDouble(bar -> bar.volume().value().doubleValue()).toArray()
        );
        average.set(average.get()/barList.size());
    }
}
