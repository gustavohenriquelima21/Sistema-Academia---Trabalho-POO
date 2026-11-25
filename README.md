# Sistema-Academia---Trabalho-POO

# Sistema de Academia

Aplicação desktop para gerenciamento de cadastros e atividades de uma academia. A interface é construída com Java Swing, e os dados são armazenados localmente na pasta `dados/`.

## Funcionalidades

- Cadastro, consulta, edição e exclusão de alunos, com validação de CPF e controle de situação ativo/inativo.
- Cadastro, consulta, edição e exclusão de instrutores, incluindo especialidade e CREF.
- Ativação e inativação de alunos (aluno inativo não pode ser matriculado nem receber treino; não é possível inativar aluno com matrícula ativa).
- Cadastro, consulta, edição, exclusão, ativação e inativação de planos (plano inativo não aceita novas matrículas).
- Matrículas de alunos em planos, com geração de mensalidades, cancelamento e encerramento automático das matrículas vencidas.
- Pagamento de mensalidades em **Dinheiro (5% de desconto)**, **Pix (2% de desconto)** ou **Cartão de crédito (3% de taxa)**, com **multa de 2%** quando a parcela é paga após o vencimento.
- Bloqueio de exclusão de alunos ou planos vinculados a matrículas.
- Cadastro e edição de treinos associados a aluno e instrutor.
- Adição e remoção de exercícios de musculação e cardio, com estimativa da duração do treino.
- Consulta dos treinos cadastrados e filtro por aluno.

## Tecnologias

- Java (o código usa recursos compatíveis com Java 8 ou posterior).
- Java Swing para a interface gráfica.
- Java NIO para arquivos texto e operações de arquivo.
- Serialização de objetos Java para persistir treinos.

Não há configuração de Maven ou Gradle no projeto.

## Estrutura do projeto

```text
src/academia/
├── app/                 Inicialização da aplicação
├── dados/               DAO genérico (ArquivoTextoDAO<T>) e DAOs concretos
├── excecoes/            Exceções específicas do domínio
├── interfacegrafica/    Janelas Swing
├── modelo/              Entidades e regras locais do domínio
└── servico/             Regras de aplicação para alunos, instrutores,
                         matrículas e treinos

dados/                   Arquivos de dados locais (criados na 1ª execução)
dados-exemplo/           Dados prontos para demonstração
docs/                    Diagramas UML (.puml e .png) e capturas de tela
out/                     Destino dos arquivos compilados
```

## Principais classes

- `academia.app.Main`: configura o visual e inicia a janela principal.
- `academia.interfacegrafica.TelaPrincipal`: menu para abrir as demais telas.
- `TelaAlunos`, `TelaInstrutores`, `TelaPlanos`, `TelaMatriculas` e `TelaTreinos`: interfaces para os respectivos cadastros e operações.
- `Aluno`, `Instrutor`, `Plano`, `Matricula` e `Mensalidade`: entidades do domínio.
- `FormaPagamento` (abstrata), `PagamentoDinheiro`, `PagamentoPix` e `PagamentoCartaoCredito`: formas de pagamento com regras de cálculo diferentes.
- `Treino`, `ExercicioMusculacao` e `ExercicioCardio`: modelo de treinos e exercícios.
- `Identificavel` e `DuracaoCalculavel`: interfaces do domínio.
- `ArquivoTextoDAO<T>` (abstrata e genérica) e `AlunoDAO`, `InstrutorDAO`, `PlanoDAO`, `MatriculaDAO`: leitura e gravação dos dados em texto.
- `AlunoService`, `InstrutorService`, `PlanoService`, `MatriculaService` e `TreinoService`: operações e regras de negócio; `ValidadorCpf` valida CPF.
- `ArquivoTexto`: gravação de arquivos texto em UTF-8 com substituição por arquivo temporário.

## Persistência

Os caminhos são relativos à pasta de execução. Inicie o programa com a pasta raiz do projeto como diretório atual para que os arquivos sejam encontrados em `dados/`.

- `dados/alunos.txt`: alunos; campos textuais codificados em Base64 e campos estruturados separados por `|`.
- `dados/instrutores.txt`: instrutores; campos textuais codificados em Base64 e campos separados por `|`.
- `dados/planos.txt`: planos; nome codificado em Base64 e demais campos separados por `|`.
- `dados/matriculas.txt`: matrículas e mensalidades em formato texto delimitado (cada mensalidade paga guarda também a forma de pagamento e o valor pago; arquivos antigos, sem esses campos, continuam sendo lidos).
- `dados/treinos.dat`: lista serializada de objetos `Treino`, incluindo seus exercícios.

