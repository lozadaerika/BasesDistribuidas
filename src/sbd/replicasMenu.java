package sbd;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultTreeCellEditor.DefaultTextField;

/**
 *
 * @author Andrés
 */
public class replicasMenu extends javax.swing.JFrame {

    /** Creates new form replicasMenu */
    public replicasMenu(String server, String base) {
        initComponents();
        servidor=server;this.base=base;
        cargarTabla(server,base);
        cargarCampos();
        cargarBases(server);
    }
    
    int nodos=0;
    String servidor,base,a="",b="",c="";
    
    DefaultListModel<String>listaIzq=new DefaultListModel<String>();
    DefaultListModel<String>listaDer=new DefaultListModel<String>();
    
    DefaultTableModel modelo;
    
    public void cargarBases(String server){
        jcBase.removeAllItems();
        jcBase.addItem("");
        String sql;
        sql="SELECT name FROM sys.databases";
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectar(server);
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            ResultSet rs=psd.executeQuery();
            while(rs.next()){
                jcBase.addItem(rs.getString("name"));
            }
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e);
        }
    }
    
    public void tablas(){
        if (String.valueOf(jcBase.getSelectedItem())!=""){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,String.valueOf(jcBase.getSelectedItem()));
        String titulos[] = null,Registros[] = null;
        String sql;
        sql="USE "+String.valueOf(jcBase.getSelectedItem())+" SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE'";
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            ResultSet rs=psd.executeQuery();
            if(rs.next()){
                cargarTabla(servidor,String.valueOf(jcBase.getSelectedItem()));
            }
            
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e);
        }
        }
    }
    
    public void cargarCampos(){
        int i=0;
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,base);
        String titulos[] = null,Registros[] = null;
        String sql_campos,sql_cantidad,sql;
        sql_cantidad="USE proyecto SELECT COUNT(COLUMN_NAME) as C FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql_campos="USE proyecto SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql="SELECT * FROM clientes";
        try{
            PreparedStatement psd_cantidad=cn.prepareStatement(sql_cantidad);
            ResultSet rs_cantidad=psd_cantidad.executeQuery();
            PreparedStatement psd_campos=cn.prepareStatement(sql_campos);
            ResultSet rs_campos=psd_campos.executeQuery();
            if(rs_cantidad.next()){
                titulos=new String[rs_cantidad.getInt("C")];
                Registros=new String[rs_cantidad.getInt("C")];
                while(rs_campos.next()){
                    listaIzq.addElement(rs_campos.getString("COLUMN_NAME"));
                }
            }
            jlCamposIzq.setModel(listaIzq);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e);
        }
    }
    
    public void cargarTabla(String server, String base){
        int i=0;
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,base);
        String titulos[] = null,Registros[] = null;
        String sql_campos,sql_cantidad,sql;
        sql_cantidad="USE proyecto SELECT COUNT(COLUMN_NAME) as C FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql_campos="USE proyecto SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql="SELECT * FROM clientes";
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
    conexion cc=new conexion();
    Connection cn;

    public void modificarTabla(){
        cn=(Connection) cc.conectarBase(servidor, base);
        String sql="";
        for(int fila=0;fila<tblTabla.getRowCount();fila++){
            sql="use [proyecto]\nUPDATE clientes SET Nombre='"+String.valueOf(tblTabla.getValueAt(fila, 1)).toUpperCase() +"', "
                            + "Apellido='"+String.valueOf(tblTabla.getValueAt(fila, 2)).toUpperCase() +"', "
                            + "Telefono='"+String.valueOf(tblTabla.getValueAt(fila, 3)) +"', "
                            + "Direccion='"+String.valueOf(tblTabla.getValueAt(fila, 4)).toUpperCase() +"', "
                            + "Ciudad='"+String.valueOf(tblTabla.getValueAt(fila, 5)) +"'"
                +"WHERE CI='"+tblTabla.getValueAt(fila, 0) +"'";    
            try {
                PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);     
                int n=psd.executeUpdate();
            if(n>0){
                System.out.println("Se actualizo correctamente");
                cargarTabla(servidor,String.valueOf(jcBase.getSelectedItem()));
            }
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null, ex); 
            }
        }
    }
    
    public void insertar(){
        cn=(Connection) cc.conectarBase(servidor, base);
        String sql="";
        sql="use [proyecto]\nINSERT INTO clientes VALUES(?,?,?,?,?,?)";
        try {
            PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);
            psd.setString(1,JOptionPane.showInputDialog(null, "CI"));
            psd.setString(2,JOptionPane.showInputDialog(null, "Nombre"));
            psd.setString(3,JOptionPane.showInputDialog(null, "Apellido"));
            psd.setInt(4,Integer.valueOf(JOptionPane.showInputDialog(null, "Telefono")));
            psd.setString(5,JOptionPane.showInputDialog(null, "Direccion"));
            psd.setString(6,JOptionPane.showInputDialog(null, "Ciudad"));
            int n=psd.executeUpdate();
            if(n>0){
                JOptionPane.showMessageDialog(null, "Se inserto correctamente "); 
                cargarTabla(servidor,String.valueOf(jcBase.getSelectedItem()));
            }           
        } 
       catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex);
        }
       }
    
    public void eliminar(int fila){
        cn=(Connection) cc.conectarBase(servidor, base);
        String sql="";
        sql="use [proyecto]\nDELETE FROM clientes WHERE CI='"+tblTabla.getValueAt(fila, 0) +"'";
        try {
            PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);
            int n=psd.executeUpdate();
            if (n>0){
                JOptionPane.showMessageDialog(null, "Registro borrado correctamente");
                cargarTabla(servidor,String.valueOf(jcBase.getSelectedItem()));
            }
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex);
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

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTabla = new javax.swing.JTable();
        btnInsertar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtNombrePub = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jchA = new javax.swing.JCheckBox();
        jchB = new javax.swing.JCheckBox();
        jchC = new javax.swing.JCheckBox();
        btnSincronizar = new javax.swing.JButton();
        jrbConectar = new javax.swing.JRadioButton();
        jrbDesconectar = new javax.swing.JRadioButton();
        btnModificar = new javax.swing.JButton();
        jcBase = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlCamposDer = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlCamposIzq = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtFiltros = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jrbSnapshot = new javax.swing.JRadioButtonMenuItem();
        jrbMezcla = new javax.swing.JRadioButtonMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jrbTranEstandar = new javax.swing.JRadioButtonMenuItem();
        jrbTranCola = new javax.swing.JRadioButtonMenuItem();
        jrbTranPeer = new javax.swing.JRadioButtonMenuItem();
        jMenu5 = new javax.swing.JMenu();

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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
        tblTabla.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblTablaKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblTablaKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblTabla);

        btnInsertar.setText("Insertar");
        btnInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertarActionPerformed(evt);
            }
        });

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        jLabel1.setText("Nombre");

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel2.setText("Nodos");

        jchA.setText("A");
        jchA.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchAItemStateChanged(evt);
            }
        });
        jchA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jchAActionPerformed(evt);
            }
        });

        jchB.setText("B");
        jchB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchBItemStateChanged(evt);
            }
        });

        jchC.setText("C");
        jchC.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jchCItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jchB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jchC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jchA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(191, 191, 191))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jchA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jchB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addComponent(jchC))
        );

        btnSincronizar.setText("Replicar");
        btnSincronizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSincronizarActionPerformed(evt);
            }
        });

        buttonGroup1.add(jrbConectar);
        jrbConectar.setText("Conectar");

        buttonGroup1.add(jrbDesconectar);
        jrbDesconectar.setText("Desconectar");

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        jLabel6.setText("Bases");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setText("Filtros Horizontales");

        jScrollPane2.setViewportView(jlCamposDer);

        jLabel5.setText("Utilizadas");

        jButton4.setText(">");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("<");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jlCamposIzq);

        jLabel4.setText("Disponibles");

        jLabel3.setText("Filtros Verticales");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(162, 162, 162)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 311, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(265, 265, 265))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(393, 393, 393)
                .addComponent(txtFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(4, 4, 4)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton4)
                                .addComponent(jButton5))
                            .addGap(10, 10, 10)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(55, 55, 55)
                            .addComponent(jLabel4)
                            .addGap(162, 162, 162)
                            .addComponent(jLabel5)))
                    .addContainerGap(526, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(180, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel4))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                            .addGap(64, 64, 64)
                            .addComponent(jButton4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jButton5))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                    .addContainerGap()))
        );

        jButton1.setText("Administrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtNombrePub, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcBase, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSincronizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jrbConectar)
                                    .addComponent(jrbDesconectar)))
                            .addComponent(btnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnInsertar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jrbConectar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jrbDesconectar))
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnInsertar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSincronizar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNombrePub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel6)
                            .addComponent(jcBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenu1.setText("Publicaciones");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });

        buttonGroup2.add(jrbSnapshot);
        jrbSnapshot.setSelected(true);
        jrbSnapshot.setText("Snapshot");
        jMenu1.add(jrbSnapshot);

        buttonGroup2.add(jrbMezcla);
        jrbMezcla.setText("Mezcla");
        jMenu1.add(jrbMezcla);

        jMenu6.setText("Transaccional");

        buttonGroup2.add(jrbTranEstandar);
        jrbTranEstandar.setText("Estandar");
        jMenu6.add(jrbTranEstandar);

        buttonGroup2.add(jrbTranCola);
        jrbTranCola.setText("Cola");
        jMenu6.add(jrbTranCola);

        buttonGroup2.add(jrbTranPeer);
        jrbTranPeer.setText("Peer to Peer");
        jMenu6.add(jrbTranPeer);

        jMenu1.add(jMenu6);

        jMenuBar1.add(jMenu1);

        jMenu5.setText("Salir");
        jMenu5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu5MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu5);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
    
}//GEN-LAST:event_jButton4MouseClicked

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    if (jlCamposIzq.getSelectedValue()!=null){
        listaDer.addElement(String.valueOf(jlCamposIzq.getSelectedValue()));
        listaIzq.removeElementAt(jlCamposIzq.getSelectedIndex());
        jlCamposDer.setModel(listaDer);   
    }
}//GEN-LAST:event_jButton4ActionPerformed

