<#function getMathcName path>
	<#return path?upper_case?replace(".", "_")?replace(" + \"/#\"", "_ITEM")>
</#function>

<#macro addInsertBeforeTrigger uri>
	<#if !uri.item && !uri.onlyQuery && uri.triggered>
	
		<#list uri.triggers as trigger>
			<#if trigger.insert && trigger.before>
		if(${uri.tableLink}.equals(table)){
			on${trigger.methodName?cap_first}BeforeInserted(values);
		}			
			</#if>
		</#list>
	</#if>
</#macro>

<#macro addInsertAfterTrigger uri>
	<#if !uri.item && !uri.onlyQuery && uri.triggered>
	
		<#list uri.triggers as trigger>
			<#if trigger.insert && trigger.after>
		if(${uri.tableLink}.equals(table)){
			on${trigger.methodName?cap_first}AfterInserted(values);
		}			
			</#if>
		</#list>
	</#if>
</#macro>

<#macro addUpdateBeforeTrigger uri>
	<#if !uri.onlyQuery && uri.triggered>
	
		<#list uri.triggers as trigger>
			<#if trigger.update && trigger.before>
		if(${uri.tableLink}.equals(table)){
			on${trigger.methodName?cap_first}BeforeUpdated(uri, values, selection, selectionArgs);
		}			
			</#if>
		</#list>
	</#if>
</#macro>

<#macro addUpdateAfterTrigger uri>
	<#if !uri.onlyQuery && uri.triggered>
	
		<#list uri.triggers as trigger>
			<#if trigger.update && trigger.after>
		if(${uri.tableLink}.equals(table)){
			on${trigger.methodName?cap_first}AfterUpdated(uri, values, selection, selectionArgs);
		}			
			</#if>
		</#list>
	</#if>
</#macro>

<#macro addDeleteBeforeTrigger uri>
	<#if !uri.onlyQuery && uri.triggered>
	
		<#list uri.triggers as trigger>
			<#if trigger.delete && trigger.before>
		if(${uri.tableLink}.equals(table)){
			on${trigger.methodName?cap_first}BeforeDeleted(uri, selection, selectionArgs);
		}			
			</#if>
		</#list>
	</#if>
</#macro>

<#macro addDeleteAfterTrigger uri>
	<#if !uri.onlyQuery && uri.triggered>
	
		<#list uri.triggers as trigger>
			<#if trigger.delete && trigger.after>
		if(${uri.tableLink}.equals(table)){
			on${trigger.methodName?cap_first}AfterDeleted(uri, selection, selectionArgs);
		}			
			</#if>
		</#list>
	</#if>
</#macro>