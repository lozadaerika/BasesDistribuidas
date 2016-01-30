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
        cargarBases(server);
    }
    
    int nodos=0;
    String servidor,base,a="",b="",c="";
    String servidorUno="EDISSON";
    String servidorDos="ANDRES\\ANDRES";
    String ServidorLocal="ERIKA-LAP";
    
    DefaultListModel<String>listaIzq=new DefaultListModel<String>();
    DefaultListModel<String>listaDer=new DefaultListModel<String>();
    DefaultListModel<String>listaFiltros=new DefaultListModel<String>();
     DefaultListModel<String>lista=new DefaultListModel<String>();
    
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
    public void cargarBasesDestino(String server){
        jcBaseDestino.removeAllItems();
        jcBaseDestino.addItem("");
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
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e);
        }
        }
    }
    
    public void cargarCampos(String base){
        if(!jcBase.getSelectedItem().toString().equals("")){
        int i=0;
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,base);
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
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e);
        }
        }
    }
    
    public void cargarTabla(String server, String base){
        int i=0;
        conexion cc= new conexion();
        Connection cn=(Connection) cc.conectarBase(servidor,base);
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
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "No se ha podido realizar el SELECT"+e);
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
            }catch(Exception ex){
                JOptionPane.showMessageDialog(null, ex); 
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
       catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex);
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
        jLabel9 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jlsFiltros = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        btnEjecutar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jcBaseDestino = new javax.swing.JComboBox();
        jButton3 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap(24, Short.MAX_VALUE))
        );

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
                .addGap(0, 21, Short.MAX_VALUE))
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
                        .addGap(61, 61, 61))))
        );

        jLabel9.setText("Campos");

        jScrollPane6.setViewportView(jlsFiltros);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel9)
                        .addGap(51, 51, 51))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
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
                                .addComponent(btnAñadir)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel7)
                        .addGap(20, 20, 20)
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
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Administrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnEjecutar.setText("! Ejecutar");
        btnEjecutar.setName("btnEjecutar"); // NOI18N

        jLabel8.setText("Base Destino");

        jcBaseDestino.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcBaseDestinoItemStateChanged(evt);
            }
        });

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtNombrePub, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcBase, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 678, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSincronizar, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnEjecutar)
                                    .addComponent(jrbDesconectar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnInsertar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jcBaseDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jrbConectar))
                                        .addGap(0, 50, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jButton3)
                                        .addGap(58, 58, 58)))
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(50, 50, 50))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnInsertar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEliminar)
                            .addComponent(btnEjecutar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSincronizar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jrbConectar)
                        .addGap(18, 18, 18)
                        .addComponent(jrbDesconectar)
                        .addGap(146, 146, 146))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNombrePub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel6)
                            .addComponent(jcBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1)
                            .addComponent(jLabel8)
                            .addComponent(jcBaseDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3))
                        .addGap(21, 21, 21)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane5.setViewportView(jTextArea1);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane7.setViewportView(jTextArea2);

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jButton2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
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