private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    if (jlCamposDer.getSelectedValue()!=null){
        listaIzq.addElement(String.valueOf(jlCamposDer.getSelectedValue()));
        listaDer.removeElementAt(jlCamposDer.getSelectedIndex());
        jlCamposIzq.setModel(listaIzq);       
    }
}//GEN-LAST:event_jButton5ActionPerformed

private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
    int fila=tblTabla.getSelectedRow();    
    eliminar(fila);
}//GEN-LAST:event_btnEliminarActionPerformed

private void jchAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchAActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jchAActionPerformed

private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
    cargarTabla(servidor,String.valueOf(jcBase.getSelectedItem()));
}//GEN-LAST:event_btnActualizarActionPerformed

private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
    modificarTabla();
}//GEN-LAST:event_btnModificarActionPerformed

private void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
    insertar();    
}//GEN-LAST:event_btnInsertarActionPerformed

private void jMenu5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu5MouseClicked
    System.exit(0);
}//GEN-LAST:event_jMenu5MouseClicked

private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
    
}//GEN-LAST:event_jMenu1MouseClicked

private void btnSincronizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSincronizarActionPerformed
    if (jrbSnapshot.isSelected())
        try {
            ejecutar(sqlPublicacionSnap(txtNombrePub.getText()));
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOOR");
        }
    else
        if (jrbTranEstandar.isSelected())
        JOptionPane.showMessageDialog(null, "Transaccional Estandar");
    else if (jrbTranCola.isSelected())    
        JOptionPane.showMessageDialog(null, "Transaccional Cola");
    else if (jrbTranPeer.isSelected())
        JOptionPane.showMessageDialog(null, "Transaccional Peer to Peer");
    else if (jrbMezcla.isSelected())    
        JOptionPane.showMessageDialog(null, "Mezcla");
    
         try {
                ejecutar(crearTablaSuscripcion(),servidor);
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
    
    //////suscripcion
    if (!"".equals(a)){
        JOptionPane.showMessageDialog(null, "A: "+a);
            try {
                ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),servidor+"\\SITIOA"),servidor);
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
            }
}
    if (!"".equals(b)){
        JOptionPane.showMessageDialog(null, "B: "+b);
            try {
                ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),servidor+"\\SITIOB"),servidor);
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
}
    if (!"".equals(c)){
           JOptionPane.showMessageDialog(null, "C: "+c);
        try {
            ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),servidor));
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "AQUI");
        }
    }
    JOptionPane.showMessageDialog(null, "Completado");
}//GEN-LAST:event_btnSincronizarActionPerformed

