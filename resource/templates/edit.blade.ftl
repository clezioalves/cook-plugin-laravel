@extends('app')
@section('title', '${modelDesign.getModelNameHumanize()}')
@section('content')

<a class="btn btn-primary" href="{{ url('${modelDesign.getResourceName()}') }}">
    <span class="glyphicon glyphicon-arrow-left"></span>
    Voltar
</a>

<fieldset>
    <legend><i class="fa fa-cloud-download"></i> ${modelDesign.getModelNameHumanizeSingularize()}</legend>
</fieldset>

<div class="container">
    <div class="row">
        <div class="col">
            {!! Form::open(['url' => '${modelDesign.getResourceName()}/'.$${modelDesign.getModelNameVariable()}->${modelDesign.getPrimaryKey()},'method'=>'PUT']) !!}
            <#list modelDesign.getAttributeList() as attribute>
            <#if attribute.getName() != modelDesign.getPrimaryKey()>
            <div class="form-group">
                {!! Form::label('${attribute.getName()}', '${attribute.getNameHumanize()}') !!}
                <#if attribute.isDateType()>
                {!! Form::date('${attribute.getName()}', old( '${attribute.getName()}', $${modelDesign.getModelNameVariable()}->${attribute.getName()}), ['class'=>'form-control']); !!}
                <#elseif 100 < attribute.getMaxLenght()>
                {!! Form::textarea('${attribute.getName()}', old( '${attribute.getName()}', $${modelDesign.getModelNameVariable()}->${attribute.getName()}), ['class'=>'form-control','maxlength'=>${attribute.getMaxLenght()},'rows'=>3]); !!}
                <#else>
                {!! Form::text('${attribute.getName()}', old( '${attribute.getName()}', $${modelDesign.getModelNameVariable()}->${attribute.getName()}), ['class'=>'form-control','maxlength'=>${attribute.getMaxLenght()}]); !!}
                </#if>
            </div>
            </#if>
            </#list>
            <#list modelDesign.getManyToOneList() as modelRelation>
            <div class="form-group">
                {!! Form::label('${modelRelation.getModelNameVariable()}', '${modelRelation.getModelNameHumanize()}') !!}
                {!! Form::select('${modelRelation.getModelNameVariable()}', $${modelRelation.getModelNameVariableList()}, old( '${modelRelation.getModelNameVariable()}', $${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariable()}->${modelRelation.getPrimaryKey()}),['placeholder' => 'Selecione','class'=>'form-control']); !!}
            </div>
            </#list>
            <#list modelDesign.getManyToManyList() as modelRelation>
            <div class="form-group">
                {!! Form::label('${modelRelation.getModelNameVariableList()}[]', '${modelRelation.getModelNameHumanize()}') !!}
                {!! Form::select('${modelRelation.getModelNameVariableList()}[]', $${modelRelation.getModelNameVariableList()}, $${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariableList()}->pluck('${modelRelation.getPrimaryKey()}')->toArray(), ['multiple'=>'multiple','class'=>'form-control']); !!}
            </div>
            </#list>
            {!! Form::submit('Salvar',['class'=>'btn btn-primary']) !!}
            {!! Form::close() !!}
        </div>
    </div>
</div>
@endsection