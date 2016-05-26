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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Sunny Patel
 */
public class adminWindow extends DBConnection {

    public JFrame frame = new JFrame("Administrator Console");
    private JButton close = new JButton("Close", new ImageIcon("Icons/system-shutdown.png"));
    JPanel modulesCallPnl = new JPanel();
    JPanel modulesDispPnl = new JPanel();
    CardLayout cardLayout = new CardLayout();
    String moduleNames[] = {
        "Store Settings",
        "Products",
        "People",
        "Inventory",
        "Reports"
    };
    JPanel storeInfoPnl = new JPanel();
    JPanel productsPnl = new JPanel();
    JPanel peoplePnl = new JPanel();
    JPanel inventoryPnl = new JPanel();
    JPanel reportsPnl = new JPanel();

    int buttonCount = moduleNames.length + 1;
    JButton[] moduleBtn = new JButton[buttonCount];

    /**
     * Render the adminWindow
     */
    public void render() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel framePnl = new JPanel();
        modulesDispPnl.setLayout(cardLayout);
        loadModulesToCallPanel();
        loadModulesToDispPanel();
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAndClose();
            }
        });

        framePnl.add(modulesCallPnl);
        framePnl.add(modulesDispPnl);
        framePnl.add(close);
        framePnl.setLayout(new BoxLayout(framePnl, BoxLayout.PAGE_AXIS));
        frame.add(framePnl);
        frame.setSize(1024, 700);
        frame.setVisible(true);
    }

    /**
     * We want to load modules to the call panel
     */
    private void loadModulesToCallPanel() {
        int modulesCount = moduleNames.length;
        for(int i = 0; i < modulesCount ; i++) {
            // for refering to in the actionListener
            final int count = i + 1;
            // Module names
            moduleBtn[i] = new JButton(moduleNames[i]);
            moduleBtn[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(modulesDispPnl, "" + count);
                }
            });
            modulesCallPnl.add(moduleBtn[i]);
        }
        
        modulesCallPnl.setPreferredSize(new Dimension(500, 70));
        modulesCallPnl.setLayout(new GridLayout(1, 7));
    }

    /**
     * Load Modules to Display Panel when a
     * new one is added it needs to be added here
     */
    private void loadModulesToDispPanel() {
        JPanel storeInfoPnl = new JPanel();
        JPanel productsPnl = new JPanel();
        JPanel peoplePnl = new JPanel();
        JPanel inventoryPnl = new JPanel();
        JPanel reportsPnl = new JPanel();

        // Load Store Info module
        StoreInfo si = new StoreInfo();
        storeInfoPnl.add(si.getMainScreenButtons());
        modulesDispPnl.add(storeInfoPnl, "1");

        // Load the products module
        Products pr = new Products();
        productsPnl.add(pr.getMainScreenButtons());
        modulesDispPnl.add(productsPnl, "2");

        // Load the People module
        People pe = new People();
        peoplePnl.add(pe.getMainScreenButtons());
        modulesDispPnl.add(peoplePnl, "3");

        // Load the Inventory module
        Inventory iv = new Inventory();
        inventoryPnl.add(iv.getMainScreenButtons());
        modulesDispPnl.add(inventoryPnl, "4");

        // Load the Reports module
        Reports rp = new Reports();
        reportsPnl.add(rp.getMainScreenButtons());
        modulesDispPnl.add(reportsPnl, "5");
    }
    
    /**
     * Clear and close this window
     */
    private void clearAndClose() {
        Component comps[] = frame.getComponents();
        for(int i = 0; i < comps.length; i++) {
            frame.remove(comps[i]);
        }
        frame.dispose();
        System.exit(0);
    }

}
