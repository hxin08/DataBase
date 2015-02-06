import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Customers and products in NY, NJ, CT, and calculate
 * each of the average value for the NY, NJ and CT, and then output them.
 * Using arrays and two-dimensional array as my data structure.
 * Using a single scan.
 * 1. Define an array for the customer to record the name of the customer;
 * 2. Define an array for the product to record the name of the product;
 * 3. Define a two-dimensional array to store the quant for the each combination
 * of product and customer, and define another array to record the addition calculation
 * times happened in this quant, then use: quant/time to calculate the average value
 * for each of the combination of product and customer in NY, NJ, CT;
 * 4. Each of the customer and product in
 * array cust[index1] and prod[index2], then the quant of this combination will 
 * store in quant[index1][index2], for example: if customer name "Tom" store 
 * in cust[1] and product name "Bread" stored in prod[2], then the quant for "Tom Bread"
 * will stored in array quant[1][2], it will make benefits if I do the output of
 * these data, it will output for each customer and product in sequence.
 */
public class sample1 {

	public static void main(String[] args) {
		String usr = "hxin";
		String pwd = "Stevens122851";
		String url = "jdbc:postgresql://155.246.89.29:5432/hxin";
		
		try {
			Class.forName("org.postgresql.Driver");
		}

		catch (Exception e) {
			System.out.println("Fail loading Driver!");
			e.printStackTrace();
		}

		// define array for customer
		String[] cust = new String[10];

		// define array for product
		String[] prod = new String[10];

		// define array for average value for each state
		int[][] nyavg = new int[10][10];
		int[][] njavg = new int[10][10];
		int[][] ctavg = new int[10][10];

		// define two variables to record the length of array customer and array
		// product that have real names

		int A = -1;
		int B = -1;

		// define array for addition calculation times in each combination of
		// customer and product for each of the state
		int[][][] time = new int[3][10][10];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 10; j++)
				for (int x = 0; x < 10; x++)
					time[i][j][x] = 1;

		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");

			while (rs.next()) {
				// only operate on 3 states
				if (rs.getString("state").equals("NY")
						|| rs.getString("state").equals("NJ")
						|| rs.getString("state").equals("CT")) {

					// check whether the combination of customer and product is
					// repeated
					int repeatcust = 0;
					int repeatprod = 0;

					// define two variables to record the index of the customer
					// and product
					int a = 0;
					int b = 0;

					// try to found the index of customer name if it is already
					// in the array
					for (int i = 0; i < 10; i++)
						if (rs.getString("cust").equals(cust[i])) {
							repeatcust = 1;
							a = i;
							break;
						}
					// if there is no such name stored in the array, add this
					// new name to the array
					if (repeatcust == 0) {
						A++;
						cust[A] = rs.getString("cust");
						a = A;
					}

					// try to found the index of product name if it is already
					// in the array
					for (int i = 0; i < 10; i++)
						if (rs.getString("prod").equals(prod[i])) {
							repeatprod = 1;
							b = i;
							break;
						}
					// if there is no such name stored in the array, add this
					// new name to the array
					if (repeatprod == 0) {
						B++;
						prod[B] = rs.getString("prod");
						b = B;
					}

					// calculate the sum of value for each combinations
					if (rs.getString("state").equals("NY")) {
						nyavg[a][b] = nyavg[a][b] + rs.getInt("quant");
						time[0][a][b]++;
					} else if (rs.getString("state").equals("NJ")) {
						njavg[a][b] = njavg[a][b] + rs.getInt("quant");
						time[1][a][b]++;
					} else if (rs.getString("state").equals("CT")) {
						ctavg[a][b] = ctavg[a][b] + rs.getInt("quant");
						time[2][a][b]++;
					}

				}
			}// end of the while

			// This is an additional operation to avoid the problem of "0" as a
			// denominator if some of the combinations may do not have any
			// record in the database
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 10; j++)
					for (int x = 0; x < 10; x++)
						if (time[i][j][x] == 1)
							time[i][j][x]++;

			// calculate the average value for each of the combinations
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++) {
					nyavg[i][j] = nyavg[i][j] / (time[0][i][j] - 1);
					njavg[i][j] = njavg[i][j] / (time[1][i][j] - 1);
					ctavg[i][j] = ctavg[i][j] / (time[2][i][j] - 1);
				}

			// output the results
			System.out.println("CUSTOMER  PRODUCT  NY_AVG  NJ_AVG  CT_AVG");
			System.out.println("========  =======  ======  ======  ======");
			
			for( int i=0; i<=A; i++){
				for(int j=0; j<=B; j++){
					System.out.format("%-10s", cust[i]);
					System.out.format("%-9s", prod[j]);
					System.out.format("%6d", nyavg[i][j]);
					System.out.format("%8d", njavg[i][j]);
					System.out.format("%8d\n", ctavg[i][j]);					
				}				
			}
		}

		catch (SQLException e) {
			System.out
					.println("Connection URL or username or password errors!");
			e.printStackTrace();
		}

	}

}
