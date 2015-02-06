import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * Customers and products in each of the state and calculate
 * the max value, minimum value, max&minimum DATES, state and the average value
 * for each of the Tuple, and then output them.
 * Using arrays and two-dimensional array as my data structure.
 * Using a single scan.
 * 1. Define an array for the customer to record the name of the customer;
 * 2. Define an array for the product to record the name of the product;
 * 3. Define a two-dimensional array to store the quant for the each Tuple
 * of product and customer, and define another array to record the addition calculation
 * times happened in this quant, then use: quant/time to calculate the average value
 * for each of the Tuple of product and customer;
 * 4. Define 6 two-dimensional arrays to record the max day, max month, max year,
 * minimum day, minimum month, minimum year; 
 * 5.Using the index for each of the customer and product in
 * array cust[index1] and prod[index2], then the quant of this Tuple will Tuple'
 * store in quant[index1][index2], for example: if customer name "Tom" store 
 * in cust[1] and product name "Bread" stored in prod[2], then the quant for "Tom Bread"
 * will stored in array quant[1][2], it will make benefits if I do the output of
 * these data, it will output for each customer and product in sequence;
 * 6. Compare each of the quant in the loop, and find the max value and minimum value 
 * for each of the Tuple, and record their date and state;
 * 7. calculate the average value of each of the Tuple.
 */
public class sample2 {

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

		try {
			Connection conn = DriverManager.getConnection(url, usr, pwd);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");

			// define array for customer
			String[] cust = new String[10];

			// define array for product
			String[] prod = new String[10];

			// define array for max quantity
			int[][] maxq = new int[10][10];

			// define array for max day and initialize
			String[][] maxday = new String[10][10];
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					maxday[i][j] = " ";
			// define array for max
			String[][] maxmonth = new String[10][10];

			// define array for max year
			String[][] maxyear = new String[10][10];

			// define array for max state
			String[][] maxst = new String[10][10];

			// define array for minimum quantity
			int[][] minq = new int[10][10];

			// define array for minimum day
			String[][] minday = new String[10][10];

			// define array for minimum month
			String[][] minmonth = new String[10][10];

			// define array for minimum year
			String[][] minyear = new String[10][10];

			// define array for minimum state
			String[][] minst = new String[10][10];

			// define array for average quaint
			int[][] avg = new int[10][10];

			// define two variables for record the length of array customer and
			// array product that have real names
			int A = -1;
			int B = -1;

			// define array for record the addition calculation time
			int[][] time = new int[10][10];
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					time[i][j] = 1;

			while (rs.next()) {

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

				// give some value for the start of this query, avoid the
				// problem that if some variables are null
				if (maxday[a][b].equals(" ")) {
					cust[a] = rs.getString("cust");
					prod[b] = rs.getString("prod");
					maxq[a][b] = rs.getInt("quant");
					maxday[a][b] = rs.getString("day");
					maxmonth[a][b] = rs.getString("month");
					maxyear[a][b] = rs.getString("year");
					maxst[a][b] = rs.getString("state");
					minq[a][b] = rs.getInt("quant");
					minday[a][b] = rs.getString("day");
					minmonth[a][b] = rs.getString("month");
					minyear[a][b] = rs.getString("year");
					minst[a][b] = rs.getString("state");
					avg[a][b] = rs.getInt("quant");
					time[a][b]++;
					continue;
				}

				// compare the value of max value
				if (rs.getInt("quant") > maxq[a][b]) {
					maxq[a][b] = rs.getInt("quant");
					maxday[a][b] = rs.getString("day");
					maxmonth[a][b] = rs.getString("month");
					maxyear[a][b] = rs.getString("year");
					maxst[a][b] = rs.getString("state");
				}

				// compare the value of minimum value
				if (rs.getInt("quant") < minq[a][b]) {
					minq[a][b] = rs.getInt("quant");
					minday[a][b] = rs.getString("day");
					minmonth[a][b] = rs.getString("month");
					minyear[a][b] = rs.getString("year");
					minst[a][b] = rs.getString("state");
				}

				// compute the sum of quantity for each Tuple
				avg[a][b] = avg[a][b] + rs.getInt("quant");
				time[a][b]++;

			}// end of the while

			// This is an additional operation to avoid the problem of "0" as a
			// denominator if some of the Tuples may do not have any
			// record in the database
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					if (time[i][j] == 1)
						time[i][j]++;

			// calculate the average value for each of the Tuples
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					avg[i][j] = avg[i][j] / (time[i][j] - 1);

			// output the results
			System.out
					.println("CUSTOMER  PRODUCT   MAX_Q  DATE        ST  MIN_Q  DATE        ST  AVG_Q");
			System.out
					.println("========  ========  =====  ==========  ==  =====  ==========  ==  =====");
			

			for (int i = 0; i <= A; i++) {
				for (int j = 0; j <= B; j++) {

					// all the following operations is to make sure that the
					// format of the table is in the right format
					System.out.format("%-10s", cust[i]);
					
					System.out.format("%-10s", prod[j]);
					
					System.out.format("%5s", maxq[i][j]);
					
					System.out.print("  ");

					if (maxmonth[i][j].length() == 1) {
						System.out.print("0");
						System.out.print(maxmonth[i][j]);
						System.out.print("/");
					} else {
						System.out.print(maxmonth[i][j]);
						System.out.print("/");
					}

					if (maxday[i][j].length() == 1) {
						System.out.print("0");
						System.out.print(maxday[i][j]);
						System.out.print("/");
					} else {
						System.out.print(maxday[i][j]);
						System.out.print("/");
					}

					System.out.print(maxyear[i][j]);

					System.out.print("  ");

					System.out.print(maxst[i][j]);

					System.out.format("%7d", minq[i][j]);
					
					System.out.print("  ");

					if (minmonth[i][j].length() == 1) {
						System.out.print("0");
						System.out.print(minmonth[i][j]);
						System.out.print("/");
					} else {
						System.out.print(minmonth[i][j]);
						System.out.print("/");
					}

					if (minday[i][j].length() == 1) {
						System.out.print("0");
						System.out.print(minday[i][j]);
						System.out.print("/");
					} else {
						System.out.print(minday[i][j]);
						System.out.print("/");
					}

					System.out.print(minyear[i][j]);

					System.out.print("  ");

					System.out.print(minst[i][j]);
					
					System.out.format("%7d\n", avg[i][j]);

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
