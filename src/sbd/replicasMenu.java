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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor.DefaultTextField;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author Andrés
 */
public class replicasMenu extends javax.swing.JFrame {
int nodos=0;  static int codigo=0;
    String servidor,base,a="",b="",c="";
    String servidorUno="ERIKA-LAP";
    String servidorDos="ANDRES\\SITIO_A";
    String ServidorLocal="ANDRES";
    /** Creates new form replicasMenu */
    public replicasMenu(String server, String base) {
        initComponents();      
        servidor=server;this.base=base;
        ServidorLocal=server;
        cargarBases(server);
        MostrarPublicaciones(server);
        MostrarSuscripcion(server);
    }
    
    
   
    DefaultListModel<String>listaIzq=new DefaultListModel<String>();
    DefaultListModel<String>listaDer=new DefaultListModel<String>();
    DefaultListModel<String>listaFiltros=new DefaultListModel<String>();
     DefaultListModel<String>lista=new DefaultListModel<String>();
    
    DefaultTableModel modelo;
    
    public void MostrarPublicaciones(String server){
     //jcbPublicaciones.removeAllItems();
        String sqlCargarPublicaciones="Use distribution \n" +
            "\n" +
            "DECLARE @ArticleName SysName\n"+
            "\n" +
            "SELECT\n" +
            "     MSP.publication AS 'Publication Name'\n" +
            "    ,MSA.publisher_db AS 'Database Name'\n" +
//            "    ,MSA.article AS 'Article Name'\n" +
//            "    ,MSA.source_owner AS 'Schema Name'\n" +
//            "    ,MSA.source_object AS 'Table Name'\n" +
            "FROM\n" +
            "    DBO.MSarticles AS MSA\n" +
            "INNER JOIN DBO.MSpublications AS MSP\n" +
            "        ON MSA.publication_id = MSP.publication_id\n" ;    
        
       
           Connection cn=(Connection) cc.conectar(ServidorLocal);
        DefaultMutableTreeNode nodo= new DefaultMutableTreeNode("Publicaciones");
        try{
            PreparedStatement psd=cn.prepareStatement(sqlCargarPublicaciones);
            ResultSet rs=psd.executeQuery();            
            
            while(rs.next()){

                        DefaultMutableTreeNode nodo1= new DefaultMutableTreeNode();
                        nodo1.setUserObject(rs.getString("Publication Name"));
                        nodo.add(nodo1);
                        DefaultTreeModel mdl=new DefaultTreeModel(nodo);
                        this.jTree1.setModel(mdl);

            }

            
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT "+e+" CODIGO: "+e.getErrorCode());
        }
   }
    
     public String SelectBasedePublicacion(String server, String publi){
        String bases="";
        String sqlBuscarBase="Use distribution \n" +
            "\n" +
            "DECLARE @ArticleName SysName\n"+
            "\n" +
            "SELECT\n" +
            "     MSP.publication AS 'Publication Name'\n" +
            "    ,MSA.publisher_db AS 'Database Name'\n" +
            "FROM\n" +
            "    DBO.MSarticles AS MSA\n" +
            "INNER JOIN DBO.MSpublications AS MSP\n" +
            "        ON MSA.publication_id = MSP.publication_id\n"+
            "        AND MSP.publication='"+publi+"'";
        
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectar(server);
        try{
            PreparedStatement psd=cn.prepareStatement(sqlBuscarBase);

            ResultSet rs=psd.executeQuery();
            while(rs.next()){
                 bases= rs.getString("Database Name");
              
            }   
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT PUBLICACIONES "+e+" CODIGO: "+e.getErrorCode());
        }
        return bases;
    }
     
    public String bas; 
    public void eliminarPublicacion(String server) throws SQLException{
        DefaultMutableTreeNode nodo= (DefaultMutableTreeNode)jTree1.getLastSelectedPathComponent();
        String publicacion=nodo.getUserObject().toString();
        
        bas= SelectBasedePublicacion(server, publicacion);
        
        String elimPublicacion="DECLARE @publication AS sysname;\n" +
            "SET @publication = N'"+publicacion+"'; \n" +
            "\n" +
            "-- Remove a transactional publication.\n" +
            "USE ["+bas+"]\n" +
            "EXEC sp_droppublication @publication = @publication;";
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectar(server);
        try{
            EliminarSuscripcion(server, publicacion,bas);
            EliminarSuscripcion(servidorUno, publicacion,bas);
            EliminarSuscripcion(servidorDos, publicacion,bas);
            
            PreparedStatement psd=cn.prepareStatement(elimPublicacion);
            psd.execute();
            JOptionPane.showMessageDialog(null, "Publicación eliminada ");
            if (nodo !=null){
                DefaultTreeModel mdl=(DefaultTreeModel)jTree1.getModel();
                mdl.removeNodeFromParent(nodo);
            }
        }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede eliminar Publicacion: "+e.getMessage()+" CODIGO: "+e.getErrorCode());
        }
        
