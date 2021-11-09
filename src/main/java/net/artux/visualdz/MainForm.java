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
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.Rotation;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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

            for (short value : arr) {
                if (counts.containsKey(value))
                    counts.put(value, counts.get(value) + 1);
                else counts.put(value,  1);
            }
        }

        for(int i = min; i< max;i++)
        {
            if (counts.containsKey((short)i))
                dataset.addValue(counts.get((short)i),"", ""+i);
            else
                dataset.addValue(0,"", ""+i);
        }

        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        this.chartPanel.removeAll();
        this.chartPanel.add(chartPanel);
        this.chartPanel.revalidate();
    }

    private JFreeChart createChart(CategoryDataset dataset)
    {
        JFreeChart chart = ChartFactory.createBarChart(
                "",
                null,                   // x-axis label
                "",                // y-axis label
                dataset, PlotOrientation.VERTICAL,false, false, false);


        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        plot.getDomainAxis().setCategoryMargin(0);
        plot.getDomainAxis().setUpperMargin(0);
        plot.getDomainAxis().setLowerMargin(0);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0);
        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);

        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, Color.black);

        return chart;
    }

}
