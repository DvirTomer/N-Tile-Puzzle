import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
public class Ex1 {
	int count = 0;
	String kind = "";
	boolean time = false;
	boolean open = false;
	int [][] x = new int[0][0];
	String data = "";
	int size1 = 0;
	int size2 = 0;
	int up1 = -1;
	int down1 = -1;
	int right1 = -1;
	int left1 = -1;
	int up2 = -1;
	int down2 = -1;
	int right2 = -1;
	int left2 = -1;
	int upup = -10;
	int downdown = -10;
	int rightright = -10;
	int leftleft = -10;
	boolean two = false;
	public int [][] curr = null;
	public int [][] goal = null;
	public Queue<Node>all = new LinkedList<>();
	public Hashtable<String, Node> open_list = new Hashtable<String, Node>();
	public Hashtable<String, Node> close_list = new Hashtable<String, Node>();
	String  result = "";
	String cutoff = "";
	boolean isCutoff = false;
	int a = -1;
	int b = -1;
	int c = -1;
	int d = -1;
	Node dad = null;
	Node y  = null;
	int s = 0;
	int cost_ = 0;
	public Stack<Node>STACK = new Stack<Node>();
	/*
	 * here, the reading from input file is Happening
	 */
	public  Ex1(String str) 
	{
		try {
			File myObj = new File(str);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				data = myReader.nextLine();
				if(count == 0) // first line
				{
					kind = data; 
				}
				if(count == 1)  //second line
				{
					if(data.contains("with")) 
					{
						time = true;
					}
				}
				if(count == 2) //third line
				{
					if(!data.contains("no")) 
					{
						open = true;
					} 
				}
				if(count == 3)  //fourth line
				{
					int x = data.indexOf("x");
					String temp = "";
					String temp2 = "";
					int i = 0;
					while(x != 0) 
					{
						temp +=data.charAt(i);
						x--;
						i++;
					}
					i = data.length()-1;
					int y = data.length()-1;;
					while(y != data.indexOf("x")) 
					{
						temp2 +=data.charAt(i);
						y--;
						i--;
					} 
					size1 = Integer.parseInt(temp) ;
					size2 = Integer.parseInt(temp2) ;
					curr = new int [size1][size2];  //the size of the matrix
					goal = new int [size1][size2];
				}
				if(count == 4) //Analyze start state
				{
					int k = 0;
					while(!data.equals("Goal state:")) 
					{  
						String [] u = data.split(",");   // Analyze each line
						for(int i = 0; i < size2; i++) 
						{
							if(!u[i].equals("_")) {   //different from blank
								curr[k][i] = Integer.parseInt(u[i]);
							}
							else 
							{
								curr[k][i] =  -1;    //equals to blank
							}
						}
						k++;
						data = myReader.nextLine();
						count++;
					}
					k = 0;
					while (myReader.hasNextLine()) {
						data = myReader.nextLine();
						String [] u = data.split(",");
						for(int i = 0; i < size2; i++) 
						{
							if(!u[i].equals("_")) {
								goal[k][i] = Integer.parseInt(u[i]);
							}
							else 
							{
								goal[k][i] =  -1;
							}
						}
						k++;     	
					}
				}
				count++;
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error - can't open the file");
			e.printStackTrace();
		}
		switch (kind) {
		case "BFS":
			BFS(curr, goal);
			break;
		case "DFID":
			DFID(curr, goal);
			break;
		case "A*":
			Astar (curr, goal);
			break;
		case "IDA*":
			IDAstar (curr, goal);
			break;
		case "DFBnB":
			DFBnB (curr, goal);
			break;
		default:
			break;
		}
	}
	//Steps that can be taken
	public void check_limit (int [][]arr)   
	{
		int k = -10;
		int d = -10;
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				if(arr[i][j] == -1) 
				{
					if(k == -10) 
					{
						k = i;
						d = j;
						if(i - 1 >= 0 && arr[i-1][j] != -1)    
						{
							down1 = 4;
						}	
						if(j - 1 >= 0 && arr[i][j-1] != -1)    
						{
							right1 = 3;
						}
						if(i + 1 < arr.length && arr[i+1][j] != -1)  
						{
							up1 = 2;
						}
						if(j + 1 < arr[0].length && arr[i][j+1] != -1)  
						{
							left1 = 1;
						}
					}
					else 
					{
						if(i - 1 >= 0 && arr[i-1][j] != -1)    
						{
							down2 = 8;
						}	
						if(j - 1 >= 0 && arr[i][j-1] != -1)     
						{
							right2 = 7;
						}
						if(i + 1 < arr.length && arr[i+1][j] != -1) 
						{
							up2 = 6;
						}
						if(j + 1 < arr[0].length && arr[i][j+1] != -1)  
						{
							left2 = 5;
						}
					}
				}
			}	
		}
		if(two == true)  //it means that there are two blank together
		{
			if(left1 != -1 && left2 != -1) leftleft = -3;
			if(up1 != -1 && up2 != -1) upup = -2;
			if(right1 != -1 && right2 != -1) rightright = -1;
			if(down1 != -1 && down2 != -1) downdown = 0;
		}
	}
	public void BFS (int [][]curr, int [][]goal) 
	{
		long start_time =  System.currentTimeMillis();//start time
		dad = new Node(curr);
		dad.way = "";   //path
		all.add(dad);	//add root condition 
		int s = 1;
		while(!all.isEmpty()) {
			dad = all.poll();  //each time, Node dad = to the top of the queue
			open_list.put(dad.toString(), dad); //add to the open list - key = the string of the matrix, value = Node
			find_blanck();
			check_limit(dad.st); //check movement 
			int num = -3; //-3 = first option,  8 = last option
			int temp = -1;//-3=left1, up1, right1, down1, left2, up2, right2, down2, upup, downdown, rightright 8=leftleft
			while(num <= 8)  //running until the last option          
			{
				temp = move(num, temp);//do the movement that Legal to do
				if(temp != -1 && temp != -10) {//when temp is different from the difultive value - there is a Legal step to do 
					if(close_list.get(y.toString())==null && open_list.get(y.toString())==null) 
					{
						if(y.st.length>0 && is_goal(y.st)==true) { //////checking if look like goal array
							s++;
							y.way = y.way.substring(0, y.way.length()-1);//remove the last char '-'
							long end_time =  System.currentTimeMillis();//end of time
							try {
								FileWriter myWriter = new FileWriter("output.txt");
								myWriter.write(y.way+"\n");
								myWriter.write("Num: "+s+"\n");
								myWriter.write("Cost: "+y.cost);
								if(time) {
									myWriter.write("\n"+"time: "+(end_time-start_time)/ 1000F);
								}
								myWriter.close();
								System.out.println("Successfully wrote to the file.");//the output file Successfully wrote
							} catch (IOException e) {
								System.out.println("An error occurred.");
								e.printStackTrace();
							}
							return;
						}
						s++;//Calculation of vertices
						open_list.put(y.toString(), y);//when we are dealing with sun Node, put him in open list 
						all.add(y);
					}
				}
				num++;//for the while loop
			}
			if(open) {//in case that we need to print the matrixes 
				System.out.println(dad.toString());
			}
			open_list.remove(dad.toString());//after we  deal with sun Node, remove him from the open list
			close_list.put(dad.toString(), dad);
		}	
	}
	public boolean is_goal (int[][]arr) {//function that checking if we got the goal matrix
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				if(arr[i][j]!=goal[i][j]) return false;//if one is different, stop checking and return false
			}
		}
		return true;//otherwise, return true
	}
	public int[] [] swap (int temp, int[][]helper, int a, int b) {//swap function in case that there is one blank (with legal steps after checking)
		int matrix[][]= new int[curr.length][curr[0].length];
		for (int i = 0; i < helper.length; i++) {
			for (int j = 0; j < helper[0].length; j++) {
				matrix[i][j] = helper[i][j];
			}
		}
		int temp2 = 0;
		if(temp == 1) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a][b+1];
			matrix[a][b+1] = temp2;
		}
		else if(temp == 2) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a+1][b];
			matrix[a+1][b] = temp2;
		}
		else if(temp == 3) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a][b-1];
			matrix[a][b-1] = temp2;	
		}
		else if(temp == 4) 
		{	
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a-1][b];
			matrix[a-1][b] = temp2;
		}
		else if(temp == 5) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a][b+1];
			matrix[a][b+1] = temp2;
		}
		else if(temp == 6) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a+1][b];
			matrix[a+1][b] = temp2;
		}
		else if(temp == 7) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a][b-1];
			matrix[a][b-1] = temp2;	
		}
		else if(temp == 8) 
		{	
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a-1][b];
			matrix[a-1][b] = temp2;
		}
		return matrix;//after swap
	}
	private int[][] swap2(int temp, int[][] helper, int a, int b, int c, int d) {//swap function in case that there are two blanks (with legal steps after checking)
		int matrix[][]= new int[curr.length][curr[0].length];
		for (int i = 0; i < helper.length; i++) {
			for (int j = 0; j < helper[0].length; j++) {
				matrix[i][j] = helper[i][j];
			}
		}
		int temp2 = 0; 
		int temp3 = 0;
		if(temp == -3) 
		{
			temp2 = matrix[a][b];
			temp3 = matrix[c][d];
			matrix[a][b] = matrix[a][b+1];
			matrix[a][b+1] = temp2;
			matrix[c][d] = matrix[c][d+1];
			matrix[c][d+1] = temp3;
		}
		else if(temp == -2) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a+1][b];
			matrix[a+1][b] = temp2;
			temp3 = matrix[c][d];
			matrix[c][d] = matrix[c+1][d];
			matrix[c+1][d] = temp3;
		}
		else if(temp == -1) 
		{
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a][b-1];
			matrix[a][b-1] = temp2;	
			temp3 = matrix[c][d];
			matrix[c][d] = matrix[c][d-1];
			matrix[c][d-1] = temp3;	
		}
		else if(temp == 0) 
		{	
			temp2 = matrix[a][b];
			matrix[a][b] = matrix[a-1][b];
			matrix[a-1][b] = temp2;
			temp3 = matrix[c][d];
			matrix[c][d] = matrix[c-1][d];
			matrix[c-1][d] = temp3;
		}
		return matrix;//after swap
	}
	public void DFID(int[][] curr2, int[][] goal2) {
		double start_time =  System.currentTimeMillis();//start time
		s = 1;//vertex 
		int limit = 1;
		result = "fail";
		while(result.equals("fail") || result.equals("cutoff") ) 
		{
			Hashtable<String, Node> hash = new Hashtable<String, Node>();
			Node start = new Node(curr);
			result = Limited_DFS(start, goal, limit,  hash); //return the path 
			if(!result.equals("fail") && !result.equals("cutoff")) {
				try {
					FileWriter myWriter = new FileWriter("output.txt");
					myWriter.write(result+"\n");
					myWriter.write("Num: "+s+"\n");
					myWriter.write("Cost: "+cost_);
					if(time) {
						double end_time =  System.currentTimeMillis();//end time
						myWriter.write("\n"+(double )(end_time-start_time)/ 1000.0+ " seconds");
					}
					myWriter.close();
					System.out.println("Successfully wrote to the file.");
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				return;
			}
			limit++;
		}
	}
	public String Limited_DFS(Node start, int[][] goal2, int limit, Hashtable<String, Node> hash ) {
		if (is_goal(start.st)) {//checking if it's matrix goal
			cost_ = start.cost;
			if(open)System.out.println(start.toString());
			return start.way.substring(0, start.way.length()-1);//remove the last char '-'
		}
		else if (limit == 0) {
			return "fail";//does not Exists 
		}
		else 
		{
			hash.put(start.toString(), start);////when we are dealing with sun Node, put him in open list 
			isCutoff = false;
			int a = -1;
			int b = -1;
			int c = -1;
			int d = -1;
			two = false;
			for (int i = 0; i < start.st.length; i++) {
				for (int j = 0; j < start.st[0].length; j++) {
					if(start.st[i][j] == -1) 
					{
						if(a==-1) {
							a = i;
							b = j;
						}
						else
						{
							c = i;
							d = j;	
							if((i - 1 >= 0 && start.st[i-1][j]==-1)|| (j - 1 >= 0 && start.st[i][j-1]==-1) 
									||(i + 1 < start.st.length && start.st[i+1][j]==-1)|| (j + 1 < start.st[0].length && start.st[i][j+1]==-1)) {
								two = true;//in case that there are two blanks together
							}
						}
					}
				}
			}
			int num = -3; //-3 = first option,  8 = last option
			int temp = -1;//-3=left1, up1, right1, down1, left2, up2, right2, down2, upup, downdown, rightright 8=leftleft
			while(num <= 8)  //running until the last option
			{
				//default values 
				left1 = -1;
				up1 = -1;
				right1 = -1;
				down1 = -1;
				left2 = -1;
				up2 = -1;
				right2 = -1;
				down2 = -1;
				upup = -10;
				downdown = -10;
				rightright = -10;
				leftleft = -10;
				check_limit(start.st);
				///checking legal movement
				if(num == -3) {temp = leftleft;}
				else if(num == -2) {temp = upup;}
				else if(num == -1) {temp = rightright;}
				else if(num == 0) {temp = downdown;}
				else if(num == 1) {temp = left1;}
				else if(num == 2) {temp = up1;}
				else if(num == 3) {temp = right1;}
				else if(num == 4) {temp = down1;}
				else if(num == 5) {temp = left2;}
				else if(num == 6) {temp = up2;}
				else if(num == 7) {temp = right2;}
				else if(num == 8) {temp = down2;}
				if(temp != -1 && temp != -10) {
					Node y = new Node(x);
					if(temp > 0 && temp <5) 
					{
						y = new Node(swap(temp,start.st,a,b));
						y.father = start;	
						y.cost += 5;
						if(y.father!=null) {
							switch(temp) {
							case 1:
								y.way += y.father.way+y.st[a][b] + "L-";
								break;
							case 2:
								y.way += y.father.way+y.st[a][b] + "U-";
								break;
							case 3:
								y.way += y.father.way+ y.st[a][b] + "R-";
								break;
							case 4:
								y.way += y.father.way+y.st[a][b] + "D-";
								break;
							default:
							}
						}
					}
					else if(temp > 4 && c !=-1) {
						y = new Node(swap(temp,start.st,c,d));
						y.father = start;	
						y.cost += 5;
						if(y.father!=null) {
							switch(temp) {
							case 5:
								y.way += y.father.way+y.st[c][d] + "L-";
								break;
							case 6:
								y.way += y.father.way+y.st[c][d] + "U-";
								break;
							case 7:
								y.way += y.father.way+y.st[c][d] + "R-";
								break;
							case 8:
								y.way += y.father.way+y.st[c][d] + "D-";
								break;

							default:
							}
						}
					}
					else if(two && temp <= 0 && c !=-1) {
						y = new Node(swap2(temp,start.st,a,b,c,d));
						y.father = start;	
						if(temp == rightright || temp == leftleft) 
						{
							y.cost += 6;
							if(y.father!=null) {

								switch(temp) {
								case -3:
									y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "L-";
									break;
								case -1:
									y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "R-";
									break;
								}
							}
						}
						else 
						{
							y.cost += 7;
							if(y.father!=null) {
								switch(temp) 
								{
								case -2:
									y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "U-";
									break;
								case 0:
									y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "D-";
									break;
								}
							}
						}
					}
					y.cost+=y.father.cost;
					if(hash.get(y.toString())!=null) 
					{
						num++;//for while loop
						continue;
					}
					result = Limited_DFS(y, goal, limit-1, hash);
					if(result == "cutoff") 
					{
						isCutoff = true;
					}
					else if (!result.equals("fail"))  {    
						return result;
					}
				}
				num++;//for while loop
			}
			s++;//vertex
			if(open)System.out.println(start.toString());
			hash.remove(start.toString());
			if(isCutoff == true) 
			{
				return "cutoff";
			}
			else
			{
				return "fail";
			}
		}
	}
	public void Astar (int [][]curr3, int [][]goal3) {
		double start_time =  System.currentTimeMillis();//start time
		int s = 1;//vertex 
		PriorityQueue<Node> PQ = new  PriorityQueue<Node>(new Node_Comparator());//using in Node_Comparator that created for sorting 
		Hashtable<String, Node> open = new Hashtable<String, Node>();//open list
		Hashtable<String, Node> close = new Hashtable<String, Node>();//close list
		dad = new Node(curr);
		PQ.add(dad);
		while(!PQ.isEmpty())     
		{
			dad =  PQ.poll();
			if(is_goal(dad.st)) 
			{
				try {
					FileWriter myWriter = new FileWriter("output.txt");
					dad.way = dad.way.substring(0, dad.way.length()-1);
					myWriter.write(dad.way+"\n");
					myWriter.write("Num: "+s+"\n");
					myWriter.write("Cost: "+dad.cost);
					if(time) {
						double end_time =  System.currentTimeMillis();
						myWriter.write("\n"+(double )(end_time-start_time)/ 1000.0+ " seconds");
					}
					myWriter.close();
					System.out.println("Successfully wrote to the file.");
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
				return;
			}
			close.put(dad.toString(), dad);
			open.remove(dad.toString());
			s++;
			find_blanck();
			check_limit(dad.st);
			int num = -3; //-3 = first option,  8 = last option
			int temp = -1;//-3=left1, up1, right1, down1, left2, up2, right2, down2, upup, downdown, rightright 8=leftleft
			while(num <= 8)  //running until the last option          
			{
				temp = move(num, temp);
				if(temp != -1 && temp != -10) {//in case that there is legal move to do
					y.manhattan = (int) (y.cost + manhattan_algorithm(y.st)*2.95);
					if(close.get(y.toString())==null && open.get(y.toString())==null) {
						PQ.add(y);
						open.put(y.toString(), y);
					}
					else if (open.get(y.toString())!=null && (y.manhattan< open.get(y.toString()).manhattan))
					{
						PQ.remove(open.get(y.toString()));
						PQ.add(y);
						open.put(y.toString(),y);
					}
				}
				num++;//for while loop
			}
		}
	}
	public void IDAstar (int [][]curr3, int [][]goal3) {
		double start_time =  System.currentTimeMillis();
		int s = 1;
		Stack<Node> L = new Stack<Node>();
		Hashtable<String, Node> H = new Hashtable<String, Node>();
		int minF = 0;
		dad = new Node(curr);
		dad.manhattan = manhattan_algorithm(curr) + dad.cost;//manhattan_algorithm
		int t = dad.manhattan;
		while(t!=Integer.MAX_VALUE) 
		{
			minF = Integer.MAX_VALUE; 
			L.add(dad);
			H.put(dad.toString(), dad);
			while(!L.isEmpty()) 
			{
				dad = L.pop();
				if(dad.mark == true) 
				{
					H.remove(dad.toString());// in case that the node is marked
				}
				else 
				{
					dad.mark = true; // mark the node
					L.add(dad);
					H.put(dad.toString(), dad);
					find_blanck();
					check_limit(dad.st);
					int num = -3; //-3 = first option,  8 = last option
					int temp = -1;//-3=left1, up1, right1, down1, left2, up2, right2, down2, upup, downdown, rightright 8=leftleft
					while(num <= 8)  //running until the last option
					{
						if(num == -3) {temp = leftleft;}
						else if(num == -2) {temp = upup;}
						else if(num == -1) {temp = rightright;}
						else if(num == 0) {temp = downdown;}
						else if(num == 1) {temp = left1;}
						else if(num == 2) {temp = up1;}
						else if(num == 3) {temp = right1;}
						else if(num == 4) {temp = down1;}
						else if(num == 5) {temp = left2;}
						else if(num == 6) {temp = up2;}
						else if(num == 7) {temp = right2;}
						else if(num == 8) {temp = down2;}
						if(temp != -1 && temp != -10) {
							Node y = new Node(x);
							if(temp > 0 && temp <5) 
							{
								y = new Node(swap(temp,dad.st,a,b));//there is one blank
								y.father = dad;	
								y.cost += 5;
								if(y.father!=null) {
									switch(temp) {
									case 1:
										y.way += y.father.way+y.st[a][b] + "L-";
										break;
									case 2:
										y.way += y.father.way+y.st[a][b] + "U-";
										break;
									case 3:
										y.way += y.father.way+ y.st[a][b] + "R-";
										break;
									case 4:
										y.way += y.father.way+y.st[a][b] + "D-";
										break;
									default:
									}
								}
							}
							else if(temp > 4 && c !=-1) {
								y = new Node(swap(temp,dad.st,c,d));//there is one blank
								y.father = dad;	
								y.cost += 5;
								if(y.father!=null) {

									switch(temp) {
									case 5:
										y.way += y.father.way+y.st[c][d] + "L-";
										break;
									case 6:
										y.way += y.father.way+y.st[c][d] + "U-";
										break;
									case 7:
										y.way += y.father.way+y.st[c][d] + "R-";
										break;
									case 8:
										y.way += y.father.way+y.st[c][d] + "D-";
										break;
									default:
									}
								}
							}
							else if(two && temp <= 0 && c !=-1) {
								y = new Node(swap2(temp,dad.st,a,b,c,d));//there are two blank
								y.father = dad;	
								if(temp == rightright || temp == leftleft) 
								{
									y.cost += 6;
									if(y.father!=null) {
										switch(temp) {
										case -3:
											y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "L-";
											break;
										case -1:
											y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "R-";
											break;
										}
									}
								}
								else 
								{
									y.cost += 7;
									if(y.father!=null) {
										switch(temp) 
										{
										case -2:
											y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "U-";
											break;
										case 0:
											y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "D-";
											break;
										}
									}
								}
							}
							y.cost+=y.father.cost; 
							y.manhattan = y.cost + manhattan_algorithm(y.st);//manhattan_algorithm - include the cost
							if(y.manhattan > t) {
								minF = Math.min(minF, y.manhattan);
								num ++;
								continue;
							}
							if(H.get(y.toString())!=null && H.get(y.toString()).mark) {
								num ++;//for while loop
								continue;//in this case, move on to the next option
							}
							if(H.get(y.toString())!=null && !H.get(y.toString()).mark) {
								if((H.get(y.toString()).manhattan) >   y.manhattan) {
									L.remove(H.get(y.toString()));
									H.remove(y.toString());
								}
								else 
								{
									num ++;//for while loop
									continue;//in this case, move on to the next option
								}
							}
							if(is_goal(y.st)) //checking if look like goal matrix
							{
								try {
									FileWriter myWriter = new FileWriter("output.txt");
									y.way = y.way.substring(0, y.way.length()-1);
									myWriter.write(y.way+"\n");
									myWriter.write("Num: "+s+"\n");
									myWriter.write("Cost: "+y.cost);
									if(time) {
										double end_time =  System.currentTimeMillis();
										myWriter.write("\n"+(double )(end_time-start_time)/ 1000.0+ " seconds");
									}
									myWriter.close();
									System.out.println("Successfully wrote to the file.");
								} catch (IOException e) {
									System.out.println("An error occurred.");
									e.printStackTrace();
								}					
								return;
							}
							s++;//vertex
							L.add(y);
							H.put(y.toString(), y);
						}
						num ++;//for while loop
					}
				}
			}
			dad.mark = false;//default mark - false
			t = minF;
		}
	}
	public void DFBnB(int[][] curr2, int[][] goal2) {
		double start_time = System.currentTimeMillis();//start time
		Stack<Node> L = new Stack<Node>();
		Hashtable<String, Node> H = new Hashtable<String, Node>();
		int kod = 1;
		int t = Integer.MAX_VALUE;
		String result = null;
		dad = new Node(curr);
		L.add(dad);
		H.put(dad.toString(), dad);
		int co = 0;
		while(!L.isEmpty()) {
			dad = L.pop();
			if(dad.mark) {
				H.remove(dad.toString());//in case that the node marked
			}
			else {
				dad.mark = true;//mark the node
				L.add(dad);
				kod++;
				PriorityQueue<Node> P = new PriorityQueue<Node>(new Node_Comparator());
				find_blanck();
				check_limit(dad.st);
				int num = -3; //-3 = first option,  8 = last option
				int temp = -1;//-3=left1, up1, right1, down1, left2, up2, right2, down2, upup, downdown, rightright 8=leftleft
				while(num <= 8)  //running until the last option
				{
					temp = move(num, temp);
					if(temp != -1 && temp != -10) {
						y.manhattan = (int) (y.cost + manhattan_algorithm(y.st)*2.95);//using in manhattan_algorithm
						P.add(y);
					}
					num ++;//for while loop
				}
				Iterator<Node> iter = P.iterator();
				while (iter.hasNext()) {
					Node child = iter.next();
					if(child.manhattan >= t) {
						while(!P.isEmpty()) 
						{
							if(!P.peek().toString().equals(child.toString())) 
							{
								STACK.add(P.poll());
							}
							else {
								P.clear();
							}
						}
						while(!STACK.isEmpty()) 
						{
							P.add(STACK.pop());
						}
						iter = P.iterator();
					}
					else if(H.get(child.toString())!=null && H.get(child.toString()).mark) {
						P.remove(child);
						iter = P.iterator();
					}
					else if(H.get(child.toString())!=null && !H.get(child.toString()).mark) {
						if((H.get(child.toString()).manhattan) <=   child.manhattan) {
							P.remove(child);
							iter = P.iterator();
						}
						else 
						{
							L.remove(H.get(child.toString()));
							H.remove(child.toString());
						}
					}
					else if(is_goal(child.st)) 
					{
						t = child.manhattan;
						result = child.way;
						co = child.cost;
						while(!P.isEmpty()) 
						{
							if(!P.peek().toString().equals(child.toString())) 
							{
								STACK.add(P.poll());
							}
							else {
								P.clear();
							}
						}
						while(!STACK.isEmpty()) 
						{
							P.add(STACK.pop());//stack for adding to the base stack in the good order 
						}
						iter = P.iterator();
					}					
				}
				Stack<Node> temp_q = new Stack<Node>();
				while(!P.isEmpty()) 
				{
					temp_q.add(P.poll());
				}
				while(!temp_q.empty()) 
				{
					Node uNode = temp_q.pop();
					L.add(uNode);
					H.put(uNode.toString(), uNode);
				}
			}	
		}
		try {
			FileWriter myWriter = new FileWriter("output.txt");
			result = result.substring(0, result.length()-1);
			myWriter.write(result+"\n");
			myWriter.write("Num: "+kod+"\n");
			myWriter.write("Cost: "+co);
			if(time) {
				double end_time =  System.currentTimeMillis();
				myWriter.write("\n"+(double )(end_time-start_time)/ 1000.0+ " seconds");
			}
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}		
	}
	/*
	 * Calculate the amount of steps that need to be taken to reach the goal matrix
	 */
	int manhattan_algorithm(int[][]arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				for (int i2 = 0; i2 < goal.length; i2++) {
					for (int j2 = 0; j2 < goal[0].length; j2++) {
						if(arr[i][j] == goal[i2][j2]) 
						{
							sum += Math.abs(i - i2) + Math.abs(j - j2);
						}
					}
				}
			}
		}
		return sum;
	}
	class Node_Comparator implements Comparator<Node>{//the Comparator was created for sorting by minimal cost
		public int compare(Node s1, Node s2) {
			if (s1.manhattan > s2.manhattan) 
			{
				return 1;

			}else if(s1.manhattan < s2.manhattan)
			{ 
				return -1;
			}return 0;
		}
	}
	public void find_blanck() {//find the location of the blanks
		left1 = -1;
		up1 = -1;
		right1 = -1;
		down1 = -1;
		left2 = -1;
		up2 = -1;
		right2 = -1;
		down2 = -1;
		upup = -10;
		downdown = -10;
		rightright = -10;
		leftleft = -10;
		a = -1;
		b = -1;
		c = -1;
		d = -1;
		two = false;
		for (int i = 0; i < dad.st.length; i++) {
			for (int j = 0; j < dad.st[0].length; j++) {
				if(dad.st[i][j] == -1) 
				{
					if(a==-1) {
						a = i;
						b = j;
					}else
					{
						c = i;
						d = j;	
						if((i - 1 >= 0 && dad.st[i-1][j]==-1)|| (j - 1 >= 0 && dad.st[i][j-1]==-1) 
								||(i + 1 < dad.st.length && dad.st[i+1][j]==-1)|| (j + 1 < dad.st[0].length && dad.st[i][j+1]==-1)) {
							two = true;//in case the two blanks are together
						}
					}
				}
			}
		}
	}
	public int move(int num, int temp){///check legal movement
		if(num == -3) {temp = leftleft;}
		else if(num == -2) {temp = upup;}
		else if(num == -1) {temp = rightright;}
		else if(num == 0) {temp = downdown;}
		else if(num == 1) {temp = left1;}
		else if(num == 2) {temp = up1;}
		else if(num == 3) {temp = right1;}
		else if(num == 4) {temp = down1;}
		else if(num == 5) {temp = left2;}
		else if(num == 6) {temp = up2;}
		else if(num == 7) {temp = right2;}
		else if(num == 8) {temp = down2;}
		if(temp != -1 && temp != -10) {
			y = new Node(x);
			if(temp > 0 && temp <5)         
			{
				y = new Node(swap(temp,dad.st,a,b));//one blank
				y.father = dad;	
				y.cost += 5;
				y.cost+=y.father.cost;
				if(y.father!=null) {
					switch(temp) {
					case 1:
						y.way += y.father.way+y.st[a][b] + "L-";
						break;
					case 2:
						y.way += y.father.way+y.st[a][b] + "U-";
						break;
					case 3:
						y.way += y.father.way+ y.st[a][b] + "R-";
						break;
					case 4:
						y.way += y.father.way+y.st[a][b] + "D-";
						break;
					default:
					}
				}
			}
			else if(temp > 4 && c !=-1) {//one blank
				y = new Node(swap(temp,dad.st,c,d));
				y.father = dad;	
				y.cost += 5;
				y.cost+=y.father.cost;
				if(y.father!=null) {

					switch(temp) {
					case 5:
						y.way += y.father.way+y.st[c][d] + "L-";
						break;
					case 6:
						y.way += y.father.way+y.st[c][d] + "U-";
						break;
					case 7:
						y.way += y.father.way+y.st[c][d] + "R-";
						break;
					case 8:
						y.way += y.father.way+y.st[c][d] + "D-";
						break;
					default:
					}
				}
			}
			else if(two && temp <= 0 && c !=-1) {//two blanks
				y = new Node(swap2(temp,dad.st,a,b,c,d));
				y.father = dad;	
				if(temp == rightright || temp == leftleft)  
				{
					y.cost += 6;
					y.cost+=y.father.cost;
					if(y.father!=null) {
						switch(temp) {
						case -3:
							y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "L-";
							break;
						case -1:
							y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "R-";
							break;
						}
					}
				}
				else 
				{
					y.cost += 7;
					y.cost+=y.father.cost;
					if(y.father!=null) {

						switch(temp) 
						{
						case -2:
							y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "U-";
							break;
						case 0:
							y.way += y.father.way+y.st[a][b] + "&" +y.st[c][d] + "D-";
							break;
						}
					}
				}
			}}
		return temp;
	}
	public static void main(String[] args) {
		Ex1 cFile =  new Ex1("input.txt");
	}
}