<#include "/java_copyright.include">
<#assign className = table.className>   
<#assign classNameLower = className?uncap_first>   
package ${basepackage}.dao;

import com.aixin.core.base.BaseMybatisDAOImpl;
import com.aixin.elder.entity.${className};
import org.springframework.stereotype.Repository;


@Repository
public class ${className}DAO extends BaseMybatisDAOImpl<${className},${table.idColumn.javaType}>{

	<#list table.columns as column>
	<#if column.unique && !column.pk>
	public ${className} load${column.columnName}(${column.javaType} v) {
		return (${className})getSqlSessionTemplate().selectOne("${className}.load${column.columnName}",v);
	}	
	</#if>
	</#list>

}