        MostrarPublicaciones(server);
        MostrarSuscripcion(server);
    }
    
    public void EliminarSuscripcion(String server, String publicacion, String base) throws SQLException{
        String sqlEliminarSuscripcion="DECLARE @publication AS sysname;\n" +
            "DECLARE @subscriber AS sysname;\n" +
            "SET @publication = N'"+publicacion+"';\n" +
            "SET @subscriber = N'"+server+"'; \n" +
            "\n" +
            "USE ["+base+"]\n" +
            "EXEC sp_dropsubscription \n" +
            "  @publication = @publication, \n" +
            "  @article = N'all',\n" +
            "  @subscriber = @subscriber;";
        
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectar(server);
        try{
            PreparedStatement psd=cn.prepareStatement(sqlEliminarSuscripcion);
            psd.execute();
           }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede eliminar Suscripcion: "+e.getMessage()+" CODIGO: "+e.getErrorCode());
        }
    }
    public void MostrarSuscripcion(String server){
        String sqlCargarSuscripciones="Use distribution \n" +
            "SELECT\n" +
            "*\n" +
            "FROM\n" +
            "                DBO.MSsubscriptions AS MSA\n" +
            "            INNER JOIN DBO.MSpublications AS MSP\n" +
            "                    ON MSA.publication_id = MSP.publication_id"+
            "    AND subscriber_db <> 'virtual'";
        
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectar(server);
        DefaultMutableTreeNode nodo= new DefaultMutableTreeNode("Suscripciones");
        try{
            PreparedStatement psd=cn.prepareStatement(sqlCargarSuscripciones);
             ResultSet rs=psd.executeQuery();           
            
            while(rs.next()){
       
                        DefaultMutableTreeNode nodo1= new DefaultMutableTreeNode();
                        nodo1.setUserObject(rs.getString("subscriber_db"));
                        nodo.add(nodo1);
                        
            
            }

            DefaultTreeModel mdl=new DefaultTreeModel(nodo);
                        this.jTree2.setModel(mdl);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT "+e+" CODIGO: "+e.getErrorCode());
        }
   }
    
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
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e+" CODIGO: "+e.getErrorCode());
        }
    }
    public void cargarBasesDestino(String server){
        jcBaseDestino.removeAllItems();
        jcBaseDestino.addItem("");
        jcBaseDestino.addItem("Nueva Base");
        String sql;
        sql="SELECT name FROM sys.databases";
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectar(server);
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            ResultSet rs=psd.executeQuery();
            while(rs.next()){
                jcBaseDestino.addItem(rs.getString("name"));
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e+" CODIGO: "+e.getErrorCode());
        }
    }
    
    public void tablas(){
        if (String.valueOf(jcBase.getSelectedItem())!=""){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,String.valueOf(jcBase.getSelectedItem()));
        String titulos[] = null,Registros[] = null;
        String sql;
        sql="USE ["+String.valueOf(jcBase.getSelectedItem())+"] "
                + "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE TABLE_TYPE='BASE TABLE'";
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            ResultSet rs=psd.executeQuery();
            if(rs.next()){
                cargarTabla(servidor,String.valueOf(jcBase.getSelectedItem()));
            }
            
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e+" CODIGO: "+e.getErrorCode());
        }
        }
    }
    
    public void cargarCampos(String base){
        if(!jcBase.getSelectedItem().toString().equals("")){
            listaIzq.removeAllElements();
            listaFiltros.removeAllElements();
        int i=0;
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(ServidorLocal,base);
        String titulos[] = null,Registros[] = null;
        String sql_campos,sql_cantidad,sql;
        sql_cantidad="USE "+base+" SELECT COUNT(COLUMN_NAME) as C FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql_campos="USE "+base+" SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
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
                    listaFiltros.addElement(rs_campos.getString("COLUMN_NAME"));
                }
            }
            jlCamposIzq.setModel(listaIzq);
            jlCampos.setModel(listaFiltros);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e+" CODIGO: "+e.getErrorCode());
        }
        }
    }
    
    public void cargarTabla(String server, String base){
        int i=0;
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(ServidorLocal,base);
        String titulos[] = null,Registros[] = null;
        String sql_campos,sql_cantidad,sql;
        sql_cantidad="USE "+base+" SELECT COUNT(COLUMN_NAME) as C FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
        sql_campos="USE "+base+" SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'clientes'";
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
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e+" CODIGO: "+e.getErrorCode());
        }
    }
    conexion cc=new conexion();
    Connection cn;

    public void modificarTabla(){
        cn=(Connection) cc.conectarBase(ServidorLocal, jcBase.getSelectedItem().toString());
        String sql="";
        for(int fila=0;fila<tblTabla.getRowCount();fila++){
            sql="use ["+jcBase.getSelectedItem()+"]\nUPDATE clientes SET Nombre='"+String.valueOf(tblTabla.getValueAt(fila, 1)).toUpperCase() +"', "
                            + "Apellido='"+String.valueOf(tblTabla.getValueAt(fila, 2)).toUpperCase() +"', "
                            + "Telefono='"+String.valueOf(tblTabla.getValueAt(fila, 3)) +"', "
                            + "Direccion='"+String.valueOf(tblTabla.getValueAt(fila, 4)).toUpperCase() +"', "
                            + "Ciudad='"+String.valueOf(tblTabla.getValueAt(fila, 5)) +"',"
                            + "Edad='"+Integer.valueOf((String) tblTabla.getValueAt(fila, 6)) +"'"
                +"WHERE CI='"+tblTabla.getValueAt(fila, 0) +"'";    
            try {
                PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);     
                int n=psd.executeUpdate();
            if(n>0){
                System.out.println("Se ingreso correctamente");
            }
            }catch(SQLException e){
                  JOptionPane.showMessageDialog(null, "No se ha podido realizar el update"+e+" CODIGO: "+e.getErrorCode());
            }
        }
        cargarTabla(ServidorLocal,String.valueOf(jcBase.getSelectedItem()));
    }
    
    public void insertar(String base){
        cn=(Connection) cc.conectarBase(ServidorLocal, jcBase.getSelectedItem().toString());
        String sql="";
        sql="use ["+jcBase.getSelectedItem().toString()+"]\nINSERT INTO clientes VALUES(?,?,?,?,?,?,?)";
        try {
            PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);
            psd.setString(1,JOptionPane.showInputDialog(null, "CI"));
            psd.setString(2,JOptionPane.showInputDialog(null, "Nombre"));
            psd.setString(3,JOptionPane.showInputDialog(null, "Apellido"));
            psd.setString(4,JOptionPane.showInputDialog(null, "Telefono"));
            psd.setString(5,JOptionPane.showInputDialog(null, "Direccion"));
            psd.setString(6,JOptionPane.showInputDialog(null, "Ciudad"));
            psd.setInt(7,Integer.valueOf(JOptionPane.showInputDialog(null, "Edad")));
            int n=psd.executeUpdate();
            if(n>0){
                JOptionPane.showMessageDialog(null, "Se inserto correctamente "); 
                cargarTabla(ServidorLocal,String.valueOf(jcBase.getSelectedItem()));
            }           
        } 
       catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex+" CODIGO: "+ex.getErrorCode());
        }
       }
    
    public void eliminar(int fila){
        cn=(Connection) cc.conectarBase(ServidorLocal, jcBase.getSelectedItem().toString());
        String sql="";
        sql="use ["+jcBase.getSelectedItem().toString()+"]\nDELETE FROM clientes WHERE CI='"+tblTabla.getValueAt(fila, 0) +"'";
        try {
            PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);
            int n=psd.executeUpdate();
            if (n>0){
                JOptionPane.showMessageDialog(null, "Registro borrado correctamente");
                cargarTabla(ServidorLocal,String.valueOf(jcBase.getSelectedItem()));
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex+" CODIGO: "+ex.getErrorCode());
        }
        
    }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jInternalFrame1 = new javax.swing.JInternalFrame();
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
        btnPublicar = new javax.swing.JButton();
        jrbConectar = new javax.swing.JRadioButton();
        jrbDesconectar = new javax.swing.JRadioButton();
        btnModificar = new javax.swing.JButton();
        jcBase = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtFiltros = new javax.swing.JTextField();
        btnAñadir = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jlCampos = new javax.swing.JList();
        txtCampos = new javax.swing.JTextField();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminarFiltro = new javax.swing.JButton();
        lblFiltro = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnIgual = new javax.swing.JButton();
        btnMayorIgual = new javax.swing.JButton();
        btnMayorQue = new javax.swing.JButton();
        btnMenorQue = new javax.swing.JButton();
        btnMenorIgual = new javax.swing.JButton();
        btnDiferente = new javax.swing.JButton();
        btnNoEsteEn = new javax.swing.JButton();
        btnEsteEn = new javax.swing.JButton();
        btnContega = new javax.swing.JButton();
        btnEmpiece = new javax.swing.JButton();
        btnTermine = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jlsFiltros = new javax.swing.JList();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jlCamposDer = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlCamposIzq = new javax.swing.JList();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnEjecutar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jcBaseDestino = new javax.swing.JComboBox();
        jScrollPane7 = new javax.swing.JScrollPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jButton2 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTree2 = new javax.swing.JTree();
        btnSuscribir = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jrbSnapshot = new javax.swing.JRadioButtonMenuItem();
        jrbMezcla = new javax.swing.JRadioButtonMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jrbTranEstandar = new javax.swing.JRadioButtonMenuItem();
        jrbTranCola = new javax.swing.JRadioButtonMenuItem();
        jrbTranPeer = new javax.swing.JRadioButtonMenuItem();
        jmAdministrarReplicaciones = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        jInternalFrame1.setVisible(true);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

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
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jchA))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jchB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jchC))
        );

        btnPublicar.setText("Publicar");
        btnPublicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPublicarActionPerformed(evt);
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

        jcBase.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcBaseItemStateChanged(evt);
            }
        });

        jLabel6.setText("Bases");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setText("Filtros Horizontales");

        btnAñadir.setText("Añadir");
        btnAñadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirActionPerformed(evt);
            }
        });

        jScrollPane4.setViewportView(jlCampos);

        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnEliminarFiltro.setText("Eliminar");
        btnEliminarFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarFiltroActionPerformed(evt);
            }
        });

        lblFiltro.setText("Filtro");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tipos de Filtros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Agency FB", 0, 12))); // NOI18N

        btnIgual.setText("=");
        btnIgual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIgualActionPerformed(evt);
            }
        });

        btnMayorIgual.setText(">=");
        btnMayorIgual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMayorIgualActionPerformed(evt);
            }
        });

        btnMayorQue.setText(">");
        btnMayorQue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMayorQueActionPerformed(evt);
            }
        });

        btnMenorQue.setText("<");
        btnMenorQue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenorQueActionPerformed(evt);
            }
        });

        btnMenorIgual.setText("<=");
        btnMenorIgual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenorIgualActionPerformed(evt);
            }
        });

        btnDiferente.setText("<>");
        btnDiferente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDiferenteActionPerformed(evt);
            }
        });

        btnNoEsteEn.setText("No este en");
        btnNoEsteEn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoEsteEnActionPerformed(evt);
            }
        });

        btnEsteEn.setText("Este en");
        btnEsteEn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEsteEnActionPerformed(evt);
            }
        });

        btnContega.setText("Contenga");
        btnContega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContegaActionPerformed(evt);
            }
        });

        btnEmpiece.setText("Empiece con");
        btnEmpiece.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmpieceActionPerformed(evt);
            }
        });

        btnTermine.setText("Termine con");
        btnTermine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTermineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnIgual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMayorQue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMenorQue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMenorIgual)
                .addGap(18, 18, 18)
                .addComponent(btnMayorIgual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDiferente)
                .addGap(18, 18, 18)
                .addComponent(btnContega, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnNoEsteEn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEsteEn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEmpiece)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTermine))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnIgual)
                    .addComponent(btnMayorQue)
                    .addComponent(btnMenorQue)
                    .addComponent(btnMenorIgual)
                    .addComponent(btnDiferente)
                    .addComponent(btnMayorIgual)
                    .addComponent(btnContega))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNoEsteEn)
                    .addComponent(btnEsteEn)
                    .addComponent(btnEmpiece)
                    .addComponent(btnTermine))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel9.setText("Campos");

        jScrollPane6.setViewportView(jlsFiltros);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Filtros Verticales");

        jLabel4.setText("Disponibles");

        jLabel5.setText("Utilizadas");

        jScrollPane2.setViewportView(jlCamposDer);

        jScrollPane1.setViewportView(jlCamposIzq);

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(47, 47, 47))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 30, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addGap(5, 5, 5))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addGap(61, 61, 61)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel9))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminarFiltro))
                        .addGap(47, 47, 47)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel7)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(btnNuevo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(21, 21, 21)
                                        .addComponent(lblFiltro)))
                                .addGap(18, 18, 18)
                                .addComponent(txtFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAñadir))))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnNuevo)
                            .addComponent(txtCampos, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFiltro)
                            .addComponent(txtFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAñadir))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminarFiltro))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jButton1.setText("Ver");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnEjecutar.setText("! Ejecutar");
        btnEjecutar.setName("btnEjecutar"); // NOI18N
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });

        jLabel8.setText("Base Destino");

        jcBaseDestino.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcBaseDestinoItemStateChanged(evt);
            }
        });

        jScrollPane5.setViewportView(jTree1);

        jScrollPane7.setViewportView(jScrollPane5);

        jButton2.setText("Emilinar publicacion");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane9.setViewportView(jTree2);

        jScrollPane8.setViewportView(jScrollPane9);

        btnSuscribir.setText("Suscripcion");
        btnSuscribir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuscribirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombrePub, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcBase, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(57, 57, 57)
                                .addComponent(btnPublicar, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addGap(791, 791, 791)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jrbDesconectar)
                            .addComponent(jrbConectar))
                        .addGap(124, 124, 124))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButton2)
                                        .addGap(22, 22, 22)))
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(84, 84, 84)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnSuscribir, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(13, 13, 13)
                                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(55, 55, 55)
                                        .addComponent(jcBaseDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnEjecutar)
                                .addGap(66, 66, 66)
                                .addComponent(btnInsertar, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jrbConectar)
                        .addComponent(btnPublicar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtNombrePub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(jcBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jcBaseDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSuscribir, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jrbDesconectar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jLabel8)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsertar)
                    .addComponent(btnModificar)
                    .addComponent(btnEliminar)
                    .addComponent(btnActualizar)
                    .addComponent(btnEjecutar))
                .addGap(21, 21, 21)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jMenu1.setText("Crear Replicaciones");
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

        jmAdministrarReplicaciones.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jmAdministrarReplicaciones.setText("Administrar Bases");
        jmAdministrarReplicaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jmAdministrarReplicacionesMouseClicked(evt);
            }
        });
        jmAdministrarReplicaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmAdministrarReplicacionesActionPerformed(evt);
            }
        });
        jMenuBar1.add(jmAdministrarReplicaciones);

        jMenu5.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1096, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
    int fila=tblTabla.getSelectedRow();    
    eliminar(fila);
}//GEN-LAST:event_btnEliminarActionPerformed

