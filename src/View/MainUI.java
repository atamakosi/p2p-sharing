/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package View;

import Model.Observer;
import Model.PeerNode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

/**
 *
 * @author Adam
 */
public class MainUI extends javax.swing.JFrame implements Observer {

    private PeerNode node;
    private JPanel peerPnl;
    private JPanel contentPnl;
    private JPanel filePnl;
    private JTextArea fileTxtArea;
    private JList fileList;
    private JToolBar toolBar;
    private JButton getBtn;
    private JButton disconnectBtn;
    private JButton putBtn;
    private JTextField searchFld;
    private JButton searchBtn;
    private GridBagConstraints gridBagConstraint;
    private DefaultListModel listModel;
    
    /**
     * Creates new form MainUI
     */
    public MainUI() {
        initComponents();
        setSize(400,600);
        connectItm.setEnabled(false);
        contentPnl = new JPanel();
        contentPnl.setLayout(new BorderLayout());
        toolBar = new JToolBar();
        getBtn = new JButton("GET");
        getBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Sending request for file...");
                //need to implement retrieval in peernode
                //node.getFile(fileList.getSelected()); for example
            }
        });
        putBtn = new JButton("PUT");
        putBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Sending request to send file...");
                //need to implement remote method call to get file from local
            }
        });
//        disconnectBtn = new JButton("Disconnect");
      
        searchBtn = new JButton("Search");
        searchFld = new JTextField();
        searchFld.setSize(100, WIDTH);
        searchFld.setToolTipText("Search...");
        toolBar.add(getBtn);
        toolBar.add(putBtn);
//        toolBar.add(disconnectBtn);
        toolBar.add(searchFld);
        toolBar.add(searchBtn);
        toolBar.setFloatable(false);
        filePnl = new JPanel();
        filePnl.setLayout(new BorderLayout());
        filePnl.setBorder(BorderFactory.createLineBorder(Color.black));
        //fileTxtArea = new JTextArea(); 
        //TO DO : add file names from peers to JList for selection
        listModel = new DefaultListModel();
        fileList = new JList(listModel);
        fileList.setLayoutOrientation(JList.VERTICAL);
        fileList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        filePnl.add(fileList);
        peerPnl = new JPanel();
        peerPnl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        peerPnl.setLayout(new GridBagLayout());
        //adds new peers to top of panel and stacks them vertically
        gridBagConstraint = new GridBagConstraints();
        gridBagConstraint.anchor = GridBagConstraints.NORTH;
        gridBagConstraint.weighty = 1;
        contentPnl.add(toolBar, BorderLayout.NORTH);
        contentPnl.add(peerPnl, BorderLayout.WEST);
        contentPnl.add(filePnl, BorderLayout.CENTER);
        this.add(contentPnl);
    }

    public void setNode(PeerNode node)  {
        this.node = node;
        node.register(this);
    }
    
    @Override
    public void update(Map<InetAddress, PeerNode> peers) {
        //Add new peers to ui
        peerPnl.removeAll();
        Iterator it = peers.values().iterator();
        while (it.hasNext())   {
            String str = it.next().toString();
            JLabel pTxt = new JLabel(str);
            peerPnl.add(pTxt, gridBagConstraint);
        }
        peerPnl.repaint();
        this.revalidate();
               
        //Add new files to ui waiting on PeerNode.geFileList()
//        filePnl.removeAll();
        Iterator it2 = node.getFileList().iterator();
        while (it2.hasNext()) {
            String str = it2.next().toString();
//            System.out.println("File: " + str);
//            JLabel fTxt = new JLabel(str);
            listModel.addElement(str);
        }
        
//        filePnl.repaint();
//        this.revalidate();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        fileMnu = new javax.swing.JMenu();
        connectItm = new javax.swing.JMenuItem();
        disconnectItm = new javax.swing.JMenuItem();
        exitItm = new javax.swing.JMenuItem();
        editMnu = new javax.swing.JMenu();
        settingsItm = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileMnu.setText("File");

        connectItm.setText("Connect...");
        connectItm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectItmActionPerformed(evt);
            }
        });
        fileMnu.add(connectItm);

        disconnectItm.setText("Disconnect...");
        disconnectItm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectItmActionPerformed(evt);
            }
        });
        fileMnu.add(disconnectItm);

        exitItm.setText("Exit");
        exitItm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItmActionPerformed(evt);
            }
        });
        fileMnu.add(exitItm);

        menuBar.add(fileMnu);

        editMnu.setText("Edit");

        settingsItm.setText("Settings");
        editMnu.add(settingsItm);

        menuBar.add(editMnu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitItmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItmActionPerformed
        node.stopSockets();
        node.stopThreads();
        dispose();
    }//GEN-LAST:event_exitItmActionPerformed

    private void connectItmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectItmActionPerformed
        node.start();
        disconnectItm.setEnabled(true);
        connectItm.setEnabled(false);
    }//GEN-LAST:event_connectItmActionPerformed

    private void disconnectItmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectItmActionPerformed
        System.out.println("Stopping node services...");
        node.stopSockets();
        peerPnl.removeAll();
        fileList.removeAll();
        peerPnl.repaint();
        filePnl.repaint();
        fileList.repaint();
        connectItm.setEnabled(true);
        disconnectItm.setEnabled(false);
    }//GEN-LAST:event_disconnectItmActionPerformed

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
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem connectItm;
    private javax.swing.JMenuItem disconnectItm;
    private javax.swing.JMenu editMnu;
    private javax.swing.JMenuItem exitItm;
    private javax.swing.JMenu fileMnu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem settingsItm;
    // End of variables declaration//GEN-END:variables

    
}
