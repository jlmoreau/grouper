/*
  Copyright 2004-2006 University Corporation for Advanced Internet Development, Inc.
  Copyright 2004-2006 The University Of Chicago

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


import  java.io.Serializable;
import  org.apache.commons.lang.builder.*;


/** 
 * Group math factors.
 * @author blair christensen.
 *     
 */
public class UnionFactor extends Factor implements Serializable {

  // Protected Constants
  protected static final String KLASS = "union";


  // Constructors

  /**
   * For Hibernate.
   */
  public UnionFactor() {
    super();
  } // public UnionFactor()

  public UnionFactor(Group left, Group right) {
    this.setKlass(  KLASS );
    this.setLeft(   left  );
    this.setRight(  right );
  } // public UnionFactor(left, right)

}

