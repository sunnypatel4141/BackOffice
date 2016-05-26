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

import backoffice.Report.ShelfEdgeTickets;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Sunny Patel
 */
public class Reports extends adminWindow {

    JButton reportLabels, moreReportsBtn;
    
    public JPanel getMainScreenButtons() {
        
        JPanel thisPanel = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        
        ShelfEdgeTickets tickets = new ShelfEdgeTickets();
        tabbedPane.addTab("Print Labels", tickets.getTabs());
        
        OfferLabels offerLabels = new OfferLabels();
        tabbedPane.addTab("Offer Labels", offerLabels.getTabs());
        
        MoreReports moreReports = new MoreReports();
        tabbedPane.addTab("More Reports", moreReports.getTabs());
        
        thisPanel.add(tabbedPane);
        
        return thisPanel;
    }
}
