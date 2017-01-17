<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\${modelDesign.getModelName()};
use Illuminate\Database\QueryException;

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
        $${modelDesign.getModelNameVariable()}->${attribute.getName()} = $${modelDesign.getModelNameVariable()}Dto->${attribute.getName()};
        </#list>
        $${modelDesign.getModelNameVariable()}->save();
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
        $${modelDesign.getModelNameVariable()}->${attribute.getName()} = $${modelDesign.getModelNameVariable()}Dto->${attribute.getName()};
        </#list>
        $${modelDesign.getModelNameVariable()}->save();
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
