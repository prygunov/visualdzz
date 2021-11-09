package net.artux.visualdz;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

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
    public JFreeChart jFreeChart;

    MainForm() {
        setContentPane(rootPanel);
        setSize(900, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocation(200, 800);
        setVisible(true);
        setTitle("POOVD");

        JFreeChart chart = createChart(createDataset());
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        this.chartPanel.add(chartPanel);


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private CategoryDataset createDataset()
    {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            dataset.addValue(45, "", ""+ i);
        }
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset)
    {
        JFreeChart chart = ChartFactory.createBarChart(
                "Яркости",
                null,                   // x-axis label
                "",                // y-axis label
                dataset, PlotOrientation.VERTICAL,true, false, false);


        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        //renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0);
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);


        /*NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);

        chart.getLegend().setFrame(BlockBorder.NONE);*/

        return chart;
    }

}
