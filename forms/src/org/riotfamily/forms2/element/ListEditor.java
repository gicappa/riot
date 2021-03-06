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
package org.riotfamily.forms2.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.base.TypedState;
import org.riotfamily.forms2.base.UserInterface;
import org.riotfamily.forms2.client.FormResource;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.client.Resources;
import org.riotfamily.forms2.client.ScriptResource;
import org.riotfamily.forms2.value.Value;
import org.riotfamily.forms2.value.ValueFactory;
import org.springframework.core.convert.TypeDescriptor;

public class ListEditor extends Element {

	private Element itemEditor;
	
	private boolean sortable = true;

	public boolean dragAndDrop = true;
	
	public ListEditor() {
	}
	
	public ListEditor(Element itemEditor) {
		setItemEditor(itemEditor);
	}

	public void setItemEditor(Element itemEditor) {
		this.itemEditor = itemEditor;
		itemEditor.setParent(this);
	}
		
	@Override
	public FormResource getResource() {
		if (dragAndDrop) {
			return new ScriptResource("forms/listEditor.js", Resources.SCRIPTACULOUS_DRAG_DROP);
		}
		return null;
	}
		
	public static class State<T extends ListEditor> extends TypedState<T> {

		private List<ElementState> itemStates = Generics.newArrayList();
		
		private TypeDescriptor itemTypeDescriptor;
		
		@Override
		protected void initInternal(T list, Value value) {
			value.require(List.class, ArrayList.class);
			itemTypeDescriptor = value.getTypeDescriptor().getElementTypeDescriptor();
			if (value.get() != null) {
				Collection<?> c = value.get();
				for (Object item : c) {
					addItem(list.itemEditor, item);
				}
			}
		}
		
		private ElementState addItem(Element itemElement, Object object) {
			Value value = ValueFactory.createValue(itemTypeDescriptor);
			value.set(object);
			ElementState state = itemElement.createState(this, value);
			itemStates.add(state);
			return state;
		}
		
		@Override
		protected void populateInternal(Value value, T list) {
			value.require(List.class, ArrayList.class);
			List<Object> c = value.getOrCreate();
			c.clear();
			for (ElementState itemState : itemStates) {
				Value itemValue = ValueFactory.createValue(itemTypeDescriptor);
				itemState.populate(itemValue, list.itemEditor);
				c.add(itemValue.get());
			}
		}
		
		/**
		 * Renders all list-items, an add-button and a script to initialize
		 * drag-and-drop (if enabled).
		 */
		@Override
		protected void renderInternal(Html html, T list) {
			Html ul = html.ul().cssClass("items");
			for (ElementState itemState : itemStates) {
				renderItem(ul, list, itemState);
			}
			html.button("add");
			if (list.dragAndDrop) {
				UserInterface ui = new UserInterface();
				ui.invoke(this, "ul", "makeSortable");
				html.process(ui.getActions());
			}
		}
		
		/**
		 * Renders a single list-item, as well as buttons to move or delete it.
		 */
		private void renderItem(Html html, T list, ElementState itemState) {
			Html tr = html.li().id("item-" + itemState.getId()).table().tr();
			if (list.dragAndDrop) {
				tr.td("handle");
			}
			else if (list.sortable) {
				tr.td("moveButtons")
					.button("up", index()).up()
					.button("down", index());
			}
			
			itemState.render(tr.td("itemElement"), list.itemEditor);
			tr.td("removeButton").button("remove", index());
		}
		
		/**
		 * Adds a new item to the end of the list. The method is invoked when
		 * the user clicks the add button.
		 */
		public void add(UserInterface ui, T list, String value) {
			ElementState itemState = addItem(list.itemEditor, null);
			Html html = newHtml();
			renderItem(html, list, itemState);
			ui.insert(this, ".items", html);
			ui.invoke(this, "ul", "makeSortable");
			updateClasses(ui);
		}
		
		/**
		 * Updates the CSS class-names of the move-up/down buttons. It first
		 * removes the <code>disabled</code> class from all buttons and then
		 * adds it back to the first up and last down button.
		 */
		protected void updateClasses(UserInterface ui) {
			ui.removeClassName(this, "button", "disabled");
			ui.addClassName(this, "li:first-child .up", "disabled");
			ui.addClassName(this, "li:last-child .down", "disabled");
		} 
		
		private static String selector(int i) {
			return String.format("li:nth-child(%s)", i + 1);
		}
		
		/**
		 * Removes an element from the list. The method is invoked when a user
		 * clicks the remove button of an item. The index is determined by 
		 * evaluating the {@link #index()} JavaScript expression.
		 */
		public void remove(UserInterface ui, T list, int index) {
			itemStates.remove(index);
			ui.remove(this, selector(index));
			updateClasses(ui);
		}
		
		/**
		 * Moves an element upwards in the list and updates the UI. The method 
		 * is invoked when the user clicks the move-up button of an item.
		 */
		public void up(UserInterface ui, T list, int index) {
			if (index > 0) {
				Collections.swap(itemStates, index, index - 1);
				ui.moveUp(this, selector(index));
				updateClasses(ui);
			}
		}
		
		/**
		 * Moves an element down in the list and updates the UI. The method is 
		 * invoked when the user clicks the move-down button of an item.
		 */
		public void down(UserInterface ui, T list, int index) {
			if (index < itemStates.size() - 1) {
				Collections.swap(itemStates, index, index + 1);
				ui.moveDown(this, selector(index));
				updateClasses(ui);
			}
		}
		
		/**
		 * Returns a JavaScript expression that counts the number of siblings
		 * preceding the enclosing &lt;li&gt;.
		 */
		protected static String index() {
			return "riot.form.indexOf(this, 'li')";
		}
		
		/**
		 * Sorts the list according to the specified order, where order is a 
		 * list of {@link ElementState#getId() stateIds}. The method is invoked
		 * when the list is re-ordered via drag-and-drop.
		 */
		public void sort(UserInterface ui, T list, List<String> order) {
			Collections.sort(itemStates, new SortOrderComparator(order));
		}
		
	}
	
	/**
	 * Comparator implementation used by the {@link #sort} method.
	 */
	private static class SortOrderComparator implements Comparator<ElementState> {

		private final List<String> order;

		public SortOrderComparator(List<String> order) {
			this.order = order;
		}

		public int compare(ElementState o1, ElementState o2) {
			Integer i1 = order.indexOf(o1.getId());
			Integer i2 = order.indexOf(o2.getId());
			return i1.compareTo(i2);
		}
	}

}
