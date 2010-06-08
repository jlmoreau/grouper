/*
 * Copyright 2010 University Corporation for Advanced Internet Development, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.internet2.middleware.grouper.shibboleth.attributeDefinition;

import java.util.Arrays;
import java.util.Collection;

import org.openspml.v2.msg.spml.PSOIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.internet2.middleware.ldappc.util.PSPUtil;
import edu.internet2.middleware.shibboleth.common.attribute.BaseAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.provider.BasicAttribute;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.AttributeResolutionException;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.ShibbolethResolutionContext;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.attributeDefinition.AttributeDefinition;
import edu.internet2.middleware.shibboleth.common.attribute.resolver.provider.attributeDefinition.BaseAttributeDefinition;

/**
 * An {@link AttributeDefinition} whose values are {@link PSOIdentifier}s which are
 * computed from dependencies.
 */
public class PSOIdentifierAttributeDefinition extends BaseAttributeDefinition {

  // TODO containerID ? targetID ?

  /** logger */
  private static final Logger LOG = LoggerFactory.getLogger(PSOIdentifierAttributeDefinition.class);

  /** {@inheritDoc} */
  protected BaseAttribute<PSOIdentifier> doResolve(ShibbolethResolutionContext resolutionContext)
      throws AttributeResolutionException {

    String principalName = resolutionContext.getAttributeRequestContext().getPrincipalName();
    String msg = "resolve '" + principalName + "' ad '" + this.getId() + "'";
    LOG.debug("{}", msg);

    BasicAttribute<PSOIdentifier> attribute = new BasicAttribute<PSOIdentifier>(this.getId());

    Collection<Object> values = this.getValuesFromAllDependencies(resolutionContext, this.getSourceAttributeID());

    if (values == null || values.isEmpty()) {
      return attribute;
    }

    if (values.size() != 1) {
      throw new AttributeResolutionException("Expected a single dependency.");
    }

    PSOIdentifier psoIdentifier = new PSOIdentifier();

    psoIdentifier.setID(values.iterator().next().toString());

    attribute.setValues(Arrays.asList(new PSOIdentifier[] { psoIdentifier }));

    if (LOG.isDebugEnabled()) {
      LOG.debug("{} values {}", msg, attribute.getValues().size());
      for (Object value : attribute.getValues()) {
        LOG.debug("{} value '{}'", msg, PSPUtil.getString(value));
      }
    }

    return attribute;
  }

  /** {@inheritDoc} */
  public void validate() throws AttributeResolutionException {

  }

}
