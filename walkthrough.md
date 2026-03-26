# Análise do Projeto Compilador Java vs Requisitos

## Resultado Geral

✅ O projeto **compila e gera P-Code** com sucesso.  
❌ O P-Code gerado **NÃO executa** corretamente no [pcode.jar](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/pcode.jar) — há 3 bugs que impedem a execução.

---

## Checklist de Requisitos

| Requisito | Gramática | Visitor/Geração | Executa no pcode.jar? |
|---|---|---|---|
| Declaração de variáveis (`var`) | ✅ | ✅ | ❌ Bug nos endereços |
| Atribuição de variáveis | ✅ | ✅ | ❌ Bug nos endereços |
| `if` (condicional) | ✅ | ✅ | ❌ Bug nos endereços |
| `while` (repetição) | ✅ | ✅ | ❌ Bug nos endereços |
| `print` (saída) | ✅ | ✅ | ✅ Funciona |
| `input` (entrada) | ✅ | ✅ | ❌ Bug nos endereços |
| Expressões aritméticas (`+`,`-`,`*`,`/`) | ✅ | ✅ | ✅ Funciona |
| Operador `^` (potência) | ✅ na gramática | ❌ Lança exceção | ❌ |
| Expressões condicionais (`and`, [or](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/src/main/java/com/uepb/compiler/PCodeGeneratorVisitor.java#256-266), `true`/`false`) | ✅ | ⚠️ `true`/`false` errado | ❌ |
| GLC no ANTLR4 | ✅ | — | — |
| Geração de P-Code | — | ✅ | ❌ |
| Análise léxica e sintática | ✅ | — | — |
| CLI (`-i`, `-o`, `-v`) | ✅ | — | — |
| Tabela de símbolos | ✅ | — | — |

---

## Bugs Encontrados (3 bugs que impedem a execução)

### Bug 1: Endereços sem prefixo `$` (CRÍTICO)

O [pcode.jar](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/pcode.jar) exige que endereços de memória usem o prefixo `$`. O compilador gera `push 0`, mas deveria gerar `push $0`.

**Erro:** `IllegalArgumentException: O valor para 'sto' deve ser um endereço iniciado por '$'`

**Arquivos afetados:** [PCodeGeneratorVisitor.java](file:///home/eduardo/Área%20de%20trabalho/UEPB/projeto_compilador_java/src/main/java/com/uepb/compiler/PCodeGeneratorVisitor.java)

```diff
 // Em visitDeclVar, visitAtribuicao, visitInputStmt, visitAtom (lod)
-emit("push " + address);
+emit("push $" + address);
```

---

### Bug 2: Ordem invertida na pilha para `sto` (CRÍTICO)

O manual diz: *"sto: Retira X e A e armazena X na memória no endereço A"* — X é o topo (valor), A é o segundo (endereço). 

O compilador faz: `push valor` → `push endereço` → `sto` (endereço no topo = X, valor = A → **invertido**).

**Correção:** trocar a ordem para `push $endereço` → `push valor` → `sto` (ou equivalente: gerar expr primeiro, depois push endereço antes do sto, **invertendo** a emissão).

Na verdade, reexaminando: o correto é **primeiro empilhar o endereço, depois o valor**, para que `sto` retire X=valor (topo) e A=endereço (segundo).

```diff
 // visitDeclVar
-visit(ctx.expr());        // valor no topo
-emit("push " + address);  // endereço no topo → ERRADO
-emit("sto");
+emit("push $" + address); // endereço em baixo
+visit(ctx.expr());        // valor no topo
+emit("sto");
```

Aplicar a mesma lógica em [visitAtribuicao](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/src/main/java/com/uepb/compiler/PCodeGeneratorVisitor.java#76-86) e [visitInputStmt](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/src/main/java/com/uepb/compiler/PCodeGeneratorVisitor.java#132-142).

---

### Bug 3: `true`/`false` como `1`/`0` em vez de literais (MENOR)

O [pcode.jar](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/pcode.jar) aceita `push true` e `push false` como literais booleanos. O compilador gera `push 1` e `push 0`.

```diff
 // visitCondicaoPrim
-emit("push 1");  // true
+emit("push true");
-emit("push 0");  // false
+emit("push false");
```

---

## Bug Adicional: Operador `^` não implementado

O [visitFator](file:///home/eduardo/Área%20de%20trabalho/UEPB/projeto_compilador_java/src/main/java/com/uepb/compiler/PCodeGeneratorVisitor.java#L257-L265) lança `UnsupportedOperationException` quando encontra `^`. Não existe instrução `pow` nativa no P-Code, então é preciso implementar potência via loop ou funções auxiliares, ou usar a abordagem de multiplicação repetida.

> [!IMPORTANT]
> O requisito diz "se desejado, `^` (potência)" — é **opcional**, então o projeto pode omitir a potência sem perder pontos.

---

## Verificação Realizada

1. ✅ `mvn clean package` — build completo sem erros
2. ✅ `java -jar compiler.jar -i example.expr -o out.pcode` — gera P-Code
3. ❌ `java -jar pcode.jar -Input=out.pcode -Debug=false` — falha com erro de endereço
4. ✅ Após correção manual do P-Code (prefixo `$` + ordem correta), o [pcode.jar](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/pcode.jar) executa com sucesso:
   - `if` condicional funciona corretamente
   - `while` com loop funciona corretamente  
   - `print` de strings e números funciona
   - Expressões aritméticas funcionam

---

## Conclusão

O projeto está **muito próximo de funcionar completamente**. A gramática e a arquitetura estão corretas. São necessárias apenas **3 correções pontuais** no [PCodeGeneratorVisitor.java](file:///home/eduardo/Área%20de%20trabalho/UEPB/projeto_compilador_java/src/main/java/com/uepb/compiler/PCodeGeneratorVisitor.java) para que o P-Code gerado execute no [pcode.jar](file:///home/eduardo/%C3%81rea%20de%20trabalho/UEPB/projeto_compilador_java/pcode.jar). As correções são simples e localizadas.