private void jchAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jchAActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jchAActionPerformed

private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
    cargarTabla(ServidorLocal,String.valueOf(jcBase.getSelectedItem()));
}//GEN-LAST:event_btnActualizarActionPerformed

private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
    modificarTabla();
}//GEN-LAST:event_btnModificarActionPerformed

private void btnInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertarActionPerformed
    insertar(jcBase.getSelectedItem().toString());    
}//GEN-LAST:event_btnInsertarActionPerformed

private void jMenu5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu5MouseClicked
    System.exit(0);
}//GEN-LAST:event_jMenu5MouseClicked

private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
    
}//GEN-LAST:event_jMenu1MouseClicked

private void btnPublicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPublicarActionPerformed
    codigo=0;
  //  String baseDestino=jcBaseDestino.getSelectedItem().toString();
    String baseOrigen=jcBase.getSelectedItem().toString();
    if (jrbSnapshot.isSelected()){
        SnapshotPublicacion();   
    }
    else if (jrbTranEstandar.isSelected())
    {
    TransaccionaEstandarPublicacion();
        
    }
    else if (jrbTranCola.isSelected()) 
    {
   TransaccionalColaPublicacion();
    }
        
   else if (jrbTranPeer.isSelected()){
      PeerToPeerPublicacion(baseOrigen);  
    }
                
    else if (jrbMezcla.isSelected())    
         MezclaPublicacion(baseOrigen);
    
    
   MostrarPublicaciones(ServidorLocal);
}//GEN-LAST:event_btnPublicarActionPerformed

