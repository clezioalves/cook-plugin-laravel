<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\${modelDesign.getModelName()};
<#list modelDesign.getListaModelImports() as modelName>
use App\Models\${modelName};
</#list>

class ${modelDesign.getControllerName()} extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        return ${modelDesign.getModelName()}::all();
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $this->validate($request, ${modelDesign.getModelName()}::$rulesInsert);
        $${modelDesign.getModelNameVariable()}Dto = json_decode($request->getContent());
        $${modelDesign.getModelNameVariable()} = new ${modelDesign.getModelName()};
        <#list modelDesign.getAttributeList() as attribute>
        <#if attribute.getName() != "id" && attribute.getName() != "created_at" && attribute.getName() != "updated_at">
        $${modelDesign.getModelNameVariable()}->${attribute.getName()} = $${modelDesign.getModelNameVariable()}Dto->${attribute.getName()};
        </#if>
        </#list>
        <#list modelDesign.getOneToManyList() as modelRelation>
        $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->associate(${modelRelation.getModelName()}::find($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getColumnName()}->id));
        </#list>
        $${modelDesign.getModelNameVariable()}->save();
        <#list modelDesign.getOneToOneList() as modelRelation>
        $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()} = ${modelRelation.getModelName()}::find($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariable()}->id);
        </#list>
        <#list modelDesign.getManyToManyList() as modelRelation>
        if(isset($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariableList()})){
            foreach ($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariableList()} as $${modelRelation.getModelNameVariable()}Dto){
                $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->save(${modelRelation.getModelName()}::find($${modelRelation.getModelNameVariable()}Dto->id));
            }
        }
        </#list>
        <#list modelDesign.getManyToOneList() as modelRelation>
        foreach ($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariableList()} as $${modelRelation.getModelNameVariable()}Dto){
            //$${modelRelation.getModelNameVariable()} = new ${modelRelation.getModelName()}();
            //$${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->save($${modelRelation.getModelNameVariable()});
        }
        </#list>
        return response()->json($${modelDesign.getModelNameVariable()},201);
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
        return $${modelDesign.getModelNameVariable()};
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        $this->validate($request, ${modelDesign.getModelName()}::$rulesUpdate);
        $${modelDesign.getModelNameVariable()}Dto = json_decode($request->getContent());
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
        <#list modelDesign.getAttributeList() as attribute>
        <#if attribute.getName() != "id" && attribute.getName() != "created_at" && attribute.getName() != "updated_at">
        $${modelDesign.getModelNameVariable()}->${attribute.getName()} = $${modelDesign.getModelNameVariable()}Dto->${attribute.getName()};
        </#if>
        </#list>
        <#list modelDesign.getOneToManyList() as modelRelation>
        $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->associate(${modelRelation.getModelName()}::find($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getColumnName()}->id));
        </#list>
        $${modelDesign.getModelNameVariable()}->save();
        <#list modelDesign.getOneToOneList() as modelRelation>
        $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()} = ${modelRelation.getModelName()}::find($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariable()}->id);
        </#list>
        <#list modelDesign.getManyToManyList() as modelRelation>
        if(isset($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariableList()})){
            foreach ($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariableList()} as $${modelRelation.getModelNameVariable()}Dto){
                $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->save(${modelRelation.getModelName()}::find($${modelRelation.getModelNameVariable()}Dto->id));
            }
        }
        </#list>
        <#list modelDesign.getManyToOneList() as modelRelation>
        foreach ($${modelDesign.getModelNameVariable()}Dto->${modelRelation.getModelNameVariableList()} as $${modelRelation.getModelNameVariable()}Dto){
            //$${modelRelation.getModelNameVariable()} = new ${modelRelation.getModelName()}();
            //$${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->save($${modelRelation.getModelNameVariable()});
        }
        </#list>
        return response()->json($${modelDesign.getModelNameVariable()},201);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
        $${modelDesign.getModelNameVariable()}->delete();
        return response()->json($${modelDesign.getModelNameVariable()},204);
    }
}
