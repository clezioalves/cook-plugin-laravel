# cook-plugin-laravel

----
## O que é?
> Mais um plugin do projeto [Cook](https://github.com/itakenami/cook) para geração de código fonte CRUD de forma simples para projetos criados com o framework [Laravel](https://laravel.com/) (>= 5.3). A construção é baseada em entidades do banco de dados, então basta apenas que seja realizada a configuração de conexão no arquivo ".env" do seu projeto [Laravel](https://laravel.com/).

----
## Como usar?
>Baixe o [Cook](https://github.com/itakenami/cook) e crie a variável de ambiente de sistema "COOK_HOME" apontando para pasta "bin" do COOK:
Ex: COOK_HOME = C:\dev\cook\bin
>Complemente a variável de sistema Path com a variável criada adicionando %COOK_HOME% no final das variáveis existentes.

>Com o [Cook](https://github.com/itakenami/cook) instalado e configurado, é possível utilizar os comandos:

```
cook install laravel
```
para instalar o plugin,
```
cook laravel model
```
para criar os models,
```
cook laravel controller-resource
```
para criar os controllers RestFul,
```
cook laravel controller
```
para criar os controllers e 
```
cook laravel template
```
para criar as views.

>Observação: Para criação dos controllers é necessário fazer a escolha entre controller e controller-resource
