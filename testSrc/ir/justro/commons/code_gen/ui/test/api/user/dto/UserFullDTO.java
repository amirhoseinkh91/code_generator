package ir.justro.commons.code_gen.ui.test.api.user.dto;

import ir.justro.commons.code_gen.ui.test.model.user.User;
import ir.justro.commons.code_gen.ui.test.model.user.role.UserRole;
import ir.viratech.commons.api.search.field.PrimitiveFieldInfo;
import ir.viratech.commons.api.search.field.types.FieldInfo_String;
import ir.justro.commons.code_gen.ui.test.api.user.base.BaseUserFullDTO;

import org.apache.commons.lang.StringUtils;


/**
 * A DTO for class User.
 *
 */
public class UserFullDTO extends BaseUserFullDTO {
	
	/**
	 * FieldInfoContext for UserFullDTO
	 */
	public static class FieldInfoContext extends BaseUserFullDTO.BaseFieldInfoContext {
		
		@Override
		protected PrimitiveFieldInfo<?> createFieldInfo_FirstName(String externalName, String internalName, String internalSearchExpression, String typeKey, String bundleKey, boolean searchable, Boolean sortable) {
			return new FieldInfo_String(externalName, internalName, internalSearchExpression, typeKey, bundleKey, searchable, sortable);
		}
	}
	
	/**
	 * convert password of user to string.
	 */
	@Override
	protected String toString_Password(String password) {
		return (password == null) ? null : ("'"+StringUtils.repeat("*", password.length())+"'");
	}
	
	/**
	 * Concatenates role names of user roles in comma separated format.
	 */
	@Override
	protected String load_Roles(User user) {
		StringBuilder res = new StringBuilder("");
		for (UserRole role : user.getRoles()) {
			res.append(((res.length() == 0)?"":", ") + role.getName());
		}
		return res.toString();
	}
	
}
