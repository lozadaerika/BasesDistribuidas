package sbd;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Andrés
 */
public class Datos_Principal extends javax.swing.JInternalFrame {

    /** Creates new form Datos_Principal */
    public Datos_Principal(String server,String base, String tabla) {
        initComponents();
        this.setClosable(true);
        this.setMaximizable(true);
        baseDatos=base;
        tablaDatos=tabla;
        servidor=server;
        cargarTabla(tabla);
    }
    
    String baseDatos,tablaDatos,servidor;
    String titulos[] = null;
    DefaultTableModel modelo;
    
    public void cargarTabla(String tabla){
        int i=0;
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,baseDatos);
        String Registros[] = null;
        String sql_campos,sql_cantidad,sql;
        sql_cantidad="USE "+baseDatos+" SELECT COUNT(COLUMN_NAME) as C FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"+tablaDatos+"'";
        sql_campos="USE "+baseDatos+" SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"+tablaDatos+"'";
        sql="SELECT * FROM "+tabla;
        try{
            PreparedStatement psd_cantidad=cn.prepareStatement(sql_cantidad);
            ResultSet rs_cantidad=psd_cantidad.executeQuery();
            PreparedStatement psd_campos=cn.prepareStatement(sql_campos);
            ResultSet rs_campos=psd_campos.executeQuery();
            PreparedStatement psd_tabla=cn.prepareStatement(sql);
            ResultSet rs_tabla=psd_tabla.executeQuery();
            if(rs_cantidad.next()){
                titulos=new String[rs_cantidad.getInt("C")];
                Registros=new String[rs_cantidad.getInt("C")];
                while(rs_campos.next()){
                    titulos[i]=rs_campos.getString("COLUMN_NAME");
                    i++;
                }
                modelo=new DefaultTableModel(null, titulos);
                while(rs_tabla.next()){
                    i=0;
                    while(i<titulos.length){
                        Registros[i]=rs_tabla.getString(titulos[i]);
                        i++;
                    }
                    modelo.addRow(Registros);
                }
                tblTabla.setModel(modelo);
            }
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e);
        }
    }
    
    public void modificarTabla(){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,baseDatos);
        String sql="";
        int op=0;
        if (("msrepl_tran_version".equals(titulos[titulos.length-1]))||("rowguid".equals(titulos[titulos.length-1]))){
            for(int fila=0;fila<tblTabla.getRowCount();fila++){
            sql="use ["+baseDatos+"]\nUPDATE clientes SET ";
            for(int col=0;col<tblTabla.getColumnCount()-1;col++){
                if ((col!=titulos.length-2))
                    sql=sql+titulos[col]+"='"+String.valueOf(tblTabla.getValueAt(fila, col)+"',");
                else
                    sql=sql+titulos[col]+"='"+String.valueOf(tblTabla.getValueAt(fila, col)+"' ");
            }
            sql=sql+"WHERE CI='"+tblTabla.getValueAt(fila, 0) +"'";
            try {
                PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);     
                int n=psd.executeUpdate();
            if(n>0){
                System.out.println("Se modifico correctamente");
            }
            }catch(SQLException e){
                  JOptionPane.showMessageDialog(null, "No se ha podido realizar el update"+e+" CODIGO: "+e.getErrorCode());
            }
            sql="";
        }
        }
        else{
            for(int fila=0;fila<tblTabla.getRowCount();fila++){
            sql="use ["+baseDatos+"]\nUPDATE clientes SET ";
            for(int col=0;col<tblTabla.getColumnCount();col++){
                if ((col!=titulos.length-1))
                    sql=sql+titulos[col]+"='"+String.valueOf(tblTabla.getValueAt(fila, col)+"',");
                else
                    sql=sql+titulos[col]+"='"+String.valueOf(tblTabla.getValueAt(fila, col)+"' ");
            }
            sql=sql+"WHERE CI='"+tblTabla.getValueAt(fila, 0) +"'";
            try {
                PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);     
                int n=psd.executeUpdate();
            if(n>0){
                System.out.println("Se modifico correctamente");
            }
            }catch(SQLException e){
                  JOptionPane.showMessageDialog(null, "No se ha podido realizar el update"+e+" CODIGO: "+e.getErrorCode());
            }
            sql="";
        }
            
        }
        cargarTabla(tablaDatos);
    }
    
