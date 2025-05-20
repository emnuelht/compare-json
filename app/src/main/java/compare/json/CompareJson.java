package compare.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CompareJson {
    public static Map<String, String> run(File fileOld, File fileNew) throws IOException {
        // Instanciando o ObjectMapper para ler o arquivo JSON
        ObjectMapper mapper = new ObjectMapper();
        // Utilizar o JsonNode para percorder os valores do JSON
        JsonNode nodeFileOld = mapper.readTree(fileOld);
        JsonNode nodeFileNew = mapper.readTree(fileNew);
        // O walk vai me retornar todas as keys e seus valores, e sendo colocado em um List de Strings 
        List<String> listOld = walk(nodeFileOld, "");
        List<String> listNew = walk(nodeFileNew, "");

        // Recebendo a lista de codigos das tables
        Set<String> codTablesNew = setContentNew(listNew, 0);
        // Recebendo a lista de codigos das columns
        Set<String> codColumnsNew = setContentNew(listNew, 1);
        // Recebendo a lista de nomes das tables com o cod
        Set<String> nameTablesNew = setContentNew(listNew, 2);
        // Recebendo a lista de nomes das columns com o cod
        Set<String> nameColumnsNew = setContentNew(listNew, 3);
        // Recebendo a lista de valores com o cod da column, exemplo: cn06.length:"none"
        Set<String> optionValueNew = setContentNew(listNew, 4);

        // Recebendo as mudanças, entre um arquivo JSON e outro
        Map<String, String> change = getChange(listOld, listNew, codTablesNew, codColumnsNew, nameTablesNew, nameColumnsNew, optionValueNew);

        return change;
    }

    private static Set<String> setContentNew(List<String> listNew, int output) {
        Set<String> codTb = new HashSet<>();
        Set<String> codCl = new HashSet<>();
        Set<String> nameTb = new HashSet<>();
        Set<String> nameCl = new HashSet<>();
        Set<String> optVal = new HashSet<>();

        // O loop dentro do listNew é para inserir os vamos nos Sets acima, os nomes e os cods
        for (String str : listNew) {
            // Recebendo somente os codigos
            String[] cods = getCod(str, true);
            // Recebendo os nomes que viram junto com o codigo, desta forma: usuario__tb01
            String[] names = getCod(str, false);
            codTb.add(cods[0]);
            codCl.add(cods[1]);
            nameTb.add(names[0]);
            nameCl.add(names[1]);
            optVal.add(cods[1]+"."+names[2]);
        }

        switch (output) {
            case 0:
                return codTb;
            case 1:
                return codCl;
            case 2:
                return nameTb;
            case 3:
                return nameCl;
            case 4:
                return optVal;
            default:
                return null;
        }
    }

    private static Map<String, String> getChange(List<String> listOld, List<String> listNew, Set<String> codTablesNew, Set<String> codColumnsNew, Set<String> nameTablesNew, Set<String> nameColumnsNew, Set<String> optionValueNew) {
        Map<String, String> changeMap = new HashMap<>();
        Set<String> deleted = new HashSet<>();
        // Verificando mudanças
        for (int i = 0; i < listOld.size(); i++) {
            String codTable = getCod(listOld.get(i),true)[0];
            String codColumn = getCod(listOld.get(i),true)[1];
            String nameTable = getCod(listOld.get(i),false)[0];
            String nameColumn = getCod(listOld.get(i),false)[1];
            String nameOption = codColumn+"."+getCod(listOld.get(i),false)[2];

            String oldValue = listOld.get(i);
            String newValue = i < listNew.size() ? listNew.get(i) : "";

            if (!oldValue.equals(newValue)) {
                // Verificando se a table foi deletada
                if (!codTablesNew.contains(codTable) && !deleted.contains(codTable)) {
                    deleted.add(codTable);
                    changeMap.put("deleted.table."+codTable, nameTable);
                    continue;
                }
                // Verificando se uma column foi deletada
                if (!codColumnsNew.contains(codColumn) && !deleted.contains(codColumn)) {
                    deleted.add(codColumn);
                    changeMap.put("deleted.column."+codTable+"."+codColumn, nameColumn);
                    continue;
                }
                // Se encontrar uma table deletada ou uma column, pular o loop
                if (deleted.contains(codTable) || deleted.contains(codColumn)) {
                    continue;
                }
                // Verificando uma mudança na table
                if (!nameTablesNew.contains(nameTable) && codTablesNew.contains(codTable) && !deleted.contains(codTable)) {
                    changeMap.put("change.table."+codTable, getCod(newValue,false)[1]);
                }
                // Verificando uma mudança na column
                if (!nameColumnsNew.contains(nameColumn) && codColumnsNew.contains(codColumn) && !deleted.contains(codColumn)) {
                    changeMap.put("change.column."+codTable+"."+codColumn, getCod(newValue,false)[1]);
                }
                // Verificando uma mudança no valor
                if (!optionValueNew.contains(nameOption)) {
                    String stringSplit = nameOption.split(":")[0];
                    for (String item : optionValueNew) {
                        String string = item.split(":")[0];
                        if (stringSplit.equals(string)) {
                            changeMap.put("change.value."+codTable+"."+string, item);
                        }
                    }
                }
            }
        }
        // Verificando novas linhas
        for (String iNew : listNew) {
            if (!listOld.contains(iNew)) {
                String codTable = getCod(iNew, true)[0];
                String codColumn = getCod(iNew, true)[1];
                String nameTable = getCod(iNew, false)[0];
                String nameColumn = getCod(iNew, false)[1];
                String nameOption = codColumn + "." + getCod(iNew, false)[2];

                // Verifica se a tabela com esse código já existia
                boolean tableExists = false;
                boolean columnExists = false;

                for (String iOld : listOld) {
                    String oldCodTable = getCod(iOld, true)[0];
                    String oldCodColumn = getCod(iOld, true)[1];
                    if (codTable.equals(oldCodTable)) {
                        tableExists = true;
                    }
                    if (codColumn.equals(oldCodColumn)) {
                        columnExists = true;
                    }
                }

                if (!tableExists) {
                    changeMap.put("change.new.table." + codTable, nameTable);
                } else {
                    changeMap.put("change.table." + codTable, nameTable);
                }

                if (!columnExists) {
                    changeMap.put("change.new.column." + codColumn, nameColumn);
                } else {
                    changeMap.put("change.column." + codTable + "." + codColumn, nameColumn);
                }

                // Adicionando o novo valor
                String stringSplit = nameOption.split(":")[0];
                for (String item : optionValueNew) {
                    String string = item.split(":")[0];
                    if (stringSplit.equals(string)) {
                        if (!columnExists) {
                            changeMap.put("change.new.value." + codTable + "." + string, item);
                        } else {
                            changeMap.put("change.value." + codTable + "." + string, item);
                        }
                    }
                }
            }
        }
        // Retornando o que foi aplicado
        return changeMap;
    }

    private static String[] getCod(String string, boolean cod) {
        String[] array = new String[3];
        array[0] = "";array[1] = "";array[2] = "";
        if (!string.isEmpty()) {
            // Dividindo a string em 3 pedações, 0 => table, 1 => column e 2 => option
            String[] strings = string.split("\\.");
            String table = strings[0].length()>0?strings[0]:null;
            String column = strings[1].length()>0?strings[1]:null;
            String option = strings[2].length()>0?strings[2]:null;

            int indexTable_ = table.indexOf("__");
            String codTable = table.substring(indexTable_ + 2);

            int indexColumn_ = column.indexOf("__");
            String codColumn = column.substring(indexColumn_ + 2);
            
            // Se o cod for True, ele retorna somente o cod, se não ele retorna o nome junto com o cod, assim: usuario__tb01
            array[0] = cod ? codTable : table;
            array[1] = cod ? codColumn : column;
            array[2] = option;
        }
        return array;
    }

    private static List<String> walk(JsonNode node, String path) {
        List<String> paths = new ArrayList<>();
        // Verificando se o node é realmente um objeto JSON
        if (node.isObject()) {
            // Obtém um iterador sobre todos os pares chave/valor do objeto
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            // Vai percorrer sobre os campos do objeto
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                // Montando o caminho atual, se a path estiver vazia ele vai concatenar com a key, se não ele coloca o caminho anterior
                String currentPath = path.isEmpty() ? entry.getKey() : path + "." + entry.getKey();
                // Usamos addAll porque queremos adicionar TODOS esses caminhos na nossa lista principal (paths)
                paths.addAll(walk(entry.getValue(), currentPath));
            }
        } else {
            // o else é quando chegar em um valor simples, como 25, Jose...
            paths.add(path + ":" + node.toString());
        }
        // Retornando esses caminhos(paths)
        return paths;
    }
}
