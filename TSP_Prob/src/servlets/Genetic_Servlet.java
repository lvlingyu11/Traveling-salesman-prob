package servlets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;




/**
 * Servlet implementation class Genetic_Servlet
 */
@WebServlet("/Genetic_Servlet")
public class Genetic_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static double[] coordx;
	public static double[] coordy;
	ArrayList<ArrayList<Integer>> Tours = new ArrayList<ArrayList<Integer>>();
	double dMatrix[][];
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Genetic_Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
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
    
    public void createTours(int pop, int cityNum)
    {
    	Tours.clear();
    	for(int index=0; index<pop; index++)
    	{
    		ArrayList<Integer> tTour=new ArrayList<Integer>();
    		for (int i=0; i<cityNum; i++)
        	{
        		tTour.add(i);
        	}
        	Collections.shuffle(tTour);
            Tours.add(tTour);
            /*for(int j=0; j<100; j++)
            {
            	System.out.printf("%d", tTour.get(j));
            	System.out.print(" ");
            }
            System.out.println();*/
    	}
    }
    
    public double calDistance(double codx1, double cody1, double codx2, double cody2)
    {
    	double xs = Math.pow(codx1-codx2, 2);
    	double ys = Math.pow(cody1-cody2, 2);
    	double dis = Math.sqrt(xs+ys);
    	return dis;
    }
    
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
    
    //Crossover
    public ArrayList<ArrayList<Integer>> CrossOver(ArrayList<ArrayList<Integer>> tTours)
    {
    	ArrayList<ArrayList<Integer>> Children=new ArrayList<ArrayList<Integer>>();
    	//Randomly get parents to crossover
    	for (int index=0; index<tTours.size()/2; index++)
    	{
    		ArrayList<Integer> Parent1=new ArrayList<Integer>();
        	ArrayList<Integer> Parent2=new ArrayList<Integer>();
    		Random rand=new Random();
    		int randomNum=rand.nextInt(tTours.size());
    		Parent1=tTours.get(randomNum);
    		/*System.out.print("Parent1 size: ");
    		System.out.printf("%d", Parent1.size());
    		System.out.println();*/
    		rand=new Random();
    		randomNum=rand.nextInt(tTours.size());
    		Parent2=tTours.get(randomNum);
    		//crossover
    		rand=new Random();
    		int size=Parent1.size();
    		randomNum=rand.nextInt(size);
    		ArrayList<Integer> Child1=new ArrayList<Integer>();
        	ArrayList<Integer> Child2=new ArrayList<Integer>();
    		for(int i=0; i<randomNum; i++)
    		{
    			Child1.add(Parent1.get(i));
    			Child2.add(Parent2.get(i));
    		}
    		for(int j=0; j<Parent2.size(); j++)
    		{
    			if(!containsCity(Child1,Parent2.get(j)))
    				Child1.add(Parent2.get(j));
    			if(!containsCity(Child2, Parent1.get(j)))
    				Child2.add(Parent1.get(j));
    		}
    		//Add child1 and child2 to Children
    		Children.add(Child1);
    		Children.add(Child2);
    	}
    	return Children;
    }
    
    //Crossover trial
    public ArrayList<ArrayList<Integer>> CrossOver_Trial(ArrayList<ArrayList<Integer>> tTours)
    {
    	ArrayList<ArrayList<Integer>> Children=new ArrayList<ArrayList<Integer>>();
    	//Randomly get parents to crossover
    	for (int index=0; index<tTours.size()/2; index++)
    	{
    		ArrayList<Integer> Parent1=new ArrayList<Integer>();
        	ArrayList<Integer> Parent2=new ArrayList<Integer>();
    		Random rand=new Random();
    		int randomNum=rand.nextInt(tTours.size());
    		Parent1=tTours.get(randomNum);
    		/*.out.print("Parent1 size: ");
    		System.out.printf("%d", Parent1.size());
    		System.out.println();*/
    		rand=new Random();
    		randomNum=rand.nextInt(tTours.size());
    		Parent2=tTours.get(randomNum);
    		//crossover
    		rand=new Random();
    		int size=Parent1.size();
    		randomNum=rand.nextInt(size);  //length is randomNum
    		int length=randomNum;
    		ArrayList<Integer> Child1=new ArrayList<Integer>();
        	ArrayList<Integer> Child2=new ArrayList<Integer>();
        	rand = new Random();
        	int startPos=rand.nextInt(Parent1.size());
        	int endPos=startPos+length-1;
        	while(endPos>=size)
        	{
        		rand=new Random();
        		startPos=rand.nextInt(Parent1.size());
        		rand = new Random();
        		length=rand.nextInt(size);
        		endPos=startPos+randomNum-1;
        	}
    		for(int i=startPos; i<=endPos; i++)
    		{
    			Child1.add(Parent1.get(i));
    			Child2.add(Parent2.get(i));
    		}
    		for (int j=0; j<Parent2.size(); j++)
    		{
    			while(!containsCity(Child1,Parent2.get(j)))
    				Child1.add(Parent2.get(j));
    			while(!containsCity(Child2, Parent1.get(j)))
    				Child2.add(Parent1.get(j));
    		}
    		//Add child1 and child2 to Children
    		Children.add(Child1);
    		Children.add(Child2);
    	}
    	return Children;
    }
    
    
    //containsCity
    public boolean containsCity(ArrayList<Integer> tour, int currentCity)
    {
    	return tour.contains(currentCity);
    }
    
    //Mutation with swap algorithm
    public void Mutation_Swap(double mut_rate, int pop)
    {
    	int mutNum=(int)(mut_rate*pop);
    	//swap two city nodes in the path
    	int cityNum=(Tours.get(0)).size();
    	ArrayList<Integer> mPath=new ArrayList<Integer>();
    	for (int i=0; i<mutNum; i++)
    	{
    		//Select random index of the paths to apply the mutation
    		Random rand=new Random();
    		int index=rand.nextInt(pop);
    		mPath=Tours.get(index);
    		//Swap random two places of the path
    		rand = new Random();
    		int pos1=rand.nextInt(cityNum);
    		rand = new Random();
    		int pos2=rand.nextInt(cityNum);
    		Collections.swap(mPath, pos1, pos2);
    	}
    }
    
    //Mutation with reverse algorithm
    public void Mutation_Reverse(double mut_rate, int pop)
    {
    	int mutNum=(int)(mut_rate*pop);
    	int cityNum=(Tours.get(0)).size();
    	for (int i=0; i<mutNum; i++)
    	{
    		//Select a random path
    		Random rand = new Random();
    		int index = rand.nextInt(pop);
    		ArrayList<Integer> mPath=new ArrayList<Integer>();
        	ArrayList<Integer> temp=new ArrayList<Integer>();
    		mPath=Tours.get(index);
    		/*System.out.print(" Size of mPath:");
    		System.out.printf("%d", mPath.size());
    		System.out.println();*/
    		temp=(ArrayList<Integer>) mPath.clone();
    		/*System.out.print(" Size of temp:");
    		System.out.printf("%d", temp.size());
    		System.out.println();*/
    		//Select a random sequence of a random length and reverse the order if it can improve the performance
    		int startpos=0;
    		int endpos=cityNum;
    		int length=0;
    		while(endpos>=cityNum)
    		{
    			rand=new Random();
    			length=rand.nextInt(cityNum)+1;
    			rand = new Random();
    			startpos=rand.nextInt(cityNum-length+1);
    			endpos=startpos+length-1;
    		}
    		for (int j=0; j<(int)(length/2) && (startpos+i)<=(endpos-i); j++)
    		{
    			Collections.swap(temp,startpos+i,endpos-i);
    		}
    		double mpCost=0;
    		double tpCost=0;
    		for(int k=0; k<cityNum-1; k++)
    		{
    			mpCost+=dMatrix[mPath.get(k)][mPath.get(k+1)];
    			tpCost+=dMatrix[temp.get(k)][temp.get(k+1)];
    		}
    		if(tpCost<mpCost)
    		{
    			Tours.remove(index);
    			Tours.add(temp);
    		}
    	}
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String File_Name="C:\\Study\\AI\\Project4\\Random100.tsp";
		int cityNum=100;
		String nCity=request.getParameter("nCity");
		int numOfCity=Integer.parseInt(nCity);
		/*String Select_rate=request.getParameter("sel_rate");
		double rSelect=Double.parseDouble(Select_rate);
		rSelect=rSelect/100;*/
		String mutation_rate=request.getParameter("mut_rate");
		double rMutation=Double.parseDouble(mutation_rate);
		rMutation=rMutation/100;
		//String mCrossover=request.getParameter("ordered_crossover");
		String mMutation=request.getParameter("mutation_alg");
		String population=request.getParameter("pop");
		String generation=request.getParameter("gen_size");
		int gSize=Integer.parseInt(generation);
		double[] gen_Cost=new double[gSize];
		int pop=Integer.parseInt(population);
		coordx=new double[cityNum];
		coordy=new double[cityNum];
		ReadFile(File_Name);
		cityNum=numOfCity;
		createTours(pop,cityNum);
		//Test
		/*System.out.print("Size of Tours: ");
		System.out.printf("%d",Tours.size());
		System.out.println();
		System.out.print("Size of one path: ");
		System.out.printf("%d", Tours.get(0).size());
		System.out.println();*/
		
		dMatrix=new double[cityNum][cityNum];
		dMatrix=computeDistanceMatrix(cityNum);
		//Calculate all the cost of paths in population
		double[] pathCost=new double[pop];
		ArrayList<Integer> temp=new ArrayList<Integer>();
		for (int gen_index=0; gen_index<=gSize; gen_index++)
		{
			for(int i=0; i<pop; i++)
			{
				pathCost[i]=0;
				temp=Tours.get(i);
				for (int j=0; j<temp.size()-1; j++)
				{
					pathCost[i]+=dMatrix[temp.get(j)][temp.get(j+1)];
				}
			}
			//Sort the population by the total distance of each path
			double[] tCost=pathCost.clone();		
			Arrays.sort(tCost);
			if(gen_index<gSize)
				gen_Cost[gen_index]=tCost[0];
			System.out.print("Generation ");
			System.out.printf("%d", gen_index);
			System.out.print(" costs:");
			System.out.printf("%f", tCost[0]);
			System.out.println();
			
			PrintWriter out=response.getWriter();
			out.print("Generation ");
			out.print( gen_index);
			out.print(" costs:");
			out.print(tCost[0]);
			out.print("<BR>");
			
			//get the new generation
			ArrayList<ArrayList<Integer>> tTours=new ArrayList<ArrayList<Integer>>();
			int index;
			boolean[] pathCostFlag=new boolean[pop]; //In case there are two distance of paths are the same
			for (int i=0; i<pop; i++)
			{
				pathCostFlag[i]=false;
			}
			//retrieve the half of the population with the smaller distance than another half of the population
			for (int k=0; k<(int)(pop/2); k++) 
			{
				double tDistance=tCost[k];
				index=0;
				//Find the index of the best half population paths 
				for(int i=0; i<pathCost.length; i++)
				{
					if(pathCost[i]==tDistance && pathCostFlag[i]==false)
						break;
					else
						index++;
				}
				pathCostFlag[index]=true;
				tTours.add(Tours.get(index));
			}
			
			//Print out the best path in the last generation, that means the final path we found
			if(gen_index==gSize)
			{
				out.print("<CENTER>");
				out.print("<b>");
				out.print("The best path of the last generation is: ");
				ArrayList<Integer> final_Path=new ArrayList<Integer>();
				final_Path=tTours.get(0);
				for (int ii=0; ii<final_Path.size(); ii++)
				{
					out.print(final_Path.get(ii)+1);
					out.print(", ");
				}
				out.print("</b>");
				out.print("</CENTER>");
				out.println();
				
				//Plot the GUI for the final result	
				//Generate the improvement curve
				response.setContentType("image/png");
				XYSeriesCollection dataset=new XYSeriesCollection();
				XYSeries series=new XYSeries("Improvement Curve");
				for(int i=0; i<gen_Cost.length; i++)
				{
					series.add(i, gen_Cost[i]);
				}
				dataset.addSeries(series);
				JFreeChart lineChart=ChartFactory.createXYLineChart(
						"Improvement Curve", 
						"Generation", 
						"Cost",
						dataset,
						PlotOrientation.VERTICAL,
						true, 
						true,
						false
						);
				int width=640;
				int height=480;
				//lineChart.setBackgroundPaint(Color.WHITE);
				XYPlot cPlot=lineChart.getXYPlot();
				XYLineAndShapeRenderer rend=new XYLineAndShapeRenderer();
				cPlot.setRenderer(rend);
				rend.setSeriesPaint(0, Color.RED);
				//Set the thickness of the line
				//rend.setSeriesStroke(0, new BasicStroke(4.0f));
				cPlot.setBackgroundPaint(Color.DARK_GRAY);
				cPlot.setRangeGridlinesVisible(true);
				cPlot.setRangeGridlinePaint(Color.BLACK);
				cPlot.setDomainGridlinesVisible(true);
				cPlot.setDomainGridlinePaint(Color.BLACK);
				JFrame jf=new JFrame();
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    	ChartPanel pChart = new ChartPanel(lineChart) {

		            @Override
		            public Dimension getPreferredSize() {
		                return new Dimension(400, 320);
		            }
		        };
		        jf.add(pChart);
		        jf.pack();
		        jf.setLocationRelativeTo(null);
		        jf.setVisible(true);
		        
		        //Generate the GUI (Path Visualization)
		        response.setContentType("image/png");
		        XYSeriesCollection ds=new XYSeriesCollection();
		        XYSeries xyseries=new XYSeries("Path Visualization",false);
		        for (int i=0; i<final_Path.size(); i++)
		        {
		        	xyseries.add(coordx[final_Path.get(i)], coordy[final_Path.get(i)]);
		        }
		        xyseries.add(coordx[final_Path.get(0)], coordy[final_Path.get(0)]);
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
		        ChartPanel panel=new ChartPanel(chart){
		        	 @Override
			            public Dimension getPreferredSize() {
			                return new Dimension(400, 320);
		        	 }
		        };
		        frame.add(panel);
		        frame.pack();
		        frame.setLocationRelativeTo(null);
		        frame.setVisible(true);
			}
			
			//tTours include the half of the population paths, do crossover with them
			Tours.clear();
			ArrayList<ArrayList<Integer>> Children=CrossOver_Trial(tTours);
			for(int i=0; i<tTours.size(); i++)
			{
				Tours.add(tTours.get(i));
			}
			for (int ii=0; ii<Children.size(); ii++)
			{
				Tours.add(Children.get(ii));
			}
			if(mMutation.equals("Swap"))
			{
				Mutation_Swap(rMutation,pop);
			}
			else
				Mutation_Reverse(rMutation,pop);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