Os quatro DAOs de texto usam UTF-8 e mantêm o formato existente. A gravação substitui o arquivo a partir de um temporário. `TreinoService` também grava o `.dat` por temporário e carrega o arquivo quando é iniciado. A aplicação cria a pasta `dados/` e os arquivos texto necessários quando ainda não existem.

## Conceitos de POO aplicados

| Conceito | Onde está no código |
|---|---|
| Herança | `Pessoa` → `Aluno`, `Instrutor`; `Exercicio` → `ExercicioMusculacao`, `ExercicioCardio`; `FormaPagamento` → `PagamentoDinheiro`, `PagamentoPix`, `PagamentoCartaoCredito`; `ArquivoTextoDAO<T>` → 4 DAOs |
| Classe abstrata | `Pessoa`, `Exercicio`, `FormaPagamento`, `ArquivoTextoDAO<T>` |
| Polimorfismo | `calcularDuracaoMinutos()` (musculação × cardio), `calcularValorFinal()` (cada forma de pagamento), `converter()`/`formatar()` (cada DAO) |
| Interface | `Identificavel`, `DuracaoCalculavel` (implementada por `Exercicio` e `Treino`) e a interface gráfica Swing |
| Composição | `Matricula` cria e contém suas `Mensalidade`s; `Treino` cria e contém seus `Exercicio`s |
| Agregação / associação | `Treino` referencia `Aluno` e `Instrutor`, que existem independentemente; `Matricula` → `Aluno`, `Plano` |
| Encapsulamento | atributos `private`; `Treino.getExercicios()` devolve lista não modificável |
| Exceções customizadas | `AlunoNaoEncontradoException`, `InstrutorNaoEncontradoException`, `PlanoNaoEncontradoException`, `MatriculaInvalidaException` (não verificadas) e `TreinoNaoEncontradoException` (verificada) |
| Persistência | arquivos texto UTF-8 (alunos, instrutores, planos, matrículas) e serialização Java (`treinos.dat`) |
| Genéricos | `ArquivoTextoDAO<T extends Identificavel>` |

Diagramas: [`docs/diagrama-classes.png`](docs/diagrama-classes.png) e [`docs/diagrama-camadas.png`](docs/diagrama-camadas.png).

## Dados de demonstração

Para apresentar o sistema já com dados, copie o conteúdo de `dados-exemplo/` para `dados/` antes de executar (5 alunos, 2 instrutores, 4 planos, 4 matrículas com pagamentos e 2 treinos).

```powershell
New-Item -ItemType Directory -Force dados | Out-Null; Copy-Item dados-exemplo\* dados\ -Force
```

## Integrantes

 - Ana Laura Moreno Naves / 12521BSI209
 
 - Benjamim Martins Magalhães Sena / 12521BSI246

 - Giovanna Fernandes / 12521BSI262

 - Gustavo Henrique Silva Lima / 12521BSI237

 - Isabela Turatti Silva / 12521BSI261

 - Matheus Yoshida Pasini / 12511BSI224

 - Pedro Rezende Borges / 12521BSI216

 - Pedro Henrique Pereira da Mata / 12521BSI243

Repositório: https://github.com/gustavohenriquelima21/Sistema-Academia---Trabalho-POO

## Como compilar e executar

É necessário ter o **JDK (Java Development Kit)** instalado para executar o sistema.

### Opção 1 — Execução automática (Windows)

1. Baixe ou clone este repositório.
2. Abra a pasta principal do projeto.
3. Execute o arquivo `abrir-projeto.bat`.
4. Aguarde a compilação e a inicialização do sistema.

O arquivo realiza automaticamente a compilação dos arquivos Java e inicia a aplicação.

### Opção 2 — Execução manual pelo PowerShell

Caso prefira executar os comandos manualmente, abra o PowerShell na pasta raiz do projeto e execute:

```powershell
$javaSourceFiles = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $javaSourceFiles
java -cp out academia.app.Main
```

O primeiro comando localiza os arquivos `.java` dentro da pasta `src`. O segundo compila os arquivos para a pasta `out/`. O terceiro inicia a aplicação por meio da classe principal `academia.app.Main`.

**Importante:** execute os comandos a partir da pasta raiz do projeto para que os caminhos relativos utilizados na persistência de dados funcionem corretamente.
