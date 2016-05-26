/*
 * Copyright (C) 2015 User
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
package backoffice;

import static backoffice.base.DBConnection.conn;
import static backoffice.base.DBConnection.rs;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
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
 * @author User
 */
class OfferLabels extends Reports implements ActionListener {
    
    JDialog shelfEdgeTicketDialog = new JDialog(frame, "Offer Labels", true);
    JButton showLabels, clearQueue, refreshBtn, editBtn, addBtn;
    Object[][] data = null;
    String[] cn = {"Offer", "Name"};
    DefaultTableModel labelDtm = new DefaultTableModel(data, cn);
    JTable labelTable = new JTable(labelDtm);
    
    public Component getTabs() {
    
        JPanel reportPanel = new JPanel();
        showLabels = new JButton("Show Labels");
        showLabels.addActionListener(this);
        
        clearQueue = new JButton("Clear Queue");
        clearQueue.addActionListener(this);
        
        editBtn = new JButton("Edit");
        editBtn.addActionListener(this);
        
        addBtn = new JButton("Add Label");
        addBtn.addActionListener(this);
        
        refreshBtn = new JButton("<html>Refresh<br> Queue</html>");
        refreshBtn.addActionListener(this);
        
        labelTable.setFont(h2);
        
        loadPrintQueueToTable();
        JScrollPane labelSP = new JScrollPane(labelTable);
        
        reportPanel.add(labelSP);
        
        JPanel btnsPnl = new JPanel();
        btnsPnl.add(showLabels);
        btnsPnl.add(clearQueue);
        btnsPnl.add(editBtn);
        btnsPnl.add(addBtn);
        btnsPnl.add(refreshBtn);
        
        JPanel barcodePnl = new JPanel();
        
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
            String sql = "select * from offerlabels";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                Object[] row = {rs.getString("mainlabel"), 
                    rs.getString("secondlabel")};
                labelDtm.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void clearPrintQueue() {
        try {
            String sql = "delete from offerlabels";
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Print Queue Cleared");
        } catch (SQLException ex) {
            Logger.getLogger(Reports.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void editItem() {
        int selectedRow = labelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Must select Row to Edit", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String mainLabelStr = labelDtm.getValueAt(selectedRow, 0).toString();
            String nameLabelStr = labelDtm.getValueAt(selectedRow, 1).toString();
            showAddEditDialog(false, mainLabelStr, nameLabelStr);
        }
    }
    
    private void showAddEditDialog(final Boolean toAdd, String mainLblStr, String secondStr) {
        final JDialog addEditDlag = new JDialog(frame, "Add Edit Offer Label");
        
        final JTextField mainLblFld = new JTextField(7);
        final JTextField nameLblFld = new JTextField(7);
        JButton saveBtn = new JButton("Save");
        JButton closeDlagBtn = new JButton("Close");
        
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (toAdd) {
                    addToDB(mainLblFld.getText(), nameLblFld.getText());
                } else {
                    updateToDB(mainLblFld.getText(), nameLblFld.getText());
                }
                refreshPrintQueue();
                addEditDlag.dispose();
            }
        });
        
        closeDlagBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addEditDlag.dispose();
            }
            
        });
        
        mainLblFld.setText(mainLblStr);
        nameLblFld.setText(secondStr);
        
        addEditDlag.add(new JLabel("Main Label"));
        addEditDlag.add(mainLblFld);
        addEditDlag.add(new JLabel("Name Label"));
        addEditDlag.add(nameLblFld);
        addEditDlag.add(saveBtn);
        addEditDlag.add(closeDlagBtn);
        
        addEditDlag.setSize(300, 200);
        addEditDlag.setLayout(new GridLayout(3, 2));
        addEditDlag.setVisible(true);        
    }
    
    private void addToDB(String mainLbl, String subLbl) {
        try {
            String sql = "insert into offerlabels (mainlabel, secondlabel) values(?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mainLbl);
            pstmt.setString(2, subLbl);
            pstmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(OfferLabels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateToDB(String mainLbl, String subLbl) {
        try {
            int selectedRow = labelTable.getSelectedRow();
            String mainLabelStr = labelDtm.getValueAt(selectedRow, 0).toString();
            String nameLabelStr = labelDtm.getValueAt(selectedRow, 1).toString();
            String sql = "update offerlabels set mainlabel = ?, secondlabel=? "
                    + "where mainlabel = ? and secondlabel = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mainLbl);
            pstmt.setString(2, subLbl);
            pstmt.setString(3, mainLabelStr);
            pstmt.setString(4, nameLabelStr);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OfferLabels.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        if (trigger == showLabels) {
            ReportRunner rr = new ReportRunner("Reports/OfferLabels.jrxml");
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
        } else if (trigger == addBtn) {
            showAddEditDialog(true, "", "");
        } else if (trigger == editBtn) {
            editItem();
        }
    }
}
