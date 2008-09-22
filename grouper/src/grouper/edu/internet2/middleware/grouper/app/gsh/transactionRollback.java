/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * 
 */

package edu.internet2.middleware.grouper.app.gsh;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bsh.CallStack;
import bsh.Interpreter;
import edu.internet2.middleware.grouper.hibernate.GrouperRollbackType;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;

/**
 * Rollback a transaction
 * <p/>
 * @author  chris hyzer
 * @version $Id: transactionRollback.java,v 1.1 2008-09-22 15:06:40 mchyzer Exp $
 * @since   0.0.1
 */
public class transactionRollback {

  /** logger */
  private static final Log LOG = LogFactory.getLog(transactionRollback.class);

  /**
   * Rollback a transaction
   * <p/>
   * @param   interpreter           BeanShell interpreter.
   * @param   stack       BeanShell call stack.
   * @param grouperRollbackTypeString to use for starting transaction, must be a 
   * GrouperCommitType enum
   * @return  instructions for use
   * @throws  GrouperShellException
   * @since   0.0.1
   */
  public static String invoke(Interpreter interpreter, CallStack stack, 
      String grouperRollbackTypeString) 
    throws  GrouperShellException {
    GrouperShell.setOurCommand(interpreter, true);
    GrouperRollbackType grouperRollbackType = GrouperRollbackType
      .valueOfIgnoreCase(grouperRollbackTypeString);
    HibernateSession hibernateSession = HibernateSession._internal_hibernateSession();
    if (hibernateSession == null) {
      String error = "Cant rollback a transaction since none in scope";
      interpreter.println(error);
      LOG.error(error);
      throw new GrouperShellException(error);
    }
    hibernateSession.rollback(grouperRollbackType);
    interpreter.println("Rolled back transaction index: " + (HibernateSession._internal_staticSessions().size()-1));
    return "";
    
  }

}

