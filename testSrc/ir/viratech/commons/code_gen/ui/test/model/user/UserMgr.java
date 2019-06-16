package ir.viratech.commons.code_gen.ui.test.model.user;

import ir.viratech.commons.model.BasicEntityMgr;
import ir.viratech.commons.model.EntityModificationException;
import ir.viratech.commons.model.EntityModifier;
import ir.viratech.commons.model.EntityObjectNotFoundException;
import ir.viratech.commons.model.search.SearchQuery;
import ir.viratech.commons.paged_list.api.PagedList;
import ir.viratech.commons.util.synchronized_lazy.AbstractNotNullSynchronizedLazy;
import ir.viratech.commons.util.synchronized_lazy.SynchronizedLazy;

import java.util.List;

public class UserMgr implements BasicEntityMgr<User> {
	
	
	// ========== BEGIN singleton ==========
	private UserMgr() {
	}

	private static final SynchronizedLazy<UserMgr> SYNCH_LAZY_INSTANCE = new AbstractNotNullSynchronizedLazy<UserMgr>() {
		@Override
		public UserMgr create() {
			return new UserMgr();
		}
	};

	public static UserMgr getInstance() {
		return SYNCH_LAZY_INSTANCE.get();
	}
	// =========== END singleton ===========
	
	
	@Override
	public User add(EntityModifier<User> arg0) throws EntityModificationException {
		return null;
	}
	
	@Override
	public long countAll() {
		return 0;
	}
	
	@Override
	public User createNew() {
		return null;
	}
	
	@Override
	public User deleteByExtuid(String arg0) throws EntityObjectNotFoundException, EntityModificationException {
		return null;
	}
	
	@Override
	public User getByExtuid(String arg0) {
		return null;
	}
	
	@Override
	public User getExistingByExtuid(String arg0) throws EntityObjectNotFoundException {
		return null;
	}
	
	@Override
	public User getOrCreateByExtuid(String arg0, boolean arg1) {
		return null;
	}
	
	@Override
	public List<User> pageList(long arg0, int arg1) {
		return null;
	}
	
	@Override
	public PagedList<User> search(SearchQuery arg0) {
		return null;
	}
	
	@Override
	public User update(String arg0, EntityModifier<User> arg1) throws EntityObjectNotFoundException, EntityModificationException {
		return null;
	}
}
