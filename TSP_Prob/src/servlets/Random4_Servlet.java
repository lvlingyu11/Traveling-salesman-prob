package servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Random4_Servlet
 */
@WebServlet("/Random4_Servlet")
public class Random4_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static double[] coordx;
	public static double[] coordy;
//	public static double[] distance;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Random4_Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    //red the file into the workspace
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
    			map[i][j]=calDistance(coordx[i],coordy[i],coordx[j],coordy[j]);
    	return map;
    }
    
    //Get the cost of the given path by using the distance matrix, the path is stored as an integer array
    public double getPathCost(Integer[] path)
    {
    	if(path.length<2)
    		return 999;
    	int i;
    	double pathCost=0;
    	double[][] dMatrix=computeDistanceMatrix(path.length);
    	for (i=0;i<path.length-1;i++)
    	{
    		pathCost+=dMatrix[path[i]][path[i+1]];
    	}
    	pathCost+=dMatrix[path[path.length-1]][path[0]];  //the cost go back to the starting point
    	return pathCost;
    }
    
    //Get the cost of the given path, but the path is stored as an arraylist
    public double getPathListCost(ArrayList<Integer> PathList)
    {
    	if(PathList.size()<2)
    		return 999;
    	int i;
    	double pathListCost=0;
    	double[][] dMatrix=computeDistanceMatrix(PathList.size());
    	for(i=0; i<PathList.size()-1; i++)
    	{
    		pathListCost+=dMatrix[PathList.get(i)][PathList.get(i+1)];
    	}
    	pathListCost+=dMatrix[PathList.get(PathList.size()-1)][PathList.get(0)];
    	return pathListCost;
    }
    
    //Convert an integer array into an ArrayList
    public ArrayList<Integer> convertArrayToList(Integer[] num)
    {
    	ArrayList<Integer> item=new ArrayList<Integer>();
    	for(int h=0;h<num.length;h++)
    		item.add(num[h]);
    	return item;
    }
    
    //Convert an ArrayList to an integer array
    public Integer[] convertListToArray(ArrayList<Integer> num)
    {
    	Integer[] item=new Integer[num.size()];
    	for(int h=0; h<num.size();h++)
    		item[h]=num.get(h);
    	return item;
    }
    
    //Swap two elements inside an integer array
    public void swap(Integer[] num, int i, int j)
    {
    	int temp=num[i];
    	num[i]=num[j];
    	num[j]=temp;
    }
    
    //An recursive function, get the permutation of an integer array
    public void Permute(Integer[] num, int start, ArrayList<ArrayList<Integer>> result)
    {
    	//Final condition of stop the recursive call
    	if(start>=num.length)
    	{
    		ArrayList<Integer> item=convertArrayToList(num);
    		result.add(item);
    	}
    	else
    	{
    		for(int j=start; j<=num.length-1; j++)
    		{
    			swap(num,start,j);
    			Permute(num,start+1,result);
    			swap(num,start,j);
    		}
    	}
    }
    
    //Get the permutation by calling Permute function
    public ArrayList<ArrayList<Integer>> Permutation(Integer[] num)
    {
    	ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
    	Permute(num,0,result);
    	/*for(ArrayList<Integer>each:result)
		{
			for(int j=0; j<each.size(); j++)
			{
					System.out.printf("%d ", each.get(j));
			}
			System.out.println();
		}*/
    	return result;
    }
    
    //Used to print the path we get, and the path is stored as an Integer array
    public void printPath(Integer[] path)
    {
    	if(path==null)
    		System.out.println("Path Not Exists");
    	else
    	{
    		for(int i=0; i<path.length; i++)
    			System.out.printf("%3d, ",path[i]+1);
    		System.out.printf("%3d, ",path[0]+1);
    		System.out.println("cost="+getPathCost(path));
    	}
    }
    
    //Get the minimum cost path list, and return it back to the calling function
    public ArrayList<ArrayList<Integer>> TSPBruteForce(int iterationLimit, int cnum)
    {
    	int iterationCount=1;  //Start with 1 because 0 is used as the starting path already
    	ArrayList<ArrayList<Integer>> bestPathList=new ArrayList<ArrayList<Integer>>();
    	//ArrayList<Integer> bestPath=new ArrayList<Integer> ();
    	Integer[] path=new Integer[cnum];
    	Integer[] bestPath=new Integer[cnum];
    	for(int i=0; i<cnum; i++)
    		path[i]=i;
    	double tmpPathCost;
    	double minPathCost=getPathCost(path);
//    	double maxPathCost=0;
 //   	double[][] dMatrix=computeDistanceMatrix(cnum);
    	bestPath=path.clone();
   // 	printPath(path);
    	ArrayList<Integer> pathList=convertArrayToList(path);
    	bestPathList.add(pathList);
    	ArrayList<ArrayList<Integer>> TSPPermutation=Permutation(path);
    	while((pathList=(ArrayList<Integer>)TSPPermutation.get(iterationCount))!=null && iterationCount<iterationLimit)
    	{
    		path=convertListToArray(pathList);
    		printPath(path);
    		if(path[0]!=0)
    			break;
    		tmpPathCost=getPathCost(path);
    		if(tmpPathCost<minPathCost)
    		{
    			minPathCost=tmpPathCost;
    			bestPath=path.clone();
    			//printPath(path);
    			bestPathList.clear();
    			ArrayList<Integer> bestPathL=convertArrayToList(bestPath);
    			bestPathList.add(bestPathL);
    		}
    		else if(tmpPathCost==minPathCost)
    		{
    			bestPath=path.clone();
    			//printPath(path);
    			ArrayList<Integer> bestPathL=convertArrayToList(bestPath);
    			bestPathList.add(bestPathL);
    		}
    		iterationCount++;
    	}
    	return bestPathList;
    }
    
    //Calculate the number of permutations of a given number of cities
    public int calPermutationProduct(int num)
    {
    	int result=1;
    	for(int i=1;i<=num;i++)
    		result*=i;
    	return result;
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//String url="/Welcome.jsp";
		long startTime=System.currentTimeMillis();
		String File_Name="C:\\Study\\AI\\Project 1\\Random4.tsp";
		String cityNumStr=request.getParameter("num_city");  //Get the number of cities according the selection of user
		int cityNum=Integer.parseInt(cityNumStr);
		coordx=new double[cityNum];
		coordy=new double[cityNum];
		if(cityNum==5)
			File_Name="C:\\Study\\AI\\Project 1\\Random5.tsp";
		else if(cityNum==6)
			File_Name="C:\\Study\\AI\\Project 1\\Random6.tsp";
		else if(cityNum==7)
			File_Name="C:\\Study\\AI\\Project 1\\Random7.tsp";
		else if(cityNum==8)
			File_Name="C:\\Study\\AI\\Project 1\\Random8.tsp";
		else if(cityNum==9)
			File_Name="C:\\Study\\AI\\Project 1\\Random9.tsp";
		else if(cityNum==10)
			File_Name="C:\\Study\\AI\\Project 1\\Random10.tsp";
		else if(cityNum==11)
			File_Name="C:\\Study\\AI\\Project 1\\Random11.tsp";
		else if(cityNum==12)
			File_Name="C:\\Study\\AI\\Project 1\\Random12.tsp";
		ReadFile(File_Name);
		PrintWriter out=response.getWriter();
		out.println("The corrdinates of the cities are:");
		out.println("<BR>");
		for (int i=0;i<cityNum;i++)
		{
			out.println(coordx[i]);
			out.println("  ");
			out.println(coordy[i]);
			out.println("<BR>");
		}
		//BruteForce(4);
		int iterationLimit=calPermutationProduct(cityNum);
		//System.out.printf("%d", iterationLimit);
		//System.out.println();
		ArrayList<ArrayList<Integer>> bestPathList=TSPBruteForce(iterationLimit,cityNum);
		for(int i=0;i<bestPathList.size();i++)
		{
			ArrayList<Integer> bestPath=bestPathList.get(i);
			//Integer[] path=convertListToArray(bestPath);
			if(bestPath==null)
			{
				out.println("<CENTER>");
				out.println("<b>");
				out.println("Path Not Exists!");
				out.println("</b>");
				out.println("<BR>");
			}
			else
			{
				out.println("<CENTER>");
				out.println("<b>");
				out.println("The Shortest Path is: ");
				//for(int j=0; j<path.length; j++)
				for (int j=0; j<bestPath.size(); j++)
					out.printf("%3d,  ", bestPath.get(j)+1);
				out.println(bestPath.get(0)+1);
				out.println("<BR>");
				out.println("The Minimum cost is: "+getPathListCost(bestPath));
				out.println("</b>");
				out.println("<BR>");
				out.println("</CENTER>");
			}
			//printPath(path);
		}
		long endTime=System.currentTimeMillis();
		long totalTime=endTime-startTime;
		out.println("<CENTER>");
		out.println("Total time used for calculating the min cost path is: ");
		out.println(totalTime+" milliseconds");
		out.println("</CENTER>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
