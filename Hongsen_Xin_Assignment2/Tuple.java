//Author:Hongsen_Xin
//ID:10388303

/*
* 1. How to run the code
* Please run the code with following commands:
* javac sample.java
* java -cp .:postgresql-9.2-1003.jdbc4.jar sample
*
* 2. Data Structure
* The data structure I used for this problem are a hash maps with a Tuple key and an array value.
* The tuple has two variable: customer name and product name with overrided hashCode function to make sure every same pair of them
* will be treated as same and equal.
* The array will be used first store the total times, total quant and max for the first traverse and the reuse for the second round to calcualte the times we want.
*
* 3. Algorithm
* 3.1 Generate a tuple and try whether it is in the hashmap, if it is not in it, create a new array object and input the current record information in it
* and input these key and value into the hashmap. If it is in the hashmap, update the value in array object.
* 3.2 Calculate the avg and max value of a specific cust, prod tuple.
* 3.3 With the second traverse, calculate the times we want and update the array in the hashmap.
* 3.4 Output the result by output the Tuple and array object information one by one.
*/

public class Tuple {
	public String customerName;
	public String productName;
	
	public Tuple(String customerName, String productName) {
		this.customerName = customerName;
		this.productName = productName;
	}
	
	public String getCustomerName() {
		StringBuilder sb = new StringBuilder(this.customerName);
		while (sb.length() < 8)
			sb.append(" ");
		return sb.toString();
	}
	
	public String getProductName() {
		StringBuilder sb = new StringBuilder(this.productName);
		while (sb.length() < 7)
			sb.append(" ");
		return sb.toString();
	}
	
	public int hashCode() { // override the hashCode function to make two tuples with same customer name and product name will be hashed to same value
		return customerName.hashCode() ^ productName.hashCode();
	}
	
	public boolean equals(Object o) { // override equals function
		if (o == null ||(o.getClass() != this.getClass())) return false;
		Tuple temp = (Tuple) o;
		return this.customerName.equals(temp.customerName) && this.productName.equals(temp.productName);
	}
	
	public String toString() {
		return this.getCustomerName() + " " + this.getProductName();
	}
}
