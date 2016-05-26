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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

/**
 *
 * @author Sunny Patel
 */
public class DaySummary extends backoffice.MoreReports implements ActionListener {

    JDialog daySummaryDialog = new JDialog(frame, "More Reports", true);
    SpinnerDateModel sdmHistoryStart = new SpinnerDateModel();
    JSpinner jsnHistoryStart = new JSpinner(sdmHistoryStart);
    SpinnerDateModel sdmHistoryEnd = new SpinnerDateModel();
    JSpinner jsnHistoryEnd = new JSpinner(sdmHistoryEnd);
    
    String fullReportPath = "";

    // The buttons
    JButton closeBtn, showBtn;

    /**
     * Renders the particulars of the Day Summary
     */
    public DaySummary(String reportName) {
        fullReportPath = "Reports/" + reportName + ".jrxml";
        renderWindow();
    }

    private void renderWindow() {
        JPanel dateChoosePnl = new JPanel();
        JPanel btnPnl = new JPanel();

        showBtn = new JButton("Show");
        showBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);

        btnPnl.add(showBtn);
        btnPnl.add(closeBtn);
        
        jsnHistoryStart.setFont(new Font("Verdana", Font.BOLD, 18));
        jsnHistoryEnd.setFont(new Font("Verdana", Font.BOLD, 18));

        dateChoosePnl.add(new JLabel("Start Date"));
        dateChoosePnl.add(jsnHistoryStart);
        dateChoosePnl.add(new JLabel("End Date"));
        dateChoosePnl.add(jsnHistoryEnd);
        dateChoosePnl.setLayout(new GridLayout(2, 2));

        daySummaryDialog.add(dateChoosePnl);
        daySummaryDialog.add(btnPnl);
        daySummaryDialog.setLayout(new FlowLayout());
        daySummaryDialog.setLocation(250, 250);
        daySummaryDialog.setSize(500, 200);
        daySummaryDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Object trigger = ae.getSource();
        if(trigger == showBtn) {
            // Show Button
            java.sql.Date startSqlDate = new java.sql.Date(sdmHistoryStart.getDate().getTime());
            java.sql.Date endSqlDate = new java.sql.Date(sdmHistoryEnd.getDate().getTime());
            
            String startDate = startSqlDate.toString();
            String endDate = endSqlDate.toString();            
            
            HashMap reportData = new HashMap();
            reportData.put("startdate", startSqlDate.toString());
            reportData.put("enddate", endSqlDate.toString());
            daySummaryDialog.dispose();
            ReportRunner rr = new ReportRunner(fullReportPath, reportData);
            rr.printReport();
        } else if (trigger == closeBtn) {
            // Close Button
            daySummaryDialog.dispose();
        }
    }
}
