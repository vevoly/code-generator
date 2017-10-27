package javacommon.base;


import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDAOSupport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 通用DAO的iBatis实现，这个适用范围相对WebBaseIBatisDAO更为广泛，这个更适合用于纯Java的应用中.
 *
 * @author lixin 11-12-12 下午10:42
 */
public abstract class BaseMybatisDAO<E, PK extends Serializable> extends SqlSessionDAOSupport implements GenericDAO<E, PK> {

    protected String sqlmapNamespace;
    protected Class<?> entityType;
    protected SqlSession sqlSession;

    @Autowired
    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
        super.setSqlSessionTemplate(sqlSessionTemplate);
    }

    protected <S> S getMapper(Class<S> clazz) {
        return getSqlSession().getMapper(clazz);
    }

    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }


    /**
     * 通过反射获取传递给超类的泛型
     */
    public BaseMybatisDAO() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.entityType = (Class) pt.getActualTypeArguments()[0];
        this.sqlmapNamespace = this.entityType.getSimpleName();
        if (sqlSession == null) {
            sqlSession = getSqlSession();
        }
    }


    public String getMybatisMapperNamesapce() {
        throw new RuntimeException("not yet implement");
    }

    /**
     * 保存实体
     *
     * @param entity 实体
     * @return
     */
    public E insert(E entity) {
        sqlSession.insert(this.sqlmapNamespace + "." + SQLID_INSERT, entity);
        return entity;
    }

    /**
     * 更新实体
     *
     * @param entity 实体
     * @return
     */
    public E update(E entity) {
        sqlSession.update(this.sqlmapNamespace + "." + SQLID_UPDATE, entity);
        return entity;
    }


    /**
     * 添加或者更新
     *
     * @param entity 实体
     */
    public void insertOrUpdate(E entity) {
        if (((BaseEntity) entity).getId() == null)
            insert(entity);
        else
            update(entity);
    }

    /**
     * 合并实体
     *
     * @param entity 实体
     * @return
     */
    public E merage(E entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity e = (BaseEntity) entity;
            if (e.getId().longValue() == 0L) {
                sqlSession.insert(this.sqlmapNamespace + "." + SQLID_INSERT, entity);
                return entity;
            } else {
                this.update(entity);
                return entity;
            }
        } else {
            throw new RuntimeException("merage方法只适用于BaseEntity类型的实体！请检查你的参数是否符合条件！");
        }
    }

    /**
     * 根据主键删除一条实体
     *
     * @param pk 主键id值
     */
    public void delete(PK pk) {
        sqlSession.delete(this.sqlmapNamespace + "." + SQLID_DELETE, pk);
    }

    /**
     * 批量插入
     * </p>
     * <insert id="batchInsert" parameterType="java.util.List">
     * INSERT INTO STUDENT (id,name,sex,tel,address)
     * VALUES
     * <foreach collection="entitys" item="entity" index="index" separator="," >
     * (#{entity.id},#{entity.name},#{entity.sex},#{entity.tel},#{entity.address})
     * </foreach>
     * </insert>
     *
     * @param entitySet 集合
     * @return
     */
    @Override
    public int batchInsert(Collection<E> entitySet) {
        Iterator var2 = entitySet.iterator();

        while (var2.hasNext()) {
            Object entity = var2.next();
            sqlSession.insert(this.sqlmapNamespace + "." + SQLID_INSERT, entity);
        }

        return entitySet.size();
    }


    /**
     * 批量更新
     * <p>
     * 例如：
     * <update id="batchUpdate" parameterType="Student" >
     * UPDATE STUDENT SET name = #{name}
     * <foreach collection="entitys" index="index" item="entity" open=" " separator="," close=" ">
     * #{obj}
     * </foreach>
     * </update>
     *
     * @param entitySet
     * @return
     */
    public int batchUpdate(Collection<E> entitySet) {
        int x = 0;

        Object entity;
        for (Iterator var3 = entitySet.iterator(); var3.hasNext(); x += sqlSession.insert(this.sqlmapNamespace + "." + SQLID_UPDATE, entity)) {
            entity = var3.next();
        }

        return entitySet.size();
    }

    /**
     * 批量更新
     * <p>
     * 例如：
     * <update id="batchUpdateStudentWithMap" parameterType="java.util.Map" >
     * UPDATE STUDENT SET name = #{name} WHERE id IN
     * <foreach collection="idList" index="index" item="item" open="(" separator="," close=")">
     * #{item}
     * </foreach>
     * </update>
     *
     * @param stmtId  SQL的id
     * @param listMap map参数的集合
     * @return
     */
    public int batchUpdate(String stmtId, Collection<Map<String, Object>> listMap) {
        int x = 0;

        Map map;
        for (Iterator var4 = listMap.iterator(); var4.hasNext(); x += sqlSession.update(this.sqlmapNamespace + "." + stmtId, map)) {
            map = (Map) var4.next();
        }

        return x;
    }

    /**
     * 批量删除
     * <p>
     * 例如：
     * <delete id="batchDelete" parameterType="java.util.List">
     * DELETE FROM STUDENT WHERE id IN
     * <foreach collection="keys" index="index" item="key" open="(" separator="," close=")">
     * #{key}
     * </foreach>
     * </delete>
     *
     * @param keySet
     * @return
     */
    public int batchDelete(Collection<PK> keySet) {
        int x = 0;

        Serializable pk;
        for (Iterator var3 = keySet.iterator(); var3.hasNext(); x += sqlSession.update(this.sqlmapNamespace + "." + SQLID_DELETE, pk)) {
            pk = (Serializable) var3.next();
        }

        return x;
    }


    /**
     * 根据 map参数查询一个列表数据
     *
     * @param params Map类型的参数
     * @return
     */
    public List<E> query(Map<String, Object> params) {
        return sqlSession.selectList(this.sqlmapNamespace + "." + SQLID_QUERY, params);
    }

    /**
     * 根据主键查询一条实体
     *
     * @param pk 主键id值
     * @return
     */
    @Override
    public E load(PK pk) {
        return (E) sqlSession.selectOne(this.sqlmapNamespace + "." + SQLID_LOAD, pk);
    }


    /**
     * 根据map参数获得一条实体
     *
     * @param params map参数
     * @return
     */
    public E load(Map<String, Object> params) {
        List<E> list = this.query(params, 0, 1);
        if (list.size() == 0) return null;
        return list.get(0);
    }

    /**
     * 根据map参数统计记录条数
     *
     * @param params map 参数
     * @return
     */
    public int count(Map<String, Object> params) {
        return ((Integer) sqlSession.selectOne(this.sqlmapNamespace + "." + SQLID_COUNT, params)).intValue();
    }


    /**
     * 分页查询集合
     *
     * @param params   Map类型的参数
     * @param startRow 起始行数（不含起始行的数据）
     * @param rowSize  要查询记录数
     * @return
     */
    public List<E> query(Map<String, Object> params, int startRow, int rowSize) {
        return sqlSession.selectList(this.sqlmapNamespace + "." + SQLID_QUERY, params, new RowBounds(startRow, rowSize));
    }

    /**
     * 根据 stmtid 查询一个集合
     *
     * @param stmtId SQL的id
     * @param params Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @param <T>
     * @return
     */
    public <T> List<T> query(String stmtId, Map<String, Object> params) {
        return sqlSession.selectList(this.sqlmapNamespace + "." + stmtId, params);
    }

    /**
     * 根据stmtid 进行统计条数
     *
     * @param stmtId SQL的id
     * @param params Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @return
     */
    public int count(String stmtId, Map<String, Object> params) {
        return ((Integer) sqlSession.selectOne(this.sqlmapNamespace + "." + stmtId, params)).intValue();
    }

    /**
     * 根据stmtid 进行分页查询
     *
     * @param stmtId   SQL的id
     * @param params   Map类型的参数，如果没有查询条件约束，则传递null也可以
     * @param startRow 开始条数
     * @param rowSize  向后查几条
     * @param <T>
     * @return
     */
    public <T> List<T> query(String stmtId, Map<String, Object> params, int startRow, int rowSize) {
        return sqlSession.selectList(this.sqlmapNamespace + "." + stmtId, params, new RowBounds(startRow, rowSize));
    }

}
