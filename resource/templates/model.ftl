<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ${tableDesign.getNameModelize()} extends Model {

    protected $table = '${tableDesign.getName()}';
    <#if tableDesign.getPrimaryKey()?? && tableDesign.getPrimaryKey().getName() != 'id'>
    protected $primaryKey = '${tableDesign.getPrimaryKey().getName()}';
    </#if>
    <#if tableDesign.getFillableAttributeNameList()?size != 0>
    protected $fillable = [
        <#list tableDesign.getFillableAttributeNameList() as fillableAttributeName>
        '${fillableAttributeName}'<#if (fillableAttributeName_index + 1) < tableDesign.getFillableAttributeNameList()?size>,</#if>
        </#list>
    ];
    </#if>
    <#if !tableDesign.getTimestamps()>
    public $timestamps = false;
    </#if>

    public static $insertRules = array(
        //'field' => 'required|max:50',
    <#if tableDesign.getRuleAttributeNameList()?size != 0>
        <#list tableDesign.getRuleAttributeNameList() as ruleAttributeName>
        '${ruleAttributeName.getAttributeName()}' => '${ruleAttributeName.getRules()}',
        </#list>
    </#if>
    );

    public static $updateRules = array(
        //'field' => 'required|max:50',
    <#if tableDesign.getRuleAttributeNameList()?size != 0>
        <#list tableDesign.getRuleAttributeNameList() as ruleAttributeName>
        '${ruleAttributeName.getAttributeName()}' => '${ruleAttributeName.getRules()}',
        </#list>
    </#if>
    );

    <#list tableDesign.getManyToOneList() as attr>
    /**
     * Get the ${attr.getTableNameVariable()} record associated with the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getTableNameVariable()}(){
        return $this->belongsTo('App\Models\${attr.getTableNameModelize()}','${attr.getColumnName()}','${attr.getPkColumnName()}');
    }

    </#list>
    <#list tableDesign.getOneToOneList() as attr>
    /**
     * Get the ${attr.getTableNameVariable()} record associated with the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getTableNameVariable()}(){
        return $this->belongsTo('App\Models\${attr.getTableNameModelize()}','${attr.getPkColumnName()}','${attr.getColumnName()}');
    }

    </#list>
    <#list tableDesign.getOneToManyList() as attr>
    /**
     * Get the ${attr.getTableNameCollections()} for the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getTableNameCollections()}(){
        return $this->hasMany('App\Models\${attr.getTableNameModelize()}','${attr.getPkColumnName()}','${attr.getColumnName()}');
    }

    </#list>
    <#list tableDesign.getManyToManyList() as attr>
    /**
     * The ${attr.getManyToOne().getTableNameCollections()} that belong to the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getManyToOne().getTableNameCollections()}(){
        return $this->belongsToMany('App\Models\${attr.getManyToOne().getTableNameModelize()}','${attr.getManyToOne().getFkTableName()}','${attr.getColumnName()}','${attr.getManyToOne().getColumnName()}');
    }

    </#list>
}