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

import backoffice.base.DBConnection;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Sunny Patel
 */
public class login extends DBConnection implements ActionListener, FocusListener {
    
    JFrame frame = new JFrame("System Login");
    JPanel inputField, numPadPnl, detailPnl, optsPnl;
    String[] numberBtns = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "CLEAR", "OK"};
    JButton[] numbers = new JButton[numberBtns.length];
    JTextField unFld;
    JPasswordField pwFld;
    Integer focusField = 0;
    JButton exitBtn, recovery;
    public String un, pw;
    
    
    private void render() {
        inputField = new JPanel();
        numPadPnl = new JPanel();
        detailPnl = new JPanel();
        optsPnl = new JPanel();
        
        // Exit button
        exitBtn = new JButton(new ImageIcon("Icons/system-shutdown.png"));
        exitBtn.addActionListener(this);
        
        // Recovery button
        recovery = new JButton("Recovery", new ImageIcon("Icons/object-rotate-left.png"));
        recovery.addActionListener(this);
        
        // Add to panel
        optsPnl.add(exitBtn);
        //optsPnl.add(recovery);
        
        JLabel unFldLbl = new JLabel("User Name");
        unFldLbl.setFont(h1);
        unFld = new JTextField(14);
        unFld.setFont(h1);
        unFld.addFocusListener(this);
        
        // Add to panel
        inputField.add(unFldLbl);
        inputField.add(unFld);
        
        JLabel pwFldLbl = new JLabel("Password");
        pwFldLbl.setFont(h1);
        pwFld = new JPasswordField(14);
        pwFld.setFont(h1);
        pwFld.addFocusListener(this);
        
        // Add to panel
        inputField.add(pwFldLbl);
        inputField.add(pwFld);
        
        for(int i = 0; i < numberBtns.length; i++) {
            // Render the buttons and on
            // click call the right methods
            final String numberString = numberBtns[i];
            numbers[i] = new JButton(numberBtns[i]);
            numbers[i].setFont(h1);
            numbers[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateField(numberString);
                }
            });
            numPadPnl.add(numbers[i]);
        }
        
        numPadPnl.setLayout(new GridLayout(4, 3, 2, 2));
        numPadPnl.setPreferredSize(new Dimension(250, 250));
        inputField.setLayout(new GridLayout(2, 2, 2, 2));
        numPadPnl.setPreferredSize(new Dimension(100, 250));
        optsPnl.setLayout(new GridLayout(2, 1));
        
        JPanel framePnl = new JPanel();
        framePnl.add(inputField);
        framePnl.add(numPadPnl);
        framePnl.add(optsPnl);
        
        framePnl.setLayout(new BoxLayout(framePnl, BoxLayout.PAGE_AXIS));
        
        frame.add(framePnl);
        frame.setLayout(new FlowLayout());
        frame.setSize(1024, 786);
        frame.setVisible(true);
        
    }
    
    public static void main(String[] args) {
        login l = new login();
        l.render();
    }
    
    /**
     * update the correct field with the right number
     * Note: This only updates the correct fields
     */
    private void updateField(String buttonStringArg) {
        if ( buttonStringArg.equals("OK")) {
            un = unFld.getText();
            pw = pwFld.getText();
            boolean allow = authenticateAndLogin();
            login(Settings.get("username").toString());
            return;
        } else if(buttonStringArg.equals("CLEAR")) {
            if (focusField == 0) {
                unFld.setText("");
            } else if (focusField == 1) {
                pwFld.setText("");
            }
            return;
        }
        if(focusField == 0) {
            String currentField = unFld.getText();
            String updatedField = new StringBuffer().append(currentField).append(buttonStringArg).toString();
            unFld.setText(updatedField);
        } else if (focusField == 1) {
            String currentField = pwFld.getText();
            String updatedField = new StringBuffer().append(currentField).append(buttonStringArg).toString();
            pwFld.setText(updatedField);
        }
    }
    
    /**
     * Login on successful Authentication
     * TODO: Fix this method's logic
     */
    public boolean authenticateAndLogin() {
        boolean authorised = false;
        
        if ( un.equals("") || pw.equals("")) {
            JOptionPane.showMessageDialog(frame, "Must enter login info", "LOGIN ERROR", JOptionPane.ERROR_MESSAGE);
            // Clearly You have not entered all the information
            return false;
        }
        
        try {
            String sql = "select * from users where loginID='" + un +
                    "' and loginpass='" + pw  +"' and canlogin='1';";
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Settings.put("username", rs.getString("firstname"));
                Settings.put("userid", rs.getString("id"));
                authorised = true;
            }
            rs.close();
            if (authorised) {
                // This is a view
                sql = "select r, w, c, d from applicationright where userid='" + 
                        Settings.get("userid").toString() + "' and apid = " +
                        "(select id from application where code = 'adminwindow')";
                rs = stmt.executeQuery(sql);
                while ( rs.next() ) {
                    Settings.put("adminwindow", rs.getString("r"));
                }
                rs.close();
                if ( Settings.containsKey("adminwindow")) {
                    authorised = false;
                    JOptionPane.showMessageDialog(frame, "Access Denied", "Access Denied", JOptionPane.ERROR_MESSAGE);
                } else {
                    return true;
                }
            }
        } catch(Exception a) {
            return false;
        }
        
        // If we got this far there is something wrong;
        return authorised;
    }
    
    /**
     * Login to the system now
     */
    private void login(String loginID) {
        // Allow the login
        adminWindow aw = new adminWindow();
        aw.render();
        clearAndClose();
    }
    
    /**
     * Close the window
     */
    private void clearAndClose() {
        Component comps[] = frame.getComponents();
        for(int i = 0; i < comps.length; i++) {
            frame.remove(comps[i]);
        }
        frame.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // actionPerformed
        Object trigger = e.getSource();
        if (trigger == recovery) {
            //
        } else if ( trigger == exitBtn ) {
            // Exit Button
            System.exit(0);
        }
        for(int i = 0; i < numbers.length; i++) {
            if (trigger == numbers[i]) {
                updateField(numberBtns[i]);
                break;
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        // focusGained
        if (e.getSource() == unFld) {
            // The user name Field
            focusField = 0;
        } else if (e.getSource() == pwFld) {
            focusField = 1;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        // focusLost
    }
}
