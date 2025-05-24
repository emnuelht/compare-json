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
Em ```build.grandle.kts``` e ```repositories```, vamos colocar o seguinte:
```gradle
repositories {
    // Outros repositórios
    maven { url = uri("https://jitpack.io") }
}
```
E vamos adicionar a seguinte dependência:
```gradle
dependencies {
  implementation("com.github.emnuelht:compare-json:v1.0.3")
  // Outras dependências
}
```
Basta chamar o método estático ```run``` passando os dois arquivos JSON a serem comparados:
```java
Map<String, String> mudancas = CompareJson.run(fileOld, fileNew);
```
### 📋 Formato do Retorno
O retorno é um Map<String, String> com as mudanças detectadas. A chave indica o tipo de mudança e a estrutura (tabela, coluna, valor), e o valor mostra o que foi alterado.
#### ✅ Adições
| Tipo   | Exemplo-chave                     | Exemplo-valor         |
| ------ | --------------------------------- | --------------------- |
| Tabela | `change.new.table.tb03`           | `pedido__tb03`        |
| Coluna | `change.new.column.tb03.cn08`     | `data_pedido__cn08`   |
| Valor  | `change.new.value.tb03.cn07.type` | `cn07.type:"Integer"` |
#### 🔄 Alterações
| Tipo   | Exemplo-chave                 | Exemplo-valor           |
| ------ | ----------------------------- | ----------------------- |
| Valor  | `change.value.tb01.cn01.type` | `cn01.type:"Strinaaag"` |
| Coluna | `change.column.tb01.cn01`     | `no__cn01`              |
| Tabela | `change.table.tb01`           | `criado_em__cn03`       |
#### ❌ Remoções
| Tipo   | Exemplo-chave              | Exemplo-valor   |
| ------ | -------------------------- | --------------- |
| Tabela | `deleted.table.tb01`       | `usuario__tb01` |
| Coluna | `deleted.column.tb01.cn01` | `nome__cn01`    |

### 📎Observações
No caso de mudanças de valores, como em ```change.new.value.tb03.cn07.type - cn07.type:"Integer"```, o valor completo inclui o nome da coluna (cn07) seguido do campo alterado (type).
Se preferir, você pode remover o prefixo do nome da coluna e utilizar apenas a parte final, como:
type:"Integer"

## 🧑‍💻 Contribuição
Pull Requests são bem-vindos! Sinta-se à vontade para abrir issues ou sugerir melhorias.
