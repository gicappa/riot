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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.editor;

import java.io.IOException;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.image.ImageCropper;
import org.riotfamily.media.dao.MediaDao;
import org.riotfamily.media.model.RiotImage;
import org.riotfamily.media.model.data.CroppedImageData;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.list.RiotDaoService;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.website.cache.CacheInvalidationUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
@Transactional
public class EntityEditorImpl implements EntityEditor {

	private RiotDaoService daoService;

	private CacheService cacheService;
	
	private MediaDao mediaDao;
	
	private ImageCropper imageCropper;
	
	public EntityEditorImpl(RiotDaoService daoService) {
		this.daoService = daoService;
	}
	
	public void setCacheService(CacheService cacheService) {
		this.cacheService = cacheService;
	}
	
	public void setMediaDao(MediaDao mediaDao) {
		this.mediaDao = mediaDao;
	}

	public void setImageCropper(ImageCropper imageCropper) {
		this.imageCropper = imageCropper;
	}

	public String createObject(String listId) {
		RiotDao dao = daoService.getDao(listId);
		BeanWrapper wrapper = new BeanWrapperImpl(dao.getEntityClass());
		Object entity = wrapper.getWrappedInstance();
		AccessController.checkPermission("edit", entity);
		dao.save(entity, null);
		CacheInvalidationUtils.invalidate(cacheService, dao);
		return dao.getObjectId(entity);
	}
	
	public void deleteObject(String listId, String objectId) {
		RiotDao dao = daoService.getDao(listId);
		Object entity = dao.load(objectId);
		AccessController.checkPermission("delete", entity);
		dao.delete(entity, null);
		CacheInvalidationUtils.invalidate(cacheService, dao, entity);
	}
	
	public String getText(String listId, String objectId, String property) {
		RiotDao dao = daoService.getDao(listId);
		Object entity = dao.load(objectId);
		return PropertyUtils.getPropertyAsString(entity, property);
	}
	
	public void updateText(String listId, String objectId, String property, 
			String value) {
		
		RiotDao dao = daoService.getDao(listId);
		Object entity = dao.load(objectId);
		AccessController.checkPermission("edit", entity);
		BeanWrapper wrapper = new BeanWrapperImpl(entity);
		wrapper.setPropertyValue(property, value);
		dao.update(entity);
		CacheInvalidationUtils.invalidate(cacheService, dao, entity);
	}
	
	public String cropImage(String listId, String objectId, String property, 
			Long imageId, int width, int height, int x, int y, int scaledWidth)
			throws IOException {
	
		
		RiotImage original = (RiotImage) mediaDao.loadFile(imageId);
		RiotImage croppedImage = new RiotImage(new CroppedImageData(
				original, imageCropper, width, height, x, y, scaledWidth));
		
		RiotDao dao = daoService.getDao(listId);
		Object entity = dao.load(objectId);
		AccessController.checkPermission("edit", entity);
		BeanWrapper wrapper = new BeanWrapperImpl(entity);
		wrapper.setPropertyValue(property, croppedImage);
		dao.update(entity);
		CacheInvalidationUtils.invalidate(cacheService, dao, entity);
		return croppedImage.getUri();
	}
}
