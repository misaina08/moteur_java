package utilitaire;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Connection;
public class Connexion
{
	 public Connection getConn()
	  {
			String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
			String login = "moteur";
			String password = "moteur";
			Connection con=null;
			try{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection(url,login,password);
			  System.out.println("driver ok");
				return con;
			  
			}
			catch(Exception sqle){
				System.out.println("driver not ok");
			   sqle.printStackTrace();
			   
			}
			return con;
	  }	
}