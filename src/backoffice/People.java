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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Sunny Patel
 */
class People extends Products implements ActionListener {
    
    public JPanel getMainScreenButtons() {
        
        JPanel returnPnl = new JPanel();
        
        JButton employees = new JButton("Employees");
        employees.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Employee em = new Employee();
            }
        });
        
        // FIXME:Only for admin for now
        if (Settings.get("userid").toString().equals("1")) {
            returnPnl.add(employees);
        }
        
        return returnPnl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
    
}
