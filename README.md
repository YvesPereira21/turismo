# Turismo

O **Turismo** é uma API REST feita em FastAPI para mapear e gerenciar pontos turísticos. O projeto foca em geolocalização, permitindo que usuários encontrem destinos próximos com base em coordenadas geográficas e muito mais.

## Framework e bibliotecas utilizadas

* **FastAPI**: Framework python.
* **SQLModel + SQLAlchemy**: Para interação com o banco de dados e suporte assíncrono.
* **PostgreSQL com PostGIS**: Banco de dados relacional com extensão espacial para geolocalização.
* **GeoAlchemy2**: Integração entre o ORM e funções espaciais do PostGIS.
* **Alembic**: Gerenciamento de migrações de banco de dados de forma assíncrona.
* **Pydantic v2**: Validação de dados e serialização.
* **PyJWT & Passlib**: Sistema de autenticação com tokens JWT e hashing de senhas com bcrypt.

## Detalhes da aplicação

* **Consultas Geoespaciais**: Implementação de rotas que calculam a distância real entre o usuário e pontos turísticos, utilizando funções como `ST_Distance` e `ST_DWithin` do PostGIS.
* **Arquitetura Assíncrona**: Toda a comunicação com o banco de dados é feita utilizando `AsyncSession`.
* **RBAC (Role-Based Access Control)**: Sistema de permissões baseado em roles, diferenciado para Administradores, Guias de Turismo e Turistas.
* **Serialização Customizada**: Uso de `field_serializer` para converter elementos geográficos do banco (WKB) em formatos legíveis (JSON).

## Como rodar

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/terras-paraibana.git
cd terras-paraibana

```


2. **Configuração do Ambiente (`.env`):**
O projeto utiliza variáveis de ambiente para segurança. Você **deve criar** um arquivo chamado `.env` na raiz do projeto com as seguintes chaves:
```env
DATABASE_URL=postgresql+asyncpg://usuario:senha@localhost:5432/nome_do_banco
JWT_SECRET=sua_chave_secreta_aqui
JWT_ALGORITHM=algoritmo

```


*Nota: Certifique-se de que seu PostgreSQL tenha a extensão **PostGIS** instalada.*
3. **Instale as dependências:**
```bash
pip install -r requirements.txt

```


4. **Execute as migrações:**
```bash
alembic upgrade head

```


5. **Inicie o servidor:**
```bash
fastapi dev main.py

```
