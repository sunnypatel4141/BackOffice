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
import javax.swing.JDialog;
import backoffice.Report.DaySummary;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Sunny Patel
 */
public class MoreReports extends Reports  implements ActionListener {
    
    JButton daySummary, largeLabelsBtn, productSummary, categoryPercent;
    JPanel controlBtnPnl = new JPanel();
    
    /**
     * get the reports buttons required for the tab
     * @return panel
     */
    public Component getTabs() {
    
        JPanel mainPanel = new JPanel();
        daySummary = new JButton("Sales Summary");
        daySummary.addActionListener(this);
        
        
        productSummary = new JButton("Products Summary");
        productSummary.addActionListener(this);
        
        categoryPercent = new JButton("Category Summary");
        categoryPercent.addActionListener(this);
        
        mainPanel.add(daySummary);
        mainPanel.add(productSummary);
        mainPanel.add(categoryPercent);
        
        return mainPanel;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        
        if (trigger == daySummary) {
            backoffice.Report.DaySummary ds = new backoffice.Report.DaySummary("DaySummary");
        } else if (trigger == largeLabelsBtn) {
            // Large buttons
            backoffice.Report.largeLabels main = new backoffice.Report.largeLabels();
        } else if (trigger == productSummary) {
            backoffice.Report.DaySummary ds = new backoffice.Report.DaySummary("ProductSummary");
        } else if (trigger == categoryPercent) {
            backoffice.Report.DaySummary ds = new backoffice.Report.DaySummary("CategoryPercentBreakdown");
        }
    }

}
