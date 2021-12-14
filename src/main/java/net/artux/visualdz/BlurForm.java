package net.artux.visualdz;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlurForm extends JFrame{
    public JSpinner spinner1;
    public JSpinner spinner2;
    public JPanel rootPanel;
    public JPanel chartPanel;

    BlurForm(){
        setContentPane(rootPanel);
        setLocationByPlatform(true);
        setTitle("Ядро смаза");

        setSize(500, 500);

        SpinnerNumberModel model1 = new SpinnerNumberModel();
        model1.setMinimum(1);
        model1.setMaximum(20);
        model1.setValue(8);

        SpinnerNumberModel model2 = new SpinnerNumberModel();
        model2.setMinimum(-5);
        model2.setMaximum(5);
        model2.setValue(4);
        spinner1.setModel(model1);
        spinner2.setModel(model2);

        spinner1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateChart(chartPanel, generateTicks());
            }
        });
        spinner2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateChart(chartPanel, generateTicks());
            }
        });
        updateChart(chartPanel, generateTicks());
    }

    List<Tick> generateTicks(){
        //
        int n = (int) spinner1.getValue();
        int b = (int) spinner2.getValue();
        float max = 1 - (float) Math.abs(b)/n;
        if (Math.abs(b)>n) max = 0;
        int stepsToMax = n;
        int maxCount = Math.abs(b);

        //

        List<Tick> ticks = new ArrayList<>();
        //заполнение массива высот
        float[] arr = new float[maxCount + stepsToMax + stepsToMax + 1];
        for (int i = 0; i < stepsToMax; i++) {
            arr[i] = i * max / stepsToMax;
        }
        for (int i = stepsToMax; i < stepsToMax + maxCount; i++) {
            arr[i] = max;
        }
        int j = 0;
        for (int i = stepsToMax + maxCount; i <= stepsToMax + stepsToMax + maxCount; i++) {
            arr[i] = max - (j * (max / stepsToMax));
            j++;
        }

        for(int i = 0; i< n;i++) {
            Tick tick = new Tick("такт " + i, arr);
            ticks.add(tick);
        }

        return ticks;
    }

    float getY(XYSeries series, double x){
        for (int i = 0; i < series.getItemCount(); i++) {
            if (almostEqual(series.getDataItem(i).getX().doubleValue(), x, 0.01)){
                return series.getDataItem(i).getY().floatValue();
            }
        }
        return 0;
    }

    public static boolean almostEqual(double a, double b, double eps){
        return Math.abs(a-b)<eps;
    }

    public void updateChart(JPanel rootPanel, List<Tick> ticks){
        var dataset = new XYSeriesCollection();

        int n = (int) spinner1.getValue();
        int b = (int) spinner2.getValue();

        double offset = (float) b / n;
        float step = (float) 1/ n;

        double rnx = 1;
        if (b < 0){
            rnx = 1 + Math.abs(offset);
        }

        for(int i = 0; i< ticks.size();i++) {
            Tick tick = ticks.get(i);

            var series1 = new XYSeries(tick.getName());
            var beginFrom = offset * i;
            for (int j = 0; j < tick.getValues().length; j++){
                series1.add(beginFrom + j*step, tick.getValues()[j]);
            }
            dataset.addSeries(series1);
        }

        var total = new XYSeries("За все такты");

        double min = ((List<XYSeries>)dataset.getSeries()).get(0).getMinX();
        double max = ((List<XYSeries>)dataset.getSeries()).get(0).getMaxX();
        for(XYSeries series : (List<XYSeries>)dataset.getSeries()){
            if (min > series.getMinX())
                min = series.getMinX();
            if (max < series.getMaxX())
                max = series.getMaxX();
        }

        for (double x = min; x <= max; x+=step) {
            total.add(x, 0);

            for(XYSeries series : (List<XYSeries>)dataset.getSeries()){
                float old = getY(total, x);
                float value = getY(series, x);
                total.update(x, (double) (old + value));
            }

            if (almostEqual(x, rnx, 0.01)){
                var series1 = new XYSeries("Rn");
                float old = getY(total, x);
                series1.add(x, 0);
                series1.add(x, old);
                dataset.addSeries(series1);
            }
        }
        dataset.addSeries(total);
        JFreeChart chart;
        chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        rootPanel.removeAll();
        rootPanel.add(chartPanel);
        rootPanel.revalidate();
    }

    private JFreeChart createChart(XYSeriesCollection dataset)
    {

        JFreeChart chart = ChartFactory.createXYLineChart(
                    "",
                    "Лучи координатной оси Y",             // x-axis label
                    "Время экспонирования [такты]",                // y-axis label
                    dataset, PlotOrientation.VERTICAL,true, false, false);
        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesShapesVisible(i, false);
        }
        return chart;
    }

}
