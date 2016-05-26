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

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 */
public class StockIn extends Inventory {

    JTextField stockBarcodeFld;
    JButton closeBtn, saveBtn;
    
    String[] cn = {"ID", "Name", "Current", "Reorder"};
    String[][] data = null;
    DefaultTableModel stockInDTM = new DefaultTableModel(data, cn);
    JTable stockInTable = new JTable();
    
    public StockIn() {
        render();
    }
    
    /**
     * Takes the barcode and shows dialog to 
     */
    public boolean loadStockItem(String stockprid) {
        return true;
    }

    public boolean saveItem(String stockprid, int oldCurrentStock, int oldReorderStock) {
        return true;
    }

    public boolean save() {
        return true;
    }
    
    private void render() {
        
    }
    
}
