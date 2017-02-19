package core.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import core.Helper;

public class DatabaseSQLite extends DatabaseRelational {
	
	protected static Connection conn = null;
	
	protected PreparedStatement pstmt = null;
	protected ResultSet rs = null;
	protected int pointer = 0;
	protected boolean closed = false;
	
	public boolean connect() 
	{
		if(conn != null){
			return true;
		}
		
        try {
            Class.forName("org.sqlite.JDBC");
            File f = new File("users.db");
            boolean newDatabase = !f.exists(); 
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");
            if(newDatabase){
            	dump(loadResourceAsString("sql/dbdump.sql"));
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            conn = null;
            return false;
        }
    }
	
	public boolean dump(String dump) 
	{
		if(conn == null){
			return false;
		}
		
        try {
        	Statement stmt = conn.createStatement();
        	Vector<String> vector = Helper.parseSQLDump(dump);
        	for (String sql : vector) {
        		stmt.execute(sql);
        	}
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
	
	public boolean prepare(String sql){
		if(conn == null){
			return false;
		}
		
        try {
        	pstmt = conn.prepareStatement(sql);
        	pointer = 1;
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
	}
	
	public boolean add(String param){
		if(conn == null || pstmt == null){
			return false;
		}
		
        try {
        	pstmt.setString(pointer, param);
        	pointer++;
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
	}
	
	public boolean add(int param){
		if(conn == null || pstmt == null){
			return false;
		}
		
        try {
        	pstmt.setInt(pointer, param);
        	pointer++;
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
	}
	
	public boolean select(){
		if(conn == null || pstmt == null){
			return false;
		}
		
        try {
        	rs = pstmt.executeQuery();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
	}
	
	public boolean selectOne(){
		if(conn == null || pstmt == null){
			return false;
		}
		
        try {
        	rs = pstmt.executeQuery();
        	if(next()){
        		return true;
        	}
        	else{
        		return false;
        	}
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
	}
	
	public String getString(String paramName) throws SQLException{
		if(rs != null){
			return rs.getString(paramName);
		}
		return "";
	}
	
	public int getInt(String paramName) throws SQLException{
		if(rs != null){
			return rs.getInt(paramName);
		}
		return 0;
	}
	
	public boolean next() throws SQLException{
		if(!rs.next()){
    		rs = null;
    		return false;
    	}
		
		return true;
	}
}
