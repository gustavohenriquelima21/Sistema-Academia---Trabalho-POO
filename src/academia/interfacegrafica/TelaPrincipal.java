package academia.interfacegrafica;
import academia.dados.PlanoDAO;
import academia.servico.AlunoService;
import academia.servico.InstrutorService;
import academia.servico.MatriculaService;
import academia.servico.TreinoService;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
public class TelaPrincipal extends JFrame {
    private final AlunoService alunoService;
    private final PlanoDAO planoDAO;
    private final MatriculaService matriculaService;
    private final InstrutorService instrutorService;
    private final TreinoService treinoService;

    private TelaAlunos telaAlunos;
    private TelaInstrutores telaInstrutores;
    private TelaPlanos telaPlanos;
    private TelaMatriculas telaMatriculas;
    private TelaTreinos telaTreinos;

    public TelaPrincipal(AlunoService alunoService, PlanoDAO planoDAO,
                         MatriculaService matriculaService, InstrutorService instrutorService,
                         TreinoService treinoService) {
        super("Sistema de Academia - Menu Principal");
        this.alunoService = alunoService;
        this.planoDAO = planoDAO;
        this.matriculaService = matriculaService;
        this.instrutorService = instrutorService;
        this.treinoService = treinoService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 350);
        setLocationRelativeTo(null);

        criarComponentes();
    }

    private void criarComponentes() {
        JPanel conteudo = new JPanel(new BorderLayout(10, 20));
        conteudo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        conteudo.add(new JLabel("Selecione uma opção:", SwingConstants.CENTER),
                BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton botaoAlunos = new JButton("Alunos");
        botaoAlunos.addActionListener(e -> abrirAlunos());
        menu.add(botaoAlunos);

        JButton botaoInstrutores = new JButton("Instrutores");
        botaoInstrutores.addActionListener(e -> abrirInstrutores());
        menu.add(botaoInstrutores);

        JButton botaoPlanos = new JButton("Planos");
        botaoPlanos.addActionListener(e -> abrirPlanos());
        menu.add(botaoPlanos);

        JButton botaoMatriculas = new JButton("Matrículas");
        botaoMatriculas.addActionListener(e -> abrirMatriculas());
        menu.add(botaoMatriculas);

        JButton botaoTreinos = new JButton("Treinos");
        botaoTreinos.addActionListener(e -> abrirTreinos());
        menu.add(botaoTreinos);

        conteudo.add(menu, BorderLayout.CENTER);
        setContentPane(conteudo);
    }

    private void abrirAlunos() {
        if (telaAlunos == null || !telaAlunos.isDisplayable()) {
            telaAlunos = new TelaAlunos(this, alunoService);
        }

        telaAlunos.setVisible(true);
        telaAlunos.toFront();
        telaAlunos.requestFocus();
    }

    private void abrirInstrutores() {
        if (telaInstrutores == null || !telaInstrutores.isDisplayable()) {
            telaInstrutores = new TelaInstrutores(this, instrutorService);
        }

        telaInstrutores.setVisible(true);
        telaInstrutores.toFront();
        telaInstrutores.requestFocus();
    }

    private void abrirPlanos() {
        if (telaPlanos == null || !telaPlanos.isDisplayable()) {
            telaPlanos = new TelaPlanos(this, planoDAO);
        }
        telaPlanos.setVisible(true);
        telaPlanos.toFront();
        telaPlanos.requestFocus();
    }

    private void abrirMatriculas() {
        if (telaMatriculas == null || !telaMatriculas.isDisplayable()) {
            telaMatriculas = new TelaMatriculas(
                    this, alunoService, planoDAO, matriculaService);
        }

        telaMatriculas.setVisible(true);
        telaMatriculas.toFront();
        telaMatriculas.requestFocus();
    }

    private void abrirTreinos() {
        if (telaTreinos == null || !telaTreinos.isDisplayable()) {
            telaTreinos = new TelaTreinos(
                    this, treinoService, alunoService::listarTodos, instrutorService::listarTodos);
        }

        telaTreinos.setVisible(true);
        telaTreinos.toFront();
        telaTreinos.requestFocus();
    }
}
