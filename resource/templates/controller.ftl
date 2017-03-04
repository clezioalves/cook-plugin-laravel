<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\${modelDesign.getModelName()};
use Illuminate\Support\Facades\Session;
use App\Http\Requests\${formRequestNameStore};
use App\Http\Requests\${formRequestNameUpdate};
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
        $${modelDesign.getModelNameVariableList()} = ${modelDesign.getModelName()}::paginate(10);
        return view('${modelDesign.getResourceName()}.index',['${modelDesign.getModelNameVariableList()}' => $${modelDesign.getModelNameVariableList()}]);
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        <#list modelDesign.getOneToManyList() as modelRelation>
        $${modelRelation.getModelNameVariableList()} = ${modelRelation.getModelName()}::pluck(${modelRelation.getModelName()}::$displayField,'${modelRelation.getPrimaryKey().getName()}')->toArray();
        </#list>
        <#list modelDesign.getOneToOneList() as modelRelation>
        $${modelRelation.getModelNameVariableList()} = ${modelRelation.getModelName()}::pluck(${modelRelation.getModelName()}::$displayField,'${modelRelation.getPrimaryKey().getName()}')->toArray();
        </#list>
        <#list modelDesign.getManyToManyList() as modelRelation>
        $${modelRelation.getModelNameVariableList()} = ${modelRelation.getModelName()}::pluck(${modelRelation.getModelName()}::$displayField,'${modelRelation.getPrimaryKey().getName()}')->toArray();
        </#list>
        <#if modelDesign.getOneToManyList()?size == 0 && modelDesign.getOneToOneList()?size == 0 && modelDesign.getManyToManyList()?size == 0>
        return view('${modelDesign.getResourceName()}.create');
        <#else>
        $view = view('${modelDesign.getResourceName()}.create');
        <#list modelDesign.getOneToManyList() as modelRelation>
        $view->with(compact('${modelRelation.getModelNameVariableList()}'));
        </#list>
        <#list modelDesign.getOneToOneList() as modelRelation>
        $view->with(compact('${modelRelation.getModelNameVariableList()}'));
        </#list>
        <#list modelDesign.getManyToManyList() as modelRelation>
        $view->with(compact('${modelRelation.getModelNameVariableList()}'));
        </#list>
        return $view;
        </#if>
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(${formRequestNameStore} $request)
    {
        $input = $request->all();
        $${modelDesign.getModelNameVariable()} = new ${modelDesign.getModelName()};
        $${modelDesign.getModelNameVariable()}->fill($input);
        <#list modelDesign.getManyToOneList() as modelRelation>
        $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->associate(${modelRelation.getModelName()}::findOrFail($input['${modelRelation.getModelNameVariable()}']));
        </#list>
        $${modelDesign.getModelNameVariable()}->save();
        <#list modelDesign.getManyToManyList() as modelRelation>
        if ($input['${modelRelation.getModelNameVariableList()}'])
        {
            $${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariableList()}()->sync($input['${modelRelation.getModelNameVariableList()}']);
        }
        </#list>
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
        <#if modelDesign.getManyToOneList()?size == 0>
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
        <#else>
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::<#list modelDesign.getManyToOneList() as modelRelation>with('${modelRelation.getModelNameVariable()}')-></#list>findOrFail($id);
        </#if>
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
        <#list modelDesign.getManyToOneList() as modelRelation>
        $${modelRelation.getModelNameVariableList()} = ${modelRelation.getModelName()}::pluck(${modelRelation.getModelName()}::$displayField,'${modelRelation.getPrimaryKey().getName()}')->toArray();
        </#list>
        <#if modelDesign.getManyToOneList()?size == 0>
        return view('${modelDesign.getResourceName()}.edit')->with('${modelDesign.getModelNameVariable()}', $${modelDesign.getModelNameVariable()});
        <#else>
        return view('${modelDesign.getResourceName()}.edit')->with('${modelDesign.getModelNameVariable()}', $${modelDesign.getModelNameVariable()})
        <#list modelDesign.getManyToOneList() as modelRelation>
            ->with('${modelRelation.getModelNameVariableList()}', $${modelRelation.getModelNameVariableList()})<#if (modelRelation_index + 1) == modelDesign.getManyToOneList()?size>;</#if>
        </#list>
        </#if>
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(${formRequestNameUpdate} $request, $id)
    {
        $${modelDesign.getModelNameVariable()} = ${modelDesign.getModelName()}::findOrFail($id);
        $input = $request->all();
        $${modelDesign.getModelNameVariable()}->fill($input);
        <#list modelDesign.getManyToOneList() as modelRelation>
        $${modelDesign.getModelNameVariable()}->${modelRelation.getColumnName()}()->associate(${modelRelation.getModelName()}::findOrFail($input['${modelRelation.getColumnName()}']));
        </#list>
        <#list modelDesign.getManyToManyList() as modelRelation>
        if ($input['${modelRelation.getModelNameVariableList()}'])
        {
            $${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariableList()}()->sync($input['${modelRelation.getModelNameVariableList()}']);
        }
        </#list>
        $${modelDesign.getModelNameVariable()}->save();
        Session::flash('flash_message', 'Registro alterado com sucesso!');
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