public String sqlinsertar(){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,baseDatos);
        String Registros[] = null;
        String sql_campos,sql_cantidad,sql,campos="",datos="";
        sql_cantidad="USE "+baseDatos+" SELECT COUNT(COLUMN_NAME) as C FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql_campos="USE "+baseDatos+" SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql="SELECT * FROM clientes";
        int op=0,last=0;
        if (("msrepl_tran_version".equals(titulos[titulos.length-1]))||("rowguid".equals(titulos[titulos.length-1]))){
            for (int i=0;i<titulos.length;i++){
                if (("msrepl_tran_version".equals(titulos[titulos.length-1]))&&("rowguid".equals(titulos[titulos.length-1])))
                    op=1;
                else if (i==titulos.length-2)
                    last=1;
                        if ((!"msrepl_tran_version".equals(titulos[i]))&&(!"rowguid".equals(titulos[i]))&&(op!=1)&&(last!=1)){
                            campos=campos+titulos[i]+",";
                            datos=datos+"?,";
                        }
                        else{
                            if ((!"msrepl_tran_version".equals(titulos[i]))&&(!"rowguid".equals(titulos[i]))&&(op!=1)){
                            campos=campos+titulos[i];
                            datos=datos+"?";
                            }
                        }
                }
        }
        else{
            for (int i=0;i<titulos.length;i++){
                        if (i!=titulos.length-1){
                            campos=campos+titulos[i]+",";
                            datos=datos+"?,";
                        }
                        else{
                            campos=campos+titulos[i];
                            datos=datos+"?";
                            }
                        }
                }
        return "INSERT INTO clientes("+campos+") values("+datos+")";
    }
    
    public int sqlinsertarCantidad(){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,baseDatos);
        String sql_campos,sql_cantidad;
        sql_cantidad="USE "+baseDatos+" SELECT COUNT(COLUMN_NAME) as C FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        int i=1;
        try{
            PreparedStatement psd_cantidad=cn.prepareStatement(sql_cantidad);
            ResultSet rs_cantidad=psd_cantidad.executeQuery();
            if(rs_cantidad.next()){
                return rs_cantidad.getInt("C");
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e+" CODIGO: "+e.getErrorCode());
        }
        return 0;
    }
    
    public void insertar(String base){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,baseDatos);
        String sql_campos="USE "+baseDatos+" SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        int i=1;
        String sql=sqlinsertar();
        JOptionPane.showMessageDialog(null, sql);
        try {
            PreparedStatement psd_campos=(PreparedStatement) cn.prepareStatement(sql_campos);
            ResultSet rs_campos=psd_campos.executeQuery();
            PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);
            rs_campos.next();
            while(i<=sqlinsertarCantidad()){
                if ("Edad".equals(rs_campos.getString("COLUMN_NAME")))
                    psd.setInt(i,Integer.valueOf(JOptionPane.showInputDialog(null, rs_campos.getString("COLUMN_NAME"))));
                else
                    if (!"rowguid".equals(rs_campos.getString("COLUMN_NAME"))&&!"msrepl_tran_version".equals(rs_campos.getString("COLUMN_NAME")))
                        psd.setString(i,JOptionPane.showInputDialog(null, rs_campos.getString("COLUMN_NAME")));
                i++;
                rs_campos.next();
            }
            int n=psd.executeUpdate();
            if(n>0){
                JOptionPane.showMessageDialog(null, "Se inserto correctamente "); 
                cargarTabla(tablaDatos);
            }           
        } 
       catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex+" CODIGO: "+ex.getErrorCode());
        }
       }        
    public void eliminar(int fila){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,baseDatos);
        String sql="";
        sql="use ["+baseDatos+"]\nDELETE FROM clientes WHERE CI='"+tblTabla.getValueAt(fila, 0) +"'";
        try {
            PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);
            int n=psd.executeUpdate();
            if (n>0){
                JOptionPane.showMessageDialog(null, "Registro borrado correctamente");
                cargarTabla(tablaDatos);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex+" CODIGO: "+ex.getErrorCode());
        }
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblTabla = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btnInsertar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();

        tblTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblTabla);

        btnInsertar.setText("Insertar");
        btnInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertarActionPerformed(evt);
            }
        });

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                                .addComponent(btnInsertar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnInsertar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnActualizar)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
        insertar(baseDatos);
    }//GEN-LAST:event_btnInsertarActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        modificarTabla();
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        int fila=tblTabla.getSelectedRow();
        eliminar(fila);
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        cargarTabla(tablaDatos);
    }//GEN-LAST:event_btnActualizarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Datos_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Datos_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Datos_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Datos_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Datos_Principal("","","").setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnInsertar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTabla;
    // End of variables declaration//GEN-END:variables
}
