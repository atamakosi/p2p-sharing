/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package View;

import Model.FileServerList;
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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
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
    private JButton refreshBtn;
    private JTextField searchFld;
    private JButton searchBtn;
    private GridBagConstraints gridBagConstraint;
    private DefaultListModel fileListModel;
    private JList peerList;
    private DefaultListModel peerListModel;
    private JScrollPane peerScroll;
    private JScrollPane fileScroll;
    
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
                int fileListIndex = fileList.getSelectedIndex();
                if (fileListIndex >= 0) {
                    String file = (String) fileListModel.get(fileListIndex);
                    System.out.println("File sellected is " + file);
                    getFile(file);
                }
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
        refreshBtn = new JButton("REFRESH");
        refreshBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                update(node.getPeers());
            }
        });
      
        searchBtn = new JButton("Search");
        searchBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                searchFileNames();
            }
        });
        searchFld = new JTextField();
        searchFld.setSize(100, WIDTH);
        searchFld.setToolTipText("Search...");
        toolBar.add(refreshBtn);
        toolBar.add(getBtn);
        toolBar.add(putBtn);
        toolBar.add(searchFld);
        toolBar.add(searchBtn);
        toolBar.setFloatable(false);
        filePnl = new JPanel();
        filePnl.setLayout(new BorderLayout());
        filePnl.setBorder(BorderFactory.createLineBorder(Color.black));
        
        fileListModel = new DefaultListModel();
        fileList = new JList(fileListModel);
        fileList.setLayoutOrientation(JList.VERTICAL);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileScroll = new JScrollPane(fileList);
        filePnl.add(fileScroll);
     
        peerPnl = new JPanel();
        peerPnl.setLayout(new BorderLayout());
        peerPnl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        peerListModel = new DefaultListModel();
        peerList = new JList(peerListModel);
        peerList.setLayoutOrientation(JList.VERTICAL);
        peerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peerScroll = new JScrollPane(peerList);
        peerPnl.add(peerScroll);

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
    public void update(Map<String, PeerNode> peers) {
        //Add new peers to ui
        peerListModel.removeAllElements();
        fileListModel.removeAllElements();
        
        Iterator it = node.getPeers().values().iterator();
        while (it.hasNext())   {
            String str = it.next().toString();
            peerListModel.addElement(str);
        }
        //Add new files to ui waiting on PeerNode.geFileList()
        Iterator serIt = node.getFileList().iterator();
        while (serIt.hasNext()) {
            FileServerList fileList = (FileServerList) serIt.next();
            Iterator filIt = fileList.iterator();
            while (filIt.hasNext()) {
                String str = filIt.next().toString();
                fileListModel.addElement(str);
            }
        }
//        filePnl.repaint();
//        peerPnl.repaint();
        this.revalidate();
    }
    
    public void getFile(String fname) {
        //Get list of servers with files
        Iterator servers = node.getFileList().iterator();
        String serverNote = "none";
        while (servers.hasNext()) {
            FileServerList fileList = (FileServerList) servers.next();
            if (fileList.contains(fname)) {
                serverNote = fileList.toString();
            }
        }
        System.out.println("File found on " + serverNote);
        if (!serverNote.equals("none")) {
            node.getFileFromServer(serverNote, fname);
        }
    }
    
    public void searchFileNames()   {
        String searchString = searchFld.getText();
        System.out.println("String in search = " + searchString);
        boolean found = false;
        Iterator servers = node.getFileList().iterator();
        while (servers.hasNext()) {
            FileServerList fileList = (FileServerList) servers.next();
            if (fileList.contains(searchString)) {
                fileListModel.removeAllElements();
                fileListModel.addElement(fileList.get(fileList.indexOf(searchString)));
                filePnl.repaint();
                found = true;
                break;
            }
        }
        if (!found) {
            final JDialog searchResult = new JDialog();
            searchResult.setSize(100, 100);
            searchResult.setLayout(new BorderLayout());
            JPanel searchPnl = new JPanel();
            JLabel result = new JLabel("No results found!");
            JButton okBtn = new JButton("OK");
            okBtn.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    update(node.getPeers());
                    searchResult.dispose();
                }
            });
            searchPnl.add(result);
            searchPnl.add(okBtn);
            searchResult.add(searchPnl);
            searchResult.setVisible(true);
        }
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
        debugItm = new javax.swing.JMenuItem();

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

        debugItm.setText("Debug");
        debugItm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                debugItmActionPerformed(evt);
            }
        });
        editMnu.add(debugItm);

        menuBar.add(editMnu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitItmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitItmActionPerformed
        node.stopSockets();
//        node.stopThreads();
        dispose();
        System.exit(1);
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

    private void debugItmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_debugItmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_debugItmActionPerformed

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
    private javax.swing.JMenuItem debugItm;
    private javax.swing.JMenuItem disconnectItm;
    private javax.swing.JMenu editMnu;
    private javax.swing.JMenuItem exitItm;
    private javax.swing.JMenu fileMnu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem settingsItm;
    // End of variables declaration//GEN-END:variables

    
}
