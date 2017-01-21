<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\${modelDesign.getModelName()};
use Illuminate\Database\Eloquent\ModelNotFoundException;

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
        foreach (get_object_vars($${modelDesign.getModelNameVariable()}Dto) as $key => $value)
        {
            if($key != 'created_at' && $key != 'updated_at')
            {
                $${modelDesign.getModelNameVariable()}->$key = $value;
            }
        }
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
        try{
            $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
            return $${modelDesign.getModelNameVariable()};
        }catch(ModelNotFoundException $ex){
            return response()->json($ex->getMessage(), 422);
        }
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
        try{
            $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
            foreach (get_object_vars($${modelDesign.getModelNameVariable()}Dto) as $key => $value)
            {
                if($key != 'created_at' && $key != 'updated_at'<#if modelDesign.getPrimaryKey()??> && $key != '${modelDesign.getPrimaryKey().getName()}' </#if>)
                {
                    $${modelDesign.getModelNameVariable()}->$key = $value;
                }
            }
            $${modelDesign.getModelNameVariable()}->save();
            return response()->json($${modelDesign.getModelNameVariable()},201);
        }catch(ModelNotFoundException $ex){
            return response()->json($ex->getMessage(), 422);
        }
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        try{
            $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
            $${modelDesign.getModelNameVariable()}->delete();
            return response()->json($${modelDesign.getModelNameVariable()},204);
        }catch(ModelNotFoundException $ex){
            return response()->json($ex->getMessage(), 422);
        }
    }
}