private void btnSincronizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSincronizarActionPerformed
   
    if (jrbSnapshot.isSelected()){
        try {
            ejecutar(sqlPublicacionSnap(txtNombrePub.getText(),jcBase.getSelectedItem().toString()),ServidorLocal);
            JOptionPane.showMessageDialog(null, "Publicacion creada");
                if (jchA.isSelected()){ 
                        try {
                            ejecutar(crearTablaSuscripcion("clientes_practica"),servidorUno); // ERIKA-LAP\SITIOA
                         } catch (SQLException ex) {
                            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
                            }
                }
                  if (jchB.isSelected()){ 
                  try {
                        ejecutar(crearTablaSuscripcion("clientes_practica"),servidorDos); // ERIKA-LAP\\SITIOB
                    } catch (SQLException ex) {
                        Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
                    }
                 }
               if (jchC.isSelected()){ 
              try {
                    ejecutar(crearTablaSuscripcion("clientes_practica"),ServidorLocal); // ERIKA-LAP
                } catch (SQLException ex) {
                    Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
                }
             } 
           if (!"".equals(a)){
        JOptionPane.showMessageDialog(null, "A: "+a);
            try {
                ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),servidorUno),servidorUno); // ERIKA-LAP\\SITIOA
                  JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
            }
}
    if (!"".equals(b)){
        JOptionPane.showMessageDialog(null, "B: "+b);
            try {
                ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),servidorDos),servidorDos); // ERIKA-LAP\\SITIOB
                 JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
}
                if (!"".equals(c)){
                       JOptionPane.showMessageDialog(null, "C: "+c);
                    try {
                        ejecutar(sqlSuscripcionSnap(txtNombrePub.getText(),ServidorLocal),ServidorLocal);// ERIKA-LAP
                         JOptionPane.showMessageDialog(null, "Suscripcion creada");
                    } catch (SQLException ex) {
                        Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "AQUI");
                    }
                }
                JOptionPane.showMessageDialog(null, "Completado");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOOR");
        }   
        
    }
    else if (jrbTranEstandar.isSelected())
    {
    try {
            ejecutar(sqlPublicacionTransaccional(txtNombrePub.getText(),jcBase.getSelectedItem().toString()),ServidorLocal);
            JOptionPane.showMessageDialog(null, "Publicacion creada");
            if (jchA.isSelected()){ 
             try {
                ejecutar(crearTablaSuscripcion("clientes_transaccional"),servidorUno); // ERIKA-LAP\\SITIOA
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
        }
          if (jchB.isSelected()){ 
          try { 
                ejecutar(crearTablaSuscripcion("clientes_transaccional"),servidorDos+"\\SITIOB"); // ERIKA-LAP\\SITIOB
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
           if (jchC.isSelected()){ 
          try {
                ejecutar(crearTablaSuscripcion("clientes_transaccional"),ServidorLocal); // ERIKA-LAP
              
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
    
            if (!"".equals(a)){
        JOptionPane.showMessageDialog(null, "A: "+a);
            try {
                ejecutar(sqlSuscripcionTransaccional(txtNombrePub.getText(),servidorUno),servidorUno); // ERIKA-LAP\\SITIOA
                JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
            }
}
    if (!"".equals(b)){
        JOptionPane.showMessageDialog(null, "B: "+b);
            try {
                ejecutar(sqlSuscripcionTransaccional(txtNombrePub.getText(),servidorDos),servidorDos); // ERIKA-LAP\\SITIOB
                  JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
}
    if (!"".equals(c)){
           JOptionPane.showMessageDialog(null, "C: "+c);
        try {
            ejecutar(sqlSuscripcionTransaccional(txtNombrePub.getText(),ServidorLocal),ServidorLocal); // ERIKA-LAP
            JOptionPane.showMessageDialog(null, "Suscripcion creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "AQUI");
        }
    }
    JOptionPane.showMessageDialog(null, "Completado");
    
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORCola");
        }    
      
        
    }
    else if (jrbTranCola.isSelected()) 
    {
    try {
            ejecutar(sqlPublicacionTransacionalCola(txtNombrePub.getText(),jcBase.getSelectedItem().toString()),ServidorLocal);
            JOptionPane.showMessageDialog(null, "Publicacion creada");
            if (jchA.isSelected()){ 
             try {
                ejecutar(crearTablaSuscripcion("clientes_cola"),servidorUno); // ERIKA-LAP\\SITIOA
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI"); 
            } 
        }
          if (jchB.isSelected()){ 
          try {
                ejecutar(crearTablaSuscripcion("clientes_cola"),servidorDos);//ERIKA-LAP\\SITIOB
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
           if (jchC.isSelected()){ 
          try {
                ejecutar(crearTablaSuscripcion("clientes_cola"),ServidorLocal); // ERIKA-LAP
              
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
    
            if (!"".equals(a)){
        JOptionPane.showMessageDialog(null, "A: "+a);
            try {
                ejecutar(sqlSuscripcionCola(txtNombrePub.getText(),servidorUno),servidorUno); // ERIKA-LAP\SITIOA
                JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
            }
}
    if (!"".equals(b)){
        JOptionPane.showMessageDialog(null, "B: "+b);
            try {
                ejecutar(sqlSuscripcionCola(txtNombrePub.getText(),servidorDos),servidorDos); // ERIKA-LAP\SITIOB
                  JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
}
    if (!"".equals(c)){
           JOptionPane.showMessageDialog(null, "C: "+c);
        try {
            ejecutar(sqlSuscripcionCola(txtNombrePub.getText(),ServidorLocal),ServidorLocal); //ERIKA-LAP
            JOptionPane.showMessageDialog(null, "Suscripcion creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "AQUI");
        }
    }
    JOptionPane.showMessageDialog(null, "Completado");
    
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORCola");
        }    
    }
        
    else if (jrbTranPeer.isSelected()){
        
        if (jchA.isSelected()){ 
             try {
                ejecutar(crearTablaSuscripcion(jcBase.getSelectedItem().toString()),servidorUno); //ERIKA-LAP\\SITIOA
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
        }
          if (jchB.isSelected()){ 
          try {
                ejecutar(crearTablaSuscripcion(jcBase.getSelectedItem().toString()),servidorDos); // ERIKA-LAP\\SITIOB
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
           if (jchC.isSelected()){ 
          try {
                ejecutar(crearTablaSuscripcion(jcBase.getSelectedItem().toString()),ServidorLocal); //ERIKA-LAP
              
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
          if(jchA.isSelected())
                llenarDatosPeer(jcBase.getSelectedItem().toString(),servidorUno); //ERIKA-LAP\\SITIOA
          if(jchB.isSelected())
                llenarDatosPeer(jcBase.getSelectedItem().toString(),servidorDos); //ERIKA-LAP\\SITTIOB
          if(jchC.isSelected())
                llenarDatosPeer(jcBase.getSelectedItem().toString(),ServidorLocal);// ERIKA-LAP            
        try {
            ejecutar(sqlPublicacionPeer(txtNombrePub.getText(),jcBase.getSelectedItem().toString(),ServidorLocal),ServidorLocal); //ERIKA-LAP
            JOptionPane.showMessageDialog(null, "Publicacion creada");    
            if(!"".equals(a)){
                 ejecutar(sqlPublicacionPeer(txtNombrePub.getText(),jcBase.getSelectedItem().toString(),servidor),servidor+"\\SITIOA");
                  ejecutar(sqlSuscripcionPeer(txtNombrePub.getText(),ServidorLocal),servidorUno);//ERIKA-LAP   ERIKA-LAP\\SITIOA
            }
            if(!"".equals(b))
            {
                  ejecutar(sqlPublicacionPeer(txtNombrePub.getText(),jcBase.getSelectedItem().toString(),servidor),servidor+"\\SITIOB");
                   ejecutar(sqlSuscripcionPeer(txtNombrePub.getText(),ServidorLocal),servidorDos); //ERIKA-LAP   ERIKA-LAP\\SITIOB
            }
         if(!"".equals(a)){
              JOptionPane.showMessageDialog(null, "A: "+a);
            try {
                ejecutar(sqlSuscripcionPeer(txtNombrePub.getText(),servidor+"\\SITIOA"),servidor); // ERIKA-LA[      ------AQUI
                JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
            }
         }
    if (!"".equals(b)){
        JOptionPane.showMessageDialog(null, "B: "+b);
            try {
                ejecutar(sqlSuscripcionPeer(txtNombrePub.getText(),servidor+"\\SITIOB"),servidor);
                  JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
}
    if (!"".equals(c)){
           JOptionPane.showMessageDialog(null, "C: "+c);
        try {
            ejecutar(sqlSuscripcionPeer(txtNombrePub.getText(),servidor),servidor);
            JOptionPane.showMessageDialog(null, "Suscripcion creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "AQUI");
        }
    }
    JOptionPane.showMessageDialog(null, "Completado");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORCola");
        }          
    }
        
    else if (jrbMezcla.isSelected())
    {
    try {
        ejecutar(sqlPublicacionMerge(txtNombrePub.getText(),jcBase.getSelectedItem().toString()));            
            JOptionPane.showMessageDialog(null, "Publicacion creada");
            if (jchA.isSelected()){ 
             try {
                ejecutar(crearTablaSuscripcion("clientes_mezcla"),servidorUno); // ERIKA-LAP\\SITIOA
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
        }
          if (jchB.isSelected()){ 
          try { 
                ejecutar(crearTablaSuscripcion("clientes_mezcla"),servidorDos); // ERIKA-LAP\\SITIOB
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
           if (jchC.isSelected()){ 
          try {
                ejecutar(crearTablaSuscripcion("clientes_mezcla"),ServidorLocal); // ERIKA-LAP
              
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"eRROR AQUI");
            }
         }
    
            
    
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"ERROOOORCola");
        }    
      
        
    }
    
        
           
    
    //////suscripcion
}//GEN-LAST:event_btnSincronizarActionPerformed

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
            JOptionPane.showMessageDialog(null, "No se puede insertar la información"+ex);
        }      
}

private void tblTablaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTablaKeyReleased
    
}//GEN-LAST:event_tblTablaKeyReleased

private void tblTablaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblTablaKeyTyped
    
}//GEN-LAST:event_tblTablaKeyTyped

private void jchAItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchAItemStateChanged
    if(jchA.isSelected()){
        a=sqlPublicacionMerge(txtNombrePub.getText(),jcBase.getSelectedItem().toString());
        cargarBasesDestino(servidorUno);
    }else
        a="";
}//GEN-LAST:event_jchAItemStateChanged

private void jchBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchBItemStateChanged
    if(jchB.isSelected()){
        b=sqlSuscripcionSnap(txtNombrePub.getText(), servidorDos);    
    cargarBasesDestino(servidorDos);
    }else
        b="";
}//GEN-LAST:event_jchBItemStateChanged

private void jchCItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jchCItemStateChanged
    if(jchC.isSelected()){
        c=sqlSuscripcionSnap(txtNombrePub.getText(), ServidorLocal);
    cargarBases(ServidorLocal);
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
        // TODO add your handling code here:
    }//GEN-LAST:event_jcBaseDestinoItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jTextArea1.setText(sqlSuscripcionMerge(txtNombrePub.getText(),servidorUno));
        jTextArea2.setText(sqlPublicacionMerge(txtNombrePub.getText(),jcBase.getSelectedItem().toString()));
        if (!"".equals(a)){
        JOptionPane.showMessageDialog(null, "A: "+a);
            try {
                ejecutar(sqlSuscripcionMerge(txtNombrePub.getText(),servidorUno)); // ERIKA-LAP\\SITIOA
                JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex+"SITIOA");
            }
}
    if (!"".equals(b)){
        JOptionPane.showMessageDialog(null, "B: "+b);
            try {
                ejecutar(sqlSuscripcionMerge(txtNombrePub.getText(),servidorDos)); // ERIKA-LAP\\SITIOB
                  JOptionPane.showMessageDialog(null, "Suscripcion creada");
            } catch (SQLException ex) {
                Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
}
    if (!"".equals(c)){
           JOptionPane.showMessageDialog(null, "C: "+c);
        try {
            ejecutar(sqlSuscripcionMerge(txtNombrePub.getText(),ServidorLocal)); // ERIKA-LAP
            JOptionPane.showMessageDialog(null, "Suscripcion creada");
        } catch (SQLException ex) {
            Logger.getLogger(replicasMenu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "AQUI");
        }
    }
    JOptionPane.showMessageDialog(null, "Completado");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        cargarBasesDestino(servidorUno);
    }//GEN-LAST:event_jButton3ActionPerformed

public static String aux,atributos,tipo,sincro,filtro;
int k=1;
int l=50;

public String sqlPublicacionSnap(String nombre,String base){
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
            + " @publisher_security_mode = 1";    
atributos="\n use ["+base+"] exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased',"
+ " @description = null, @creation_script = null, @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509D, @identityrangemanagementoption = N'manual',"
+ " @destination_table = N'clientes', @destination_owner = N'dbo', @vertical_partition = ";
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
   // System.out.println("TODO: "+aux+tipo+atributos+filtro+sincro);
    return aux+tipo+atributos+filtro+sincro;
}

public String sqlPublicacionTransaccional(String nombre,String base){
    aux="";atributos="";tipo="";sincro="";filtro="";
    aux="";
    tipo=" "+
"use ["+base+"] exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'publish', @value = N'true'\n"+
"use ["+base+"] exec sp_addpublication @publication = N'"+nombre+"', @description = N'Transactional publication of database ''"+base+"'' from Publisher ''"+servidor+"''.',"
+ " @sync_method = N'concurrent', @retention = 0, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'true', @enabled_for_internet = N'false',"
+ " @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_login = N'anonymous', @allow_subscription_copy = N'false', "
+ "@add_to_active_directory = N'false', @repl_freq = N'continuous', @status = N'active', @independent_agent = N'true', @immediate_sync = N'true',"
+ " @allow_sync_tran = N'false', @autogen_sync_procs = N'false', @allow_queued_tran = N'false', @allow_dts = N'false', @replicate_ddl = 1, @allow_initialize_from_backup = N'false', @enabled_for_p2p = N'false', @enabled_for_het_sub = N'false'"
                + "\nexec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 1, @frequency_relative_interval = 1,"
            + " @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 0, "
            + "@active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null,"
            + " @publisher_security_mode = 1";    
atributos="\n use ["+base+"] exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'logbased',"
+ " @description = null, @creation_script = null, @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual',"
+ " @destination_table = N'clientes', @destination_owner = N'dbo', @vertical_partition = ";
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
    atributos=atributos+", @ins_cmd = N'CALL sp_MSins_dboclientes', @del_cmd = N'CALL sp_MSdel_dboclientes', @upd_cmd = N'SCALL sp_MSupd_dboclientes' ";
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
"use ["+base+"] exec sp_addpublication @publication = N'"+nombre+"', @description = N'Transactional publication with updatable subscriptions of database ''"+base+"'' from Publisher ''"+servidor+"''.',"
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

public String sqlPublicacionMerge(String nombre,String base){
    aux="";atributos="";tipo="";sincro="";filtro="";
    aux="";
    tipo="use master\n" +
    "exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'publish', @value = N'true'\n"
   + "use master\n" +
    "exec sp_replicationdboption @dbname = N'"+base+"', @optname = N'merge publish', @value = N'true'\n"
   +"use [proyecto] exec sp_addmergepublication @publication = N'"+nombre+"', @description = N'Merge publication of database ''"+base+"'' from Publisher ''ANDRES\\ANDRES''.', @sync_mode = N'native', @retention = 14, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'true', @enabled_for_internet = N'false', @snapshot_in_defaultfolder = N'true', @compress_snapshot = N'false', @ftp_port = 21, @ftp_subdirectory = N'ftp', @ftp_login = N'anonymous', @allow_subscription_copy = N'false', @add_to_active_directory = N'false', @dynamic_filters = N'false', @conflict_retention = 14, @keep_partition_changes = N'false', @allow_synctoalternate = N'false', @max_concurrent_merge = 0, @max_concurrent_dynamic_snapshots = 0, @use_partition_groups = null, @publication_compatibility_level = N'100RTM', @replicate_ddl = 1, @allow_subscriber_initiated_snapshot = N'false', @allow_web_synchronization = N'false', @allow_partition_realignment = N'true', @retention_period_unit = N'days', @conflict_logging = N'both', @automatic_reinitialization_policy = 0\n"
   +"exec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 14, @frequency_relative_interval = 1, @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 500, @active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = null, @job_password = null, @publisher_security_mode = 1\n";//@publisher_security_mode = 0, @publisher_login = N'sa', @publisher_password = N''    

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
        
        atributos="\n use ["+base+"] exec sp_addmergearticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo', @source_object = N'clientes', @type = N'table', @description = null, "
        + "@creation_script = null, @pre_creation_cmd = N'drop', @schema_option = 0x000000010C034FD1, @identityrangemanagementoption = N'manual', @destination_owner = N'dbo', @force_reinit_subscription = 1, "
        + "@column_tracking = N'false', @subset_filterclause = "+cadenaFiltro+", @vertical_partition = N'"+columna+"', @verify_resolver_signature = 1, @allow_interactive_resolver = N'false', @fast_multicol_updateproc = N'true', "
        + "@check_permissions = 0, @subscriber_upload_options = 0, @delete_tracking = N'true', @compensate_for_errors = N'false', @stream_blob_columns = N'false' , @partition_options = 0";
    System.out.println("TODO: "+aux+tipo+atributos);
    return aux+tipo+atributos;
}

public String sqlPublicacionPeer(String nombre,String base, String server)
{
String publicacion= "use master exec sp_replicationdboption @dbname = N'"+jcBase.getSelectedItem().toString()+"', @optname = N'publish', @value = N'true'\n"+
// "exec ["+jcBase.getSelectedItem().toString()+"].sys.sp_addlogreader_agent @job_login = null, @job_password = null, @publisher_security_mode = 1 \n"+
//  "exec ["+jcBase.getSelectedItem().toString()+"].sys.sp_addqreader_agent @job_login = null, @job_password = null, @frompublisher = 1 \n"+
  "use ["+jcBase.getSelectedItem().toString()+"]\n" +  "exec sp_addpublication @publication = N'"+nombre+"',"
   + " @description = N'Transactional publication of database ''"+jcBase.getSelectedItem().toString()+"'' from Publisher ''"+server+"''.', @sync_method = N'native', @retention " +
"= 0, @allow_push = N'true', @allow_pull = N'true', @allow_anonymous = N'false', @enabled_for_internet = N'false', @snapshot_in_defaultfolder = N'true', "
 + "@compress_snapshot = N'false', @ftp_port = 21, @ftp_login = N'anonymous', @allow_subscription_copy = N'false', @add_to_active_directory = N'false',"
+ " @repl_freq = N'continuous', @status = N'active', " +
"@independent_agent = N'true', @immediate_sync = N'true', @allow_sync_tran = N'false', " +
"@autogen_sync_procs = N'false', @allow_queued_tran = N'false', @allow_dts = N'false', " +
"@replicate_ddl = 1, @allow_initialize_from_backup = N'true', @enabled_for_p2p = N'true', " +
"@enabled_for_het_sub = N'false', @p2p_conflictdetection = N'true', @p2p_originator_id = 1 \n"+
    "exec sp_addpublication_snapshot @publication = N'"+nombre+"', @frequency_type = 4, @frequency_interval = 1, @frequency_relative_interval = 1, @frequency_recurrence_factor = 0,"+ 
"@frequency_subday = 4, @frequency_subday_interval = 1, @active_start_time_of_day = 0, @active_end_time_of_day = 235959, @active_start_date = 0, @active_end_date = 0, @job_login = "+
"null, @job_password = null, @publisher_security_mode = 1 \n"+
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'sa' \n"+
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT AUTHORITY\\SYSTEM' \n"+
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'"+servidor+"\\Erika' \n"+ // VER SERVIDOR
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\SQLSERVERAGENT' \n"+
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'NT SERVICE\\MSSQLSERVER' \n"+
//"exec sp_grant_publication_access @publication = N'"+nombre+"', @login = N'distributor_admin' \n"+
" use ["+jcBase.getSelectedItem().toString()+"]\n" +
"exec sp_addarticle @publication = N'"+nombre+"', @article = N'clientes', @source_owner = N'dbo',@source_object = N'clientes', @type = N'logbased', "
+ "@description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F,@identityrangemanagementoption = N'manual', "
  + "@destination_table = N'clientes', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL " +
"[dbo].[sp_MSins_dboclientes]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboclientes]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboclientes]'";
    
 
return publicacion;
}


public String sqlSuscripcionTransaccional(String nombre, String nodo){
 String suscripcion="use ["+jcBase.getSelectedItem().toString()+"]\n" +
"exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'clientes_transaccional', "
+ "@subscription_type = N'Push', @sync_type = N'automatic', @article = N'all', @update_mode = N'read only', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'clientes_transaccional',"
 + " @job_login = null, @job_password = null, @subscriber_security_mode = 1, @frequency_type = 64, @frequency_interval = 0, @frequency_relative_interval = 0,"
 + " @frequency_recurrence_factor = 0, @frequency_subday = 0, @frequency_subday_interval = 0, @active_start_time_of_day = 0, @active_end_time_of_day = 235959, "
 + "@active_start_date = 20160126, @active_end_date = 99991231, @enabled_for_syncmgr = N'False', @dts_package_location = N'Distributor'";   
 
 return suscripcion;
}

public String sqlSuscripcionMerge(String nombre, String nodo){
 String suscripcion="use ["+base+"]\n" +
"exec sp_addmergesubscription @publication = N'"+nombre+"', @subscriber = N'ANDRES\\SITIO_A', @subscriber_db = N'clientes_mezcla', @subscription_type = N'Push', @sync_type = N'Automatic', @subscriber_type = N'Local', @subscription_priority = 0, @description = null, @use_interactive_resolver = N'False'\n" +
"exec sp_addmergepushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'ANDRES\\SITIO_A', @subscriber_db = N'clientes_mezcla', @job_login = null, @job_password = null, @subscriber_security_mode = 1, @publisher_security_mode = 1, @frequency_type = 64, @frequency_interval = 0, @frequency_relative_interval = 0, @frequency_recurrence_factor = 0, @frequency_subday = 0, @frequency_subday_interval = 0, @active_start_time_of_day = 0, @active_end_time_of_day = 235959, @active_start_date = 20160130, @active_end_date = 99991231, @enabled_for_syncmgr = N'False'";   
 
 return suscripcion;
}



public String sqlSuscripcionPeer(String nombre, String nodo)
{
String suscripcion=""+
 "use ["+jcBase.getSelectedItem().toString()+"]\n" +
"exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'"+jcBase.getSelectedItem().toString()+"', @subscription_type = N'Push', "
+ "@sync_type = N'replication support only', @article = N'all', @update_mode = N'read only', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'"+jcBase.getSelectedItem().toString()+"', @job_login = null,"
+ "@job_password = null, @subscriber_security_mode = 1, @frequency_type = 64, @frequency_interval = 1, @frequency_relative_interval = 1,"
+ " @frequency_recurrence_factor = 0, @frequency_subday = 4, @frequency_subday_interval = 5, @active_start_time_of_day = 0, @active_end_time_of_day = " +
"235959, @active_start_date = 0, @active_end_date = 0, @dts_package_location = N'Distributor'";
    return suscripcion;
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

public String sqlSuscripcionCola(String nombre,String nodo){
String suscripcion="";
System.out.println(jcBase.getSelectedItem().toString());
suscripcion="use [proyecto_cola]\n" +
"exec sp_addsubscription @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @destination_db = N'clientes_cola', @subscription_type = N'Push', @sync_type = N'automatic', @article = N'all', @update_mode = N'queued failover', @subscriber_type = 0\n" +
"exec sp_addpushsubscription_agent @publication = N'"+nombre+"', @subscriber = N'"+nodo+"', @subscriber_db = N'clientes_cola', @job_login = null, @job_password = null, @subscriber_security_mode = 1, @frequency_type = 64, @frequency_interval = 0, "
        + "@frequency_relative_interval = 0, @frequency_recurrence_factor = 0, @frequency_subday = 0, @frequency_subday_interval = 0, @active_start_time_of_day = 0, @active_end_time_of_day = 235959, @active_start_date = 20160125, @active_end_date = 99991231, "
        + "@enabled_for_syncmgr = N'False', @dts_package_location = N'Distributor'\n" +
"use [clientes_cola]\n" +
"exec sp_link_publication @publisher = N'"+servidor+"', @publisher_db = N'proyecto_cola', @publication = N'"+nombre+"', @distributor = N'"+servidor+"', @security_mode = 2, @login = null, @password = null";
return suscripcion;
}



public String crearTablaSuscripcion(String base)
{ 
   String sentencia=""+"USE ["+base+"] SET ANSI_NULLS ON SET QUOTED_IDENTIFIER ON "
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
            + "exec sp_articlefilter @publication = N'"+nombre+"', @article = N'clientes', @filter_name = N'FLTR_clientes_"+k+"__67', @filter_clause = N'"+txtFiltros.getText() +"', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1\n"
            + "-- Adding the article synchronization object\n"
                + "exec sp_articleview @publication = N'"+nombre+"', @article = N'clientes', @view_name = N'SYNC_clientes_"+k+"__67', @filter_clause = N'"+txtFiltros.getText()+"', @force_invalidate_snapshot = 1, @force_reinit_subscription = 1";
        return text;
    }
    return "";
}

public void ejecutar(String sql) throws SQLException{
        cn=(Connection) cc.conectar(servidor);
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            psd.execute();
        }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede crear"+e.getMessage());
        }
}
public void ejecutar(String sql,String server) throws SQLException{
        cn=(Connection) cc.conectar(server);
        try{
            PreparedStatement psd=cn.prepareStatement(sql);
            psd.execute();
        }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede crear"+e.getMessage());
        }
}
public void ejecutar(String sql,String server,String base) throws SQLException{
        conexion cc2=new conexion();
        Connection  cn2=(Connection) cc2.conectarBase(server,base);
        cn2=(Connection) cc2.conectar(server);
        try{ 
            PreparedStatement psd2=cn2.prepareStatement(sql);
            psd2.execute();
        }
        catch(SQLServerException e){
            JOptionPane.showMessageDialog(null, "No se puede crear Sobrecarga"+e.getMessage());
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
    private javax.swing.JButton btnSincronizar;
    private javax.swing.JButton btnTermine;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
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
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JComboBox jcBase;
    private javax.swing.JComboBox jcBaseDestino;
    private javax.swing.JCheckBox jchA;
    private javax.swing.JCheckBox jchB;
    private javax.swing.JCheckBox jchC;
    private javax.swing.JList jlCampos;
    private javax.swing.JList jlCamposDer;
    private javax.swing.JList jlCamposIzq;
    private javax.swing.JList jlsFiltros;
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

