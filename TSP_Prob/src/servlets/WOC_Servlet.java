package servlets;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Servlet implementation class WOC_Servlet
 */
@WebServlet("/WOC_Servlet")
public class WOC_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static double[] coordx;
	public static double[] coordy;
	ArrayList<ArrayList<Integer>> Tours = new ArrayList<ArrayList<Integer>>();
	double dMatrix[][];
	int[][] agreementMatrix;
	int wocFlag; //denote in or out of the node, 1 means in, 2 means out
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WOC_Servlet() {
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
    
    public int getMax(int nextNode, int cityNum)
    {
    	int max=-1;
    	int node=0;
    	for(int i=0; i<cityNum; i++)
    	{
    		if(agreementMatrix[nextNode][i]>max)
    		{
    			max=agreementMatrix[nextNode][i];
    			node = i;
    		}
    	}
    	//After getting the edge, mark -1 in the matrix
    	for(int i=0; i<cityNum; i++)
    	{
    		agreementMatrix[nextNode][i]=-1;
    		agreementMatrix[i][nextNode]=-1;
    	}
    	return node;
    }
    
    public int getMax_Trial(ArrayList<Integer> wbestPath, int cityNum, int nextNode)
    {
    	int preNode=wbestPath.get(0);
    	int preMax=-1;
    	int previous=0;
    	int nextMax=-1;
    	int next=0;
    	for(int i=0; i<cityNum; i++)
    	{
    		if(agreementMatrix[i][preNode]>preMax)
    		{
    			preMax=agreementMatrix[i][preNode];
    			previous=i;
    		}
    		if(agreementMatrix[nextNode][i]>nextMax)
    		{
    			nextMax=agreementMatrix[nextNode][i];
    			next=i;
    		}
    	}
    	if(nextMax>=preMax)
    	{
    		for(int i=0; i<cityNum; i++)
    		{
    			agreementMatrix[nextNode][i]=-1;
    			agreementMatrix[i][next]=-1;
    		}
    		wocFlag=2;
    		return next;
    	}
    	else
    	{
    		for(int i=0; i<cityNum; i++)
    		{
    			agreementMatrix[i][preNode]=-1;
    			agreementMatrix[previous][i]=-1;
    		}
    		wocFlag=1;
    		return previous;
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String File_Name="C:\\Study\\AI\\Project5\\Random77.tsp";
		String nCity=request.getParameter("nCity");
		int cityNum=Integer.parseInt(nCity);
		String mutation_rate=request.getParameter("mut_rate");
		double rMutation=Double.parseDouble(mutation_rate);
		rMutation=rMutation/100;
		String mMutation = request.getParameter("mutation_alg");
		String population=request.getParameter("pop");
		String generation=request.getParameter("gen_size");
		int gSize=Integer.parseInt(generation);
		int pop=Integer.parseInt(population);
		double[] gen_Cost=new double[gSize];
		coordx=new double[cityNum];
		coordy=new double[cityNum];
		if(cityNum==11)
			File_Name="C:\\Study\\AI\\Project5\\Random11.tsp";
		if(cityNum==22)
			File_Name="C:\\Study\\AI\\Project5\\Random22.tsp";
		if(cityNum==44)
			File_Name="C:\\Study\\AI\\Project5\\Random44.tsp";
		if(cityNum==97)
			File_Name="C:\\Study\\AI\\Project5\\Random97.tsp";
		if(cityNum==222)
			File_Name="C:\\Study\\AI\\Project5\\Random222.tsp";
		ReadFile(File_Name);
		
		ArrayList<ArrayList<Integer>> wocPaths=new ArrayList<ArrayList<Integer>>();
		
		dMatrix=new double[cityNum][cityNum];
		dMatrix=computeDistanceMatrix(cityNum);
		//Calculate all the cost of paths in population
		double[] pathCost=new double[pop];
		ArrayList<Integer> temp=new ArrayList<Integer>();
		wocPaths.clear();
		double[] bestGenerationCost=new double[10];
		for(int ga_size=0; ga_size<10; ga_size++)
		{
			createTours(pop,cityNum);
		for(int gen_index=0; gen_index<=gSize; gen_index++)
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
			/*out.print("Generation ");
			out.print( gen_index);
			out.print(" costs:");
			out.print(tCost[0]);
			out.print("<BR>");*/
			
			//get the new generation 
			ArrayList<ArrayList<Integer>> tTours=new ArrayList<ArrayList<Integer>>();
			int index;
			boolean[] pathCostFlag=new boolean[pop]; //In case there are two distance of paths are the same
			for(int i=0; i<pop; i++)
				pathCostFlag[i]=false;
			
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
				out.print("For iteration "+ga_size);
				out.print("<BR>");
				out.print("<CENTER>");
				out.print("The best path of the last generation is: ");
				ArrayList<Integer> final_Path=new ArrayList<Integer>();
				final_Path=tTours.get(0);
				wocPaths.add(final_Path);
				
				for (int ii=0; ii<final_Path.size(); ii++)
				{
					out.print(final_Path.get(ii)+1);
					out.print(", ");
				}
				out.print("</CENTER>");
				out.print("<b>");
				out.print("This path cost is: ");
				double bc=0;
				for (int j=0; j<final_Path.size()-1; j++)
				{
					bc+=dMatrix[final_Path.get(j)][final_Path.get(j+1)];
				}
				bestGenerationCost[ga_size]=bc;
				out.print(bc);
				out.print("</b>");
				out.print("<BR>");
				out.print("<BR>");
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
		
		//Do the wisdom of crowds
		PrintWriter out=response.getWriter();
		out.print("The paths used for wisom of crowds are: ");
		out.print("<BR>");
		for(int i=0; i<wocPaths.size(); i++)
		{
			out.print((i+1)+"th path: ");
			ArrayList<Integer> twocPath=new ArrayList<Integer>();
			twocPath=wocPaths.get(i);
			for (int j=0; j<twocPath.size(); j++)
			{
				out.print(twocPath.get(j)+1);
				out.print(", ");
			}
			out.print("<BR>");
		}
		
		//Find the matrix
		agreementMatrix=new int[cityNum][cityNum];
		boolean[] visitCity=new boolean[cityNum];   //flag the city is visited or not
		for (int i=0; i<cityNum; i++)
			visitCity[i]=false;
		//Calculate the matrix that records the times pairwise nodes appears
		int numIndex;
		for (int i=0; i<cityNum; i++)
		{
			for (int j=0; j<cityNum; j++)
				agreementMatrix[i][j]=0;
		}
		for(int i=0; i<cityNum; i++)
		{
			for(int j=0; j<cityNum; j++)
			{
				numIndex=0;
				if(i==j)
					agreementMatrix[i][j]=-1;
				else
				{
					for (int k=0; k<wocPaths.size(); k++)
					{
						ArrayList<Integer> tList=wocPaths.get(k);
						int ii=0;
						while(tList.get(ii)!=i && ii<tList.size())
							ii++;
						if(ii==tList.size()-1)
							;
						else if(tList.get(ii+1)==j)
							agreementMatrix[i][j]++;
					}
				}
			}
		}
		//Check the matrix
		/*out.print("The agreement matrix is: ");
		out.print("<BR>");
		for (int i=0; i<cityNum; i++)
		{
			for (int j=0; j<cityNum; j++)
			{
				out.printf("%d",agreementMatrix[i][j]);
				out.print(", ");
			}
			out.print("<BR>");
		}*/
		//Already got the agreement matrix, find the best path according to the matrix
		//Start with the cell with the largest value in the matrix
		int preNode=0, nextNode=0;
		int maxValue=0;
		//Get the starting node and starting edge
		ArrayList<Integer> wbestPath=new ArrayList<Integer>();
		for(int i=0; i<cityNum; i++)
		{
			for(int j=0; j<cityNum; j++)
			{
				if(agreementMatrix[i][j]>maxValue)
				{
					maxValue=agreementMatrix[i][j];
					preNode=i;
					nextNode=j;
				}
			}
		}
		wbestPath.add(preNode);
		wbestPath.add(nextNode);
		//Check column preNode, and row nextNode to get the better edge
		/*for(int i=0; i<cityNum; i++)
		{
			agreementMatrix[preNode][i]=-1;
			agreementMatrix[i][nextNode]=-1;
		}
		for(int i=0; i<cityNum-2; i++)
		{
			int node=getMax(wbestPath, cityNum,nextNode);
			if(wocFlag==1)
			{
				ArrayList<Integer> tempWPath=new ArrayList<Integer>();
				tempWPath.add(node);
				for(int j=0; j<wbestPath.size(); j++)
				{
					tempWPath.add(wbestPath.get(j));
				}
				wbestPath.clear();
				wbestPath=(ArrayList<Integer>)tempWPath.clone();
			}
			else if(wocFlag==2)
			{
				wbestPath.add(node);
				nextNode=node;
			}
		}*/
		//If the node has already visited, mark all the column and row of the node to -1
		for(int i=0; i<cityNum; i++)
		{
			agreementMatrix[preNode][i]=-1;
			agreementMatrix[i][preNode]=-1;
		}
		//find the best path with the starting edge has found
		for(int i=0; i<cityNum-2; i++)
		{
			int node=getMax(nextNode,cityNum);
			wbestPath.add(node);
			nextNode=node;
		}
		
		//print out the best path
		out.print("The best path found by using wisdom of crowds is: ");
		out.print("<BR>");
		for(int i=0; i<wbestPath.size(); i++)
		{
			out.print(wbestPath.get(i)+1);
			out.print(", ");
		}
		out.print("<BR>");
		//calculate the cost of the best path found by WOC
		double bestCost=0;
		for (int j=0; j<wbestPath.size()-1; j++)
		{
			bestCost+=dMatrix[wbestPath.get(j)][wbestPath.get(j+1)];
		}
		out.print("The cost of the path found by WOC is: "+bestCost);
		out.print("<BR>");
		
		//graph the best cost overall (including GA and WOC)
		double bestGACost,worstGACost,bestOverallCost,totalGACost,averageOverallCost;
		int bestOverallPathIndex=0;   //If index is less than 10, it's the best path of GA, if it is 10, then it's the WOC path
		bestGACost=bestGenerationCost[0];
		worstGACost=bestGenerationCost[0];
		totalGACost=bestGenerationCost[0];
		for (int i=1; i<10; i++)
		{
			totalGACost+=bestGenerationCost[i];
			if(bestGenerationCost[i]>worstGACost)
				worstGACost=bestGenerationCost[i];
			if(bestGenerationCost[i]<bestGACost)
			{
				bestGACost=bestGenerationCost[i];
				bestOverallPathIndex=i;
			}
		}
		averageOverallCost=totalGACost/10;
		if(bestCost<bestGACost)
		{
			bestOverallCost=bestCost;
			bestOverallPathIndex=10;
		}
		else
			bestOverallCost=bestGACost;
		out.print("<BR>");
		out.print("<CENTER>");
		out.print("<b>");
		out.print("The best overall cost is: ");
		System.out.print("The best overall cost is: ");
		out.print(bestOverallCost);
		System.out.printf("%f", bestOverallCost);
		out.print("<BR>");
		out.print("The worst overall cost is: ");
		System.out.print("The worst overall cost is: ");
		out.print(worstGACost);
		System.out.printf("%f", worstGACost);
		System.out.println();
		out.print("<BR>");
		out.print("The average cost is: "+averageOverallCost);
		System.out.print("The average cost is: ");
		System.out.printf("%f", averageOverallCost);
		System.out.println();
		out.print("<BR>");
		out.print("The WOC path cost is: "+bestCost);
		System.out.print("The WOC path cost is: ");
		System.out.printf("%f", bestCost);
		System.out.println();
		out.print("<BR>");
		out.print("</b>");
		out.print("</CENTER>");
		
		//Plot the best path visualization
		ArrayList<Integer> bestOverallPath=new ArrayList<Integer>();
		if(bestOverallPathIndex<10)
		{
			bestOverallPath=(ArrayList<Integer>)wocPaths.get(bestOverallPathIndex).clone();
		}
		else if(bestOverallPathIndex==10)
		{
			bestOverallPath=(ArrayList<Integer>)wbestPath.clone();
		}
		 //Generate the GUI (Path Visualization)
        response.setContentType("image/png");
        XYSeriesCollection ds=new XYSeriesCollection();
        XYSeries xyseries=new XYSeries("Path Visualization",false);
        for (int i=0; i<bestOverallPath.size(); i++)
        {
        	xyseries.add(coordx[bestOverallPath.get(i)], coordy[bestOverallPath.get(i)]);
        }
        xyseries.add(coordx[bestOverallPath.get(0)], coordy[bestOverallPath.get(0)]);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
