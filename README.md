# Sistema-Academia---Trabalho-POO

# Sistema de Academia

Aplicação desktop para gerenciamento de cadastros e atividades de uma academia. A interface é construída com Java Swing, e os dados são armazenados localmente na pasta `dados/`.

## Funcionalidades

- Cadastro, consulta, edição e exclusão de alunos, com validação de CPF e controle de situação ativo/inativo.
- Cadastro, consulta, edição e exclusão de instrutores, incluindo especialidade e CREF.
- Cadastro, consulta, edição e exclusão de planos, com nome, valor mensal, duração e situação.
- Matrículas de alunos em planos, com geração de mensalidades, cancelamento e registro de pagamento.
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
├── dados/               DAOs e persistência em arquivos
├── excecoes/            Exceções específicas do domínio
├── interfacegrafica/    Janelas Swing
├── modelo/              Entidades e regras locais do domínio
└── servico/             Regras de aplicação para alunos, instrutores,
                         matrículas e treinos

dados/                   Arquivos de dados locais
out/                     Destino dos arquivos compilados
```

## Principais classes

- `academia.app.Main`: configura o visual e inicia a janela principal.
- `academia.interfacegrafica.TelaPrincipal`: menu para abrir as demais telas.
- `TelaAlunos`, `TelaInstrutores`, `TelaPlanos`, `TelaMatriculas` e `TelaTreinos`: interfaces para os respectivos cadastros e operações.
- `Aluno`, `Instrutor`, `Plano`, `Matricula` e `Mensalidade`: entidades do domínio.
- `Treino`, `ExercicioMusculacao` e `ExercicioCardio`: modelo de treinos e exercícios.
- `AlunoDAO`, `InstrutorDAO`, `PlanoDAO` e `MatriculaDAO`: leitura e gravação dos dados em texto.
- `AlunoService`, `InstrutorService`, `MatriculaService` e `TreinoService`: operações e regras de negócio.
- `ArquivoTexto`: gravação de arquivos texto em UTF-8 com substituição por arquivo temporário.

## Persistência

Os caminhos são relativos à pasta de execução. Inicie o programa com a pasta raiz do projeto como diretório atual para que os arquivos sejam encontrados em `dados/`.

- `dados/alunos.txt`: alunos; campos textuais codificados em Base64 e campos estruturados separados por `|`.
- `dados/instrutores.txt`: instrutores; campos textuais codificados em Base64 e campos separados por `|`.
- `dados/planos.txt`: planos; nome codificado em Base64 e demais campos separados por `|`.
- `dados/matriculas.txt`: matrículas e mensalidades em formato texto delimitado.
- `dados/treinos.dat`: lista serializada de objetos `Treino`, incluindo seus exercícios.

Os quatro DAOs de texto usam UTF-8 e mantêm o formato existente. A gravação substitui o arquivo a partir de um temporário. `TreinoService` também grava o `.dat` por temporário e carrega o arquivo quando é iniciado. A aplicação cria a pasta `dados/` e os arquivos texto necessários quando ainda não existem.

## Integrantes

Os arquivos do projeto consultados não identificam nominalmente os integrantes. Inclua aqui os nomes do grupo, se necessário.

## Compilar e executar

É necessário ter um JDK instalado, pois a compilação usa `javac`. No PowerShell, abra o terminal na pasta raiz do projeto e execute:

```powershell
$javaSourceFiles = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out $javaSourceFiles
java -cp out academia.app.Main
```

O primeiro comando compila os fontes para `out/`; o segundo inicia `academia.app.Main`. Execute ambos a partir da raiz do projeto para que os arquivos relativos em `dados/` sejam usados corretamente.
