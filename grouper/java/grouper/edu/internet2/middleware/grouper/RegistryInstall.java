/*
  Copyright 2004-2005 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2005 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;


import  edu.internet2.middleware.subject.*;
import  edu.internet2.middleware.subject.provider.*;
import  java.util.*;
import  net.sf.hibernate.*;
import  org.apache.commons.logging.*;


/** 
 * Install the Groups Registry.
 * <p />
 * @author  blair christensen.
 * @version $Id: RegistryInstall.java,v 1.9 2005-12-09 07:35:38 blair Exp $    
 */
public class RegistryInstall {

  // Private Class Constants
  private static final Log LOG = LogFactory.getLog(RegistryInstall.class);


  // Public Class Methods

  public static void main(String[] args) {
    // Install group types, fields and privileges
    _installFieldsAndTypes();
    _installRootStem();
  } // public static void main(args)


  // Private Class Methods
  private static void _installFieldsAndTypes() {
    Set base_f    = new LinkedHashSet();
    Set fields    = new LinkedHashSet();
    Set naming_f  = new LinkedHashSet();
    Set types     = new LinkedHashSet();
   
    // Base Attributes
    base_f.add(
      new Field(
        "description"       , FieldType.ATTRIBUTE,
        AccessPrivilege.READ, AccessPrivilege.ADMIN,
        true
      )
    );
    base_f.add(
      new Field(
        "displayName"       , FieldType.ATTRIBUTE,
        AccessPrivilege.VIEW, AccessPrivilege.SYSTEM,
        false
      )
    );
    base_f.add(
      new Field(
        "displayExtension"  , FieldType.ATTRIBUTE,
        AccessPrivilege.VIEW, AccessPrivilege.ADMIN,
        false
      )
    );
    base_f.add(
      new Field(
        "extension"         , FieldType.ATTRIBUTE,
        AccessPrivilege.VIEW, AccessPrivilege.ADMIN,
        false
      )
    );
    base_f.add(
      new Field(
        "name"              , FieldType.ATTRIBUTE,
        AccessPrivilege.VIEW, AccessPrivilege.SYSTEM,
        false
      )
    );
    // Base Access Privileges
    base_f.add(
      new Field(
        "admins"                , FieldType.ACCESS,
        AccessPrivilege.ADMIN   , AccessPrivilege.ADMIN,
        true
      )
    );
    base_f.add(
      new Field(
        "members"               , FieldType.LIST,
        AccessPrivilege.READ    , AccessPrivilege.UPDATE,
        true
      )
    );
    base_f.add(
      new Field(
        "optins"                , FieldType.ACCESS,
        AccessPrivilege.UPDATE  , AccessPrivilege.UPDATE,
        true
      )
    );
    base_f.add(
      new Field(
        "optouts"               , FieldType.ACCESS,
        AccessPrivilege.UPDATE  , AccessPrivilege.UPDATE,
        true
      )
    );
    base_f.add(
      new Field(
        "readers"               , FieldType.ACCESS,
        AccessPrivilege.ADMIN   , AccessPrivilege.ADMIN,
        true
      )
    );
    base_f.add(
      new Field(
        "updaters"              , FieldType.ACCESS,
        AccessPrivilege.ADMIN   , AccessPrivilege.ADMIN,
        true
      )
    );
    base_f.add(
      new Field(
        "viewers"               , FieldType.ACCESS,
        AccessPrivilege.ADMIN   , AccessPrivilege.ADMIN,
        true
      )
    );
    // Naming Privileges
    naming_f.add(
      new Field(
        "creators"              , FieldType.NAMING,
        NamingPrivilege.STEM    , NamingPrivilege.STEM,
        true
      )
    );
    naming_f.add(
      new Field(
        "stemmers"              , FieldType.NAMING,
        NamingPrivilege.STEM    , NamingPrivilege.STEM,
        true
      )
    );

    GroupType base    = new GroupType("base", base_f);
    types.add(base);
    GroupType naming  = new GroupType("naming", naming_f);
    types.add(naming);
  
    fields.addAll(base_f);
    fields.addAll(naming_f);

    try {
      Session hs = HibernateHelper.getSession();
      Set objects = new LinkedHashSet();
      objects.addAll(types);
      HibernateHelper.save(objects);
      hs.close();
      LOG.info("group types installed: " + types.size());
      LOG.info("fields installed     : " + fields.size());
    }
    catch (HibernateException eH) {
      String err = "error installing schema: " + eH.getMessage();
      LOG.fatal(err);
      throw new RuntimeException(err);
    }
  } // private static void _installFieldsAndTypes()

  private static void _installRootStem() {
    try {
      GrouperSession s = GrouperSession.startSession(
        SubjectFinder.findById(
          "GrouperSystem", "application"
        )
      );
      Stem.addRootStem(s);
      LOG.info("root stem installed");
    }
    catch (Exception e) { 
      String err = "error installing root stem: " + e.getMessage();
      LOG.fatal(err);
      throw new RuntimeException(err);
    }
  } // private static void _installRootStem()

}

