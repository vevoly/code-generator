<#include "/macro.include"/>
<#include "/java_copyright.include">
<#assign className = table.className>
<#assign classNameLower = className?uncap_first>
package ${basepackage}.vo.query;

import com.aixin.core.entity.Page;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

public class ${className}Query extends Page implements Serializable {
    private static final long serialVersionUID = 3148176768559230877L;

	<@generateFields/>
	<@generateProperties/>

	public String toString() {
		return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE);
	}

}

<#macro generateFields>

	<#list table.columns as column>
	/** ${column.columnAlias} */
	<#if column.isDateTimeColumn && !column.contains("begin,start,end")>
	private ${column.javaType} ${column.columnNameLower}Begin;
	private ${column.javaType} ${column.columnNameLower}End;
	</#if>
	private ${column.javaType} ${column.columnNameLower};
	</#list>

</#macro>

<#macro generateProperties>
	<#list table.columns as column>
	<#if column.isDateTimeColumn && !column.contains("begin,start,end")>
	public ${column.javaType} get${column.columnName}Begin() {
		return this.${column.columnNameLower}Begin;
	}

	public void set${column.columnName}Begin(${column.javaType} value) {
		this.${column.columnNameLower}Begin = value;
	}

	public ${column.javaType} get${column.columnName}End() {
		return this.${column.columnNameLower}End;
	}

	public void set${column.columnName}End(${column.javaType} value) {
		this.${column.columnNameLower}End = value;
	}

	<#else>
	public ${column.javaType} get${column.columnName}() {
		return this.${column.columnNameLower};
	}

	public void set${column.columnName}(${column.javaType} value) {
		this.${column.columnNameLower} = value;
	}

	</#if>
	</#list>
</#macro>



