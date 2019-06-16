package ir.viratech.commons.code_gen.ui.test.api.user.base;

import ir.viratech.commons.code_gen.ui.test.model.user.User;

/**
 *  Base class for "UserResource".
 *  It is an automatically generated file and should not be edited.
 */
public abstract class BaseUserResource extends ir.viratech.commons.code_gen.ui.test.api.AbstractMgrBasedResource<User, ir.viratech.commons.code_gen.ui.test.api.user.dto.UserFullDTO> {
	
	/**
	 * The path which is handled by this resource/
	 */
	public static final String RESOURCE_PATH = "/user"+PATH_PARAM_PART__ITEMS;
	
	/**
	 * Provides the entity manager used by the resource.
	 * @return an instance of the required entity manager
	 */
	@Override
	protected ir.viratech.commons.model.BasicEntityMgr<User> getMgr() {
		return ir.viratech.commons.code_gen.ui.test.model.user.UserMgr.getInstance();
	}

	/**
	 * Factory method for FullDTO.
	 * @return a FullDTO instance
	 */
	@Override
	protected ir.viratech.commons.code_gen.ui.test.api.user.dto.UserFullDTO createFullDTO() {
		return new ir.viratech.commons.code_gen.ui.test.api.user.dto.UserFullDTO();
	}

	/**
	 * Provides the class of FieldInfoContext used for search.
	 * @return the class of FieldInfoContext 
	 */
	@Override
	protected Class<ir.viratech.commons.code_gen.ui.test.api.user.dto.UserFullDTO.FieldInfoContext> getFullDtoFieldInfoContextClass() {
		return ir.viratech.commons.code_gen.ui.test.api.user.dto.UserFullDTO.FieldInfoContext.class;
	}

	/**
	 * Provides the bundle prefix used in i18n.
	 * @return the bundle prefix
	 */
	@Override
	protected String getBundlePrefix() {
		return "user.";
	}
	
	/**
	 * Provides the feature entity name by which access checking is performed.
	 * @return the feature entity name
	 */
	@Override
	protected String getFeatureEntityName() {
		return "USER";
	}
	
	
	/**
	 * Default constructor.
	 * Also adds the extents.
	 */
	public BaseUserResource() {
		super();
	}

}
