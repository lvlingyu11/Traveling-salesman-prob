package servlets;

import java.util.List;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.File;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.axis.NumberAxis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;

/**
 * Servlet implementation class Greedy_Servlet
 */
@WebServlet("/Greedy_Servlet")
public class Greedy_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static double[] coordx;
	public static double[] coordy;
	boolean greedyFlag[];
    ArrayList<Integer> greedyPath=new ArrayList<Integer>();  
    double[][] dMatrix;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Greedy_Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }
  
    //Read File
    public void ReadFile(String FName)
    {
    	try
		{
    		File f=new File(FName);
        	Scanner sc = new Scanner(f);
        	//skip the first 7 lines of the file because first 7 lines are not useful
			for (int i=0;i<7;i++)
				sc.nextLine();
			int count=0;
			while (sc.hasNextLine())  
			{
				String line = sc.nextLine();  //read lines
				String[] cod=line.split(" ");
				//get the coordinates and store x coordiantes into coordx and y into coordy
				coordx[count]=Double.parseDouble(cod[1]);
				coordy[count]=Double.parseDouble(cod[2]);
				System.out.printf("%f", coordx[count]);
				System.out.print("  ");
				System.out.printf("%f", coordy[count]);
				System.out.println();
				count++;
			}
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
    }
    
    //Given two points with coordinates, calculate the distance between them
    public double calDistance(double codx1, double cody1, double codx2, double cody2)
    {
    	double xs = Math.pow(codx1-codx2, 2);
    	double ys = Math.pow(cody1-cody2, 2);
    	double dis = Math.sqrt(xs+ys);
    	return dis;
    }
    
    //Compute the distance matrix of the cities 
    public double[][] computeDistanceMatrix(int cnum)
    {
    	double[][] map=new double[cnum][cnum];
    	for (int i=0;i<cnum;i++)
    		for (int j=0;j<cnum;j++)
    		{
    			if(i>j)
    				map[i][j]=map[j][i];
    			else
    				map[i][j]=calDistance(coordx[i],coordy[i],coordx[j],coordy[j]);
    		}
    	return map;
    }
    
    //Greedy Algorithm
    public void Greedy(int srcID, int cityNum)
    {
    	greedyFlag=new boolean[cityNum];
    	for (int i=0; i<cityNum; i++)
    	{
    		greedyFlag[i]=false;
    	}
    	greedyFlag[srcID-1]=true;      //mark the starting city as visited
    	greedyPath.add(srcID-1);
    	int minIndex=0;
    	int current=srcID-1;
    	double minDistance=9999;
    	int count=0;
    	while(count<cityNum-1)
    	{
    		for(int j=0; j<cityNum; j++)
    		{
    			if(current==j)
    				continue;
    			//City is unvisited and the distance is smaller than the minDistance
    			if(greedyFlag[j]==false && dMatrix[current][j]<=minDistance)   
    			{
    				minIndex=j;
    				minDistance=dMatrix[current][j];
    			}
    		}
    		current=minIndex;
    		greedyPath.add(minIndex);
    		greedyFlag[minIndex]=true;
    		minDistance=9999;
    		count++;
    	}
    }
    
    private static class LabelGenerator implements XYItemLabelGenerator {

        @Override
        public String generateLabel(XYDataset dataset, int series, int item) {
            LabeledXYDataset labelSource = (LabeledXYDataset) dataset;
            return labelSource.getLabel(series, item);
        }

    }

    //Used for creating the graphical user interface
    private static class LabeledXYDataset extends AbstractXYDataset {

        private static final int N = 40;
        private List<Number> x = new ArrayList<Number>(N);
        private List<Number> y = new ArrayList<Number>(N);
        private List<String> label = new ArrayList<String>(N);

        public void add(double x, double y, String label){
            this.x.add(x);
            this.y.add(y);
            this.label.add(label);
        }

        public String getLabel(int series, int item) {
            return label.get(item);
        }

        @Override
        public int getSeriesCount() {
            return 1;
        }

        @Override
        public Comparable getSeriesKey(int series) {
            return "Unit";
        }

        @Override
        public int getItemCount(int series) {
            return label.size();
        }

        @Override
        public Number getX(int series, int item) {
            return x.get(item);
        }

        @Override
        public Number getY(int series, int item) {
            return y.get(item);
        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String File_Name="C:\\Study\\AI\\Project3\\Random30.tsp";
		String str_ncity=request.getParameter("num_city");
		int cityNum=Integer.parseInt(str_ncity);
		coordx=new double[cityNum];
		coordy=new double[cityNum];
		if (cityNum==40)
			File_Name="C:\\Study\\AI\\Project3\\Random40.tsp";
		ReadFile(File_Name);
		dMatrix=new double[cityNum][cityNum];
		dMatrix=computeDistanceMatrix(cityNum);
		long startTime=System.currentTimeMillis();
		greedyPath.clear();
		Greedy(1,cityNum);
		
		PrintWriter out=response.getWriter();
		out.print("The corrdinates of the cities are:");
		out.print("<BR>");
		for (int i=0;i<cityNum;i++)
		{
			out.print(coordx[i]);
			out.print("  ");
			out.print(coordy[i]);
			out.print("<BR>");
		}
		//Print the path found by using greedy search
		out.print("<BR>");
		out.print("The path found by greedy search is: ");
		out.print("<BR>");
		double totalCost=0;
		for (int i=0; i<greedyPath.size()-1; i++)
		{
			out.println(greedyPath.get(i)+1);
			out.println(", ");
			totalCost+=dMatrix[greedyPath.get(i)][greedyPath.get(i+1)];
		}
		out.print(greedyPath.get(greedyPath.size()-1)+1);
		out.print(", 1");
		totalCost+=dMatrix[greedyPath.get(greedyPath.size()-1)][0];
		out.print("<BR>");
		out.print("The total cost of the distance is: "+totalCost);
		out.print("<BR>");
		long endTime=System.currentTimeMillis();
		long totalTime=endTime-startTime;
		out.print("The total time used to greedy search of "+cityNum+" cities cost ");
		out.print(totalTime+" milliseconds");
		
		//Plot the GUI 
		//response.setContentType("image/png");	
		/*LabeledXYDataset ds= new LabeledXYDataset();
    	int currentCity;
    	for (int i=0; i<greedyPath.size()-1; i++)
    	{
    		currentCity=greedyPath.get(i);
    		String label=String.valueOf(currentCity+1);
    		ds.add(coordx[i], coordy[i], label);
    	}
    	ds.add(coordx[0], coordy[0], "1");
    	NumberAxis domain=new NumberAxis("X");
    	NumberAxis range=new NumberAxis("Y");
    	domain.setAutoRangeIncludesZero(false);
    	XYItemRenderer renderer=new XYLineAndShapeRenderer(true,false);
    	renderer.setBaseItemLabelGenerator(new LabelGenerator());
    	renderer.setBaseItemLabelPaint(Color.green.darker());
    	renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER,TextAnchor.CENTER));
    	renderer.setBaseItemLabelFont(renderer.getBaseItemLabelFont().deriveFont(14f));
    	renderer.setBaseItemLabelsVisible(true);
    	renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
    	XYPlot plot=new XYPlot(ds, domain, range, renderer);
    	JFreeChart chart=new JFreeChart("XYLineChart", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
    	JFrame f=new JFrame();
    	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	ChartPanel chartPanel = new ChartPanel(chart) {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 320);
            }
        };
        f.add(chartPanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);*/
		
	    response.setContentType("image/png");
        XYSeriesCollection ds=new XYSeriesCollection();
        XYSeries xyseries=new XYSeries("Path Visualization",false);
        for (int i=0; i<greedyPath.size(); i++)
        {
        	xyseries.add(coordx[greedyPath.get(i)], coordy[greedyPath.get(i)]);
        }
        xyseries.add(coordx[greedyPath.get(0)], coordy[greedyPath.get(0)]);
        ds.addSeries(xyseries);
        JFreeChart chart=ChartFactory.createXYLineChart(
        		"Path Visualization", 
        		"X", 
        		"Y",
        		ds,
        		PlotOrientation.VERTICAL,
        		true,
        		true, 
        		false);
        XYPlot xyplot=chart.getXYPlot();
        XYLineAndShapeRenderer renderer=new XYLineAndShapeRenderer();
        xyplot.setRenderer(renderer);
        JFrame frame=new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChartPanel panel=new ChartPanel(chart);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}

}
