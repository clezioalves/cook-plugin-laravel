@extends('app')
@section('title', '${modelDesign.getModelNameHumanize()}')
@section('content')

<a class="btn btn-primary" href="{{ url('${modelDesign.getResourceName()}/create') }}">
    <span class="glyphicon glyphicon-plus"></span>
    ${modelDesign.getModelNameHumanizeSingularize()}
</a>

<!-- /.panel-heading -->
<div class="panel-body">
    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
            <tr>
                <#list modelDesign.getAttributeList() as attribute>
                <#if attribute.getName() == modelDesign.getDisplayField() || attribute.getName() == modelDesign.getPrimaryKey()>
                <th<#if attribute_index == 0> width="1%"</#if>>${attribute.getNameHumanize()}</th>
                </#if>
                </#list>
                <#list modelDesign.getManyToOneList() as modelRelation>
                <th>${modelRelation.getModelNameHumanizeSingularize()}</th>
                </#list>
                <th width="25%"><#if lang == 'en'>Action<#else>Ação</#if></th>
            </tr>
            </thead>
            <tbody>
            @foreach($${modelDesign.getModelNameVariableList()} as $${modelDesign.getModelNameVariable()})
            <tr>
                <#list modelDesign.getAttributeList() as attribute>
                <#if attribute.getName() == modelDesign.getDisplayField()>
                <td><a href="{{ route('${modelDesign.getResourceName()}.show', $${modelDesign.getModelNameVariable()}->${modelDesign.getPrimaryKey()}) }} ">{{ $${modelDesign.getModelNameVariable()}->${attribute.getName()} }}</a></td>
                <#elseif attribute.getName() == modelDesign.getPrimaryKey()>
                <td>{{ $${modelDesign.getModelNameVariable()}->${attribute.getName()} }}</td>
                </#if>
                </#list>
                <#list modelDesign.getManyToOneList() as modelRelation>
                <td>{{ $${modelDesign.getModelNameVariable()}->${modelRelation.getModelNameVariable()}->${modelRelation.getDisplayField()} }}</td>
                </#list>
                <td>
                    <a class="btn btn-success" href="{{ route('${modelDesign.getResourceName()}.edit', $${modelDesign.getModelNameVariable()}->${modelDesign.getPrimaryKey()}) }}" style="float: left;">
                        <span class="glyphicon glyphicon-edit" aria-hidden="true"></span>
                        <#if lang == 'en'>Edit<#else>Editar</#if>
                    </a>
                    <form action="{{ route('${modelDesign.getResourceName()}.destroy', $${modelDesign.getModelNameVariable()}->${modelDesign.getPrimaryKey()}) }}" method="POST" style="float: left; margin-left: 5px;">
                        <input type="hidden" name="_method" value="delete">
                        <input type="hidden" name="_token" value="{{ csrf_token() }}">
                        <button type="submit" class="btn btn-danger">
                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>
                            <#if lang == 'en'>Delete<#else>Excluir</#if>
                        </button>
                    </form>
                </td>
            </tr>
            @endforeach
            </tbody>
        </table>
        {{ $${modelDesign.getModelNameVariableList()}->links() }}
    </div>
    <!-- /.table-responsive -->
</div>
@endsection