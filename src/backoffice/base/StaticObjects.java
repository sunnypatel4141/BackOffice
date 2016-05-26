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

/**
 *
 * @author Sunny Patel
 */
package backoffice.base;

import java.awt.Font;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author Sunny Patel
 */
class StaticObjects {
        //
    // here static objets are declard to be used by the entire system
    public Font h1 = new Font("Verdana", Font.BOLD, 18);
    public Font h2 = new Font("Verdana", Font.PLAIN, 12);
    public Font h3 = new Font("Verdana", Font.PLAIN, 16);
    public Font large = new Font("Verdana", Font.BOLD, 16);
    
    public String getCurrency(String amountArg) {
        String returnval = "";
        try {
            Formatter fb = new Formatter(Locale.UK);
            Float floatVal = Float.parseFloat(amountArg);
            returnval = fb.format("%,.2f", floatVal).toString();
        } catch(Exception a) {
            a.printStackTrace();
        }
        
        return returnval;
    }
    
    public float getCurrencyInFloat(String amountArg) {
        return Float.parseFloat(getCurrency(amountArg));
    }
    
    public float getCurrencyInFloat(float amountArg) {
        String amountArgStr = "" + amountArg;
        return Float.parseFloat(getCurrency(amountArgStr));
    }
    
    HashMap apprights = new HashMap();
    
    public boolean readRight(String app) {
        boolean returnVal = false;
        Object ar = apprights.get("" + app);
        if ( ar == null ) {
            returnVal = false;
        } else {
            String right = ar.toString();
            if ( right.equals("1")) {
                returnVal = true;
            } else {
                returnVal = false;
            }
        }
        
        //FIXME: need to revert this to true
        returnVal = true;
        return returnVal;
    }
    
    public void loadRights(String userid) {
        if( userid.equals("")) {
            // Ok lets not do anything and exit
        } else {
            try {
                String sql = "select * from rights where user='" + userid +"'";
                DBConnection dbc = new DBConnection();
                dbc.conn = dbc.getConnection();
                dbc.stmt = dbc.conn.createStatement();
                dbc.rs = dbc.stmtext.executeQuery(sql);
                while(dbc.rs.next()) {
                    String key = dbc.rs.getString("apcode");
                    String val = dbc.rs.getString("r");
                    apprights.put(key, val);
                }
                // Loop and set the rights
            } catch (Exception a) {
                a.printStackTrace();
            }
        }
    }
}
