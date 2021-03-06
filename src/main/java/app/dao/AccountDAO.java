package app.dao;

import app.dao.utils.DatabaseUtils;
import app.model.Account;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.mindrot.jbcrypt.BCrypt;



public class AccountDAO {
    public static final String SALT = "$2a$10$h.dl5J86rGH7I8bD9bZeZe";


    /**
     * Method to fetch users from the database.
     * You should use this as an example for future queries, though the sql statement
     * will change -and you are supposed to write them.
     *
     * Current user: caramel 6, password (the password is "password" without quotes)
     * @param username what the user typed in the log in form.
     * @return Some of the user data to check on the password. Null if there
     *         no matching user.
     */
    //contains a list of all the accounts in the system
    private static List<Account> accounts = getAllAccountsFromData();
    
    public static List<Account> getUnApprovedAccounts(){
    	List<Account> tmp = new ArrayList<Account>();
    	
    	for(Account a : accounts) {
    		if(a.isApproved() == false) {
    			tmp.add(a);
    		}
    	}
    	
    	return tmp;
    }
    
    //returns an account of a user
    public static Account getUserByUsername(String username) {
    	
    	for(Account a : accounts) {
    		if(a.getUsername().equals(username)) {
    			return a;
    		}
    	}
    	
    	return null;
    }
    
    //used for changing a column in the database
    //just pass an sql statemenmt
    public static void alterAccount(String sqlFormat) throws SQLException {
    	
		try {
			// open a connection to the database
			Connection connection = DatabaseUtils.connectToDatabase();
			Statement statement = connection.createStatement();

			statement.executeUpdate(sqlFormat);

			// close connection to the databsse
			DatabaseUtils.closeConnection(connection);
			// update the show list
			accounts = getAllAccountsFromData();

		} catch (Exception e) {
			throw new SQLException();
		}

	}
     
    
   //used for inserting into the database
    public static void insertNewAccount(Account account, boolean isNormal) {
    	//hash password
    	String sql = "";
    	String hashedPassword =  BCrypt.hashpw(account.getPassword(), SALT);
    	//convert booleans (only way it worked)
    	int isAdmin = (account.isAdmin()) ? 1 : 0;
    	int isProco = (account.isProco()) ? 1 : 0;
    	int isFilmCritic = (account.isFilmCritic()) ? 1 : 0;
    	int approved = (account.isApproved()) ? 1 : 0;
    	
    	//check if standard account or not and do something according to that
    	//used for the approved boolean in the database
    	if(isNormal) {
    		sql = "INSERT INTO imbd.account (username, password, email, country, postCode, gender, year_of_birth, first_name, last_name, admin, proCo, film_critic, approved) "
    			+ "VALUES('" + account.getUsername() + "', '" + hashedPassword + "', '" + account.getEmail() + "', '" + account.getCountry() + "', '" + account.getPostCode() + "', '" + account.getGender() + "', '" + account.getYearOfBirth() + "', '" + account.getFirstName() + "', '" + account.getLastName() + "', '" + isAdmin + "', '" + isProco + "', '" + isFilmCritic+ "', '" + approved + "')";
    	}else {
    		sql = "INSERT INTO imbd.account (username, password, email, country, postCode, gender, year_of_birth, first_name, last_name, admin, proCo, film_critic, organisation_name, organisation_phone, approved)"
        			+ "VALUES('" + account.getUsername() + "', '" + hashedPassword + "', '" + account.getEmail() + "', '" + account.getCountry() + "', '" + account.getPostCode() + "', '" + account.getGender() + "', '" + account.getYearOfBirth() + "', '" + account.getFirstName() + "', '" + account.getLastName() + "', '" + isAdmin + "', '" + isProco + "', '" + isFilmCritic + "', '" + account.getOrganisationName() + "', '" + account.getOrganisationPhone() + "', '" + approved + "')";
    	}
    	
    	try {
			// opens a connection to the database
			Connection connection = DatabaseUtils.connectToDatabase();
			// create statement object
			Statement statement = connection.createStatement();
			// use .executeUpdate() for inserting items into the database
			statement.executeUpdate(sql);
			
			accounts = getAllAccountsFromData();

			// close connection
			DatabaseUtils.closeConnection(connection);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    //gets all the accounts from the database
    private static List<Account> getAllAccountsFromData() {

    	List<Account> tmp = new ArrayList<Account>();
    	String sql = "SELECT * FROM imbd.account";
    	
    	try {
    		Connection connection = DatabaseUtils.connectToDatabase();
    		Statement statement = connection.createStatement();
    		ResultSet result = statement.executeQuery(sql);
    		
    		while(result.next()) {
    			tmp.add(new Account(result.getString("first_name"), result.getString("last_name"), result.getString("postCode"), result.getString("country"), result.getString("gender"), result.getInt("year_of_birth"), result.getString("email"), result.getString("username"), result.getString("password"), result.getBoolean("admin"), result.getBoolean("proCo"), result.getBoolean("film_critic"), result.getBoolean("approved"), result.getString("organisation_name"), result.getString("organisation_phone")));
    		}
    		
    		
    		DatabaseUtils.closeConnection(connection);
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	
    	return tmp;
    }

    //only gets the approved accounts in the database
	public static List<Account> getApprovedPCOAndFCAccounts() {
		List<Account> tmp = new ArrayList<Account>();
		for(Account a : accounts) {
			if(a.isFilmCritic() && a.isApproved()|| a.isProco() && a.isApproved()) {
				tmp.add(a);
			}
		}
		return tmp;
	}



}
