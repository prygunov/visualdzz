package net.artux.visualdz;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainForm extends JFrame {
    public JPanel rootPanel;
    public JButton chooseButton;
    public JTextField xField;
    public JSlider offsetSlider;
    public JTextField yField;
    public JLabel imageFrame;
    public JPanel frame;
    public JScrollPane scrollPane;
    public JTextField factBrightnessField;
    public JTextField brightnessField;
    public JLabel Position;
    public JComboBox<String> filesBox;
    public JLabel sizeLabel;
    public JButton showButton;
    public JSpinner beginRowField;
    public JTextField factYField;
    public JPanel chartPanel;
    public JSlider leftSlider;
    public JSlider rightSlider;
    public JLabel rightValue;
    public JLabel leftValue;
    public JCheckBox lockCheckBox;
    public JPanel chartPanel1;
    public JPanel chartPanel2;

    MainForm() {
        setContentPane(rootPanel);
        setSize(900, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocation(200, 800);
        setVisible(true);
        setTitle("POOVD");


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    Map<Short, Integer> counts = new HashMap<>();
    int lastHashCode = 0;

    public void updateChart(short[] arr, int min, int max){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (Arrays.hashCode(arr)!=lastHashCode){
            lastHashCode = Arrays.hashCode(arr);
            counts = new HashMap<>();

            for (int i = 0; i < 256; i++) {
                counts.put((short)i, 0);
            }

            for (short value : arr) {
                counts.put(value, counts.get(value) + 1);
            }
        }

        for(int i = min; i<= max;i++) {
            dataset.addValue(counts.get((short)i),"", ""+i);
        }

        JFreeChart chart = createChart(dataset, false, PlotOrientation.VERTICAL);
        ChartPanel chartPanel = new ChartPanel(chart);
        this.chartPanel.removeAll();
        this.chartPanel.add(chartPanel);
        this.chartPanel.revalidate();
    }

    public void updateLineChart(JPanel rootPanel, short[] arr, int min, int max, boolean vertical){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for(int i = 0; i< arr.length;i++) {
            if (arr[i] < min)
                arr[i] = 0;
            if (arr[i] > max)
                arr[i] = 255;
            dataset.addValue(arr[i],"", ""+i);
        }
        JFreeChart chart;
        if (vertical)
            chart = createChart(dataset, true, PlotOrientation.VERTICAL);
        else
            chart = createChart(dataset, true, PlotOrientation.HORIZONTAL);

        ChartPanel chartPanel = new ChartPanel(chart);
        rootPanel.removeAll();
        rootPanel.add(chartPanel);
        rootPanel.revalidate();
    }

    private JFreeChart createChart(CategoryDataset dataset, boolean line, PlotOrientation orientation)
    {
        JFreeChart chart = ChartFactory.createBarChart(
                "",
                null,                   // x-axis label
                "",                // y-axis label
                dataset, orientation,false, false, false);
        if (line)
            chart = ChartFactory.createLineChart(
                    "",
                    null,                   // x-axis label
                    "",                // y-axis label
                    dataset, orientation,false, false, false);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.getDomainAxis().setCategoryMargin(0);
        plot.getDomainAxis().setUpperMargin(0);
        plot.getDomainAxis().setLowerMargin(0);

        if (plot.getRenderer() instanceof LineAndShapeRenderer){
            plot.getRenderer().setSeriesPaint(0, Color.black);
        }else if (plot.getRenderer() instanceof BarRenderer) {
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setDrawBarOutline(false);
            renderer.setItemMargin(0);
            renderer.setShadowVisible(false);
            renderer.setDrawBarOutline(false);
            renderer.setBarPainter(new StandardBarPainter());
            renderer.setSeriesPaint(0, Color.black);
        }



        return chart;
    }

}
