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
    public static String solucion="Conectese a un servidor disponible";
    public Connection conectar(String server){
        Connection cn=null;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cn=DriverManager.getConnection("jdbc:sqlserver://"+server+";user=sa;password=sa");
        }catch(Exception ex){
            if(replicasMenu.codigo!=0 && replicasMenu.codigo!=18483)
           JOptionPane.showMessageDialog(null, " ERROR CONEXION:"
                   + " \n\n EL PROBLEMA GENERADO PUEDE DEBERSE A LOS SIGUIENTES FACTORES: "
                   + "\n"+ex.getMessage().substring(0,46)+
                   "\n\nPOR FAVOR, PRUEBE LA SGUIENTE SOLUCION"+
                   "\n"+solucion);
            System.out.println("error: " +ex.getMessage());
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
            if(replicasMenu.codigo!=0 && replicasMenu.codigo!=18483)
         JOptionPane.showMessageDialog(null, " ERROR CONEXION:"
                   + " \n\n EL PROBLEMA GENERADO PUEDE DEBERSE A LOS SIGUIENTES FACTORES: "
                   + "\n"+ex.getMessage().substring(0,46)+
                   "\n\nPOR FAVOR, PRUEBE LA SGUIENTE SOLUCION"+
                   "\n"+solucion);
            System.out.println("error: " +ex.getMessage());
        }
        return cn;
    }
    
}
