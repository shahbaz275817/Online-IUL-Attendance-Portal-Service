package com.sk;

/**
 * Created by shahbaz275817 on 10/2/17.
 */


import java.sql.*;
public class UserDataDao {

	private static Connection conn;
	
	public UserDataDao() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://xx.xx.xx.xx/clgdb", "root","xxxxxx");
        
	}
	
		
    public UserData getUser(int i){
        UserData userData=new UserData();
        String query="select * from users where id=?";
        try {
            PreparedStatement st=conn.prepareStatement(query);
            st.setInt(1,i);
            ResultSet rs=st.executeQuery();
            rs.next();
            String enrollmentNo=rs.getString(2);
            String password=rs.getString(3);
            userData.setEnrollmentNo(enrollmentNo);
            userData.setPassword(password);
            return userData;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return userData;
    }

    public void saveHTMLData(int i,String html){
        String query="update users set html_data=? where id=?";
        try {
            PreparedStatement st=conn.prepareStatement(query);
            st.setString(1,html);
            st.setInt(2,i);
            st.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public int getTotalUsers(){
        int i=0;
        
        
        String query="select id from users;";
        try {
            PreparedStatement st=conn.prepareStatement(query);
            ResultSet rs=st.executeQuery();
            rs.last();
            i=rs.getRow();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return i;
    }
}
