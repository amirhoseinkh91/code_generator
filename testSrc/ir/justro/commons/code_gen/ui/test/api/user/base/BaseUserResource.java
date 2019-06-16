package ir.justro.commons.code_gen.ui.test.api.user.base;

import ir.justro.commons.code_gen.ui.test.api.AbstractMgrBasedResource;
import ir.justro.commons.code_gen.ui.test.api.user.dto.UserFullDTO;
import ir.justro.commons.code_gen.ui.test.model.user.User;
import ir.justro.commons.code_gen.ui.test.model.user.UserMgr;

/**
 *  Base class for "UserResource".
 *  It is an automatically generated file and should not be edited.
 */
public abstract class BaseUserResource extends AbstractMgrBasedResource<User, UserFullDTO> {
	
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
		return UserMgr.getInstance();
	}

	/**
	 * Factory method for FullDTO.
	 * @return a FullDTO instance
	 */
	@Override
	protected UserFullDTO createFullDTO() {
		return new UserFullDTO();
	}

	/**
	 * Provides the class of FieldInfoContext used for search.
	 * @return the class of FieldInfoContext 
	 */
	@Override
	protected Class<UserFullDTO.FieldInfoContext> getFullDtoFieldInfoContextClass() {
		return UserFullDTO.FieldInfoContext.class;
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
