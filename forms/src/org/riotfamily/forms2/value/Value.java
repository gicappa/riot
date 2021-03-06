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
package org.riotfamily.forms2.value;

import org.springframework.core.convert.TypeDescriptor;

public interface Value {

	public <T, D extends T> Value require(Class<T> requiredType, Class<D> defaultType);
	
	public TypeDescriptor getTypeDescriptor();

	public void set(Object object);
	
	public <T> T get();
	
	public <T> T getOrCreate();

	public Value getNested(String name);
	
	public void setNested(String name, Object object);
	
}
