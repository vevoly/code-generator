package javacommon.base;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
/**
 * @author lixin
 */
@Transactional
public abstract class BaseManager <E,PK extends Serializable>{
	
	protected Log log = LogFactory.getLog(getClass());

	protected abstract EntityDAO getEntityDAO();

	@Transactional(readOnly=true)
	public E getById(PK id) throws DataAccessException{
		return (E)getEntityDAO().getById(id);
	}
	
	@Transactional(readOnly=true)
	public List<E> findAll() throws DataAccessException{
		return getEntityDAO().findAll();
	}
	
	/** 根据id检查是否插入或是更新数据 */
	public void saveOrUpdate(E entity) throws DataAccessException{
		getEntityDAO().saveOrUpdate(entity);
	}
	
	/** 插入数据 */
	public void save(E entity) throws DataAccessException{
		getEntityDAO().save(entity);
	}
	
	public void removeById(PK id) throws DataAccessException{
		getEntityDAO().deleteById(id);
	}
	
	public void update(E entity) throws DataAccessException{
		getEntityDAO().update(entity);
	}
	
	@Transactional(readOnly=true)
	public boolean isUnique(E entity, String uniquePropertyNames) throws DataAccessException {
		return getEntityDAO().isUnique(entity, uniquePropertyNames);
	}
	
}