public void SnapshotPublicacion()
{
    try {
            ejecutar(sqlPublicacionSnap(txtNombrePub.getText(),jcBase.getSelectedItem().toString()));
            if(codigo!=14016)JOptionPane.showMessageDialog(null, "Publicacion creada");
          //if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null, "Completado");
        } catch (SQLException ex) {
             Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORSNAP");
        }   //     ejecutar(crearTablaSuscripcion(baseDestino),servidorDos,baseDestino);
}
public void TransaccionaEstandarPublicacion()
{
    try {
            ejecutar(sqlPublicacionTransaccional(txtNombrePub.getText(),jcBase.getSelectedItem().toString()));
       if(codigo!=14016)JOptionPane.showMessageDialog(null, "Publicacion creada");
 
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORESTANDAR");
        }    
      
}
public void TransaccionalColaPublicacion()
{
     try {
            ejecutar(sqlPublicacionTransacionalCola(txtNombrePub.getText(),jcBase.getSelectedItem().toString()));
    if(codigo!=14016)JOptionPane.showMessageDialog(null, "Publicacion creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORCOLA");
        } 
}
public void PeerToPeerPublicacion(String baseOrigen)
{    // Crear la tabla en el sitio en el que se va a crear la replicacion
      
        try {
            ejecutar(sqlPublicacionPeer(baseOrigen, baseOrigen,txtNombrePub.getText(),ServidorLocal)); //ERIKA-LAP
        if(codigo!=14016)JOptionPane.showMessageDialog(null, "Publicacion creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORPEER");
        }                   
              
}
public void MezclaPublicacion(String baseOrigen)
{
    JOptionPane.showMessageDialog(null, sqlPublicacionMerge(txtNombrePub.getText(),baseOrigen));
    try {
            ejecutar(sqlPublicacionMerge(txtNombrePub.getText(),baseOrigen));            
                JOptionPane.showMessageDialog(null, "Publicacion creada");
    } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"eRROR:"+ ex);
        }
}
public void SnapshotSuscripcion(String baseDestino)
{
    try {
            if (jchA.isSelected()){ 
                ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),servidorUno,baseDestino));
            }
            if (jchB.isSelected()){ 
                  ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),servidorDos,baseDestino));
            }
           if (jchC.isSelected()){ 
                 ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),ServidorLocal,baseDestino));
           }    
          if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SNAP");
        } 
       //     ejecutar(crearTablaSuscripcion(baseDestino),servidorDos,baseDestino);
}
public void TransaccionaEstandarSuscripcion(String baseDestino)
{     
            if (jchA.isSelected()){
            try {
                ejecutar(sqlSuscripcionTransaccional(txtNombrePub.getText(),servidorUno,baseDestino));
            if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ESTANDAR");
            }
}
    if (jchB.isSelected()){
            try {
                ejecutar(sqlSuscripcionTransaccional(txtNombrePub.getText(),servidorDos,baseDestino));
             if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
              } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ESTANDAR");
            }
}
    if (jchC.isSelected()){
        try {
            ejecutar(sqlSuscripcionTransaccional(txtNombrePub.getText(),ServidorLocal,baseDestino));
            if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       // if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
}
public void TransaccionalColaSuscripcion(String baseDestino)
{

            if (jchA.isSelected()){
            try {
                ejecutar(sqlSuscripcionCola(txtNombrePub.getText(),servidorUno,baseDestino));
                ejecutar(sqlSuscripcionColaParteDos(txtNombrePub.getText(),baseDestino),servidorUno,baseDestino);
              if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
            }
}
    if (jchB.isSelected()){

            try {
                ejecutar(sqlSuscripcionCola(txtNombrePub.getText(),servidorDos,baseDestino));
                  ejecutar(sqlSuscripcionColaParteDos(txtNombrePub.getText(),baseDestino),servidorDos,baseDestino);
                if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
}
    if (jchC.isSelected()){
        try {
              ejecutar(sqlSuscripcionCola(txtNombrePub.getText(),ServidorLocal,baseDestino));
              ejecutar(sqlSuscripcionColaParteDos(txtNombrePub.getText(),baseDestino));
          if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
        
}

public void PeerToPeerSuscripcion(String baseOrigen, String baseDestino)
{    
    // Crear la tabla en el sitio en el que se va a crear la replicacion
      if (jchA.isSelected()){ 
             try {
               ejecutar(crearTablaSuscripcion(baseDestino),servidorUno,baseDestino);
               llenarDatosPeer(baseDestino,servidorUno);     
             } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
        }             
          if (jchB.isSelected()){ 
          try {
                ejecutar(crearTablaSuscripcion(baseDestino),servidorDos,baseDestino);
                llenarDatosPeer(baseDestino,servidorDos);
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }         
    
            if(jchA.isSelected()){
                try {
                 ejecutar(sqlPublicacionPeer(baseDestino,baseOrigen,txtNombrePub.getText(),ServidorLocal),servidorUno,baseDestino);
                 ejecutar(sqlSuscripcionPeer(baseDestino,baseOrigen,txtNombrePub.getText(),ServidorLocal),servidorUno,baseDestino);//ERIKA-LAP   ERIKA-LAP\\SITIOA
                 ejecutar(sqlSuscripcionPeer(baseOrigen,baseDestino,txtNombrePub.getText(),servidorUno)); 
                 if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
             } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORCola");
        } 
            }
            if(jchB.isSelected())
            {
                try {
                  ejecutar(sqlPublicacionPeer(baseDestino,baseOrigen,txtNombrePub.getText(),ServidorLocal),servidorDos,baseDestino);
                 ejecutar(sqlSuscripcionPeer(baseDestino,baseOrigen,txtNombrePub.getText(),ServidorLocal),servidorDos,baseDestino);//
                  ejecutar(sqlSuscripcionPeer(baseOrigen,baseDestino,txtNombrePub.getText(),servidorDos));
                          if(codigo!=14016 &&codigo!=20026&&codigo!=14058&&codigo!=2714)  JOptionPane.showMessageDialog(null," Suscripcion Creada");
                          } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORCola");
        } 
            }            
}
public void MezclaSuscripcion(String baseDestino)
{
    if (jchA.isSelected()){ 
             try {
                    JOptionPane.showMessageDialog(null, sqlSuscripcionMerge(txtNombrePub.getText(),servidorUno,baseDestino));
                    ejecutarMerge(sqlSuscripcionMerge(txtNombrePub.getText(),servidorUno,baseDestino)); // ERIKA-LAP\\SITIOA
                    JOptionPane.showMessageDialog(null, "Suscripcion creada");
                } catch (SQLException ex) {
                    Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
                }
    }
        if(jchB.isSelected())
            {
                try {
                    JOptionPane.showMessageDialog(null, sqlSuscripcionMerge(txtNombrePub.getText(),servidorDos,baseDestino));
                    ejecutarMerge(sqlSuscripcionMerge(txtNombrePub.getText(),servidorDos,baseDestino)); // ERIKA-LAP\\SITIOB
                      JOptionPane.showMessageDialog(null, "Suscripcion creada");
                } catch (SQLException ex) {
                    Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    if (jchC.isSelected()){
        try {
                JOptionPane.showMessageDialog(null, sqlSuscripcionMerge(txtNombrePub.getText(),ServidorLocal,baseDestino));
                ejecutarMerge(sqlSuscripcionMerge(txtNombrePub.getText(),ServidorLocal,baseDestino)); // ERIKA-LAP\\SITIOA
                JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "AQUI");
            }
        }
}


public void llenarDatosPeer(String base,String nodo)
{  int n=0;
     cn=(Connection) cc.conectarBase(nodo, base);
        String sql="";
        sql="use ["+base+"]\nINSERT INTO clientes VALUES(?,?,?,?,?,?,?)";
        try {
            System.out.println("Filas: "+tblTabla.getRowCount());
            for(int i=0; i<tblTabla.getRowCount();i++){
            PreparedStatement psd=(PreparedStatement) cn.prepareStatement(sql);
            psd.setString(1,String.valueOf(tblTabla.getValueAt(i, 0)));
            psd.setString(2,String.valueOf(tblTabla.getValueAt(i, 1)));
            psd.setString(3,String.valueOf(tblTabla.getValueAt(i, 2)));
            psd.setString(4,String.valueOf(tblTabla.getValueAt(i, 3)));
            psd.setString(5,String.valueOf(tblTabla.getValueAt(i, 4)));
            psd.setString(6,String.valueOf(tblTabla.getValueAt(i, 5)));
             psd.setInt(7,Integer.valueOf(String.valueOf(tblTabla.getValueAt(i, 6))));
           n=psd.executeUpdate();  
            }
             if(n>0){    
               JOptionPane.showMessageDialog(null, "Se inserto correctamente ");
            }      
        } 
       catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex+" CODIGO: "+ex.getErrorCode());
        }      
}

private void tblTablaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTablaKeyReleased
    
}//GEN-LAST:event_tblTablaKeyReleased

private void tblTablaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTablaKeyTyped
    
}//GEN-LAST:event_tblTablaKeyTyped


 public String nuevaBase(String server){
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectar(server);
        String sql;
        String nombre= JOptionPane.showInputDialog(null, "Nombre de la nueva Base");
        sql="CREATE DATABASE "+ nombre;
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            psd.execute();
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null, "No se puede crear la base"+e+" CODIGO: "+e.getErrorCode());
        }
        return nombre;
    }


private void jchAItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchAItemStateChanged
    if(jchA.isSelected()){
        
        a=sqlSuscripcionSnap(txtNombrePub.getText(), servidorDos,"");
        cargarBasesDestino(servidorUno);
    
    }else
        a="";
}//GEN-LAST:event_jchAItemStateChanged

private void jchBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchBItemStateChanged
    if(jchB.isSelected()){
        
         
        a=sqlSuscripcionSnap(txtNombrePub.getText(), servidorDos,"");
        cargarBasesDestino(servidorDos);
    
    }else
        b="";
}//GEN-LAST:event_jchBItemStateChanged

