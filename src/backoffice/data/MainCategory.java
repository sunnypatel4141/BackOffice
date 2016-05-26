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
import java.util.Vector;

/**
 *
 * @author Sunny Patel
 */
public class MainCategory extends DBConnection {
    
    // using vector here so that we can simply extend this somehow
    public Vector CategoryInfo = new Vector();
    
    /**
     * Gets a list of all the main categories and 
     */
    public MainCategory() {
        loadAllCategories();
    }
    
    /**
     * Loads all the categories currently available in the db
     */
    private void loadAllCategories() {
        try {
            String sql = "select * from maincategory order by name";
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                Object[] data = {rs.getString("id"), rs.getString("name"), rs.getString("agerestrict")};
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
    public int getMainCategoryID(String categoryNameArg) {
        int returnValue = 0;
        try {
            for(int i = 0; i < CategoryInfo.size(); i++) {
                Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
                String categoryName = CategoryRow[1].toString();
                if(categoryName.equals(categoryNameArg)) {
                    String CategoryIDStr = CategoryRow[0].toString();
                    returnValue = Integer.parseInt(CategoryIDStr);
                }
            }
        } catch(Exception a) {
            a.printStackTrace();
        }
        
        return returnValue;
    }
    
    /**
     * Get the CategoryName for the ID Provided
     */
    public String getMainCategoryName(String categoryIDArg) {
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

    public int getSelectedCategory(int mcid) {
        
        final String mcidStr = "" + mcid;
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String categoryID = CategoryRow[0].toString();
            if(categoryID.equals(mcidStr)) {
                return (i + 1);
            }
        }
        
        return 0;
    }
    
    public boolean isCategoryAgeRestricted(int mcid) {
        
        final String mcidStr = "" + mcid;
        for(int i = 0; i < CategoryInfo.size(); i++) {
            Object[] CategoryRow = (Object[]) CategoryInfo.get(i);
            String categoryID = CategoryRow[0].toString();
            
            Object ageRestrictStr = CategoryRow[2];
            
            if(ageRestrictStr != null && categoryID.equals(mcidStr) && ageRestrictStr.equals("1")) {
                return true;
            }
        }
        
        return false;
    }
}
