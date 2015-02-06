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
* 2.2 Computer the avg and max value of a specific cust, prod tuple.
* 2.3 With the second traverse, calculate the times we want and update the array in the hashmap.
* 2.4 Output the result by output the Tuple and array object information one by one.
*
* 3.Structure
* The data structure I used for this problem are a hash maps with a Tuple key and an array value.
* The tuple has two variable: customer name and product name with overrided hashCode and equals function to make sure every same pair of them
* will be treated as same and equal.
* The array will be used first store the total times, total quant and max for the first traverse and the reuse for the second round to calcualte the times we want.
*

---------------------------------------------------------------------------------------------------------------------------------------------------------------------*/


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

public class sample3 {

	public static void main(String[] args) 
	{
		String usr ="hxin"; // postgres username
		String pwd ="Stevens122851"; // postgres password
		String url ="jdbc:postgresql://155.246.89.29:5432/hxin"; // postgres address, port number and database name
		
		try 
		{
			Class.forName("org.postgresql.Driver"); // load database driver
			System.out.println("Success loading Driver!");
		} 

		catch(Exception e) 
		{
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		try 
		{
			Connection conn = DriverManager.getConnection(url, usr, pwd); // connect to the database server with user name, password and DB address
			System.out.println("Success connecting server!");

			Statement stmt = conn.createStatement(); // get a statement object
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales"); // excute "SELECT * FROM Sales"
			
			HashMap<Tuple, int[]> cusprod = new HashMap<Tuple, int[]>();

			System.out.println("CUSTOMER PRODUCT QUARTER BEFORE_TOT AFTER_TOT");
			System.out.println("======== ======= ======= ========== =========");
			while (rs.next()) 
			{
				Tuple tuple = new Tuple(rs.getString("cust"), rs.getString("prod")); // create a new object with customer name and product name
				if (cusprod.containsKey(tuple)) { // compare it with current tuples in the HashMap to see whether this combination has appeared before
					int[] initialcpTotal = cusprod.get(tuple);
					int month = Integer.parseInt(rs.getString("month"));
					initialcpTotal[(month - 1) / 3 * 4] += Integer.parseInt(rs.getString("quant")); // update the total quant
					initialcpTotal[(month - 1) / 3 * 4 + 1] += 1; // update the appearance time
					initialcpTotal[(month - 1) / 3 * 4 + 2] = Math.max(initialcpTotal[(month - 1) / 3 * 4 + 2], Integer.parseInt(rs.getString("quant"))); // update the max quant
				} else {
					int[] initialcpTotal = new int[16];
					int month = Integer.parseInt(rs.getString("month"));
					initialcpTotal[(month - 1) / 3 * 4] = Integer.parseInt(rs.getString("quant")); // total
					initialcpTotal[(month - 1) / 3 * 4 + 1] = 1; // times
					initialcpTotal[(month - 1) / 3 * 4 + 2] = Integer.parseInt(rs.getString("quant")); // init max
					cusprod.put(tuple, initialcpTotal); // put the tuple and array into the hashmap
				}
			}

			Iterator<Tuple> it = cusprod.keySet().iterator(); // iterator for the hashmap
			while (it.hasNext()) { // calculate the avg and reset one index for the use of 'total' for next step
				Tuple tuple = it.next();
				int[] initialcpTotal = cusprod.get(tuple);
				for (int i = 0; i < initialcpTotal.length; i += 4) {
					if (initialcpTotal[i + 1] != 0) {
						initialcpTotal[i] /= initialcpTotal[i + 1]; // calcualte the avg
						initialcpTotal[i + 1] = 0; // reset the value for next traverse use
					}
				}
			}

			ResultSet rs2 = stmt.executeQuery("SELECT * FROM Sales"); // second scan

			while (rs2.next()) {
				Tuple tuple = new Tuple(rs2.getString("cust"), rs2.getString("prod"));
				int[] initialcpTotal = cusprod.get(tuple);
				int month = Integer.parseInt(rs2.getString("month"));
				int quant = Integer.parseInt(rs2.getString("quant"));

				if (month >= 1 && month <= 3) { // Q1
					if (quant >= initialcpTotal[(month + 3 - 1) / 3 * 4] && quant <= initialcpTotal[(month + 3 - 1) / 3 * 4 + 2])
						initialcpTotal[(month + 3 - 1) / 3 * 4 + 1] += 1; // update the times
				} else if (month >= 4 && month <= 9) { // Q2 Q3
					if (quant >= initialcpTotal[(month + 3 - 1) / 3 * 4] && quant <= initialcpTotal[(month + 3 - 1) / 3 * 4 + 2])
						initialcpTotal[(month + 3 - 1) / 3 * 4 + 1] += 1; // update the times
					if (quant >= initialcpTotal[(month - 3 - 1) / 3 * 4] && quant <= initialcpTotal[(month - 3 - 1) / 3 * 4 + 2])
						initialcpTotal[(month - 3 - 1) / 3 * 4 + 3] += 1; // update the times
				} else { // Q4
					if (quant >= initialcpTotal[(month - 3 - 1) / 3 * 4] && quant <= initialcpTotal[(month - 3 - 1) / 3 * 4 + 2])
						initialcpTotal[(month - 3 - 1) / 3 * 4 + 3] += 1; // update the times
				}
			}


			it = cusprod.keySet().iterator(); // iterator for the hashmap
			while (it.hasNext()) {
				Tuple tuple = it.next();
				int[] initialcpTotal = cusprod.get(tuple);
				for (int i = 0; i < initialcpTotal.length; i += 4) { // output the result
					System.out.println(tuple + " " + "Q" + (i / 4 + 1) + "      " + (i == 0 ? "    <NULL>" : String.format("% 10d", initialcpTotal[i + 1])) + " " + (i == 12 ? "   <NULL>" : String.format("% 9d", initialcpTotal[i + 3])));
				}
			}
			
		}
		
		catch(SQLException e) 
		{
			System.out.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}

}
