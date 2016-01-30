/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sbd;
import java.sql.*;
import javax.swing.JOptionPane;
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
            cn=DriverManager.getConnection("jdbc:sqlserver://"+server+";integratedSecurity=true");
        }catch(Exception ex){
            System.out.println("error: " +ex);
        }
        return cn;
    }
        public Connection conectarServidor(String server){
        Connection cn=null;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cn=DriverManager.getConnection("jdbc:sqlserver://"+server+":1433;integratedSecurity=true");
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
            cn=DriverManager.getConnection("jdbc:sqlserver://"+server+";databaseName="+base+";integratedSecurity=true");
        }catch(Exception ex){
            System.out.println("error: " +ex);
        }
        return cn;
    }
    
}
