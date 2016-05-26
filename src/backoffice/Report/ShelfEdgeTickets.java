/*
 * Copyright (C) 2015 Sunny Patel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package backoffice.Report;

import backoffice.ReportRunner;
import backoffice.Reports;
import static backoffice.base.DBConnection.conn;
import static backoffice.base.DBConnection.rs;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 */
public class ShelfEdgeTickets extends Reports implements ActionListener, KeyListener {

    JDialog shelfEdgeTicketDialog = new JDialog(frame, "Shelf Edge Tickets", true);
    JButton report21PerSheet, clearQueue, refreshBtn, closeBtn;
    JTextField barcodeFld;
    Object[][] data = null;
    String[] cn = {"Product ID", "Name", "Barcode", "Price"};
    DefaultTableModel labelDtm = new DefaultTableModel(data, cn);
    JTable labelTable = new JTable(labelDtm);
    
    public Component getTabs() {
    
        JPanel reportPanel = new JPanel();
        report21PerSheet = new JButton("Show Labels");
        report21PerSheet.addActionListener(this);
        
        clearQueue = new JButton("Clear Queue");
        clearQueue.addActionListener(this);
        
        refreshBtn = new JButton("<html>Refresh<br> Queue</html>");
        refreshBtn.addActionListener(this);
        
        barcodeFld = new JTextField(7);
        barcodeFld.addKeyListener(this);
        barcodeFld.setFont(large);
        
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        
        labelTable.setFont(h2);
        
        loadPrintQueueToTable();
        JScrollPane labelSP = new JScrollPane(labelTable);
        
        reportPanel.add(labelSP);
        
        JPanel btnsPnl = new JPanel();
        btnsPnl.add(report21PerSheet);
        btnsPnl.add(clearQueue);
        btnsPnl.add(refreshBtn);
        btnsPnl.add(closeBtn);
        
        JPanel barcodePnl = new JPanel();
        barcodePnl.add(new JLabel("Barcode"));
        barcodePnl.add(barcodeFld);
        
        btnsPnl.setLayout(new GridLayout(1, 4));
        
        reportPanel.add(barcodePnl);
        
        reportPanel.add(btnsPnl);
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.PAGE_AXIS));
        
        return reportPanel;
    }
    
    /**
     * Refreshes the print queue table
     */
    public void refreshPrintQueue() {
        int rows = labelDtm.getRowCount();
        while(rows > 0) {
            rows--;
            labelDtm.removeRow(0);
        }
        loadPrintQueueToTable();
    }
    
    /**
     * Load Print Queue to GUI Table
     */
    private void loadPrintQueueToTable() {
        try {
            String sql = "select * from searchproducts where prid in " +
                "(select prid from printqueue)";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                Object[] row = {rs.getString("prid"), 
                    rs.getString("name"), 
                    rs.getString("barcode"), 
                    rs.getString("price")};
                labelDtm.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void clearPrintQueue() {
        try {
            String sql = "delete from printqueue";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Print Queue Cleared");
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     private void findScannedProduct(String productBarcode) {
        // Edit Product
        try {
            String sql = "select * from `searchproducts` where `barcode`= ? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productBarcode);
            rs = pstmt.executeQuery();
            boolean found = false;
            String prid = "";
            while(rs.next()) {
                found = true;
                prid = rs.getString("prid");
                Object[] row = {rs.getString("prid"), 
                    rs.getString("name"), 
                    rs.getString("barcode"), 
                    rs.getString("price")};
                labelDtm.addRow(row);
            }
            
            if (found) {
                sql = "insert into printqueue (prid) values (?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, prid);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "Added to print queue");
            } else {
                JOptionPane.showMessageDialog(frame, "Cannot find label to print",
                        "Cannot Find Product", JOptionPane.ERROR_MESSAGE);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        if (trigger == report21PerSheet) {
            ReportRunner rr = new ReportRunner("Reports/21Labels.jrxml");
            rr.printReport();
        } else if (trigger == clearQueue) {
            String zreadMessage = "Are you sure you want to clear the queue? "
                    + "\n This cannot be undone!";
            int decision = JOptionPane.showConfirmDialog(null, zreadMessage,
                    "Z Read", JOptionPane.YES_NO_OPTION);
            // If it is a yes
            if (decision == 0) {
                clearPrintQueue();
            }
        } else if (trigger == refreshBtn) {
            refreshPrintQueue();
        } else if (trigger == closeBtn) {
            shelfEdgeTicketDialog.dispose();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if( e.getKeyCode() == 10 ) {
            // We have scanned it and saved it
            String barcode = barcodeFld.getText();
            // Clear it to stop further triggers
            barcodeFld.setText("");
            if(!barcode.equals("")) {
                findScannedProduct(barcode);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
