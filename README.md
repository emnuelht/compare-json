# 📊 Compare-JSON

## 🛠️ Tecnologias Utilizadas

### Linguagem
- Java 21

### Build & Dependências
- Gradle (Kotlin DSL)

### Bibliotecas
- Jackson (parse de JSON)

---

## 💡 O que é o Compare-JSON?

O **Compare-JSON** é um programa criado para **comparar dois arquivos JSON** e identificar **mudanças nas chaves e valores**, como:

- ✅ Novos valores adicionados  
- ❌ Valores removidos  
- 🔄 Alterações em valores existentes 

Foi desenvolvido com o objetivo de **ajudar na detecção de mudanças estruturais** em arquivos JSON que representam tabelas e colunas, permitindo a criação **dinâmica de tabelas de banco de dados**.

---

## 📐 Formato esperado do JSON

O comparador espera que os JSONs sigam uma estrutura específica, baseada no modelo de tabelas:
- Pai (tabela): deve conter um ID no formato __tbXX, por exemplo: usuarios__tb01, produtos__tb02
- Filho (coluna): deve conter um ID no formato __cnXX, por exemplo: nome__cn01, preco__cn02
- Opções disponíveis:
  - type: tipo do dado (ex: String, Integer, Timestamp)
  - length: tamanho do campo
  - default: valor padrão

```json
{
  "tabela__tb01": {
    "coluna1__cn01": {
      "type": "String",
      "length": 10,
      "default": "Nenhum"
    },
    "coluna2__cn02": {
      "type": "Integer",
      "length": 5,
      "default": "0"
    }
  }
}
```
## 📌 Por que esse formato?
Esse formato simula a estrutura de tabelas e colunas de um banco de dados, permitindo uma visualização clara e comparável de alterações.

O objetivo é transformar mudanças em arquivos JSON em uma base para geração automática de scripts ou estruturas de banco, economizando tempo e evitando erros manuais.

## ⚙️ Para rodar o programa
Utilize o comando ```./gradlew run```, e o programa será iniciado :)
