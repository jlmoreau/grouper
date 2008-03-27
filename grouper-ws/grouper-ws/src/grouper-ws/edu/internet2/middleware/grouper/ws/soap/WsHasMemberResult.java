/**
 * 
 */
package edu.internet2.middleware.grouper.ws.soap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.internet2.middleware.grouper.ws.soap.WsHasMemberLiteResult.WsHasMemberLiteResultCode;
import edu.internet2.middleware.grouper.ws.soap.WsSubjectLookup.SubjectFindResult;
import edu.internet2.middleware.subject.Subject;

/**
 * Result of seeing if one subject is a member of a group.  The number of
 * results will equal the number of subjects sent in to the method
 * 
 * @author mchyzer
 */
public class WsHasMemberResult {

  /** logger */
  private static final Log LOG = LogFactory.getLog(WsHasMemberResult.class);

  /** sujbect info for hasMember */
  private WsSubject wsSubject;

  /**
   * metadata about the result
   */
  private WsResultMeta resultMetadata = new WsResultMeta();

  /**
   * @return the wsSubject
   */
  public WsSubject getWsSubject() {
    return this.wsSubject;
  }

  /**
   * @param wsSubjectResult1 the wsSubject to set
   */
  public void setWsSubject(WsSubject wsSubjectResult1) {
    this.wsSubject = wsSubjectResult1;
  }

  /** empty constructor */
  public WsHasMemberResult() {
    //nothing
  }

  /**
   * result based on a subject lookup.  Might set stat codes meaning abort mission
   * @param wsSubjectLookup
   * @param subjectAttributeNamesToRetrieve
   */
  public WsHasMemberResult(WsSubjectLookup wsSubjectLookup,
      String[] subjectAttributeNamesToRetrieve) {

    this.wsSubject = new WsSubject(wsSubjectLookup);

    Subject subject = wsSubjectLookup.retrieveSubject();

    // make sure the subject is there
    if (subject == null) {
      // see why not
      SubjectFindResult subjectFindResult = wsSubjectLookup.retrieveSubjectFindResult();
      String error = "Subject: " + wsSubjectLookup + " had problems: "
          + subjectFindResult;
      this.getResultMetadata().setResultMessage(error);
      if (subjectFindResult != null) {
        this.assignResultCode(subjectFindResult.convertToHasMemberResultCode());
      }
    } else {
      this.wsSubject = new WsSubject(subject, subjectAttributeNamesToRetrieve);
    }
  }

  /**
   * result code of a request
   */
  public enum WsHasMemberResultCode {

    /** found multiple results */
    SUBJECT_DUPLICATE {
      
      /** 
       * if there is one result, convert to the results code
       * @return WsHasMemberResultLiteCode
       */
      @Override
      public WsHasMemberLiteResultCode convertToLiteCode() {
        return WsHasMemberLiteResultCode.SUBJECT_DUPLICATE;
      }

    },

    /** cant find the subject */
    SUBJECT_NOT_FOUND {
      
      /** 
       * if there is one result, convert to the results code
       * @return WsHasMemberResultLiteCode
       */
      @Override
      public WsHasMemberLiteResultCode convertToLiteCode() {
        return WsHasMemberLiteResultCode.SUBJECT_NOT_FOUND;
      }

    },

    /** the subject is a member  (success = T) */
    IS_MEMBER {
      
      /** 
       * if there is one result, convert to the results code
       * @return WsHasMemberResultLiteCode
       */
      @Override
      public WsHasMemberLiteResultCode convertToLiteCode() {
        return WsHasMemberLiteResultCode.IS_MEMBER;
      }

    },

    /** the subject was found and is not a member (success = T) */
    IS_NOT_MEMBER {
      
      /** 
       * if there is one result, convert to the results code
       * @return WsHasMemberResultLiteCode
       */
      @Override
      public WsHasMemberLiteResultCode convertToLiteCode() {
        return WsHasMemberLiteResultCode.IS_NOT_MEMBER;
      }

    },

    /** problem with query */
    EXCEPTION {
      
      /** 
       * if there is one result, convert to the results code
       * @return WsHasMemberResultLiteCode
       */
      @Override
      public WsHasMemberLiteResultCode convertToLiteCode() {
        return WsHasMemberLiteResultCode.EXCEPTION;
      }

    },

    /** invalid query (e.g. if everything blank) */
    INVALID_QUERY {
      
      /** 
       * if there is one result, convert to the results code
       * @return WsHasMemberResultLiteCode
       */
      @Override
      public WsHasMemberLiteResultCode convertToLiteCode() {
        return WsHasMemberLiteResultCode.INVALID_QUERY;
      }

    };

    /**
     * if this is a successful result
     * @return true if success
     */
    public boolean isSuccess() {
      return this.equals(IS_MEMBER) || this.equals(IS_NOT_MEMBER);
    }
    
    /** 
     * if there is one result, convert to the results code
     * @return result code
     */
    public abstract WsHasMemberLiteResultCode convertToLiteCode();

  }

  /**
   * assign the code from the enum
   * @param hasMemberResultCode
   */
  public void assignResultCode(WsHasMemberResultCode hasMemberResultCode) {
    this.getResultMetadata().assignResultCode(
        hasMemberResultCode == null ? null : hasMemberResultCode.name());
    this.getResultMetadata().assignSuccess(hasMemberResultCode.isSuccess() ? "T" : "F");
  }

  /**
   * assign a resultcode of exception, and process/log the exception
   * @param e
   * @param wsSubjectLookup
   */
  public void assignResultCodeException(Exception e, WsSubjectLookup wsSubjectLookup) {
    this.assignResultCode(WsHasMemberResultCode.EXCEPTION);
    this.getResultMetadata().setResultMessage(ExceptionUtils.getFullStackTrace(e));
    LOG.error(wsSubjectLookup + ", " + e, e);
  }

  /**
   * @return the resultMetadata
   */
  public WsResultMeta getResultMetadata() {
    return this.resultMetadata;
  }

  /**
   * convert string to result code
   * @return the result code
   */
  public WsHasMemberResultCode resultCode() {
    return WsHasMemberResultCode.valueOf(this.getResultMetadata().getResultCode());
  }

  
  /**
   * @param resultMetadata1 the resultMetadata to set
   */
  public void setResultMetadata(WsResultMeta resultMetadata1) {
    this.resultMetadata = resultMetadata1;
  }

}