private void jchCItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchCItemStateChanged
    if(jchC.isSelected()){
       
        a=sqlSuscripcionSnap(txtNombrePub.getText(), ServidorLocal,"");
        cargarBasesDestino(ServidorLocal);
    
    }
    else
        c="";
}//GEN-LAST:event_jchCItemStateChanged

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    
    if(!jcBase.getSelectedItem().toString().equals("")){
      cargarTabla(servidor,jcBase.getSelectedItem().toString());
      
       cargarCampos(jcBase.getSelectedItem().toString());
       }
}//GEN-LAST:event_jButton1ActionPerformed

    private void btnIgualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIgualActionPerformed
        lblFiltro.setText("=");
    }//GEN-LAST:event_btnIgualActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (jlCamposDer.getSelectedValue()!=null){
            listaIzq.addElement(String.valueOf(jlCamposDer.getSelectedValue()));
            listaDer.removeElementAt(jlCamposDer.getSelectedIndex());
            jlCamposIzq.setModel(listaIzq);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (jlCamposIzq.getSelectedValue()!=null){
            listaDer.addElement(String.valueOf(jlCamposIzq.getSelectedValue()));
            listaIzq.removeElementAt(jlCamposIzq.getSelectedIndex());
            jlCamposDer.setModel(listaDer);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked

    }//GEN-LAST:event_jButton4MouseClicked

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
       if(jlCampos.getSelectedValue()!=null)
       {
       txtCampos.setText(jlCampos.getSelectedValue().toString());
       }
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnAñadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirActionPerformed
 String filtro="";
 String aux="";
 String valor="";
   if(lblFiltro.getText().equals("contenga")||lblFiltro.getText().equals("no este en")||lblFiltro.getText().equals("este en")||lblFiltro.getText().equals("empiece con")||lblFiltro.getText().equals("termine con")){
       if(lblFiltro.getText().equals("contenga")) {aux= "LIKE"; valor="%"+txtFiltros.getText()+"%";}
        if(lblFiltro.getText().equals("empiece con")) {aux= "LIKE"; valor="%"+txtFiltros.getText();}
         if(lblFiltro.getText().equals("termine con")) {aux= "LIKE"; valor=txtFiltros.getText()+"%";}
          if(lblFiltro.getText().equals("no este en")) {aux= "NOT IN"; valor="("+txtFiltros.getText()+")";}
           if(lblFiltro.getText().equals("no este en")) {aux= "IN"; valor="("+txtFiltros.getText()+")";}                       
   }
   else{ 
       aux=lblFiltro.getText();
       valor=txtFiltros.getText();
   }
         if(!txtCampos.getText().equals("Edad"))
                 filtro="["+txtCampos.getText()+"] "+aux+" ''"+valor+"''";
         else   filtro="["+txtCampos.getText()+"] "+aux+" "+valor;
   
 lista.addElement(filtro);
 jlsFiltros.setModel(lista);
 System.out.println(filtro);
  lblFiltro.setText("Filtro"); 
    }//GEN-LAST:event_btnAñadirActionPerformed

    private void btnMayorQueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMayorQueActionPerformed
       lblFiltro.setText(">");
    }//GEN-LAST:event_btnMayorQueActionPerformed

    private void btnMenorQueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenorQueActionPerformed
         lblFiltro.setText("<");
    }//GEN-LAST:event_btnMenorQueActionPerformed

    private void btnMenorIgualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenorIgualActionPerformed
        lblFiltro.setText("<=");
    }//GEN-LAST:event_btnMenorIgualActionPerformed

    private void btnMayorIgualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMayorIgualActionPerformed
      lblFiltro.setText(">=");
    }//GEN-LAST:event_btnMayorIgualActionPerformed

    private void btnDiferenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiferenteActionPerformed
       lblFiltro.setText("<>");  // TODO add your handling code here:
    }//GEN-LAST:event_btnDiferenteActionPerformed

    private void btnContegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContegaActionPerformed
      lblFiltro.setText("contenga");
    }//GEN-LAST:event_btnContegaActionPerformed

    private void btnNoEsteEnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoEsteEnActionPerformed
 lblFiltro.setText("no este en");        // TODO add your handling code here:
    }//GEN-LAST:event_btnNoEsteEnActionPerformed

    private void btnEsteEnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEsteEnActionPerformed
 lblFiltro.setText("este en");        // TODO add your handling code here:
    }//GEN-LAST:event_btnEsteEnActionPerformed

    private void btnEmpieceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmpieceActionPerformed
 lblFiltro.setText("empiece con");        // TODO add your handling code here:
    }//GEN-LAST:event_btnEmpieceActionPerformed

    private void btnTermineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTermineActionPerformed
 lblFiltro.setText("termine con");        // TODO add your handling code here:
    }//GEN-LAST:event_btnTermineActionPerformed

    private void btnEliminarFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarFiltroActionPerformed
        if (jlsFiltros.getSelectedValue()!=null){
            lista.removeElementAt(jlsFiltros.getSelectedIndex());
            jlsFiltros.setModel(lista);
        }
    }//GEN-LAST:event_btnEliminarFiltroActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        String cadena=jlsFiltros.getSelectedValue().toString();
        System.out.println("Editar: "+cadena);
         if (jlsFiltros.getSelectedValue()!=null){
            lista.removeElementAt(jlsFiltros.getSelectedIndex());
            jlsFiltros.setModel(lista);
        }
        
    }//GEN-LAST:event_btnEditarActionPerformed

    private void jcBaseItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcBaseItemStateChanged
       
    }//GEN-LAST:event_jcBaseItemStateChanged

    private void jcBaseDestinoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcBaseDestinoItemStateChanged
      if(jcBaseDestino.getSelectedIndex()>=0&&jcBaseDestino.getSelectedItem().equals("Nueva Base")){
        String nombre="";
        if(jchA.isSelected()){
        nombre=nuevaBase(servidorUno);
        cargarBasesDestino(servidorUno);
        }
           if(jchB.isSelected()){
        nombre=nuevaBase(servidorDos);
        cargarBasesDestino(servidorDos);
        }
              if(jchC.isSelected()){
        nombre=nuevaBase(ServidorLocal);
        cargarBasesDestino(ServidorLocal);
        }
     jcBaseDestino.setSelectedItem(nombre);
            
        }  
    }//GEN-LAST:event_jcBaseDestinoItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
         try {
            // TODO add your handling code here:
            eliminarPublicacion(servidor);
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        cargarTabla(servidor,jcBase.getSelectedItem().toString());
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnSuscribirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuscribirActionPerformed
       codigo=0;
        String baseDestino=jcBaseDestino.getSelectedItem().toString();
          String baseOrigen=jcBase.getSelectedItem().toString();
        if (jrbSnapshot.isSelected()){
        SnapshotSuscripcion(baseDestino);   
    }
    else if (jrbTranEstandar.isSelected())
    {
    TransaccionaEstandarSuscripcion(baseDestino);
        
    }
    else if (jrbTranCola.isSelected()) 
    {
   TransaccionalColaSuscripcion(baseDestino);
    }
        
   else if (jrbTranPeer.isSelected()){
       PeerToPeerSuscripcion(baseOrigen,baseDestino);  
    }
                
    else if (jrbMezcla.isSelected())    
         MezclaSuscripcion(baseDestino);
    }//GEN-LAST:event_btnSuscribirActionPerformed

    private void jmAdministrarReplicacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmAdministrarReplicacionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jmAdministrarReplicacionesActionPerformed

    private void jmAdministrarReplicacionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jmAdministrarReplicacionesMouseClicked
    BaseDeDatos bd= new BaseDeDatos(ServidorLocal);
    bd.setVisible(true);
    }//GEN-LAST:event_jmAdministrarReplicacionesMouseClicked

public static String aux,atributos,tipo,sincro,filtro;
int k=1;
int l=50;

