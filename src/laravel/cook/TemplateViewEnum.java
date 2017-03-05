package laravel.cook;

/**
 * Created by Clezio on 04/03/2017.
 */
public enum TemplateViewEnum {

    INDEX_BLADE("index.blade"),
    CREATE_BLADE("create.blade"),
    EDIT_BLADE("edit.blade"),
    DETAILS_BLADE("details.blade");

    private final String valor;

    TemplateViewEnum(String valorOpcao){
        valor = valorOpcao;
    }
    public String getValor(){
        return valor;
    }
}
