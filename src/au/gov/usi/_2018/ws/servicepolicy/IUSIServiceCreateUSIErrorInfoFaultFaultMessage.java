
package au.gov.usi._2018.ws.servicepolicy;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.3.5
 * 2020-03-27T13:59:32.180+11:00
 * Generated source version: 3.3.5
 */

@WebFault(name = "ArrayOfErrorInfo", targetNamespace = "http://usi.gov.au/2018/ws")
public class IUSIServiceCreateUSIErrorInfoFaultFaultMessage extends Exception {

    private au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo;

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage() {
        super();
    }

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message) {
        super(message);
    }

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message, java.lang.Throwable cause) {
        super(message, cause);
    }

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo) {
        super(message);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public IUSIServiceCreateUSIErrorInfoFaultFaultMessage(String message, au.gov.usi._2018.ws.ArrayOfErrorInfo arrayOfErrorInfo, java.lang.Throwable cause) {
        super(message, cause);
        this.arrayOfErrorInfo = arrayOfErrorInfo;
    }

    public au.gov.usi._2018.ws.ArrayOfErrorInfo getFaultInfo() {
        return this.arrayOfErrorInfo;
    }
}
