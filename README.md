# Sobre o Projeto

O projeto é uma aplicação criada para fins de controle financeiro, desenvolvido nos padrões de projetos MVC.

# Endpoints

 Método | Path                                  | Descrição                                         
--------|---------------------------------------|---------------------------------------------------
 GET    | /api/usuarios/{id}/saldo              | Obter saldo de um usuário (Com autenticação)  
 POST   | /api/usuarios                         | Cadastrar um novo usuário (Sem autenticação)      
 POST   | /api/usuarios/autenticar              | Autenticar-se no sistema (Sem autenticação)
 GET    | /api/lancametos                       | Listar lançamentos cadastrados (Com autenticação) 
 GET    | /api/lancametos/{id}                  | Obter um lançamento especifico (Com autenticação)
 GET    | /api/lancamentos/download             | Fazer download de uma lista de lançamentos filtrada (Com autenticação)
 POST   | /api/lancamentos                      | Cadastrar um novo lançamento (Com autenticação)
 POST   | /api/lancamentos/upload               | Cadastrar lançamentos a partir de um arquivo CSV (Com autenticação)
 PUT    | /api/lancamentos/{id}                 | Atualizar um lançamento (Com autenticação)       
 PUT    | /api/lancamentos/{id}/atualiza-status | Atualizar o status de um lançamento (Com autenticação)     
 DELETE | /api/lancamentos/{id}                 | Excluir um lançamento (Com autenticação)
 GET    | /api/categorias                       | Listar categorias cadastradas (Com autenticação) 
 POST   | /api/categorias                       | Cadastrar uma nova categoria (Com autenticação)

# Como Usar

Para utilizar esta API em ambiente local você precisara ter já instalado na sua máquina os seguintes pacotes:

- Java 1.8
- Git
- PostgreSQL 9.4

Caso não tenha eles instalados recomento acessar suas documentações para a instalação de ambos, agora com eles instalados em sua máquina vamos para o passo a passo.

### Configuração do banco de dados

Crie abra o gerenciador de banco de dados de sua preferencia e crie o banco "myfinances" utilizando PostgreSQL com o comando:

` CREATE DATABASE myfinacnes `

Após isso execute este script dentro dele:

```
CREATE TABLE finances.user (
id bigserial NOT NULL,
"name" varchar(150) NULL,
email varchar(100) NULL,
"password" varchar(255) NULL,
registration_date date DEFAULT now() NULL,
CONSTRAINT user_pkey PRIMARY KEY (id)
);

CREATE TABLE finances.entry (
id bigserial NOT NULL,
description varchar(100) NULL,
"month" int4 NOT NULL,
"year" int4 NOT NULL,
value numeric(16, 2) NOT NULL,
"type" varchar(20) NULL,
status varchar(20) NULL,
id_user bigserial NOT NULL,
registration_date date DEFAULT now() NULL,
latitude varchar(255) NOT NULL,
longitude varchar(255) NOT NULL,
updated_date date NULL,
CONSTRAINT entry_pkey PRIMARY KEY (id),
CONSTRAINT entry_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('CANCELED'::character varying)::text, ('EFFECTIVE'::character varying)::text]))),
CONSTRAINT entry_type_check CHECK (((type)::text = ANY (ARRAY[('RECIPE'::character varying)::text, ('EXPENSE'::character varying)::text])))
);

ALTER TABLE finances.entry ADD CONSTRAINT entry_user_fk FOREIGN KEY (id_user) REFERENCES finances."user"(id) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE finances.category (
id bigserial NOT NULL,
description varchar(255) NOT NULL,
active bool DEFAULT true NULL,
registration_date date NULL,
CONSTRAINT category_pk PRIMARY KEY (id)
);

CREATE TABLE finances.categories_entries (
category_id bigserial NOT NULL,
entry_id bigserial NOT NULL,
CONSTRAINT categories_entries_pkey PRIMARY KEY (category_id, entry_id)
);

ALTER TABLE finances.categories_entries ADD CONSTRAINT categories_entries_category_fk FOREIGN KEY (category_id) REFERENCES finances.category(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE finances.categories_entries ADD CONSTRAINT categories_entries_entry_fk FOREIGN KEY (entry_id) REFERENCES finances.entry(id) ON DELETE CASCADE ON UPDATE CASCADE;
```

### Windows

É uma maneira bem simples de se conseguir utilizar.

#### Primeiro passo

Clone este repositório em uma pasta de sua preferência com o seguinte comando

`git clone https://muralisti@dev.azure.com/muralisti/Programa%20de%20Est%C3%A1gio%20da%20Muralis/_git/pem-ruan-dias-back`

Ou utilizando o Git GUI, use o de sua preferência.

#### Segundo passo

Abra a pasta na IDE de sua preferência e atualize o projeto maeven, assim as dependências serão baixadas para sua maquina.

#### Segundo passo

Crie um banco de dados com o nome de "myfinances", caso fique com dúvida de como fazer, abra o PgAdmin ou um gerenciador de sua
preferência e execute o comando:

`CREATE DATABASE mifinaces;`

Se tudo tiver ocorrido de maneira correta, a aplicação estará disponível na Porta 8080 e seu Banco de Dados estará na porta 5432.
