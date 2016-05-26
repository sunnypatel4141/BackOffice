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
package backoffice.data;

import backoffice.base.DBConnection;
import static backoffice.base.DBConnection.rs;
import static backoffice.base.DBConnection.stmt;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunny Patel
 */
public class Tax extends DBConnection {    
         
    // using vector here so that we can simply extend this somehow
    Vector CategoryInfo = new Vector();
    
    /**
     * Gets a list of all the main categories and 
     */
    public Tax() {
        loadAllCategories();
    }
    
    /**
     * Loads all the categories currently available in the db
     */
    private void loadAllCategories() {
        try {
            String sql = "select * from taxcode";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Object[] data = {rs.getString("id"), rs.getString("taxcodename"), 
                    rs.getString("taxpercentage")};
                CategoryInfo.add(data);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }
    
    /**
     * Gets the Tax ID for the ID provided
     * if duplicate names exist gets the first one in the list
     */
    public int getTaxID(Integer categoryIndexArg) {
        int returnValue = 0;
        try {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(categoryIndexArg);
            String CategoryIDStr = CategoryRow[0].toString();
                    returnValue = Integer.parseInt(CategoryIDStr);
        } catch(Exception a) {
            a.printStackTrace();
        }
        
        return returnValue;
    }
    
    public String getTaxRate(int taxID) {
        String taxRate = "0.0";
        
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String categoryID = CategoryRow[0].toString();
            if(categoryID.equals("" + taxID)) {
                String CategoryIDStr = CategoryRow[2].toString();
                taxRate = CategoryIDStr;
            }
        }
        
        return taxRate;
    }
    
    /**
     * Get the Tax Name for the ID Provided
     */
    public String getTaxName(String categoryIDArg) {
        String returnVal = "";
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String categoryID = CategoryRow[0].toString();
            if(categoryID.equals(categoryIDArg)) {
                String CategoryIDStr = CategoryRow[1].toString();
                returnVal = CategoryIDStr;
            }
        }
        
        return returnVal;
    }

    public String[] getCategories() {
        String[] categoryString = new String[CategoryInfo.size()];
        
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String fullCodeName = "" + CategoryRow[1] + " (" + 
                    CategoryRow[2] + "%)";
            categoryString[i] = fullCodeName;
        }
        
        return categoryString;
    }
    
    public int getSelectedCategory(int taxidArg) {
        
        final String taxidStr = "" + taxidArg;
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String categoryID = CategoryRow[0].toString();
            if(categoryID.equals(taxidStr)) {
                return i;
            }
        }
        
        return 0;
    }
    
    public int getSelectedCategoryByRate(String taxRateArg) {
        
        final String taxRateStr = "" + taxRateArg;
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String categoryID = CategoryRow[2].toString();
            if(categoryID.equals(taxRateStr)) {
                return i;
            }
        }
        
        return 0;
    }
    
}