private void tblTablaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTablaKeyReleased
    
}//GEN-LAST:event_tblTablaKeyReleased

private void tblTablaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTablaKeyTyped
    
}//GEN-LAST:event_tblTablaKeyTyped

private void jchAItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchAItemStateChanged
    if(jchA.isSelected())
        a=sqlSuscripcionSnap(txtNombrePub.getText(), "ERIKA-LAP\\SITIOA");
    else
        a="";
}//GEN-LAST:event_jchAItemStateChanged

private void jchBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchBItemStateChanged
    if(jchB.isSelected())
        b=sqlSuscripcionSnap(txtNombrePub.getText(), "ERIKA-LAP\\SITIOB");    
    else
        b="";
}//GEN-LAST:event_jchBItemStateChanged

private void jchCItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchCItemStateChanged
    if(jchC.isSelected())
        c=sqlSuscripcionSnap(txtNombrePub.getText(), servidor);
    else
        c="";
}//GEN-LAST:event_jchCItemStateChanged

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    BaseDeDatos bd=new BaseDeDatos(servidor);
    bd.show();
}//GEN-LAST:event_jButton1ActionPerformed

public static String aux,atributos,tipo;
int k=1;

public String sqlPublicacionSnap(String nombre){
    aux="";atributos="";tipo="";
    aux=""+
            "use [proyecto] exec sp_replicationdboption @dbname = N'proyecto', @optname = N'publish', @value = N'true'";
    tipo=""+
"use [proyecto] exec sp_replicationdboption @dbname = N'proyecto', @optname = N'publish', @value = N'true'\n"+
"use [proyecto] exec sp_addpublication @publication = N'"+nombre+"', @description = N'Snapshot publication of database ''proyecto'' from Publisher ''"+servidor+"''.',"
+ " @sync_method = N'native', @retention = 0, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'true', @enabled_for_internet = N'false',"
+ " @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_login = N'anonymous', @allow_subscription_copy = N'false', "
+ "@add_to_active_directory = N'false', @repl_freq = N'snapshot', @status = N'active', @independent_agent = N'true', @immediate_sync = N'true',"
+ " @allow_sync_tran = N'false', @autogen_sync_procs = N'false', @allow_queued_tran = N'false', @allow_dts = N'false', @replicate_ddl = 1"
                + "\nexec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 1, @frequency_relative_interval = 1,"
            + " @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 0, "
            + "@active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null,"
            + " @publisher_security_mode = 1";    
atributos="use [proyecto] exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased',"
+ " @description = null, @creation_script = null, @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509D, @identityrangemanagementoption = N'manual',"
+ " @destination_table = N'clientes', @destination_owner = N'dbo', @vertical_partition = ";
    
    if (listaDer.isEmpty())
        atributos=atributos+"N'false'";
    else{
        atributos=atributos+"N'true'";
        for (int i=0;i<listaDer.getSize();i++){
            atributos=atributos+""
                    + " exec sp_articlecolumn @publication = N'"+nombre+"', @article = N'clientes', @column = N'"+listaDer.getElementAt(i)+"', @operation = N'add', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        }
        atributos=atributos
                + " exec sp_articleview @publication = N'"+nombre+"', @article = N'clientes', @view_name = N'SYNC_clientes_"+k+"__65', @filter_clause = null, @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        k++;
    }
    return aux+tipo+atributos;
}

public String sqlSuscripcionSnap(String nombre,String nodo){
String suscripcion="";
suscripcion=""+ "exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'clientes_practica',"
+ "@subscription_type = N'Push', @sync_type = N'automatic', @article = N'all', @update_mode = N'read only', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'clientes_practica', "
+ "@job_login = null, @job_password = null, @subscriber_security_mode = 1, @frequency_type = 64, @frequency_interval = 0, @frequency_relative_interval = 0,"
+ " @frequency_recurrence_factor = 0, @frequency_subday = 0, @frequency_subday_interval = 0, @active_start_time_of_day = 0, @active_end_time_of_day = 235959,"
+ " @active_start_date = 20160122, @active_end_date = 99991231, @enabled_for_syncmgr = N'False', @dts_package_location = N'Distributor'";
return suscripcion;
}

public String crearTablaSuscripcion()
{ 
   String sentencia=""+"USE [clientes_practica] SET ANSI_NULLS ON SET QUOTED_IDENTIFIER ON "
            + "CREATE TABLE [dbo].[clientes](";
    if(listaDer.isEmpty())
    {
    sentencia= sentencia
            + "[CI] [nchar](10) NOT NULL,"
            + "[Nombre] [nchar](20) NULL,"
            + "[Apellido] [nchar](20) NULL,"
            + "[Telefono] [nchar](10) NULL,"
            + "[Direccion] [nchar](30) NULL,"
            + "[Ciudad] [nchar](20) NULL,"
            + "[Edad] [int] NULL,"
            + "CONSTRAINT [PK_clientes] PRIMARY KEY CLUSTERED ([CI] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]) ON [PRIMARY]";
    } else
    {
        if(listaDer.contains("CI"))
        sentencia+="[CI] [nchar](10) NOT NULL,";
        if(listaDer.contains("Nombre"))
        sentencia+="[Nombre] [nchar](20) NULL,";
        if(listaDer.contains("Apellido"))
        sentencia+="[Apellido] [nchar](20) NULL,";
        if(listaDer.contains("Telefono"))
         sentencia+="[Telefono] [nchar](10) NULL,";
        if(listaDer.contains("Direccion"))
          sentencia+="[Direccion] [nchar](30) NULL,";
        if(listaDer.contains("Ciudad"))
          sentencia+="[Ciudad] [nchar](20) NULL,";
        if(listaDer.contains("Edad"))
            sentencia+="[Edad] [int] NULL,";
        sentencia= sentencia+"CONSTRAINT [PK_clientes] PRIMARY KEY CLUSTERED ([CI] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]) ON [PRIMARY]";
    }
return sentencia;
}


public String filtros(String nombre) {
    String text="";
    if ("".equals(txtFiltros.getText())){
        text=text+",@filter_clause = N'"+txtFiltros.getText()+"'\n"
                 + "-- Adding the article filter\n"
            + "exec sp_articlefilter @publication = N'"+nombre+"', @article = N'clientes', @filter_name = N'FLTR_clientes_"+k+"__67', @filter_clause = N'"+txtFiltros.getText() +"', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1\n"
            + "-- Adding the article synchronization object\n"
                + "exec sp_articleview @publication = N'"+nombre+"', @article = N'clientes', @view_name = N'SYNC_clientes_"+k+"__67', @filter_clause = N'"+txtFiltros.getText()+"', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        return text;
    }
    return "";
}

public void ejecutar(String sql) throws SQLException{
        cn=(Connection) cc.conectarBase(servidor,base);
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            psd.execute();
        }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede crear"+e.getMessage());
        }
}
public void ejecutar(String sql,String server) throws SQLException{
        cn.close();
        cn=(Connection) cc.conectar(server);
        try{ 
            PreparedStatement psd=cn.prepareStatement(sql);
            psd.execute();
        }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede crear Sobrecarga"+e.getMessage());
        }
}
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
            java.util.logging.Logger.getLogger(replicasMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(replicasMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(replicasMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(replicasMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new replicasMenu("","").setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnInsertar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnSincronizar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JComboBox jcBase;
    private javax.swing.JCheckBox jchA;
    private javax.swing.JCheckBox jchB;
    private javax.swing.JCheckBox jchC;
    private javax.swing.JList jlCamposDer;
    private javax.swing.JList jlCamposIzq;
    private javax.swing.JRadioButton jrbConectar;
    private javax.swing.JRadioButton jrbDesconectar;
    private javax.swing.JRadioButtonMenuItem jrbMezcla;
    private javax.swing.JRadioButtonMenuItem jrbSnapshot;
    private javax.swing.JRadioButtonMenuItem jrbTranCola;
    private javax.swing.JRadioButtonMenuItem jrbTranEstandar;
    private javax.swing.JRadioButtonMenuItem jrbTranPeer;
    private javax.swing.JTable tblTabla;
    private javax.swing.JTextField txtFiltros;
    private javax.swing.JTextField txtNombrePub;
    // End of variables declaration//GEN-END:variables
}