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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
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
import org.jfree.chart.plot.CategoryPlot;
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




/**
 * Servlet implementation class Genetic_Servlet
 */
@WebServlet("/Pro6_Servlet")
public class Pro6_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static int[] item_Length;
	public static int[] num_Required;
	public int stock_length, numItem;
	ArrayList<ArrayList<Integer>> Tours = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> wocParents=new ArrayList<ArrayList<Integer>>();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Pro6_Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void ReadFile(String FName, int prob_index)
    {
    	try
		{
    		File f=new File(FName);
    		String sIndex="Problem "+Integer.toString(prob_index)+":";
    		String nextIndex="Problem "+Integer.toString(prob_index+1)+":";
        	Scanner sc = new Scanner(f);
        	//skip the first 7 lines of the file because first 7 lines are not useful
        	String line="";
			while(!((line=sc.nextLine()).equals(sIndex)))
				;
			stock_length=0;
			line=sc.nextLine();
			while (!(line.equals(nextIndex)))  
			{
				if(line.equals("item number"))
				{
					line=sc.nextLine();
					numItem=Integer.parseInt(line);
					System.out.print("The total number of item is: ");
					System.out.printf("%d", numItem);
					System.out.println();
				}
				if(line.equals("stock length"))
				{
					line=sc.nextLine();
					stock_length=Integer.parseInt(line);
					System.out.print("The stock length is: ");
					System.out.printf("%d", stock_length);
					System.out.println();
				}
				if(line.equals("item length"))
				{
					line=sc.nextLine();
					String[] cod=line.split(" ");
				//get the item length and store them into the item_Length array
					System.out.print("The item length array is: ");
					item_Length=new int[cod.length];
					for(int i=0; i<cod.length; i++)
					{
						item_Length[i]=Integer.parseInt(cod[i]);
						System.out.printf("%d", item_Length[i]);
						System.out.print("  ");
					}
					System.out.println();
				}
				if(line.equals("No. required"))
				{
					numItem=0;
					line=sc.nextLine();
					String[] cod=line.split(" ");
					System.out.print("The number of items required are: ");
					num_Required=new int[cod.length];
					for(int i=0; i<cod.length; i++)
					{
						num_Required[i]=Integer.parseInt(cod[i]);
						System.out.printf("%d", num_Required[i]);
						System.out.print(" ");
						numItem+=num_Required[i];
					}
					System.out.println();
				}
				line=sc.nextLine();
			}
			sc.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
    }
    
    public void createTours(int pop, int chromosomeSize)
    {
    	Tours.clear();
    	for(int index=0; index<pop; index++)
    	{
    		ArrayList<Integer> tTour=new ArrayList<Integer>();
    		for (int i=0; i<item_Length.length; i++)
        	{
        		for(int j=0; j<num_Required[i]; j++)
        		{
        			tTour.add(item_Length[i]);
        		}
        	}
        	Collections.shuffle(tTour);
            Tours.add(tTour);
           /* for(int j=0; j<tTour.size(); j++)
            {
            	System.out.printf("%d", tTour.get(j));
            	System.out.print(" ");
            }
            System.out.println();*/
    	}
    }   
    
    //Mutation with swap algorithm, generate random 3 positions
    public ArrayList<ArrayList<Integer>> Mutation_Swap(int pop)
    {
    	int pos1, pos2, pos3;
    	//swap three nodes in the path
    	ArrayList<Integer> mPath=new ArrayList<Integer>();
    	ArrayList<ArrayList<Integer>> Children=new ArrayList<ArrayList<Integer>>();
    	for (int i=0; i<pop; i++)
    	{
    		//Select random index of the paths to apply the mutation
    		mPath=Tours.get(i);
    		ArrayList<Integer> temp=new ArrayList<Integer>();
    		temp=(ArrayList<Integer>)mPath.clone();
    		//Swap random two places of the path
    		/*System.out.print("The total number of Item is: ");
    		System.out.printf("%d", numItem);
    		System.out.println();*/
    		Random rand = new Random();
    		pos1=rand.nextInt(numItem);
    		rand = new Random();
    		pos2=rand.nextInt(numItem);
    		Collections.swap(temp, pos1, pos2);
    		rand=new Random();
    		pos3=rand.nextInt(numItem);
    		Collections.swap(temp, pos1, pos3);
    		Children.add(temp);
    	}
    	return Children;
    }
    
    //Mutation with reverse algorithm
    public ArrayList<ArrayList<Integer>> Mutation_Reverse(int pop)
    {
    	ArrayList<ArrayList<Integer>> Children=new ArrayList<ArrayList<Integer>>();
    	int nodeNum=(Tours.get(0)).size();
    	for (int i=0; i<pop; i++)
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
    		int endpos=nodeNum;
    		int length=0;
    		while(endpos>=nodeNum)
    		{
    			rand=new Random();
    			length=rand.nextInt(nodeNum)+1;
    			rand = new Random();
    			startpos=rand.nextInt(nodeNum-length+1);
    			endpos=startpos+length-1;
    		}
    		for (int j=0; j<(int)(length/2) && (startpos+i)<=(endpos-i); j++)
    		{
    			Collections.swap(temp,startpos+i,endpos-i);
    		}
    		Children.add(temp);
    	}
    	return Children;
    }
    
    public int getWastage(ArrayList<Integer> path)
    {
    	int totalCost, wastage;
    	totalCost=0;
    	wastage=0;
    	for (int i=0; i<path.size(); i++)
    	{
    		totalCost+=path.get(i);
    		if(i==path.size()-1 && totalCost<=stock_length)
    		{
    			wastage+=stock_length-totalCost;
    		}
    		else if(totalCost<stock_length)
    			;
			else if(totalCost==stock_length)
			{
				totalCost=0;
			}
			else
			{
				wastage+=stock_length-(totalCost-path.get(i));
				i--;
				totalCost=0;
			}
    	}
    	return wastage;
    }
    
    public ArrayList<Integer> WisdomOfCrowds()
    {
    	ArrayList<Integer> cuttingSize=new ArrayList<Integer>();
    	for(int i=0; i<item_Length.length; i++)
    	{
    		cuttingSize.add(item_Length[i]);
    	}
    	String pattern= getPattern("",cuttingSize);
    	System.out.println("The final pattern we found is: ");
    	System.out.printf("%s", pattern);
    	ArrayList<Integer> patternList=new ArrayList<Integer>();
    	ArrayList<String> list=new ArrayList<String>(Arrays.asList(pattern.split(", ")));
    	for (int i=0; i<list.size(); i++)
    	{
    		int temp;
    		temp=Integer.parseInt(list.get(i));
    		patternList.add(temp);
    	}
    	System.out.println();
    	System.out.print("The pattern length is: ");
    	System.out.printf("%d", list.size());
    	System.out.println();
    	return patternList;
    }
    
    public String getPattern(String nextPattern, ArrayList<Integer> cuttingSize)
    {
    	String temp=new String();
    	if(!nextPattern.equals(""))
    		temp=nextPattern;
    	HashMap<String,Integer> patternCount = new LinkedHashMap<String, Integer>();
    	if(nextPattern=="")
    	{
    		 for(int i:cuttingSize)
    		 {
    			 for(int m=0;m<cuttingSize.size();m++)
    			 {
    				 String pattern = String.valueOf(i)+", "+String.valueOf(cuttingSize.get(m));
    				 //System.out.println("patterns "+pattern+"\n");
    				 int count = getCount(pattern,wocParents);
    				 //System.err.println(pattern+ " with count ---------->"+count);
    				 patternCount.put(pattern, count);
    			 }
    		 }
    		// System.err.println("final "+patternCount.toString());
    		 int maxValueInMap=(Collections.max(patternCount.values()));  // This will return max value in the Hashmap
    		// System.out.println("maxValueInMap "+maxValueInMap);
    		         for (Entry<String, Integer> entry : patternCount.entrySet()) {  // Iterate through hashmap
    		             if (entry.getValue()==maxValueInMap) {
    		             nextPattern=entry.getKey();
    		                 //System.out.println(entry.getKey()); // Print the key with max value
    		                 //System.out.println("pattern with maximum votes is "+entry.getKey()+"="+entry.getValue());
    		                 break;
    		             }
    		         }
    	}
    	else{
    		//System.out.println("next patter for other than first iteration "+nextPattern);
    		patternCount = new LinkedHashMap<String, Integer>();
    		for(int m=0;m<cuttingSize.size();m++)
    		 {          
    		           String pattern = String.valueOf(nextPattern)+", "+String.valueOf(cuttingSize.get(m));
    		           //System.out.println("patterns "+pattern+"\n");
    		           int count = getCount(pattern,wocParents);
    		           //System.err.println(pattern+ " with count ---------->"+count);
    		           patternCount.put(pattern, count);
    		           }

    		//String nextPattern ="";
    		//System.err.println("final "+patternCount.toString());
    		int maxValueInMap=(Collections.max(patternCount.values()));  // This will return max value in the Hashmap
    		//System.out.println("maxValueInMap "+maxValueInMap);


    		//stop recursion method right now
    		if(maxValueInMap==0)//add logic
    		{
    		   return temp;
    		    
    		}
    		else{
    		        for (Entry<String, Integer> entry : patternCount.entrySet()) {  // Iterate through hashmap
    		            if (entry.getValue()==maxValueInMap) {
    		            nextPattern=entry.getKey();
    		               // System.out.println(entry.getKey()); // Print the key with max value
    		               // System.out.println("pattern with maximum votes is "+entry.getKey()+"="+entry.getValue());
    		                break;
    		            }
    		}}
    		} 
    		        return getPattern(nextPattern,cuttingSize);
    		 
    }
    
    public int getCount(String pattern, ArrayList<ArrayList<Integer>>bestParents)
    {
    	int count = 0;
        for(int i=0;i<bestParents.size();i++){
        //System.out.println(bestParents.get(i).toString()+" contains "+s+" ???? ");
        if(bestParents.get(i).toString().contains(pattern)){
        //System.out.println(pattern+" Present in "+bestParents.get(i));
        count++;
        }
        else{
        //System.err.println(pattern+" not Present in "+bestParents.get(i));
        }
        }

        return count;
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String prob_num=request.getParameter("prob_num");
		int prob_index=Integer.parseInt(prob_num);
		//String mCrossover=request.getParameter("ordered_crossover");
		String population=request.getParameter("pop");
		String generation=request.getParameter("gen_size");
		int gSize=Integer.parseInt(generation);
		double[] gen_Cost=new double[gSize];
		int pop=Integer.parseInt(population);
		String File_Name="C:\\Study\\AI\\Project#6_Lingyu Lyu\\pro6_data.txt";
		ReadFile(File_Name,prob_index);
		int chromosomeSize=numItem;
		
		//Calculate all the cost of paths in population
		int[] pWastage=new int[pop];
		int[] cWastage=new int[pop];
		int bestWastage, worstWastage=0,wisdomBest;
		int totalCost;
		ArrayList<Integer> temp=new ArrayList<Integer>();
		System.out.print("stock length in main: ");
		System.out.printf("%d", stock_length);
		System.out.println();
		
		PrintWriter out=response.getWriter();
		out.print("Problem"+prob_num);
		out.print("<BR>");
		out.print("Stock Length: "+stock_length);
		out.print("<BR>");
		
		for(int num_lab=0; num_lab<3; num_lab++)
		{
		createTours(pop,chromosomeSize);
		for (int gen_index=0; gen_index<=gSize; gen_index++)
		{
			for(int i=0; i<pop; i++)
			{
				pWastage[i]=0;
				totalCost=0;
				temp=Tours.get(i);
				pWastage[i]=getWastage(temp);
			}
			//Sort the population by the total distance of each path
			//int[] tWastage=wastage.clone();		
			//Arrays.sort(tWastage);
			/*if(gen_index<gSize)
				gen_Cost[gen_index]=tWastage[0];
			System.out.print("Generation ");
			System.out.printf("%d", gen_index);
			System.out.print(" wastage:");
			System.out.printf("%d", tWastage[0]);
			System.out.println();*/
			
			//get the union tours and the waste of each path
			ArrayList<ArrayList<Integer>> Children=Mutation_Swap(pop);
			//ArrayList<ArrayList<Integer>> Children = Mutation_Swap_Trial(pop);
			ArrayList<ArrayList<Integer>> unionTours=new ArrayList<ArrayList<Integer>>();
			for(int i=0; i<Tours.size(); i++)
			{
				unionTours.add(Tours.get(i));
			}
			for (int i=0; i<Children.size(); i++)
			{
				unionTours.add(Children.get(i));
				cWastage[i]=0;
				totalCost=0;
				temp=Children.get(i);
				cWastage[i]=getWastage(temp);
			}
			/*for (int i=0; i<cWastage.length; i++)
			{
				System.out.printf("%d", pWastage[i]);
				System.out.print(", ");
				System.out.printf("%d", cWastage[i]);
				System.out.println();
			}*/
			
			int[] unionWastage= new int[pop*2];
			for (int i=0; i<cWastage.length; i++)
			{
				unionWastage[i]=cWastage[i];
			}
			for (int i=0; i<cWastage.length; i++)
			{
				unionWastage[cWastage.length+i]=cWastage[i];
			}
			
			//Do the pairwise comparison
			ArrayList<ArrayList<Integer>> survive=new ArrayList<ArrayList<Integer>>();
			int oppo_Cost, indiv_Cost;
			int[] survive_waste=new int[unionTours.size()];
			for(int i=0; i<unionTours.size(); i++)
			{
				temp=unionTours.get(i);
				indiv_Cost=unionWastage[i];
				ArrayList<Integer> opponents=new ArrayList<Integer>();
				for(int j=0; j<10; j++)
				{
					Random rand=new Random();
					int randomNum=rand.nextInt(unionTours.size());
					opponents=unionTours.get(randomNum);
					oppo_Cost=unionWastage[randomNum];
					if(oppo_Cost<indiv_Cost)
					{
						temp=opponents;
						indiv_Cost=oppo_Cost;
					}
				}
				survive.add(temp);
				survive_waste[i]=indiv_Cost;
			}
			//Sort the survive cost and get the first half of the population to act as parents as next generation
			int[] tWastage=survive_waste.clone();
			Arrays.sort(tWastage);
			if(gen_index<gSize)
				gen_Cost[gen_index]=tWastage[0];
			System.out.print("Generation ");
			System.out.printf("%d", gen_index);
			System.out.print(" wastage:");
			System.out.printf("%d", tWastage[0]);
			System.out.println();
			
			//Print out in the webpage
			out.print("Generation "+gen_index+" wastage: "+tWastage[0]);
			out.print("<BR>");
			
			Tours.clear();
			int index;
			boolean[] pathWasteFlag=new boolean[survive.size()]; //In case there are two distance of paths are the same
			for (int i=0; i<survive.size(); i++)
			{
				pathWasteFlag[i]=false;
			}
			for (int current=0; current<(int)(survive.size()/2); current++)
			{
				int tWaste=survive_waste[current];
				index=0;
				for(int k=0; k<tWastage.length; k++)
				{
					if(pathWasteFlag[k]==false && tWaste==survive_waste[k])
						break;
					else
						index++;
				}
				pathWasteFlag[index]=true;
				Tours.add(survive.get(index));
			}
			if(gen_index==0)
			{
				worstWastage=tWastage[0];
			}
			if(gen_index==gSize)
			{
				bestWastage=tWastage[0];
				//worstWastage=tWastage[tWastage.length-1];
				wisdomBest=bestWastage;
				wocParents.add(Tours.get(0));
				if(num_lab==0)
				{
				out.print("<CENTER>");
				out.print("<b>");
				out.print("The best path of the last generation is: ");
				ArrayList<Integer> final_Path=new ArrayList<Integer>();
				final_Path=Tours.get(0);
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
						"Generations", 
						"Wastage",
						dataset,
						PlotOrientation.VERTICAL,
						true, 
						true,
						false
						);
				final int width=500;
				final int height=500;
				//lineChart.setBackgroundPaint(Color.WHITE);
				XYPlot cPlot=lineChart.getXYPlot();
				XYLineAndShapeRenderer rend=new XYLineAndShapeRenderer();
				cPlot.setRenderer(rend);
				rend.setSeriesPaint(0, Color.GREEN);
				//Set the thickness of the line
				//rend.setSeriesStroke(0, new BasicStroke(4.0f));
				//cPlot.setBackgroundPaint(Color.DARK_GRAY);
				//cPlot.setRangeGridlinesVisible(true);
				//cPlot.setRangeGridlinePaint(Color.BLACK);
				//cPlot.setDomainGridlinesVisible(true);
				//cPlot.setDomainGridlinePaint(Color.BLACK);
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
		        
		        //Create simple bar chart
		        response.setContentType("image/png");
		        DefaultCategoryDataset bar_dataset=new DefaultCategoryDataset();
		        bar_dataset.setValue(bestWastage, "Wastage", "Best");
		        bar_dataset.setValue(worstWastage, "Wastage", "Worst");
		        bar_dataset.setValue(wisdomBest, "Wastage", "WisdomBest");
		        JFreeChart barchart=ChartFactory.createBarChart3D("Best and worst wastage", "Generation", "Wastage", bar_dataset,PlotOrientation.VERTICAL, false, true, false);
		        barchart.setBorderPaint(Color.YELLOW);
		        barchart.getTitle().setPaint(Color.BLACK);
		        CategoryPlot p=barchart.getCategoryPlot();
		        p.setBackgroundPaint(Color.GRAY);
		        p.setRangeGridlinePaint(Color.BLUE);
		        JFrame bar_jf=new JFrame();
		        ChartPanel bChart=new ChartPanel(barchart){
		        	@Override
	                  public Dimension getPreferredSize() {
	                  return new  Dimension(width, height);
	        	}
	        	};
	        	bar_jf.add(bChart);
	        	bar_jf.pack();
	        	bar_jf.setLocationRelativeTo(null);
	        	bar_jf.setVisible(true);

		        
				}
			}
			
		
		}
	  }
		ArrayList<Integer> bPattern = WisdomOfCrowds();
		boolean foundFlag=false;
		int index=0;
		int bPosition=0;    //Position of best parent
		int currentNode;
		for(int i=0; i<wocParents.size(); i++)
		{
			if(foundFlag==true)
				break;
			ArrayList<Integer> tp =new ArrayList<Integer>();
			for(int j=0; j<tp.size(); j++)
			{
				currentNode=bPattern.get(index);
				if(tp.get(j)!=currentNode)
					;
				else if(currentNode==tp.get(j))
					index++;
			}
			if(index==bPattern.size()-1)
			{
				foundFlag=true;
				bPosition=i;
			}
		}
		System.out.println("The best path after using wisdom of crowds is: ");
		out.print("The best path after using wisdom of crowds is: ");
		for(int i=0; i<wocParents.get(bPosition).size(); i++)
		{
			//System.out.printf("%d", wocParents.get(bPosition).get(i));
			//System.out.print(", ");
			
			out.print(wocParents.get(bPosition).get(i));
			out.print(", ");
		}
		System.out.println();
		System.out.print("The wastage after applying WOC is: ");
		System.out.printf("%d", getWastage(wocParents.get(bPosition)));
		
		out.print("<BR>");
		out.print("The wastage after applying WOC is: ");
		out.print(getWastage(wocParents.get(bPosition)));
		out.print("<BR>");
		
		
	}
		

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
