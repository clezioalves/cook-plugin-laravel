@extends('app')
@section('title', '${modelDesign.getModelNameHumanize()}')
@section('content')

<a class="btn btn-primary" href="{{ url('${modelDesign.getResourceName()}') }}">
    <span class="glyphicon glyphicon-arrow-left"></span>
    <#if lang == 'en'>Back<#else>Voltar</#if>
</a>

<fieldset>
    <legend><i class="fa fa-cloud-download"></i> ${modelDesign.getModelNameHumanizeSingularize()}</legend>
</fieldset>

<div class="container">
    <div class="row">
        <div class="col">
            <dl class="dl-horizontal">
                <#list modelDesign.getAttributeList() as attribute>
                <div class="form-group">
                    <dt>${attribute.getNameHumanize()}</dt>
                    <#if attribute.isDateType()>
                    <dd>{!! $${modelDesign.getModelNameVariable()}->${attribute.getName()}->format('<#if lang == "en">Y-m-d<#else>d/m/Y</#if>') !!}</dd>
                    <#else>
                    <dd>{!! $${modelDesign.getModelNameVariable()}->${attribute.getName()} !!}</dd>
                    </#if>
                </div>
                </#list>
                <#list modelDesign.getManyToOneList() as modelRelation>
                <div class="form-group">
                    <dt>${modelRelation.getModelNameHumanizeSingularize()}</dt>
                    <dd>{{ $${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariable()}->${modelRelation.getDisplayField()} }}</dd>
                </div>
                </#list>
                <#list modelDesign.getManyToManyList() as modelRelation>
                @if(!$${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariableList()}->isEmpty())
                    <div class="form-group">
                        <dt>${modelRelation.getModelNameHumanize()}</dt>
                        <dd>
                            <ul class="list-group">
                                @foreach ($${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariableList()} as $${modelRelation.getModelNameVariable()})
                                <li class="list-group-item">{{ $${modelRelation.getModelNameVariable()}->${modelRelation.getDisplayField()} }}</li>
                                @endforeach
                            </ul>
                        </dd>
                    </div>
                @endif
                </#list>
                <#list modelDesign.getOneToManyList() as modelRelation>
                @if(!$${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariableList()}->isEmpty())
                    <div class="form-group">
                        <dt>${modelRelation.getModelNameHumanize()}</dt>
                        <dd>
                            <ul class="list-group">
                                @foreach ($${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariableList()} as $${modelRelation.getModelNameVariable()})
                                <li class="list-group-item">{{ $${modelRelation.getModelNameVariable()}->${modelRelation.getDisplayField()} }}</li>
                                @endforeach
                            </ul>
                        </dd>
                    </div>
                @endif
                </#list>
            </dl>
        </div>
    </div>
</div>
@endsection