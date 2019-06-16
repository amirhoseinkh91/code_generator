package ir.viratech.commons.code_gen.ui.test.api.user;

import ir.viratech.commons.code_gen.ui.test.api.user.base.BaseUserResource;

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

/**
 *  This is a REST Resource class for entity "User".
 *
 */
@Component
@Path(BaseUserResource.RESOURCE_PATH)
public class UserResource extends BaseUserResource {


}
