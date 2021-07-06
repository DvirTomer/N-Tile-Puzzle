
public class Node {
	public int [][] st;
	public Node father;
	public int cost;
	public int manhattan;
	public boolean mark = false;
	public String way = "";   // the path from the beginning
   
    public Node(int [][] st) 
    {    
        this.st = st;    
    }
    public String toString() 
    {
    	String temp = "";
		for (int i = 0; i < this.st.length; i++) {
			for (int j = 0; j < this.st[0].length; j++) {
				
				temp += this.st[i][j]+" ";
			}
			temp += "\n";
		}
		return temp;
	}
    	
    }

 




