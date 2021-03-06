/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.forms2.client;

import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.annotate.JsonValue;
import org.riotfamily.common.util.FormatUtils;
import org.springframework.context.MessageSource;
import org.w3c.dom.Element;


public class Html extends DomBuilder<Html> {

	private MessageSource messageSource;

	private Locale locale;
	
	private IdGenerator idGenerator;
	
	public Html(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
	
	protected Html(Element child, Html parent) {
		super(child, parent);
		this.idGenerator = parent.idGenerator;
	}
	
	@Override
	protected Html getThis() {
		return this;
	}
	
	public void setMessageKeyPrefix(String prefix) {
		
	}

	private String message(String defaultText) {
		return message(FormatUtils.toCssClass(defaultText), defaultText);
	}
	
	private String message(String labelKey, String defaultText) {
		if (messageSource == null) {
			return defaultText;
		}
		return messageSource.getMessage(labelKey, null, defaultText, locale);
	}
	
	public Html messageText(String defaultText) {
		return text(message(defaultText));
	}
	
	public Html messageText(String labelKey, String defaultText) {
		return text(message(labelKey, defaultText));
	}
	
	public Html messageAttr(String name, String defaultText) {
		return attr(name, message(defaultText));
	}
	
	public Html messageAttr(String name, String labelKey, String defaultText) {
		return attr(name, message(labelKey, defaultText));
	}
	
	public Html div() {
		return elem("div");
	}
	
	public Html div(String cssClass) {
		return div().cssClass(cssClass);
	}
	
	public Html ul() {
		return elem("ul");
	}
	
	public Html li() {
		return elem("li");
	}
	
	public Html table() {
		return elem("table");
	}
	
	public Html tr() {
		return elem("tr");
	}
	
	public Html td() {
		return elem("td");
	}
	
	public Html td(String className) {
		return td().cssClass(className);
	}
	
	public Html label(String defaultText) {
		return label(defaultText, idGenerator.next());
	}
	
	public Html labelPrev(String defaultText) {
		return label(defaultText, idGenerator.prev());
	}
	
	protected Html label(String defaultText, String inputId) {
		return elem("label")
				.attr("for", inputId)
				.messageText(defaultText);
	}
	
	public Html input(String type, String value) {
		return elem("input")
			.id(idGenerator.inputId())
			.cssClass(type)
			.attr("type", type)
			.attr("value", value);
	}
	
	public Html input(String type, String name, String value) {
		return input(type, value).attr("name", name);
	}
	
	public Html hiddenInput(String name, String value) {
		return elem("input")
			.attr("type", "hidden")
			.attr("name", name)
			.attr("value", value);
	}
	
	public Html button(String handler) {
		return button(handler, null);
	}
	
	public Html button(String handler, String exp) {
		return elem("button").cssClass(handler).propagate("click", handler, exp);
	}
	
	public Html submit(String defaultText) {
		return input("submit", message(defaultText));
	}
	
	public Html multipartForm(String action) {
		return elem("form")
			.attr("action", action)
			.attr("method", "POST")
			.attr("enctype", "multipart/form-data");
	}
	
	public Html propagate(String event, String handler) {
		return propagate(event, handler, "$F(this)");
	}
	
	public Html propagate(String event, String handler, String exp) {
		return attr("on" + event, String.format("riot.form.submitEvent(this, '%s', %s)", handler, exp));
	}
	
	public Html process(List<Action> actions) {
		script("riot.form.processActions(%s)", FormatUtils.toJSON(actions));
		return this;
	}
	
	public Html script(CharSequence script) {
		return elem("script").text(script.toString());
	}
	
	public Html script(String script, Object... args) {
		return elem("script").text(String.format(script, args));
	}
	
	public Html id(String value) {
		return attr("id", value);
	}
	
	public Html cssClass(String value) {
		return attr("class", value);
	}
	
	public Html addClass(String className) {
		String attr = element().getAttribute("class");
		if (!attr.matches("\\b" + className + "\\b")) {
			attr += " " + className;
		}
		return cssClass(attr.trim());
	}
	
	public Html style(String value) {
		return attr("style", value);
	}
	
	public Html style(String format, Object... args) {
		return style(String.format(format, args));
	}
	
	@Override
	@JsonValue
	public String toString() {
		return super.toString();
	}
	
	@Override
	protected Html createNested(Element child) {
		return new Html(child, this);
	}

}
