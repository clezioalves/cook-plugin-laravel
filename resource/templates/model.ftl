<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ${tableDesign.getNameModelize()} extends Model {

    protected $table = '${tableDesign.getName()}';
    
    <#if tableDesign.getRuleAttributeNameList()?size != 0>
    public static $rulesInsert = array(
        <#list tableDesign.getRuleAttributeNameList() as ruleAttributeName>
        '${ruleAttributeName.getAttributeName()}' => '${ruleAttributeName.getRules()}',
        </#list>
    );

    public static $rulesUpdate = array(
        <#list tableDesign.getRuleAttributeNameList() as ruleAttributeName>
        '${ruleAttributeName.getAttributeName()}' => '${ruleAttributeName.getRules()}',
        </#list>
    );
    </#if>

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