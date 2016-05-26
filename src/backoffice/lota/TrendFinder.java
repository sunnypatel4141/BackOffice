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
package backoffice.lota;

import backoffice.base.DBConnection;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sunny Patel
 * The idea of this class is to find sold product combinations and their 
 * sold count with the given prid within the supplied dates
 */
public class TrendFinder extends DBConnection {
    
    String sql = "select distinct prid, name, count(name) as totalcount "
            + "from saleitem where saleid in (select s.saleid from saleitem s "
            + "where s.prid = ? ) and ? group by name order by totalcount asc";
    
    String[] tdcn = {"Product ID", "Product Name", "Count"};
    Object[][] tddata = null;
    
    public TrendFinder() {
        DefaultTableModel tddtm = new DefaultTableModel(tddata, tdcn);
        JTable tdtable = new JTable(tddtm);
        
    }
}
