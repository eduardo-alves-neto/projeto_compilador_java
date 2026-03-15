````md
# Compilador de Código PCode

**Data:** 2026-02-04  

Baseado no projeto do **Professor Dr. Daniel Lucrédio da UFSCar**, disponibilizado no GitHub.

---

# Como usar

Para utilizar o compilador PCode, execute o JAR da seguinte forma:

```bash
java -jar pcode.jar -Input=<FILE> -Debug=<BOOLEAN> -WaitTime=<INT>
````

### Parâmetros

* **-Input | -i**
  Caminho para o arquivo contendo o código P-Code.

* **-Debug | -d** *(Opcional)*
  `true` para habilitar o modo de depuração, `false` para desabilitar.
  **Padrão:** `true`

* **-WaitTime | -w** *(Opcional)*
  Usado apenas no modo debug. Define quanto tempo será esperado até executar o próximo comando.
  **Padrão:** `500ms`

---

# Instruções da P-Code Machine

## Entrada / Saída

| Instrução | Descrição                                               | Tipos de Token       |
| --------- | ------------------------------------------------------- | -------------------- |
| `in`      | Lê um valor X da entrada e armazena X no topo da pilha  | DATA_TYPE            |
| `out`     | Retira um valor X do topo da pilha e escreve X na saída | NUMBER, STRING, BOOL |

---

# Manipulação de Dados

| Instrução | Descrição                              | Tipos de Token                      |
| --------- | -------------------------------------- | ----------------------------------- |
| `push C`  | Insere C no topo da pilha              | NUMBER, STRING, BOOL, ADDRESS, CHAR |
| `pop`     | Remove o valor do topo da pilha        | ANY                                 |
| `dup`     | Duplica o valor no topo da pilha       | ANY                                 |
| `swap`    | Troca os dois valores do topo da pilha | ANY                                 |

---

# Aritmética

| Instrução | Descrição                                        | Tipos de Token |
| --------- | ------------------------------------------------ | -------------- |
| `to T`    | Converte o valor do topo da pilha para o tipo T  | NUMBER         |
| `add`     | Retira X e Y do topo da pilha e insere **X + Y** | NUMBER         |
| `sub`     | Retira X e Y do topo da pilha e insere **Y - X** | NUMBER         |
| `mul`     | Retira X e Y e insere **X * Y**                  | NUMBER         |
| `div`     | Retira X e Y e insere **Y / X**                  | NUMBER         |
| `mod`     | Retira X e Y e insere **Y % X**                  | NUMBER         |

---

# Operações Lógicas

| Instrução | Descrição                                 | Tipos de Token |
| --------- | ----------------------------------------- | -------------- |
| `grt`     | Y > X                                     | NUMBER         |
| `let`     | Y < X                                     | NUMBER         |
| `gte`     | Y ≥ X                                     | NUMBER         |
| `lte`     | Y ≤ X                                     | NUMBER         |
| `equ`     | X == Y                                    | NUMBER, STRING |
| `neq`     | X != Y                                    | NUMBER, STRING |
| `and`     | X && Y                                    | BOOL           |
| `or`      | X || Y                                    | BOOL           |
| `not`     | Inverte o valor booleano no topo da pilha | BOOL           |
| `xor`     | Operação XOR entre dois valores booleanos | BOOL           |

---

# Memória

| Instrução | Descrição                                                                       | Tipos de Token |
| --------- | ------------------------------------------------------------------------------- | -------------- |
| `lod`     | Troca o endereço presente no topo da pilha pelo valor armazenado nesse endereço | ADDRESS        |
| `sto`     | Retira X e A e armazena X na memória no endereço A                              | VALUE, ADDRESS |

---

# Controle de Fluxo

| Instrução | Descrição                                       | Tipos de Token |
| --------- | ----------------------------------------------- | -------------- |
| `L:`      | Marca uma posição de código com um rótulo       | LABEL          |
| `ujp L`   | Salta para a instrução marcada com L            | LABEL          |
| `fjp L`   | Se X for falso, salta para L                    | BOOL           |
| `tjp L`   | Se X for verdadeiro, salta para L               | BOOL           |
| `call`    | Chama uma função especificada por um rótulo     | LABEL          |
| `ret`     | Retorna de uma função para a instrução anterior | -              |

---

# Bitwise

| Instrução | Descrição                                    | Tipos de Token |
| --------- | -------------------------------------------- | -------------- |
| `shl`     | Deslocamento à esquerda em Y na quantidade X | NUMBER         |
| `shr`     | Deslocamento à direita em Y na quantidade X  | NUMBER         |

---

# Strings

| Instrução | Descrição                           | Tipos de Token |
| --------- | ----------------------------------- | -------------- |
| `sln`     | Calcula o comprimento de uma string | STRING         |
| `sct`     | Concatena duas strings/char         | STRING, CHAR   |

---

# Finalização

| Instrução | Descrição             |
| --------- | --------------------- |
| `hlt`     | Interrompe a execução |

---

# Observações

* **NUMBER:** valores inteiros ou de ponto flutuante
* **FLOAT:** números de ponto flutuante
* **BOOL:** valores booleanos (`true` ou `false`)
* **ADDRESS:** endereços de memória
* **STRING:** cadeias de caracteres
* **CHAR:** caractere único
* **DATA_TYPE:** tipos como `int`, `float`, `long`, `double`
* **LABEL:** rótulos usados em instruções de salto

```
```
