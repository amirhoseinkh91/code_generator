package ir.viratech.commons.code_gen.ui.test.api;

import ir.viratech.commons.api.dto.EntityFullDTO;
import ir.viratech.commons.model.UIDAndDisplayStringProvider;
import ir.viratech.commons.util.i18n.MessageTranslator;

/**
 * The base class for MgrBased resources.
 *
 * @param <E> The entity type
 * @param <D> The full DTO type
 */
public abstract class AbstractMgrBasedResource<E extends UIDAndDisplayStringProvider, D extends EntityFullDTO<E>> extends ir.viratech.commons.api.resource.BaseAbstractMgrBasedResource<E, D> {
	
	@Override
	protected boolean hasAccessToFeature(String f) {
		return true;
	}
	
	@Override
	protected MessageTranslator getMessageTranslator() {
		return null;
	}
	
	@Override
	protected String getFeatureByEntityName_Add(String featureEntityName) {
		return "ADD_"+featureEntityName;
	}
	@Override
	protected String getFeatureByEntityName_Delete(String featureEntityName) {
		return "DELETE_"+featureEntityName;
	}
	@Override
	protected String getFeatureByEntityName_Edit(String featureEntityName) {
		return "EDIT_"+featureEntityName;
	}
	@Override
	protected String getFeatureByEntityName_List(String featureEntityName) {
		return "LIST_"+featureEntityName;
	}
	@Override
	protected String getFeatureByEntityName_View(String featureEntityName) {
		return "VIEW_"+featureEntityName;
	}
	
}
