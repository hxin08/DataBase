

//Author:Hongsen_Xin
//ID:10388303

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1. How to run the code
* Linux commands:
* javac sample.java
* java -cp .:postgresql-9.2-1003.jdbc4.jar sample
* 
* 2. Strategy
* 2.1 Generate a tuple and try whether it is in the hashmap, if it is not in it, create a new array object and input the current record information in it
* and input these key and value into the hashmap. If it is in the hashmap, update the value in array object.
* 2.2 Computer the avg value of a specific cust, prod tuple, calculate the times we want and update the array in the hashmap.
* 2.3 With the second traverse, assigns the match avg value to each quarter's before and after avg variable, also the specific condition <NULL>
* 2.4 Output the result by output the Tuple and array object information one by one.
*
* 3.Structure
* The data structure I used for this problem are a hash maps with a Tuple key and an array value.
* The tuple has two variable: customer name and product name with overrided hashCode and equals function to make sure every same pair of them
* will be treated as same and equal.
* The array will be used first store the total times, total quant and max for the first traverse and the reuse for the second round to calcualte the times we want.
*

---------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class sample2 {

	public static void main(String[] args) 
	{
		String usr ="hxin"; // postgres username
		String pwd ="Stevens122851"; // postgres password
		String url ="jdbc:postgresql://155.246.89.29:5432/hxin"; // postgres address, port number and database name
		try 
		{
			Class.forName("org.postgresql.Driver");
			System.out.println("Success loading Driver!");
		} 

		catch(Exception e)
		{
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try 
		{
			Connection conn = DriverManager.getConnection(url, usr, pwd);
			System.out.println("Success connecting server!");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");
			HashMap<Tuple, int[]> cusprod = new HashMap<Tuple, int[]>();
			System.out.println("CUSTOMER PRODUCT QUARTER BEFORE_AVG AFTER_AVG");
			System.out.println("======== ======= ======= ========== ========="); 
			while (rs.next()) 
			{
				Tuple tuple = new Tuple(rs.getString("cust"),rs.getString("prod"));	// create a new object with customer name and product name
				int month = Integer.parseInt(rs.getString("month"));
				if(cusprod.containsKey(tuple)){    // compare it with current tuples in the HashMap to see whether this combination has appeared before
					int[] initialcpTotal = cusprod.get(tuple); //Initialize array
					if(month>0 && month<=3){
						initialcpTotal[0] = cusprod.get(tuple)[0] + Integer.parseInt(rs.getString("quant"));//update quant
						initialcpTotal[1] = cusprod.get(tuple)[1] + 1;//update times
					}else if(month>3&& month<=6){
						initialcpTotal[2] = cusprod.get(tuple)[2] + Integer.parseInt(rs.getString("quant"));//update quant
						initialcpTotal[3] = cusprod.get(tuple)[3] + 1;//update times
					}else if(month>6&&month<=9){
						initialcpTotal[4] = cusprod.get(tuple)[4] + Integer.parseInt(rs.getString("quant"));//update quant
						initialcpTotal[5] = cusprod.get(tuple)[5] + 1;//update times
					}else{
						initialcpTotal[6] = cusprod.get(tuple)[6] + Integer.parseInt(rs.getString("quant"));//update quant
						initialcpTotal[7] = cusprod.get(tuple)[7] + 1;//update times
					}
				}
				else{						
					int[] initialcpTotal = new int[8];
					if(month>0 && month<=3){
						initialcpTotal[0] = Integer.parseInt(rs.getString("quant")); //update quant
						initialcpTotal[1] = 1;//update times
					}else if(month>3&& month<=6){
						initialcpTotal[2] = Integer.parseInt(rs.getString("quant"));//update quant
						initialcpTotal[3] = 1;//update times
					}else if(month>6&&month<=9){
						initialcpTotal[4] = Integer.parseInt(rs.getString("quant"));//update quant
						initialcpTotal[5] = 1;//update times
					}else{
						initialcpTotal[6] = Integer.parseInt(rs.getString("quant"));//update quant
						initialcpTotal[7] = 1;
					}
					cusprod.put(tuple,initialcpTotal);// put the tuple and array into the hashmap
					
				}
			}
			Iterator<Entry<Tuple, int[]>> it = cusprod.entrySet().iterator();// iterator for the hashmap
		    while (it.hasNext()) {// calculate the avg and reset one index for the use of 'total' for next step
		        Entry<Tuple, int[]> pairs = it.next();
		        int[] initialcpTotal = pairs.getValue();
		        for (int i = 0; i < initialcpTotal.length; i += 2) {
					if (initialcpTotal[i + 1] != 0) {
						initialcpTotal[i] /= initialcpTotal[i + 1]; // calcualte the avg
						initialcpTotal[i + 1] = 0; // reset the value for next traverse use
					}
				}
		        //Intialize variables
		        Iterator<Entry<Tuple, int[]>> it2 = cusprod.entrySet().iterator();// iterator for the hashmap
				String squater = null;
				String beforeAverage = null;
	    		String afterAverage = null;
	    		//traverse to assign each quarter's before and after avg
		        while (it2.hasNext()) {// calculate the avg and reset one index for the use of 'total' for next step
		        	Entry<Tuple, int[]> pairs2 = it2.next();
		        	for(int quarter = 1; quarter <=4; quarter++){
		        		if(quarter ==1){
		        			beforeAverage = "    <NULL>";  // set before 
		        			afterAverage = String.format("% 9d", pairs2.getValue()[2*quarter-2+2]); //set after
		        		}
		        		else if(quarter == 4){
		        			beforeAverage = String.format("% 10d", pairs2.getValue()[2*quarter-2-2]);//set before
		        			afterAverage= "   <NULL>"; //set after
		        		}
		        		else{
		        			beforeAverage = String.format("% 10d", pairs2.getValue()[2*quarter-2-2]);
		        			afterAverage = String.format("% 9d", pairs2.getValue()[2*quarter-2+2]);
		        		}
		        		squater = String.format("%-7s","Q"+quarter);//String squarter
		        		System.out.println(pairs2.getKey().getCustomerName() + " " + pairs2.getKey().getProductName() +"  "+ squater +" "+ beforeAverage +" "+ afterAverage);//output one by one
		        	}
		        }
			    it.remove(); // avoids a ConcurrentModificationException
		    }
		} 
		catch(SQLException e) 
		{
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}
	}
}

