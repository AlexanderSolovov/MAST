package com.rmsi.mast.viewer.report;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * Interface for reports functions
 */
public interface ReportsSerivce {
    JasperPrint getDenialLetter(long usin);
    
    JasperPrint getAdjudicationForms(String projectName, long startUsin, long endUsin, String appUrl);
}
