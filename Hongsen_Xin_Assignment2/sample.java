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
* 2.2 Computer the avg value of a specific cust, prod tuple and the total value and times of each prod.
* 2.3 Use total value of prod subtract total value of that cust and prod then divides their times difference
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

public class sample {

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
			HashMap<String,int[]> prod = new HashMap<String, int[]>();
			HashMap<Tuple, int[]> cusprod = new HashMap<Tuple, int[]>();
			System.out.println("CUSTOMER PRODUCT  CUST_AVG  OTHERS_AVG");
			System.out.println("======== =======  ========  ==========");
			while (rs.next()) 
			{
				if(prod.containsKey(rs.getString("prod"))){   // compare it with current prod in the HashMap to see whether this combination has appeared before
					prod.get(rs.getString("prod"))[0] = prod.get(rs.getString("prod"))[0] + Integer.parseInt(rs.getString("quant"));
					prod.get(rs.getString("prod"))[1] = prod.get(rs.getString("prod"))[1] + 1;
				}
				else{
					int[] initialpTotal = new int[2];
					initialpTotal[0] = Integer.parseInt(rs.getString("quant"));//update quant
					initialpTotal[1] = 1;//update times
					prod.put(rs.getString("prod"),initialpTotal); //put tuple and array value into prod
				}
				Tuple tuple = new Tuple(rs.getString("cust"),rs.getString("prod"));		
				if(cusprod.containsKey(tuple)){ // compare it with current tuples in the HashMap to see whether this combination has appeared before
					cusprod.get(tuple)[0] = cusprod.get(tuple)[0] + Integer.parseInt(rs.getString("quant"));//update quant
					cusprod.get(tuple)[1] = cusprod.get(tuple)[1] + 1;//update times
				}
				else{
					int[] initialcpTotal = new int[2];
					initialcpTotal[0] = Integer.parseInt(rs.getString("quant"));//update quant
					initialcpTotal[1] = 1;//update times
					cusprod.put(tuple,initialcpTotal);//put tuple and array value into prod
				}
			}
			System.out.println("+++++++" + cusprod.size());
			Iterator<Entry<Tuple, int[]>> it = cusprod.entrySet().iterator();// iterator for the hashmap
			int otherAverage;
		    while (it.hasNext()) {// calculate the avg and reset one index for the use of 'total' for next step
		        Entry<Tuple, int[]> pairs = it.next();
		        if((prod.get(pairs.getKey().productName)[1] - pairs.getValue()[1]) == 0){
		        	otherAverage = 0;
		        	System.out.println(prod.get(pairs.getKey().productName)[1]);
		        	System.out.println(pairs.getValue()[1]);
		        }
		        else{
		        	otherAverage = (prod.get(pairs.getKey().productName)[0] - pairs.getValue()[0]) / (prod.get(pairs.getKey().productName)[1] - pairs.getValue()[1]);
		        }   
			    System.out.println(pairs.getKey().getCustomerName() + " " + pairs.getKey().getProductName() +" "+ String.format("% 8d", pairs.getValue()[0]/pairs.getValue()[1]) +"  "+ String.format("% 10d", otherAverage));
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

