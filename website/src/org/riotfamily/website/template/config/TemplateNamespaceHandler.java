/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.template.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * NamespaceHandler that handles the <code>template</code> namespace,
 * defined in <code>template.xsd</code>.
 *
 * TODO Insert example XML code here
 */
public class TemplateNamespaceHandler extends NamespaceHandlerSupport {

	TemplateDefinitionParser definitionParser = new TemplateDefinitionParser();
	
	public void init() {
		registerBeanDefinitionParser("config", new ConfigParser());
		registerBeanDefinitionParser("definition", definitionParser);
	}
	
	private class ConfigParser implements BeanDefinitionParser {
		
		public BeanDefinition parse(Element element, ParserContext parserContext) {
			definitionParser.setPrefix(element.getAttribute("prefix"));
			definitionParser.setSuffix(element.getAttribute("suffix"));
			return null;
		}
		
	}

}