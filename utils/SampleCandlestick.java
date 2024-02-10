package TestJavaClient.utils;

import com.ib.client.Bar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
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

    SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    DefaultHighLowDataset dataset;

    public ChartPanel chartPanel;


    public SampleCandlestick(final String title, List<Bar> barList) {
        drawCandleSticks(barList);
        final JFreeChart chart = createChart(dataset);
        chart.setTitle(title);
        chart.getXYPlot().setOrientation(PlotOrientation.VERTICAL);
        XYPlot plot = chart.getXYPlot();
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(min.get(), max.get());
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(700, 400));
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

    public void drawCandleSticks(List<Bar> barList){

         dataset = new DefaultHighLowDataset("ES FUT", barList.stream().map(bar -> {
            StringBuilder date = new StringBuilder(bar.time());
            if (date.length() > 18){
            date.substring(0, 17);
            } else if (date.length() == 8){
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
//        chart = new SampleCandlestick("Candlestick Demo", dataset, min.get(), max.get());
//        chart.pack();
//        RefineryUtilities.centerFrameOnScreen(chart);
//        chart.setVisible(true);
    }
}
