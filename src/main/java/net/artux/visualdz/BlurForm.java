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
        model1.setValue(1);

        SpinnerNumberModel model2 = new SpinnerNumberModel();
        model2.setMinimum(-5);
        model2.setMaximum(5);
        model2.setValue(1);
        spinner1.setModel(model1);
        spinner2.setModel(model2);

        spinner1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateChart(chartPanel);
            }
        });
        spinner2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateChart(chartPanel);
            }
        });
        updateChart(chartPanel);
    }

    public void updateChart(JPanel rootPanel){
        var dataset = new XYSeriesCollection();

        int n = (int) spinner1.getValue();
        int b = (int) spinner2.getValue();
        float step = (float) b /n;
        float firstBigStep = 5;
        for(int i = 0; i< n;i++) {
            var series1 = new XYSeries("такт " + (i+1));
            series1.add(step * i, 0);
            series1.add(step*i+firstBigStep, 1);
            series1.add(step*i+firstBigStep + step, 1);
            series1.add(step*i+firstBigStep + firstBigStep, 0);

            dataset.addSeries(series1);
        }
        /*var total = new XYSeries("За все такты");

        for (int x = 0; x < ; x+=step/2) {

        }
        for(XYSeries series : (List<XYSeries>)dataset.getSeries()){

        }*/

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
                    "Лучи координатной оси Y",                   // x-axis label
                    "Время экспонирования",                // y-axis label
                    dataset, PlotOrientation.VERTICAL,true, false, false);
        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);


        return chart;
    }

}
