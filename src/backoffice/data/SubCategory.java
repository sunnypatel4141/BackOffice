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
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Sunny Patel
 */
public class SubCategory extends DBConnection {
      
    // using vector here so that we can simply extend this somehow
    public Vector CategoryInfo = new Vector();
    
    /**
     * Gets a list of all the main categories and 
     */
    public SubCategory() {
        loadAllCategories();
    }
    
    /**
     * Loads all the categories currently available in the db
     */
    private void loadAllCategories() {
        try {
            String sql = "select * from subcategory order by name";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Object[] data = {rs.getString("id"), rs.getString("name"), rs.getString("mcid")};
                CategoryInfo.add(data);
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
    }
    
    /**
     * Gets the Category ID for the name provided
     * if duplicate names exist gets the first one in the list
     */
    public int getSubCategoryID(String categoryNameArg) {
        int returnValue = 0;
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String categoryName = CategoryRow[1].toString();
            if(categoryName.equals(categoryNameArg)) {
                String CategoryIDStr = CategoryRow[0].toString();
                returnValue = Integer.parseInt(CategoryIDStr);
            }
        }
        
        return returnValue;
    }
    
    /**
     * Get the CategoryName for the ID Provided
     */
    public String getSubCategoryName(String categoryIDArg) {
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

    public String[] getAllCategories() {
        int CategoryArraySize = CategoryInfo.size() + 1;
        String[] categoryString = new String[CategoryArraySize];
        categoryString[0] = "";
        
        for(int i = 0; i < CategoryInfo.size(); i++) {
            int j = i + 1;
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            categoryString[j] = CategoryRow[1].toString();
        }
        
        return categoryString;
    }
    
    /**
     * get subctegory names in an object array based on the given
     * main category id
     */
    public Object[] getCategoryByMCID(String mcidArg) {
        ArrayList<String> categoryString = new ArrayList<String>();
        for(int i =0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            if (CategoryRow[2].toString().equals(mcidArg)) {
                categoryString.add( CategoryRow[1].toString());
            }
        }
        
        return categoryString.toArray();
    }
    
    public Object[] getCategoryByMCID(int mcidArg) {
        return getCategoryByMCID("" + mcidArg);
    }
    
    /**
     * Get Subcateogry ID in an Object array based on the given 
     * main category id
     */
    public Object[] getCategoryIDByMCID(String mcidArg) {
        ArrayList<String> categoryString = new ArrayList<String>();
        for(int i =0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            if (CategoryRow[2].toString().equals(mcidArg)) {
                categoryString.add( CategoryRow[0].toString());
            }
        }
        
        return categoryString.toArray();
    }
    
    public Object[] getCategoryIDByMCID(int mcidArg) {
        return getCategoryIDByMCID("" + mcidArg);
    }

    /**
     * Selected Categories are taken from a selected main category
     * because the editor will filter sub categories based on the 
     * selected main category 
     */
    public int getSelectedCategory(int scidArg, int mcidArg) {
        
        final String scidStr = "" + scidArg;
        Object[] filteredSubCat = getCategoryIDByMCID(mcidArg);
        for(int i = 0; i < filteredSubCat.length; i++) {
            String categoryID = filteredSubCat[i].toString();
            if(categoryID.equals(scidStr)) {
                return i;
            }
        }
        
        // If something was found it would have returned by now
        return 0;
    }
}
