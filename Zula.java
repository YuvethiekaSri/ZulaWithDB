import static java.lang.System.exit;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Zula{
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		while(true) {
            System.out.println();
            System.out.println("\t\t\tWelcome to ZULA!!");
            System.out.println("""
                    1. Cab driver login
                    2. Customer Login
                    3. Administrator login
                    4. Quit""");
            System.out.println("Please choose a service: ");
            Scanner input = new Scanner(System.in);
            int choice = input.nextInt();
            switch (choice) 
            {
                case 1 -> driver();
                case 2 -> {
                    System.out.println("\t\t\tCustomer's portal");
                    customer();}
                case 3 -> {
                    System.out.println("\t\t\tAdmin's Portal");
                    admin();}
                case 4 -> {
                    System.out.println("Thanks for using ZULA. Have a nice day... See you again!!!");
                    exit(0);}
                default -> System.out.println("Please enter a valid choice.");}}}
	

	private static void admin() throws SQLException {
		Scanner ain = new Scanner(System.in);
        System.out.println();
        System.out.println("Enter your name: ");
        String verifyName = ain.nextLine();
        System.out.println("Enter password: ");
        String verifyPass = ain.nextLine();
        boolean isAuthentic = verifyName.equals("admin") && verifyPass.equals("admin");
        if(isAuthentic) {
            System.out.println();
            System.out.println("\t\tWelcome Admin!");
            adminChoiceSwitch();}
        else {
            System.out.println("Sorry! Username or Password incorrect...");}}
		

	private static void adminChoiceSwitch() throws SQLException {
		Scanner ain = new Scanner(System.in);
        System.out.println();
        System.out.println("1. Add driver\n2. Show summary\n3. View drivers\n4. View customers\n5.Exit");
        int adminChoice = ain.nextInt();
        switch(adminChoice) {
            case 1:
			try {
				addDriver();
			} catch (SQLException e) {
				System.out.println("Driver coudn't be added at the moment. Try again");
			}
                adminChoiceSwitch();
                break;
            case 2:
              getSummary();
                adminChoiceSwitch();
                break;
            case 3:
            	try {
            		printAvailableDrivers();
            	} catch (SQLException e) {
            		System.out.println("Sorry! Try again later...");	
            	}
                adminChoiceSwitch();
                break;
            case 4:
            	try {
            		printAvailableCustomers();
            	} catch (SQLException e) {
            		System.out.println("Sorry! Try again later.....");
            	}
                	adminChoiceSwitch();
                	break;
            case 5:
                break;
            default:
                System.out.println("Please enter a valid choice");
                adminChoiceSwitch();}}

	private static void getSummary() throws SQLException {
		String loopingQuery = "select count(distinct driverId) as count from drivers;";
		Connection conn= null;
	    ResultSet rs = null;
	    Statement statement = null;
	    int count =0;
	    try {
	    	Class.forName("com.mysql.cj.jdbc.Driver");
			    conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
	        statement =conn.createStatement();
	       rs= statement.executeQuery(loopingQuery);
	        rs.next();
	        count = rs.getInt("count");
	        
	        for(int driId = 1; driId<=count;driId++) {
	            adminSummary(driId);}
			
		} catch (Exception e) {
			System.out.println("Something went wrong. Please try again later");
		}
	    finally {
	    	rs.close();
	    	statement.close();
			conn.close();
		}
	    
	}

	private static void adminSummary(int driId) throws SQLException {
		int totalTrips=0, fareCollected =0;
        float zulaCommission = 0.0f;
        
        
        Connection conn= null;
	    ResultSet res = null;
	    String colletionQuery = "select count(customerId) as trips,sum(fare) as totalFare, sum(zulaCommission) as totalCommission from summary where cabId = "+driId;
        try {
	    	Class.forName("com.mysql.cj.jdbc.Driver");
		    conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
		    Statement st =conn.createStatement();
		    res= st.executeQuery(colletionQuery);
		    res.next();
		    fareCollected = res.getInt("totalFare");
		    zulaCommission = res.getFloat("totalCommission");
		    totalTrips = res.getInt("trips");
		    st.close();
		    
        if(totalTrips==0) {
            System.out.println();
            System.out.println("Cab id: " + driId);
            System.out.println("Total Number of Trips: " + totalTrips);
            System.out.println("Total Fare collected: " + fareCollected);
            System.out.println("Total ZULA commission: " + zulaCommission);
            System.out.println("Trip details: ");
            System.out.println("No trips taken...");}
        else {
                System.out.println();
                System.out.println("Cab id: " + driId);
                System.out.println("Total Number of Trips: " + totalTrips);
                System.out.println("Total Fare collected: " + fareCollected);
                System.out.println("Total ZULA commission: " + zulaCommission);
                System.out.println();
                System.out.println("Trip details: ");
                System.out.printf("%15s %15s %20s %10s %20s", "Source", "Destination", "Customer Detail", "Fare", "Zula Commission");
                System.out.println();
               
                String queryString= "select * from summary inner join drivers on  summary.cabId = drivers.driverId and drivers.driverId=?;";
                PreparedStatement statement=conn.prepareStatement(queryString);
                statement.setInt(1, driId);
                ResultSet sr =  statement.executeQuery();
                while(sr.next())
                {
	        		String src = sr.getString("source");
	        		String dest = sr.getString("destination");
	        		int custId = sr.getInt("customerId");
	        		int custFare= sr.getInt("fare");
	        		Float zulaComm = sr.getFloat("zulaCommission");
	        		System.out.printf("%15s %15s %20s %10s %20s",src,dest,custId,custFare,zulaComm);
	                System.out.println();
	        	}
                sr.close();
    			statement.close();
	        }
        }
        catch (Exception e) {
        	System.out.println("Sorry Admin! Something went wrong...");
		}
        finally {
			res.close();
			conn.close();
		}
	}


	private static void printAvailableCustomers() throws SQLException {
		System.out.println("\t\t\tCustomer's details: ");
        System.out.printf("%10s %10s %10s","Id","Name","Age");
        System.out.println();
        Connection conn= null;
        ResultSet rs = null;
        Statement statement = null;
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
		 conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
         statement =conn.createStatement();
         rs = statement.executeQuery("select * from customers;");
        while(rs.next()) {
        	int id = rs.getInt("customerId");
        	String name = rs.getString("name");
        	int age = rs.getInt("age");
            System.out.printf("%10d %10s %10d",id,name,age);
            System.out.println();}
        }
        catch (Exception e) {
			System.out.println("Unable to display details...");
		}
        finally {
        	rs.close();
            statement.close();
            conn.close();		
		}
	}


	private static void printAvailableDrivers() throws SQLException {
		System.out.println("\t\t\tDriver's details: ");
        System.out.printf("%10s %10s %10s","Id","Name","Age");
        System.out.println();
        
        Connection conn= null;
        ResultSet rs = null;
        Statement statement = null;
        try {
        Class.forName("com.mysql.cj.jdbc.Driver");
		 conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
         statement =conn.createStatement();
         rs = statement.executeQuery("select * from drivers;");
        while(rs.next()) {
        	int id = rs.getInt("driverId");
        	String name = rs.getString("name");
        	int age = rs.getInt("age");
            System.out.printf("%10d %10s %10d",id,name,age);
            System.out.println();}
        }
        catch (Exception e) {
			System.out.println("Unable to display details...");
		}
        finally {
        	rs.close();
            statement.close();
            conn.close();		
		}
   }


	private static void addDriver() throws SQLException {
		Scanner din = new Scanner(System.in);
        System.out.println("Name: ");
        String dname = din.nextLine();
        System.out.println("Age: ");
        int dage = din.nextByte();
        Scanner sc = new Scanner(System.in);
        System.out.println("pwd: ");
        String dpwd = sc.nextLine();
        Connection conn = null;
		ResultSet rs = null;
		int rst=0;
		PreparedStatement smt = null;
		Statement statement=null;
		PreparedStatement updateStatement = null;
		int driId =0;
       try {
    	   Class.forName("com.mysql.cj.jdbc.Driver");
			conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			String signUpQueryString = "insert into drivers values(driverId,?,?,?,true)";
			smt = conn.prepareStatement(signUpQueryString);
			smt.setString(1, dname);
			smt.setString(2, dpwd);
			smt.setInt(3, dage);
			smt.execute();
			statement = conn.createStatement();
			rs = statement.executeQuery("select * from drivers order by driverId desc limit 1;");
			rs.next();
			driId = rs.getInt("driverId");
			System.out.println("Successfully added!");
	        System.out.println("\t\tDetails of the driver: ");
	        System.out.println("Driver ID: "+driId);
	        System.out.println("Driver Name: "+dname);
	        System.out.println("Driver Password: "+dpwd);
	        System.out.println("Driver Age: "+dage);
	        String locationChangeQueryString = "insert into cabposition value(?,\"A\");";
	        updateStatement = conn.prepareStatement(locationChangeQueryString);
	        updateStatement.setInt(1, driId);
	        updateStatement.executeUpdate();	}        
       catch (InputMismatchException imee) {
    	   System.out.println("Enter valid input");} 
       catch (Exception e) {
    	   System.out.println("Sorry! Unable to add. Please try again later...");} 
       finally {
		if(rs!= null)
		{
			rs.close();
		}
		if(smt != null)
		{
			smt.close();
		}
		if(updateStatement!=null)
		{
			updateStatement.close();
		}
		if(conn != null)
		{
			conn.close();
		}
       }
    }

	private static void customer() {
		try {
            System.out.println();
            System.out.println("1. Login\n2. Signup\n3. Exit");
            Scanner input = new Scanner(System.in);
            int customerChoice = input.nextByte();
            switch (customerChoice) {
                case 1:
                    customerlogin();
                    break;

                case 2:
                    customerSignup();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Please enter a valid choice.");
            }}
        catch (Exception e) {
            System.out.println("Kindly enter a valid choice");
        }}
		
	

	private static void customerlogin() throws SQLException {
		 System.out.println("Enter your name: ");
	        Scanner cin = new Scanner(System.in);
	        String verifyName = cin.nextLine();
	        System.out.println("Enter password: ");
	        String verifyPass = cin.nextLine();
	        boolean isAuthentic = false;
	        
	       
	        Connection conn = null;
	        PreparedStatement statement= null;
	        Statement st=null;
	        ResultSet rs=null;
	        int custId=0;
	        
	        try {
	        	 Class.forName("com.mysql.cj.jdbc.Driver");
	        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
	            String loginQuery = "Select * from customers where name = ? && password = ? ";
	            st=conn.createStatement();
	            statement=conn.prepareStatement(loginQuery);
	        	statement.setString(1, verifyName);
	        	statement.setString(2, verifyPass);
	        	rs = statement.executeQuery();
	        	if(rs.next())
	        	{
	        		custId = rs.getInt(1);
	        		isAuthentic = true;
		            System.out.println();
	        		System.out.println("\t\tWelcome "+verifyName+"!");
		            customerChoice(custId);
	        		
	        	}
	        	else {
		            System.out.println("Sorry! Username or Password incorrect...");}
	        }
	        catch (Exception e) {
				System.out.println("Could not log in.. Please try again");
			}
	        finally {
				rs.close();
				statement.close();
				conn.close();}
			}
	private static void customerChoice(int custId) {
				 try {
			            System.out.println();
			            System.out.println("What service are you looking for?");
			            System.out.println("1. Hail a cab.\n2. Cabs near me.\n3. Show my summary.\n4. Exit");
			            Scanner acin = new Scanner(System.in);
			            int authenticCustomerChoice = acin.nextInt();
			            switch (authenticCustomerChoice) {
			                case 1 -> hailCab(custId);
			                case 2 -> {
			                    printCabParked();
			                    customerChoice(custId);}
			                case 3 -> {
			                    customerSummary(custId);
			                    customerChoice(custId);}
			                case 4 -> {
			                    System.out.println();
			                    System.out.println("Thanks for visiting ZULA!");}
			                default -> {
			                    System.out.println("Please enter a valid choice!");
			                    customerChoice(custId);}}}
			        catch (Exception e){
			            System.out.println("Kindly enter a valid choice...");
			            customerChoice(custId);
			        }}
			
	private static void customerSummary(int custId) throws SQLException {
		Connection conn = null;
        PreparedStatement statement= null;
        Statement st=null;
        ResultSet rs=null, sr=null;
        
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
        	String queryString= "select * from summary inner join customers on summary.customerId = customers.customerId and customers.customerId=?;";
            st=conn.createStatement();
            sr = st.executeQuery("select name from customers where customerId = "+custId);
            sr.next();
            String custName = sr.getString("name");
            
            System.out.println();
            System.out.println("Customer id: "+custId);
            System.out.println("Customer Name: "+ custName);
            System.out.println("Trip details: ");
            System.out.printf("%15s %15s %15s %15s", "Source","Destination","Cab Detail","Fare");
            System.out.println();
            statement=conn.prepareStatement(queryString);
        	statement.setInt(1, custId);
        	rs = statement.executeQuery();
        	while(rs.next())
        	{
        		String src = rs.getString("source");
        		String dest = rs.getString("destination");
        		int cabId = rs.getInt("cabId");
        		int cabFare= rs.getInt("fare");
        		System.out.printf("%15s %15s %15d %15d", src, dest, cabId, cabFare);
                System.out.println();
        	}
        }
        catch (Exception e) {
        	System.out.println("Sorry! Unable to display the details now. Please try again later");
		}
        finally {
			rs.close();
			sr.close();
			st.close();
			statement.close();
			conn.close();
		}
}


	private static void hailCab(int custId) {
	        try {
	            Scanner cabin = new Scanner(System.in);
	            System.out.println("Enter Source [A/B/C/D/E/F/G/H] : ");
	            String source = cabin.nextLine().toUpperCase().trim();
	            System.out.println("Enter Destination [A/B/C/D/E/F/G/H] : ");
	            String destination = cabin.nextLine().toUpperCase().trim();
	            int fare = calculateFare(source, destination);
	            checkCab(source, destination, custId, fare);
	        }
	        catch (NullPointerException | SQLException e) {
	        	System.out.println(e.getMessage());
	            System.out.println("Kindly check the locations...");
	            hailCab(custId);}}
			


	private static void checkCab(String source, String destination, int custId, int fare) throws SQLException {
		boolean isavail = false;
        String loc = null;
        int cabToBook=0, cabsAtSource=0;
        Connection connection = null;
        PreparedStatement preparedStatement = null, ps1=null, ps2=null, preparedStatement2 = null, smt = null;
        Statement statement  = null;
        ResultSet rst = null, rSet = null, rset=null,r1=null,r2=null, resultSet = null;
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
			connection  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			statement = connection.createStatement();
			String locationsCountQuery = "select count(locationId) as count from locations;";
			rst = statement.executeQuery(locationsCountQuery);
			rst.next();
			int countLocation = rst.getInt("count");
			int[] cabsAtLocation = new int[countLocation];
			int j=0;
        
			String extractQuery = "select * from cabposition join drivers where cabposition.location =? and drivers.driverId = cabposition.cabId and drivers.hasRest = true;";
		    preparedStatement = connection.prepareStatement(extractQuery);
			preparedStatement.setString(1, source);
		    rSet = preparedStatement.executeQuery();
			while(rSet.next())
			{
				cabsAtLocation[j] = rSet.getInt("cabId");
				j++;
				cabsAtSource++;
				isavail = true;
			}
			loc = source;
			if(isavail) {
	            int cab = 0;
	            if (cabsAtSource == 1) {
	            	cab = cabsAtLocation[0];
	            }
	           	else {
	           		int[] cabTrips = new int[cabsAtSource];
	           		for (int i = 0; i < cabsAtSource; i++) {
	                    int cabNum = cabsAtLocation[i];
	                     String tripsQuery = "select count(customerId) as trips from summary where cabId = ?;";
	                    preparedStatement2 = connection.prepareStatement(tripsQuery);
	                    preparedStatement2.setInt(1, cabNum);
	                    resultSet = preparedStatement2.executeQuery();
	                    resultSet.next();
	                    int trips = resultSet.getInt("trips");
	                    cabTrips[i] = trips;
	           		}
	           		int minIndex = 0;
	                for (int i = 1; i < cabsAtSource; i++) {
	                    if (cabTrips[minIndex] > cabTrips[i]) {
	                        minIndex = i;} 
	                }
	                
	                cab = cabsAtLocation[minIndex];
	  
	           	}
	            cabToBook = cab;
	            if(cabToBook != 0) {
	                bookcab(custId,loc, destination, fare, cabToBook);}
	           
			}// if(isAvail)
			else {
				String queryString = "select * from locations where locations.name=?;";
			    smt = connection.prepareStatement(queryString);
			    smt.setString(1, source);
			    rset = smt.executeQuery();
//				statement.execute(extractQuery)
				rset.next();
				int srcDistance = rset.getInt("distanceFromOrigin");
				int min = checkMinDistance(source, srcDistance);
	            int op1 = srcDistance + min;
	            int op2 = srcDistance - min;
	            String cabCurrentLocation;
	            
	            String optionQueryString = "select * from locations join cabposition where locations.distanceFromOrigin=? and locations.name = cabposition.location;";
	            ps1 = connection.prepareStatement(optionQueryString);
	            ps1.setInt(1, op1);
	            r1= ps1.executeQuery();
	            if(r1.next())
	            {
	            	cabToBook = r1.getInt("cabId");
	            	cabCurrentLocation = r1.getString("location");
                    bookcab(custId, source, destination, fare, cabCurrentLocation, cabToBook);
//                    return 0;
	            }
	            ps2 = connection.prepareStatement(optionQueryString);
	            ps2.setInt(1, op2);
	            r2= ps2.executeQuery();
	            if(r2.next())
	            {
	            	cabToBook = r2.getInt("cabId");
	            	cabCurrentLocation = r2.getString("location");
                    bookcab(custId, source, destination, fare, cabCurrentLocation, cabToBook);
	            }
//	            return 0;   
	            if(rSet!=null)	rset.close();
	            
			}     
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Unable to find cab at the moment");
		}
        finally {
        	if(rst!=null)	rst.close();
        	if(rSet!=null)	rSet.close();
        	if(resultSet!=null)	resultSet.close();
        	if(r1!=null)	r1.close();
        	if(r2!=null)	r2.close();
        	if(statement!=null)	statement.close();
			if(preparedStatement!=null)	preparedStatement.close();
			if(preparedStatement2!=null)	preparedStatement2.close();
            
            if(smt!=null)	smt.close();
            if(ps1!=null)	ps1.close();
            if(ps2!=null)	ps2.close();
            connection.close();
        }
       }
	 private static void bookcab(int custId, String source, String destination, int fare, int cabToBook) throws SQLException
	    {
	        int driverID=0, cabID;
	        int userChoice;

	        cabID = cabToBook;

	        Scanner confirm = new Scanner(System.in);
	        System.out.println("Cab " + cabID + " is available at location " + source);
	        System.out.println("Are you sure to book Cab " + cabID + "? (1-y / 0-n): ");
	        userChoice = confirm.nextInt();
	        userChoiceSwitch(userChoice);
	        if(userChoice==1) {
	            float zulaCommission = fare*0.3f;
	            printBookingDetails(custId, source, destination,cabID, fare);
	            updateSummary(cabID, custId, fare, zulaCommission, source, destination);
	            updateDriverRest(cabID);
	            changeCabLocations(cabID,destination);}
	    }

	 private static void bookcab(int custId, String source, String destination, int fare, String cabCurrentLocation, int cabID) throws SQLException {
	        int userChoice;
	        Scanner confirm = new Scanner(System.in);
	        System.out.println("Cab " + cabID + " is available at location " + cabCurrentLocation);
	        System.out.println("Are you sure to book Cab " + cabID + "? (1-y / 0-n): ");
	        userChoice = confirm.nextInt();
	        userChoiceSwitch(userChoice);
	        if(userChoice==1) {
	            float zulaCommission = fare*0.3f;
	            printBookingDetails(custId, source,destination, cabID,fare);
	            updateSummary(cabID, custId, fare, zulaCommission, source, destination);
	            updateDriverRest(cabID);
	            changeCabLocations(cabID,destination);}}
	 
	 private static void printBookingDetails(int custId, String source, String destination, int cabID, int fare) throws SQLException {
		 Connection connection = null;
		 PreparedStatement pst = null;
		 ResultSet rst=null;
		 String fetchNameQuery="select name from customers where customerId = ?;";
		 try {
			 Class.forName("com.mysql.cj.jdbc.Driver");
			connection  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			pst = connection.prepareStatement(fetchNameQuery);
			pst.setInt(1, custId);
			rst = pst.executeQuery();
			rst.next();
			System.out.println("\t\tBooking details: ");
	        System.out.println("Customer id: "+custId+"\nname: "+ rst.getString("name")+"\nsource: "+source+"\ndestination: "+destination+"\nCab Id: "+cabID+"\nFare: "+fare);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Could not fetch name. Please wait");
		}
		 finally {
			rst.close();
			pst.close();
			connection.close();
		}
	 }
	 
	 private static void updateSummary(int cabId, int customerId, int fare, float commission, String source, String destination) throws SQLException {
		 
	  
		 
		 Connection connection = null;
		 ResultSet rst = null;
		 PreparedStatement pst = null;
		 String updateSummaryQuery="insert into summary values(?, ?, ?, ?, ?, ?);";
		 try {
			 Class.forName("com.mysql.cj.jdbc.Driver");
			connection  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			pst = connection.prepareStatement(updateSummaryQuery);
			pst.setInt(1, cabId);
			pst.setInt(2, customerId);
			pst.setInt(3, fare);
			pst.setFloat(4, commission);
			pst.setString(5, source);
			pst.setString(6, destination);
			pst.executeUpdate();
		} catch (Exception e) {
			System.out.println("Could not update the trips. Please wait");
		}
		 finally {
			pst.close();
			connection.close();
		}
		 
	 }
		
	
	 
	 
	 private static void changeCabLocations(int cabId, String destination) throws SQLException {
		 
		 Connection connection = null;
		 PreparedStatement pst = null;
		 String updateCabQuery="update cabposition set location = ? where cabId = ?;";
		 
		 try {
			 Class.forName("com.mysql.cj.jdbc.Driver");
			connection  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			pst = connection.prepareStatement(updateCabQuery);
			pst.setString(1, destination);
			pst.setInt(2, cabId);
			pst.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Could not fetch cab positions. Please wait");
		}
		 finally {
			pst.close();
			connection.close();
		}
	 }
	    
	 private static void updateDriverRest(int driverID) throws SQLException {
		 Connection conn = null;
		 PreparedStatement pst = null,pst2=null;
		 
		 String changeQuery="update drivers set hasRest = false where drivers.driverId = ?;";
		 String updateQuery = "update drivers set hasRest = true where drivers.driverId != ?;";
		 try {
			 Class.forName("com.mysql.cj.jdbc.Driver");
			conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			pst = conn.prepareStatement(changeQuery);
			pst.setInt(1, driverID);
			pst.executeUpdate();
			
			pst2 = conn.prepareStatement(updateQuery);
			pst2.setInt(1, driverID);
			pst2.executeUpdate();
			
		} catch (Exception e) {
			System.out.println("All drivers are at rest. Please wait");
		}
		 finally {
			pst.close();
			pst2.close();
			conn.close();
		}
	 }	
	    
	 
	private static int checkMinDistance(String source, int srcDistance) throws SQLException {
		
		int min = 0;
        Connection connection = null;
        PreparedStatement st = null, pst = null;
        Statement statement = null;
        ResultSet rs = null, rset = null, resPst = null;
        
        String cabParkingQuery = "select count(cabId) as count from cabposition;";
		String cabLocationStringQuery = "select * from drivers join cabposition where cabposition.location != ? and cabposition.cabId = drivers.driverId and drivers.hasRest = true;";
        String nearbyLocationQuery = "select distanceFromOrigin dis from locations where name = ?;";
        

        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
			connection  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			statement = connection.createStatement();
			rs = statement.executeQuery(cabParkingQuery);
			rs.next();
			int parkings = rs.getInt("count"); 
			
			String[] cabParkings = new String[parkings];
	        int j=0;
			
	        st = connection.prepareStatement(cabLocationStringQuery);
	        st.setString(1, source);
			rset = st.executeQuery();
			
			while(rset.next())
			{
				cabParkings[j] = rset.getString("location");
				j++;
			}
	        int[] distanceFromSource = new int[j];
			
	        for (int i = 0; i < j; i++) {
	        	String nearbyLocation = cabParkings[i];
	        	pst = connection.prepareStatement(nearbyLocationQuery);
	        	pst.setString(1, nearbyLocation);
	        	resPst = pst.executeQuery();
	        	resPst.next();
	        	int destDistance = resPst.getInt("dis");
	        	int distanceDifference = Math.abs(srcDistance - destDistance);
	            distanceFromSource[i] = distanceDifference;
	        }
	        System.out.println();
	        int i;
	        min = distanceFromSource[0];
	        for (i = 1; i < j; i++) {
	            if (min > distanceFromSource[i]) {
	                min = distanceFromSource[i];}}	
	        return min;
		} catch (Exception e) {
			System.out.println("Unable to check the cab near you. Please try again later...");
		}
        finally {
        	rs.close();
        	rset.close();
        	resPst.close();
        	statement.close();
        	st.close();
        	pst.close();
        	connection.close();
		}
        return min;
	}

	private static void userChoiceSwitch(int userChoice) {
        switch (userChoice) {
            case 1 -> System.out.println("Booking cab...");
            case 0 -> System.out.println("Thank You!");
            default -> System.out.println("Please enter a valid choice");}}

	private static int calculateFare(String source, String destination) throws SQLException {
		
		int src,dest, fare;
        Connection conn=null;
        PreparedStatement smt=null;
        ResultSet rs=null;
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
			conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			
			String findDistance = "select distanceFromOrigin from locations where name = ? or name = ?;";
			smt=conn.prepareStatement(findDistance);
			smt.setString(1,source);
			smt.setString(2,destination);
			rs= smt.executeQuery();
			rs.next();
			src = rs.getInt(1);
			rs.next();
			dest = rs.getInt(1);
		     int distance = Math.abs(src - dest);
		     fare = distance * 10;
		     System.out.println("You might tend to pay Rs." + fare);
		    return fare;	
        }
        catch (Exception e) {
        	System.out.println("Unable to calculate distance. Please try again...");
		}
        finally {
        	rs.close();
        	smt.close();
        	conn.close();
		}
        return 0;
	}


	private static void printCabParked() throws SQLException {
		System.out.println("\t\t\tOur cabs are currently in :");
        System.out.printf("%15s %15s","Location","Cab ID");
        System.out.println();
        
        Connection conn=null;
        Statement smt=null;
        ResultSet rs=null;
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
			conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			smt=conn.createStatement();
			rs= smt.executeQuery("select * from cabposition;");
			while(rs.next())
			{
				String location = rs.getString("location");
				int cabId = rs.getInt("cabId");
				System.out.printf("%15s %15d",location, cabId);
				System.out.println();
			}
        }
        catch (Exception e) {
        	System.out.println("Sorry! This content is unavailable to display...");
		}
        finally {
        	rs.close();
        	smt.close();
        	conn.close();
		}
	}


	private static void customerSignup() throws SQLException {
		Scanner cin = new Scanner(System.in);
        System.out.println("Name: ");
        String cname = cin.nextLine();
        System.out.println("Age: ");
        int cage = cin.nextByte();
        Scanner sc = new Scanner(System.in);
        System.out.println("pwd: ");
        String cpwd = sc.nextLine();
        Connection conn = null;
		ResultSet rs = null;
		PreparedStatement smt = null;
		int custId =0;
       try {
    	   Class.forName("com.mysql.cj.jdbc.Driver");
			conn  = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
			String signUpQueryString = "insert into customers values(customerId,?,?,?)";
			smt = conn.prepareStatement(signUpQueryString);
			smt.setString(1, cname);
			smt.setString(2, cpwd);
			smt.setInt(3, cage);
			smt.executeUpdate();		
			rs = smt.executeQuery("select * from drivers order by driverId desc limit 1;");
			rs.next();
			custId = rs.getInt(1);
			System.out.println("Successfully added!");
	        System.out.println("Please log in to continue...");
	       	}        
       catch (InputMismatchException imee) {
    	   System.out.println("Enter valid input");} 
       catch (Exception e) {
    	   System.out.println("Sorry! Unable to add. Please try again later...");} 
       finally {
		if(rs!= null)
		{
			rs.close();
		}
		if(smt != null)
		{
			smt.close();
		}
		if(conn != null)
		{
			conn.close();
		}
       }
		
	}


	private static void driver() throws SQLException {
		System.out.println();
        System.out.println("\t\t\tDriver's portal");
        driverlogin();}


	private static void driverlogin() throws SQLException {
		System.out.println("Enter your name: ");
        Scanner din = new Scanner(System.in);
        String verifyName = din.nextLine();
        System.out.println("Enter password: ");
        String verifyPass = din.nextLine();
        boolean isAuthentic = false;
        
       
        Connection conn = null;
        PreparedStatement statement= null;
        Statement st=null;
        ResultSet rs=null;
        int driId=0;
        
        try {
        	 Class.forName("com.mysql.cj.jdbc.Driver");
        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
            String loginQuery = "Select * from drivers where name = ? && password = ? ";
            st=conn.createStatement();
            statement=conn.prepareStatement(loginQuery);
        	statement.setString(1, verifyName);
        	statement.setString(2, verifyPass);
        	rs = statement.executeQuery();
        	if(rs.next())
        	{
        		driId = rs.getInt(1);
        		isAuthentic = true;
	            System.out.println();
        		System.out.println("\t\tWelcome "+verifyName+"!");
        		driverChoiceSwitch(driId);
        		
        	}
        	else {
	            System.out.println("Sorry! Username or Password incorrect...");}
        }
        catch (Exception e) {
			System.out.println("Could not log in.. Please try again");
		}
        finally {
			rs.close();
			statement.close();
			conn.close();}
	}


	private static void driverChoiceSwitch(int driId) throws SQLException {
		System.out.println("1. Show my summary.\n2. Exit");
        Scanner drin = new Scanner(System.in);
        byte driverChoice = drin.nextByte();
        switch(driverChoice) {
            case 1:
                driverSummary(driId);
                driverChoiceSwitch(driId);
                break;
            case 2:
                break;
            default:
                System.out.println("Please enter a valid choice.");
                driverChoiceSwitch(driId);}}


	private static void driverSummary(int driId) throws SQLException {
	    Connection conn = null;
        PreparedStatement statement= null;
        Statement st=null;
        ResultSet rs=null, sr=null;
        
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
        	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zula", "root", "Srithika@070202");
        	String queryString= "select * from summary inner join drivers on  summary.cabId = drivers.driverId and drivers.driverId=?;";
            st=conn.createStatement();
            sr = st.executeQuery("select name from drivers where driverId = "+driId);
            sr.next();
            String driName = sr.getString("name");
            System.out.println();
            System.out.println("Driver id: "+driId);
            System.out.println("Driver Name: "+ driName);
            System.out.println("Trip details: ");
            System.out.printf("%15s %15s %20s %10s %20s", "Source","Destination","Customer Detail","Fare","Zula Commission");
            System.out.println();
            
           
            statement=conn.prepareStatement(queryString);
        	statement.setInt(1, driId);
        	rs = statement.executeQuery();
        	while(rs.next())
        	{
        		String src = rs.getString("source");
        		String dest = rs.getString("destination");
        		int custId = rs.getInt("customerId");
        		int custFare= rs.getInt("fare");
        		Float zulaComm = rs.getFloat("zulaCommission");
        		System.out.printf("%15s %15s %20s %10s %20s",src,dest,custId,custFare,zulaComm);
                System.out.println();
        	}
        }
        catch (Exception e) {
        	System.out.println("Sorry! Unable to display the details now. Please try again later");
		}
        finally {
			rs.close();
			sr.close();
			st.close();
			statement.close();
			conn.close();
		}
}
}
