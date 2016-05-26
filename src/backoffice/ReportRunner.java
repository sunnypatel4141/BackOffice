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

import java.awt.Frame;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Sunny Patel
 * 
 * This class is used to give a specific height to the jasper report
 * It can be called in the normal way
 * 
 * ReportRunner rr = new ReportRunner("myreport.jrxml", new HashMap());
 * rr.viewReprot();
 */
public class ReportRunner extends backoffice.base.DBConnection {
    
    String fileName = "";
    JasperDesign jd;
    HashMap reportdata = new HashMap();
    JFrame report = new JFrame("Report");
    
    public ReportRunner(String fileNameArgument, HashMap dataArg) {
        // Get the jasperReportsContext
        fileName = fileNameArgument;
        reportdata = dataArg;
    }
    
    public ReportRunner(String fileNameArgument) {
        // Get the jasperReportsContext
        fileName = fileNameArgument;
    }
    
    public void printReport() {
        try {
            conn = getConnection();
            jd = JRXmlLoader.load(fileName);
            JasperReport jReport = JasperCompileManager.compileReport(jd);
            JasperPrint jPrint = JasperFillManager.fillReport(jReport, reportdata, conn);
            
            JRViewer viewer = new JRViewer(jPrint);
            viewer.setOpaque(true);
            report.add(viewer);
            report.setLocation(0, 0);
            report.setSize(1001, 701);
            report.setVisible(true);
            
        } catch (JRException ex) {
            Logger.getLogger(ReportRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