public String sqlPublicacionSnap(String nombre,String base){
    //String sql=" = N'pub_Java', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509D, @identityrangemanagementoption = N'none', @destination_table = N'clientes', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'SQL', @del_cmd = N'SQL', @upd_cmd = N'SQL'\n" ;
    aux="";atributos="";tipo="";sincro="";filtro="";
    aux="";
    tipo=" "+
"use ["+base+"] exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'publish', @value = N'true'\n"+
"use ["+base+"] exec sp_addpublication @publication = N'"+nombre+"', @description = N'Snapshot publication of database ''"+base+"'' from Publisher ''"+servidor+"''.',"
+ " @sync_method = N'native', @retention = 0, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'true', @enabled_for_internet = N'false',"
+ " @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_login = N'anonymous', @allow_subscription_copy = N'false', "
+ "@add_to_active_directory = N'false', @repl_freq = N'snapshot', @status = N'active', @independent_agent = N'true', @immediate_sync = N'true',"
+ " @allow_sync_tran = N'false', @autogen_sync_procs = N'false', @allow_queued_tran = N'false', @allow_dts = N'false', @replicate_ddl = 1"
                + "\nexec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 1, @frequency_relative_interval = 1,"
            + " @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 0, "
            + "@active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null,"
            + " @publisher_security_mode = 1 \n"+
            "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'sa'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT AUTHORITY\\SYSTEM'\n" +
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'ERIKA-LAP\\Erika'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\SQLSERVERAGENT'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\MSSQLSERVER'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'distributor_admin'";
atributos="\n use ["+base+"] exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased',"
+ " @description = null, @creation_script = null, @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509D, @identityrangemanagementoption = N'none',"
+ " @destination_table = N'clientes', @destination_owner = N'dbo', @status = 24, @vertical_partition = ";
   String atrib=""; 
    if (listaDer.isEmpty())
        atributos=atributos+"N'false'";
    else{
        atributos=atributos+"N'true'";
        atrib="\n";
        for (int i=0;i<listaDer.getSize();i++){
            atrib=atrib+""
                    + " exec sp_articlecolumn @publication = N'"+nombre+"', @article = N'clientes', @column = N'"+listaDer.getElementAt(i)+"',"
                    + " @operation = N'add', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        }
    }
    String cadenaFiltro="";
    if(!lista.isEmpty())
    {
         filtro="\n"+
                 "exec sp_articlefilter @publication = N'"+nombre+"', @article = N'clientes', @filter_name = N'FLTR_clientes_"+k+"__"+l+"', "
                 + "@filter_clause = N'";
         //System.out.println(filtro);
                  for (int i=0;i<lista.getSize();i++){
                      if(i==0){
                          filtro=filtro+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                      else{
                          filtro=filtro+"AND "+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                     // System.out.println(cadenaFiltro);
                  }
          filtro=filtro+"', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        //  System.out.println(filtro); 
    }
    if(lista.isEmpty())
    {
        atributos=atributos+atrib;
    }
    else
    {
        atributos=atributos+",@ins_cmd = N'SQL', @del_cmd = N'SQL', @upd_cmd = N'SQL', @filter_clause =N'"+cadenaFiltro+"' "+atrib;
    }
    String fil="";
    if(lista.isEmpty()){
        fil="null";
    }
    else
    {
       fil= "N'"+cadenaFiltro+"'";
    }
       sincro=""
                + "\n exec sp_articleview @publication = N'"+nombre+"', @article = N'clientes', @view_name = N'SYNC_clientes_"+k+"__"+l+"', @filter_clause = "+fil+", @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
    //System.out.println("Sincro: "+sincro);
       k++;
    l++;
    System.out.println("TODO: "+aux+tipo+atributos+filtro+sincro);
    return aux+tipo+atributos+filtro+sincro;
}
public String sqlPublicacionTransaccional(String nombre,String base){
    aux="";atributos="";tipo="";sincro="";filtro="";
    aux="";
    tipo=" "+
"use ["+base+"] exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'publish', @value = N'true'\n"+
"use ["+base+"] exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'merge publish', @value = N'true'\n" +
"use ["+base+"] exec sp_addpublication @publication = N'"+nombre+"', @description = N'Transactional publication of database ''"+base+"'' from Publisher ''"+servidor+"''.',"
+ " @sync_method = N'concurrent', @retention = 0, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'true', @enabled_for_internet = N'false',"
+ " @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_login = N'anonymous', @allow_subscription_copy = N'false', "
+ "@add_to_active_directory = N'false', @repl_freq = N'continuous', @status = N'active', @independent_agent = N'true', @immediate_sync = N'true',"
+ " @allow_sync_tran = N'false', @autogen_sync_procs = N'false', @allow_queued_tran = N'false', @allow_dts = N'false', @replicate_ddl = 1, @allow_initialize_from_backup = N'false', @enabled_for_p2p = N'false', @enabled_for_het_sub = N'false'"
                + "\nexec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 1, @frequency_relative_interval = 1,"
            + " @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 0, "
            + "@active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null,"
            + " @publisher_security_mode = 1\n"+
           "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'sa'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT AUTHORITY\\SYSTEM'\n" +
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'ERIKA-LAP\\Erika'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\SQLSERVERAGENT'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\MSSQLSERVER'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'distributor_admin'\n"  ;
atributos="\n use ["+base+"] exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased',"
+ " @description = null, @creation_script = null, @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'none',"
+ " @destination_table = N'clientes', @destination_owner = N'dbo', @status = 24, @vertical_partition = ";
   String atrib=""; 
    if (listaDer.isEmpty())
        atributos=atributos+"N'false'";
    else{
        atributos=atributos+"N'true'";
        atrib="\n";
        for (int i=0;i<listaDer.getSize();i++){
            atrib=atrib+""
                    + " exec sp_articlecolumn @publication = N'"+nombre+"', @article = N'clientes', @column = N'"+listaDer.getElementAt(i)+"', @operation = N'add', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        }
    }
    atributos=atributos+"";
    String cadenaFiltro="";
    if(!lista.isEmpty())
    {
         filtro="\n"+
                 "exec sp_articlefilter @publication = N'"+nombre+"', @article = N'clientes', @filter_name = N'FLTR_clientes_"+k+"__"+l+"', "
                 + "@filter_clause = N'";
         //System.out.println(filtro);
                  for (int i=0;i<lista.getSize();i++){
                      if(i==0){
                          filtro=filtro+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                      else{
                          filtro=filtro+"AND "+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                     // System.out.println(cadenaFiltro);
                  }
          filtro=filtro+"', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        //  System.out.println(filtro); 
    }
    if(lista.isEmpty())
    {
        atributos=atributos+atrib;
    }
    else
    {
        atributos=atributos+", @filter_clause =N'"+cadenaFiltro+"' "+atrib;
    }
    String fil="";
    if(lista.isEmpty()){
        fil="null";
    }
    else
    {
       fil= "N'"+cadenaFiltro+"'";
    }
       sincro=""
                + "\n exec sp_articleview @publication = N'"+nombre+"', @article = N'clientes', @view_name = N'SYNC_clientes_"+k+"__"+l+"', @filter_clause = "+fil+", @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
    //System.out.println("Sincro: "+sincro);
       k++;
    l++;
   // System.out.println("TODO: "+aux+tipo+atributos+filtro+sincro);
    return aux+tipo+atributos+filtro+sincro;
}


public String sqlPublicacionTransacionalCola(String nombre,String base){
    aux="";atributos="";tipo="";sincro="";filtro="";
    aux="";
    tipo=" "+
"use ["+base+"] exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'publish', @value = N'true'\n"+
"use ["+base+"] exec sp_addpublication @publication = N'"+nombre+"', @description = N'Transactional publication with updatable subscriptions of database ''"+base+"'' from Publisher ''"+ServidorLocal+"''.',"
+ " @sync_method = N'concurrent', @retention = 0, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'true', @enabled_for_internet = N'false',"
+ " @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_login = N'anonymous', @allow_subscription_copy = N'false', "
+ "@add_to_active_directory = N'false', @repl_freq = N'continuous', @status = N'active', @independent_agent = N'true', @immediate_sync = N'true',"
+ " @allow_sync_tran = N'true', @autogen_sync_procs = N'true', @allow_queued_tran = N'true', @allow_dts = N'false' , @conflict_policy = N'pub wins',"
            + " @centralized_conflicts = N'true', @conflict_retention = 14, @queue_type = N'sql' , @replicate_ddl = 1 , @allow_initialize_from_backup = N'false',"
            + " @enabled_for_p2p = N'false', @enabled_for_het_sub = N'false'"
                + "\nexec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 1, @frequency_relative_interval = 1,"
            + " @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 0, "
            + "@active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null,"
            + " @publisher_security_mode = 1";    
atributos="\n use ["+base+"] exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased',"
+ " @description = null, @creation_script = null, @pre_creation_cmd = N'drop', @schema_option = 0x0000000008035CDF, @identityrangemanagementoption = N'manual',"
+ " @destination_table = N'clientes', @destination_owner = N'dbo', @status = 16, @vertical_partition = ";
   String atrib=""; 
    if (listaDer.isEmpty())
        atributos=atributos+"N'false'";
    else{
        atributos=atributos+"N'true'";
        atrib="\n";
        for (int i=0;i<listaDer.getSize();i++){
            atrib=atrib+""
                    + " exec sp_articlecolumn @publication = N'"+nombre+"', @article = N'clientes', @column = N'"+listaDer.getElementAt(i)+"', @operation = N'add', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        }
    }
    String cadenaFiltro="";
    if(!lista.isEmpty())
    {
         filtro="\n"+
                 "exec sp_articlefilter @publication = N'"+nombre+"', @article = N'clientes', @filter_name = N'FLTR_clientes_"+k+"__"+l+"', "
                 + "@filter_clause = N'";
         //System.out.println(filtro);
                  for (int i=0;i<lista.getSize();i++){
                      if(i==0){
                          filtro=filtro+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                      else{
                          filtro=filtro+"AND "+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                     // System.out.println(cadenaFiltro);
                  }
          filtro=filtro+"', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        //  System.out.println(filtro); 
    }
    if(lista.isEmpty())
    {
        atributos=atributos+atrib;
    }
    else
    {
        atributos=atributos+", @filter_clause =N'"+cadenaFiltro+"' "+atrib;
    }
    String fil="";
    if(lista.isEmpty()){
        fil="null";
    }
    else
    {
       fil= "N'"+cadenaFiltro+"'";
    }
       sincro=""
                + "\n exec sp_articleview @publication = N'"+nombre+"', @article = N'clientes', @view_name = N'SYNC_clientes_"+k+"__"+l+"', @filter_clause = "+fil+", @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
    //System.out.println("Sincro: "+sincro);
       k++;
    l++;
    System.out.println("TODO: "+aux+tipo+atributos+filtro+sincro);
    return aux+tipo+atributos+filtro+sincro;
}
int contadorPeer=0;

public String sqlPublicacionPeer(String origen, String destino, String nombre,String servidorLocal)
{
    contadorPeer++;
String publicacion= "use master exec sp_replicationdboption @dbname = N'"+origen+"', @optname = N'publish', @value = N'true'\n" +
"use ["+origen+"]\n" +
"exec sp_addpublication @publication = N'"+nombre+"', @description = N'Transactional publication of database ''"+destino+"'' from Publisher "+
"''"+servidorLocal+"''.', @sync_method = N'native', @retention = 0, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'false', "+
"@enabled_for_internet = N'false', @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_login = N'anonymous',"+
" @allow_subscription_copy = N'false', @add_to_active_directory = N'false', @repl_freq = N'continuous', @status = N'active', @independent_agent = N'true',"+
" @immediate_sync = N'true', @allow_sync_tran = N'false', @autogen_sync_procs = N'false', @allow_queued_tran = N'false', @allow_dts = N'false', "+
"@replicate_ddl = 1, @allow_initialize_from_backup = N'true', @enabled_for_p2p = N'true', @enabled_for_het_sub = N'false', @p2p_conflictdetection = N'true',"+
" @p2p_originator_id ="+contadorPeer +" \n" +
"exec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 1, @frequency_relative_interval = 1, "+
"@frequency_recurrence_factor = 0, @frequency_subday = 8, @frequency_subday_interval = 1, @active_start_time_of_day = 0, @active_end_time_of_day = 235959, "+ 
 "@active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null, @publisher_security_mode = 0,"
+ " @publisher_login = N'sa', @publisher_password = N'sa'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'sa'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT AUTHORITY\\SYSTEM'\n" +
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'ERIKA-LAP\\Erika'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\SQLSERVERAGENT'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\MSSQLSERVER'\n" +
"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'distributor_admin'\n" +
"use ["+origen+"]\n" +
"exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased',"+
" @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual',"+
" @destination_table = N'clientes', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false',"
        + " @ins_cmd = N'CALL [dbo].[sp_MSins_dboclientes]',"+
" @del_cmd = N'CALL [dbo].[sp_MSdel_dboclientes]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboclientes]'" ;
System.out.println("Pibicacion: \n"+publicacion);
return publicacion;
}

public String sqlPublicacionMerge(String nombre,String base){
    aux="";atributos="";tipo="";sincro="";filtro="";
    aux="";
    tipo="use master\n" +
    "exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'publish', @value = N'true'\n"
   + "use master\n" +
    "exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'merge publish', @value = N'true'\n"
   +"use [proyecto] exec sp_addmergepublication @publication = N'"+nombre+"', @description = N'Merge publication of database ''"+base+"'' from Publisher ''ANDRES\\ANDRES''.', @sync_mode = N'native', @retention = 14, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'true', @enabled_for_internet = N'false', @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_subdirectory = N'ftp', @ftp_login = N'anonymous', @allow_subscription_copy = N'false', @add_to_active_directory = N'false', @dynamic_filters = N'false', @conflict_retention = 14, @keep_partition_changes = N'false', @allow_synctoalternate = N'false', @max_concurrent_merge = 0, @max_concurrent_dynamic_snapshots = 0, @use_partition_groups = null, @publication_compatibility_level = N'100RTM', @replicate_ddl = 1, @allow_subscriber_initiated_snapshot = N'false', @allow_web_synchronization = N'false', @allow_partition_realignment = N'true', @retention_period_unit = N'days', @conflict_logging = N'both', @automatic_reinitialization_policy = 0\n"
   +"exec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 14, @frequency_relative_interval = 1, @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 500, @active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null, @publisher_security_mode = 0, @publisher_login = N'sa', @publisher_password = N'sa'"    
            + "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'sa'"
            + "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT AUTHORITY\\SYSTEM'"
            + "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'ANDRES\\Andrés'"
            + "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\SQLSERVERAGENT'"
            + "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\MSSQLSERVER'"
            + "exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'distributor_admin'";

   String atrib=""; 

        for (int i=0;i<listaDer.getSize();i++){
            atrib=atrib+""
                    + "\nexec sp_mergearticlecolumn @publication = N'"+nombre+"', @article = N'clientes', "
                    + "@column = N'"+listaDer.getElementAt(i)+"', @operation = N'add', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        }
    String cadenaFiltro="null";
    if(!lista.isEmpty())
    {
         filtro="\n";
         cadenaFiltro="N'";
         //System.out.println(filtro);
                  for (int i=0;i<lista.getSize();i++){
                      if(i==0){
                          filtro=filtro+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                      else{
                          filtro=filtro+"AND "+lista.getElementAt(i);
                          cadenaFiltro=cadenaFiltro+lista.getElementAt(i);
                      }
                     // System.out.println(cadenaFiltro);
                  }
        //  System.out.println(filtro); 
                  cadenaFiltro=cadenaFiltro+"'";
    }
    String columna="";
    if (listaDer.isEmpty())
        columna="false";
    else
        columna="true";
        
        atributos="\n use ["+base+"] exec sp_addmergearticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'table', @description = N'', @creation_script = N'', "
        + "@pre_creation_cmd = N'drop', @schema_option = 0x000000010C034FD1, @identityrangemanagementoption = N'none', @destination_owner = N'dbo', @force_reinit_subscription = 1, "
        + "@column_tracking = N'false', @subset_filterclause = "+cadenaFiltro+", @vertical_partition = N'"+columna+"', @verify_resolver_signature = 1, @allow_interactive_resolver = N'false', @fast_multicol_updateproc = N'true', "
        + "@check_permissions = 0, @subscriber_upload_options = 0, @delete_tracking = N'true', @compensate_for_errors = N'false', @stream_blob_columns = N'false' , @partition_options = 0\n"
                + ""+atrib;
    System.out.println("TODO: "+aux+tipo+atributos);
    return aux+tipo+atributos;
}

public String sqlSuscripcionMerge(String nombre, String nodo,String baseDestino){
 String suscripcion="use ["+base+"]\n"
         + "exec sp_addmergesubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'"+baseDestino+"', "
         + "@subscription_type = N'Push', @sync_type = N'Automatic', @subscriber_type = N'Local', @subscription_priority = 0, "
         + "@description = null, @use_interactive_resolver = N'False'\n"
         + ""
         + "exec sp_addmergepushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'"+baseDestino+"', "
         + "@job_login = null, @job_password = null, @subscriber_security_mode = 0, @subscriber_login = N'sa', @subscriber_password = 'sa', "
         + "@publisher_security_mode = 0,@publisher_login = N'sa', @publisher_password = N'sa', @frequency_type = 64, @frequency_interval = 0, @frequency_relative_interval = 0, "
         + "@frequency_recurrence_factor = 0, @frequency_subday = 0, @frequency_subday_interval = 0, @active_start_time_of_day = 0, "
         + "@active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 99991231, @enabled_for_syncmgr = N'False'";
 
 return suscripcion;
}

public String sqlSuscripcionTransaccional(String nombre, String nodo,String base){
 String suscripcion="use ["+jcBase.getSelectedItem().toString()+"]\n" +  // revisar si tiene que ser del origen o del destino
"exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'"+base+"', "
+ "@subscription_type = N'Push', @sync_type = N'automatic', @article = N'all', @update_mode = N'read only', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'"+base+"',"
 + " @job_login = null, @job_password = null, @subscriber_security_mode = 0,@subscriber_login = N'sa',@subscriber_password = N'sa',  @frequency_type = 64,"
        + " @frequency_interval = 1, @frequency_relative_interval = 1, @frequency_recurrence_factor = 0, @frequency_subday = 4, "
        + "@frequency_subday_interval = 5, @active_start_time_of_day = 0, @active_end_time_of_day = 235959, @active_start_date = 0, "
        + "@active_end_date = 0, @enabled_for_syncmgr = N'False', @dts_package_location = N'Distributor'"; 

 System.out.println("Suscripcion: "+suscripcion);
 return suscripcion;
}

public String sqlSuscripcionPeer(String origen,String destino, String nombre,String nodo)
{
String suscripcion=""+
 "use ["+origen+"]\n" +
"exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'"+destino+"', @subscription_type = N'Push', "
+ "@sync_type = N'replication support only', @article = N'all', @update_mode = N'read only', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'"+destino+"', @job_login = null,"
+ "@job_password = null, @subscriber_security_mode = 0,@subscriber_login = N'sa',@subscriber_password = N'sa', @frequency_type = 64, @frequency_interval = 1, @frequency_relative_interval = 1,"
+ " @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 5, @active_start_time_of_day = 0, @active_end_time_of_day = " +
"235959, @active_start_date = 0, @active_end_date = 0, @dts_package_location = N'Distributor'";
System.out.println("Suscripcion: \n"+suscripcion);    
return suscripcion;
}

public String sqlSuscripcionSnap(String nombre,String nodo,String base){
String suscripcion="";
suscripcion=""+ 
"use ["+jcBase.getSelectedItem().toString()+"]\n" +
 "exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'"+base+"',"
+ "@subscription_type = N'Push',@sync_type = N'automatic', @article = N'all', @update_mode = N'read only', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'"+base+"', "
+ "@job_login = null, @job_password = null, @subscriber_security_mode = 0,@subscriber_login = N'sa',@subscriber_password = N'sa',  @frequency_type = 64,"
        + " @frequency_interval = 1, @frequency_relative_interval = 1, @frequency_recurrence_factor = 0, @frequency_subday = 4, "
        + "@frequency_subday_interval = 5, @active_start_time_of_day = 0, @active_end_time_of_day = 235959, @active_start_date = 0, "
        + "@active_end_date = 0, @dts_package_location = N'Distributor'";
        return suscripcion;
}

public String sqlSuscripcionCola(String nombre,String nodo,String base){
String suscripcion="";
System.out.println(jcBase.getSelectedItem().toString());
suscripcion="use ["+jcBase.getSelectedItem().toString()+"]\n" +
"exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'"+base+"', @subscription_type = N'Push', "
        + "@sync_type = N'automatic', @article = N'all', @update_mode = N'queued failover', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'"+base+"',"
+ " @job_login = null, @job_password = null, @subscriber_security_mode = 0,@subscriber_login = N'sa',@subscriber_password = N'sa',"
+ " @frequency_type = 64, @frequency_interval = 1, @frequency_relative_interval = 1, @frequency_recurrence_factor = 0, @frequency_subday = 4, "
+ "@frequency_subday_interval = 5, @active_start_time_of_day = 0,"
+ " @active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @dts_package_location = N'Distributor'\n";
System.out.println("COLA: "+suscripcion);
return suscripcion;
}

public String sqlSuscripcionColaParteDos(String nombre,String base){
    String parteDos=""+
          "use ["+base+"]\n" +
"exec sp_link_publication @publisher = N'"+ServidorLocal+"', @publisher_db = N'"+jcBase.getSelectedItem().toString()+"', @publication = N'"+nombre+"', "
            + "@distributor = N'"+ServidorLocal+"', @security_mode = 2, @login = N'sa', @password = N'sa'";  // REVISAR
System.out.println("COLA2: "+parteDos);    
return parteDos;
}

public String crearTablaSuscripcion(String base)
{ 
   String sentencia=""+"USE ["+base+"] SET ANSI_NULLS ON SET QUOTED_IDENTIFIER ON "
            + "CREATE TABLE [dbo].[clientes](";
  //  if(listaDer.isEmpty())
 // {
    sentencia= sentencia
            + "[CI] [nchar](10) NOT NULL,"
            + "[Nombre] [nchar](20) NULL,"
            + "[Apellido] [nchar](20) NULL,"
            + "[Telefono] [nchar](10) NULL,"
            + "[Direccion] [nchar](30) NULL,"
            + "[Ciudad] [nchar](20) NULL,"
            + "[Edad] [int] NULL,"
            + "CONSTRAINT [PK_clientes] PRIMARY KEY CLUSTERED ([CI] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]) ON [PRIMARY]";
  //  }
    //else
//    {
//        sentencia+="[CI] [nchar](10) NOT NULL,";
//        if(listaDer.contains("Nombre"))
//        sentencia+="[Nombre] [nchar](20) NULL,";
//        if(listaDer.contains("Apellido"))
//        sentencia+="[Apellido] [nchar](20) NULL,";
//        if(listaDer.contains("Telefono"))
//         sentencia+="[Telefono] [nchar](10) NULL,";
//        if(listaDer.contains("Direccion"))
//          sentencia+="[Direccion] [nchar](30) NULL,";
//        if(listaDer.contains("Ciudad"))
//          sentencia+="[Ciudad] [nchar](20) NULL,";
//        if(listaDer.contains("Edad"))
//            sentencia+="[Edad] [int] NULL,";
//        sentencia= sentencia+"CONSTRAINT [PK_clientes] PRIMARY KEY CLUSTERED ([CI] ASC)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]) ON [PRIMARY]";
//    }
return sentencia;
}


public String filtros(String nombre) {
    String text="";
    if ("".equals(txtFiltros.getText())){
        text=text+",@filter_clause = N'"+txtFiltros.getText()+"'\n"
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
            Excepciones.GestionarExcepcion(e);
            String mensaje=Excepciones.GetMensajePersonalizado();
           codigo=Excepciones.GetCodigoError();
           if(codigo!=21745){
           JOptionPane.showMessageDialog(null, "No se puede crear "+mensaje);
           System.out.println( "CODIGO: "+codigo);
           }
        }
}
 conexion cc2=new conexion();
  Connection  cn2;
public void ejecutar(String sql,String server,String base) throws SQLException{
       
         cn2=(Connection) cc2.conectarBase(server,base);
       
        try{ 
            PreparedStatement psd2=cn2.prepareStatement(sql);
            psd2.execute();
        }
        catch(SQLServerException e){
         Excepciones.GestionarExcepcion(e);
            String mensaje=Excepciones.GetMensajePersonalizado();
         if(codigo!=21745){
           JOptionPane.showMessageDialog(null, "No se puede crear Sobrecarga 1"+mensaje);
           System.out.println( "CODIGO: "+codigo);
           }
        }
}
public void ejecutar2(String sql,String server,String base) throws SQLException{
        conexion cc3=new conexion();
        Connection  cn3=(Connection) cc3.conectarBase(server,base);
        cn3=(Connection) cc3.conectar(server);
        try{ 
            PreparedStatement psd3=cn3.prepareStatement(sql);
            psd3.execute();
        }
        catch(SQLServerException e){
        Excepciones.GestionarExcepcion(e);
        String mensaje=Excepciones.GetMensajePersonalizado();
         if(codigo!=21745){
           JOptionPane.showMessageDialog(null, "No se puede crear Sobrecarga2 "+mensaje);
           System.out.println( "CODIGO: "+codigo);
           }
        }
}

public void ejecutarMerge(String sql) throws SQLException{
        cn=(Connection) cc.conectar(servidor);
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            psd.executeQuery();
        }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede crear"+e.getMessage()+"\n CODIGO: "+e.getErrorCode());
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
    private javax.swing.JButton btnAñadir;
    private javax.swing.JButton btnContega;
    private javax.swing.JButton btnDiferente;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarFiltro;
    private javax.swing.JButton btnEmpiece;
    private javax.swing.JButton btnEsteEn;
    private javax.swing.JButton btnIgual;
    private javax.swing.JButton btnInsertar;
    private javax.swing.JButton btnMayorIgual;
    private javax.swing.JButton btnMayorQue;
    private javax.swing.JButton btnMenorIgual;
    private javax.swing.JButton btnMenorQue;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNoEsteEn;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnPublicar;
    private javax.swing.JButton btnSuscribir;
    private javax.swing.JButton btnTermine;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTree jTree1;
    private javax.swing.JTree jTree2;
    private javax.swing.JComboBox jcBase;
    private javax.swing.JComboBox jcBaseDestino;
    private javax.swing.JCheckBox jchA;
    private javax.swing.JCheckBox jchB;
    private javax.swing.JCheckBox jchC;
    private javax.swing.JList jlCampos;
    private javax.swing.JList jlCamposDer;
    private javax.swing.JList jlCamposIzq;
    private javax.swing.JList jlsFiltros;
    private javax.swing.JMenu jmAdministrarReplicaciones;
    private javax.swing.JRadioButton jrbConectar;
    private javax.swing.JRadioButton jrbDesconectar;
    private javax.swing.JRadioButtonMenuItem jrbMezcla;
    private javax.swing.JRadioButtonMenuItem jrbSnapshot;
    private javax.swing.JRadioButtonMenuItem jrbTranCola;
    private javax.swing.JRadioButtonMenuItem jrbTranEstandar;
    private javax.swing.JRadioButtonMenuItem jrbTranPeer;
    private javax.swing.JLabel lblFiltro;
    private javax.swing.JTable tblTabla;
    private javax.swing.JTextField txtCampos;
    private javax.swing.JTextField txtFiltros;
    private javax.swing.JTextField txtNombrePub;
    // End of variables declaration//GEN-END:variables
}

