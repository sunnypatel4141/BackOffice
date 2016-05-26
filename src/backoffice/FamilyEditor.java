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
package backoffice;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 */

// HERE WE WANT TO BE ABLE TO MANAGE THE GROUP THAT WE ALL BELONG TO
class FamilyEditor extends Products implements ActionListener {
    JDialog familyEditorDialog = new JDialog(frame, "Family Editor", true);
    JButton editBtn, addBtn, deleteBtn, closeBtn;
    
    String[] cnfg = {"Family Group ID", "Name", "Created" };
    Object[][] datafg = null;
    DefaultTableModel dtmfg = new DefaultTableModel(datafg, cnfg);
    JTable tablefg = new JTable(dtmfg);
    
    /**
     * Family Editor is here
     */
    public FamilyEditor() {
        renderWindow();
    }
    
    /**
     * Render the GUI
     */
    private void renderWindow() {
        JPanel controlPnl = new JPanel();
        JPanel tablePnl = new JPanel();
        JPanel mainPnl = new JPanel();
        
        JScrollPane jspfg = new JScrollPane(tablefg);
        loadGroups();
        editBtn = new JButton("Edit");
        editBtn.addActionListener(this);
        addBtn = new JButton("Add");
        addBtn.addActionListener(this);
        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        
        controlPnl.add(editBtn);
        controlPnl.add(deleteBtn);
        controlPnl.add(addBtn);
        controlPnl.add(closeBtn);
        controlPnl.setLayout(new GridLayout(1, 4));
        controlPnl.setPreferredSize(new Dimension(200, 50));
        
        tablePnl.add(jspfg);
        
        mainPnl.add(tablePnl);
        mainPnl.add(controlPnl);
        mainPnl.setLayout(new BoxLayout(mainPnl, BoxLayout.PAGE_AXIS));
        
        familyEditorDialog.add(mainPnl);
        
        familyEditorDialog.setLocation(0, 100);
        familyEditorDialog.setSize(700, 500);
        familyEditorDialog.setVisible(true);
    }
    
    /**
     * Loads the family group table
     */
    private void loadGroups() {
        try {
            while(dtmfg.getRowCount() > 0) {
                dtmfg.removeRow(0);
            }
            String sql = "select * from familygroup";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while(rs.next()) {
                //"Family Group ID", "Name", "Created" }
                Object[] row = {rs.getString("id"), rs.getString("name"), rs.getString("created")};
                dtmfg.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FamilyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        //editBtn, createBtn, deleteBtn, closeBtn;
        if(trigger == closeBtn) {
            familyEditorDialog.dispose();
        } else if (trigger == editBtn) {
            editItem();
        } else if (trigger == deleteBtn) {
            deleteItem();
        } else if (trigger == addBtn) {
            String groupNameStr = JOptionPane.showInputDialog("What would you like to call this group");
            if (groupNameStr!= null) {
                FamilyGroupItems fgi = new FamilyGroupItems(groupNameStr);
            }
        }
        loadGroups();
    }
    
    private void deleteItem() {
        int selectedRow = tablefg.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(familyEditorDialog, "Must select group to delete",
                    "Delete Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String familySelectedRow = dtmfg.getValueAt(selectedRow, 0).toString();
        FamilyGroupItems fgi = new FamilyGroupItems(familySelectedRow, false);
    }
    
    private void editItem() {
        int selectedRow = tablefg.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(familyEditorDialog, "Must select group to edit",
                    "Edit Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String familySelectedRow = dtmfg.getValueAt(selectedRow, 0).toString();
        FamilyGroupItems fgi = new FamilyGroupItems(familySelectedRow, true);
    }
    
}
