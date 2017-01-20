<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ${tableDesign.getNameModelize()} extends Model {

    protected $table = '${tableDesign.getName()}';

    <#if !tableDesign.getTimestamps()>
    public $timestamps = false;
    </#if>

    public static $rulesInsert = array(
        //'field' => 'required|max:50',
    <#if tableDesign.getRuleAttributeNameList()?size != 0>
        <#list tableDesign.getRuleAttributeNameList() as ruleAttributeName>
        '${ruleAttributeName.getAttributeName()}' => '${ruleAttributeName.getRules()}',
        </#list>
    </#if>
    );

    public static $rulesUpdate = array(
        //'field' => 'required|max:50',
    <#if tableDesign.getRuleAttributeNameList()?size != 0>
        <#list tableDesign.getRuleAttributeNameList() as ruleAttributeName>
        '${ruleAttributeName.getAttributeName()}' => '${ruleAttributeName.getRules()}',
        </#list>
    </#if>
    );

    <#list tableDesign.getManyToOneList() as attr>
    /**
     * Get the ${attr.getTableNameSingularize()} record associated with the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getTableNameSingularize()}(){
        return $this->belongsTo('App\Models\${attr.getTableNameModelize()}');
    }

    </#list>
    <#list tableDesign.getOneToOneList() as attr>
    /**
     * Get the ${attr.getTableNameSingularize()} record associated with the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getTableNameSingularize()}(){
        return $this->belongsTo('App\Models\${attr.getTableNameModelize()}');
    }

    </#list>
    <#list tableDesign.getOneToManyList() as attr>
    /**
     * Get the ${attr.getTableNameCollections()} for the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getTableNameCollections()}(){
        return $this->hasMany('App\Models\${attr.getTableNameModelize()}');
    }

    </#list>
    <#list tableDesign.getManyToManyList() as attr>
    /**
     * The ${attr.getManyToOne().getTableNameCollections()} that belong to the ${tableDesign.getNameSingularize()}.
     */
    public function ${attr.getManyToOne().getTableNameCollections()}(){
        return $this->belongsToMany('App\Models\${attr.getManyToOne().getTableNameModelize()}');
    }

    </#list>
}