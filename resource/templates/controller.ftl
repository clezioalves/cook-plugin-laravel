<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\${modelDesign.getModelName()};
use Illuminate\Support\Facades\Session;
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
        $${modelDesign.getModelNameVariableList()} = ${modelDesign.getModelName()}::all();
        return view('${modelDesign.getResourceName()}.index',['${modelDesign.getModelNameVariableList()}' => $${modelDesign.getModelNameVariableList()}]);
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        return view('${modelDesign.getResourceName()}.create');
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $this->validate($request, ${modelDesign.getModelName()}::$insertRules);
        $input = $request->all();
        ${modelDesign.getModelName()}::create($input);
        Session::flash('flash_message', 'Registro incluído com sucesso!');
        return redirect('${modelDesign.getResourceName()}');
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
        return view('${modelDesign.getResourceName()}.details',['${modelDesign.getModelNameVariable()}' => $${modelDesign.getModelNameVariable()}]);
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
        return view('${modelDesign.getResourceName()}.edit',['${modelDesign.getModelNameVariable()}' => $${modelDesign.getModelNameVariable()}]);
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
        $this->validate($request, ${modelDesign.getModelName()}::$updateRules);
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
        $input = $request->all();
        $${modelDesign.getModelNameVariable()}->fill($input)->save();
        Session::flash('flash_message', 'Registro alterado salvo com sucesso!');
        return redirect('${modelDesign.getResourceName()}');
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
        Session::flash('flash_message', 'Registro excluído com sucesso!');
        return redirect()->route('${modelDesign.getResourceName()}.index');
    }
}
