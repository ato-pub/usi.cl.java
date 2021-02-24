
package au.gov.usi._2020.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.3.6
 * 2021-02-24T17:30:47.826+11:00
 * Generated source version: 3.3.6
 */

@WebFault(name = "ErrorInfo", targetNamespace = "http://usi.gov.au/2020/ws")
public class IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle extends Exception {

    private au.gov.usi._2020.ws.ErrorInfo errorInfo;

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle() {
        super();
    }

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message) {
        super(message);
    }

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2020.ws.ErrorInfo errorInfo) {
        super(message);
        this.errorInfo = errorInfo;
    }

    public IUSIServiceBulkUploadErrorInfoFaultFaultMessageSingle(String message, au.gov.usi._2020.ws.ErrorInfo errorInfo, java.lang.Throwable cause) {
        super(message, cause);
        this.errorInfo = errorInfo;
    }

    public au.gov.usi._2020.ws.ErrorInfo getFaultInfo() {
        return this.errorInfo;
    }
}
