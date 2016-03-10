package servlets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BFSDFS_Servlet
 */
@WebServlet("/BFSDFS_Servlet")
public class BFSDFS_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static double[] coordx;
	public static double[] coordy;
	double[][] AdjMatrix;
	Queue<Integer> bfsQueue = new LinkedList<Integer>();
	boolean bfsFlag[];     //flag indicates that every is visited or not
	int bfsPre[];
	ArrayList<Integer> dfsPath = new ArrayList<Integer>();
	boolean[] dfsFlag;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BFSDFS_Servlet() {
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
    
    public void setAdjMatrix(int cNum,double [][]AdjMatrix)
    {
    	for (int i=0; i<cNum; i++)
    	{
    		for (int j=0; j<cNum; j++)
    		{
    			if (i==j)
    				AdjMatrix[i][j]=0;
    			else
    				AdjMatrix[i][j]=-1;
    		}
    	}
    }
 
    //BFS algorithm
    public void BFSSearch(int srcID, int destID)
    {
    	//Prepare for loop 
    	bfsPre=new int[11];    //previously city visited before current city
    	bfsQueue.clear();      
    	bfsFlag = new boolean[11];
    	for (int i=0; i<11; i++)   //Initialize all the flag and bfsPre
    	{
    		bfsFlag[i]=false;
    		bfsPre[i]=-1;
    	}
    	//mark the starting city as visited
    	bfsQueue.offer(srcID-1);
    	bfsFlag[srcID-1]=true;
    	while(!bfsQueue.isEmpty())
    	{  
    		int current = bfsQueue.poll();
    		for (int index=0; index<11; index++)
    		{
    			if (current==index)
    				continue;
    			if(AdjMatrix[current][index]>0) //They are connected to each other
    			{
    				if(index==(destID-1))      //already found the final destination
    					
    				{
    					bfsFlag[index]=true;
    					bfsPre[index]=current;
    					return;
    				}
    				if(bfsFlag[index]==false)  //have not found destination yet
    				{
    					bfsFlag[index]=true;
    					bfsPre[index]=current;
    					bfsQueue.offer(index);
    				}
    			}
    		}
    	}
    }
    
    //DFS algorithm
    public void DFSSearch(int srcID, int destID)
    {
    	//dfsPath.clear();
    	dfsFlag=new boolean[11];
    	for (int i=0; i<11; i++)
    	{
    		dfsFlag[i]=false;
    	}
    	dfsPath.add(srcID-1);
    	dfsFlag[srcID-1]=true;
    	dfs_RevOrder(srcID-1,destID-1);
    }
//Visit cities in reverse numerical order if need to break a tie  
    public void dfs_RevOrder(int srcID, int destID)
    {
    	for (int index=10; index>=0; index--)
    	{
    		if(srcID==index)
    			continue;
    		if(AdjMatrix[srcID][index]>0)
    		{
    			if(index==destID)   //Found the destination
    			{
    				dfsFlag[index]=true;
    				dfsPath.add(index);
    				return;
    			}
    			if (dfsFlag[index]==false)
    			{
    				dfsFlag[index]=true;
    				dfsPath.add(index);
    				dfs_RevOrder(index,destID);
    				if(dfsFlag[destID]==false)   //Destination can't be found from current path
    					dfsPath.remove(index);
    				else
    					return;
    			}
    		}
    	}
    }
//visit city in numerical order if need to break a tie   
    public void dfs(int srcID, int destID)
    {
    	for (int index=0; index<11; index++)
    	{
    		if(srcID==index)
    			continue;
    		if(AdjMatrix[srcID][index]>0)
    		{
    			if(index==destID)   //Found the destination
    			{
    				dfsFlag[index]=true;
    				dfsPath.add(index);
    				return;
    			}
    			if (dfsFlag[index]==false)
    			{
    				dfsFlag[index]=true;
    				dfsPath.add(index);
    				dfs(index,destID);
    				if(dfsFlag[destID]==false)   //Destination can't be found from current path
    					dfsPath.remove(index);
    				else
    					return;
    			}
    		}
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
    			map[i][j]=calDistance(coordx[i],coordy[i],coordx[j],coordy[j]);
    	return map;
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String File_Name="C:\\Study\\AI\\Project2\\11PointDFSBFS.tsp";
		int cNum=11;
		coordx=new double[cNum];
		coordy=new double[cNum];
		ReadFile(File_Name);
		//Print the coordinates of the cities
		PrintWriter out=response.getWriter();
		out.println("The corrdinates of the cities are:");
		out.println("<BR>");
		for (int i=0;i<cNum;i++)
		{
			out.println(coordx[i]);
			out.println("  ");
			out.println(coordy[i]);
			out.println("<BR>");
		}
		out.println("<BR>");
		
		AdjMatrix=new double[cNum][cNum];
		//out.println(AdjMatrix.length);
		setAdjMatrix(cNum, AdjMatrix);
		AdjMatrix[0][1]=1;
		AdjMatrix[0][2]=1;
		AdjMatrix[0][3]=1;
		AdjMatrix[1][2]=1;
		AdjMatrix[2][3]=1;
		AdjMatrix[2][4]=1;
		AdjMatrix[3][4]=1;
		AdjMatrix[3][5]=1;
		AdjMatrix[3][6]=1;
		AdjMatrix[4][6]=1;
		AdjMatrix[4][7]=1;
		AdjMatrix[5][7]=1;
		AdjMatrix[6][8]=1;
		AdjMatrix[6][9]=1;
		AdjMatrix[7][8]=1;
		AdjMatrix[7][9]=1;
		AdjMatrix[7][10]=1;
		AdjMatrix[8][10]=1;
		AdjMatrix[9][10]=1;
		double [][] dMatrix=computeDistanceMatrix(11);

		long startTime=System.currentTimeMillis();
		//Print out the path by using BFS, calculate the transition and cost
		int transition=0;
		BFSSearch(1,11);
		int index;
		out.println("The path by using BFS is: ");
		index=10;
		int temp=index;
		double totalCost=0;
		out.println(index+1);
		out.println(" ");
		index=bfsPre[index];
		do
		{
			totalCost+=dMatrix[temp][index];
			temp=index;
			out.println("<- ");
			out.println(index+1);
			out.println(" ");
			index=bfsPre[index];
			transition++;
		}while(index!=-1);
		out.println("<BR>");
		//print out the transition
		out.println("The transitions it spent is: ");
		out.println(transition);
		out.println("<BR>");
		//print out the total cost
		out.println("The total cost of the BFS search is: ");
		out.println(totalCost);
		out.println("<BR>");
		
		long endTime=System.currentTimeMillis();
		long totalTime=endTime - startTime;
		out.println("The total time used to do BFS search is: "+totalTime+" milliseconds");
		out.println("<BR>");
		
		//DFS
		long startTimeD=System.currentTimeMillis();
		transition=0;
		totalCost=0;
		out.println("<BR>");
		//since 1 connected to 2,3,4, then we considered them separately for DFS
		//city 2 is considered
		dfsPath.add(0);
		DFSSearch(2,11);
		out.println("<b>");
		out.println("Vist the city in reverse numerical order when it's needed to break a tie");
		out.println("<BR>");
		out.println("After visiting city 1, considering city 2 in the next step");
		out.println("</b>");
		out.println("<BR>");
		out.println("The path by using DFS is: ");
		for (int i=0; i<dfsPath.size()-1; i++)
		{
			out.println(dfsPath.get(i)+1);
			out.println(" ");
			out.println("->");
			out.println(" ");
			transition++;
			totalCost+=dMatrix[dfsPath.get(i)][dfsPath.get(i+1)];
		}
		out.println(dfsPath.get(dfsPath.size()-1)+1);
		out.println("<BR>");
		//print the transition
		out.println("The transition it spent is: ");
		out.println(transition);
		out.println("<BR>");
		//output the total cost
		out.println("The total cost of DFS search is: ");
		out.println(totalCost);
		out.println("<BR>");
		
		//city 3 is considered
		out.println("<b>");
		out.println("After visiting city 1, considering city 3 in the next step");
		out.println("</b>");
		dfsPath.clear();
		dfsPath.add(0);
		DFSSearch(3,11);
		out.println("The path by using DFS is: ");
		transition=0;
		totalCost=0;
		for (int i=0; i<dfsPath.size()-1; i++)
		{
			out.println(dfsPath.get(i)+1);
			out.println(" ");
			out.println("->");
			out.println(" ");
			transition++;
			totalCost+=dMatrix[dfsPath.get(i)][dfsPath.get(i+1)];
		}
		out.println(dfsPath.get(dfsPath.size()-1)+1);
		out.println("<BR>");
		//print the transition
		out.println("The transition it spent is: ");
		out.println(transition);
		out.println("<BR>");
		//output the total cost
		out.println("The total cost of DFS search is: ");
		out.println(totalCost);
		out.println("<BR>");
		
		//City 4 is considered
		out.println("<b>");
		out.println("After visiting city 1, considering city 4 in the next step");
		out.println("</b>");
		dfsPath.clear();
		dfsPath.add(0);
		DFSSearch(4,11);
		out.println("The path by using DFS is: ");
		transition=0;
		totalCost=0;
		for (int i=0; i<dfsPath.size()-1; i++)
		{
			out.println(dfsPath.get(i)+1);
			out.println(" ");
			out.println("->");
			out.println(" ");
			transition++;
			totalCost+=dMatrix[dfsPath.get(i)][dfsPath.get(i+1)];
		}
		out.println(dfsPath.get(dfsPath.size()-1)+1);
		out.println("<BR>");
		//print the transition
		out.println("The transition it spent is: ");
		out.println(transition);
		out.println("<BR>");
		//output the total cost
		out.println("The total cost of DFS search is: ");
		out.println(totalCost);
		out.println("<BR>");
		
		//Time spent on DFS
		long endTimeD=System.currentTimeMillis();
		totalTime=endTimeD-startTimeD;
		out.println("The total time used to do DFS search is: ");
		out.println(totalTime+" milliseconds");
		out.println("<BR>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request,response);
	}

}
