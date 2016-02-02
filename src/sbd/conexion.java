/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sbd;
import java.sql.*;
import javax.swing.JOptionPane;
import java.sql.SQLException;
/**
 *
 * @author Andrés
 */
public class conexion {
    
    public Connection conectar(String server){
        Connection cn=null;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cn=DriverManager.getConnection("jdbc:sqlserver://"+server+";user=sa;password=sa");
        }catch(Exception ex){
            System.out.println("error: " +ex);
        }
        return cn;
    }
    
    public Connection conectarBase(String server,String base){
        Connection cn=null;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cn=DriverManager.getConnection("jdbc:sqlserver://"+server+";databaseName="+base+";user=sa;password=sa");
        }catch(Exception ex){
            System.out.println("error: " +ex);
        }
        return cn;
    }
    
}
