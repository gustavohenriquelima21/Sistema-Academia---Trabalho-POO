package academia.interfacegrafica;

import academia.modelo.Instrutor;
import academia.servico.InstrutorService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class TelaInstrutores extends JDialog {

    private final InstrutorService instrutorService;

    private final JTextField campoNome = new JTextField();
    private final JTextField campoCpf = new JTextField();
    private final JTextField campoTelefone = new JTextField();
    private final JTextField campoEndereco = new JTextField();
    private final JTextField campoEspecialidade = new JTextField();
    private final JTextField campoCref = new JTextField();
    private final JTextField campoBusca = new JTextField(22);

    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new String[]{"ID", "Nome", "CPF", "Telefone", "Endereço", "Especialidade", "CREF"}, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    private final JTable tabelaInstrutores = new JTable(modeloTabela);
    private final TableRowSorter<DefaultTableModel> ordenadorTabela =
            new TableRowSorter<>(modeloTabela);

    private Integer idInstrutorSelecionado;
    private boolean atualizandoTabela;

    public TelaInstrutores(JFrame telaPrincipal, InstrutorService instrutorService) {
        super(telaPrincipal, "Gerenciamento de Instrutores", false);
        this.instrutorService = instrutorService;

        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(telaPrincipal);

        criarComponentes();
        atualizarTabela();

        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                atualizarTabela();
            }
        });
    }

    private void criarComponentes() {
        JPanel conteudo = new JPanel(new BorderLayout(10, 10));
        conteudo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        conteudo.add(criarFormulario(), BorderLayout.NORTH);
        conteudo.add(criarPainelTabela(), BorderLayout.CENTER);

        JButton botaoVoltar = new JButton("Voltar ao menu");
        botaoVoltar.addActionListener(e -> {
            dispose();
            getOwner().toFront();
            getOwner().requestFocus();
        });
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.add(botaoVoltar);
        conteudo.add(rodape, BorderLayout.SOUTH);

        setContentPane(conteudo);
    }

    private JPanel criarFormulario() {
        JPanel campos = new JPanel(new GridLayout(3, 4, 10, 8));
        campos.add(new JLabel("Nome:"));
        campos.add(campoNome);
        campos.add(new JLabel("CPF:"));
        campos.add(campoCpf);
        campos.add(new JLabel("Telefone:"));
        campos.add(campoTelefone);
        campos.add(new JLabel("Endereço:"));
        campos.add(campoEndereco);
        campos.add(new JLabel("Especialidade:"));
        campos.add(campoEspecialidade);
        campos.add(new JLabel("CREF:"));
        campos.add(campoCref);

        JButton botaoNovo = new JButton("Novo");
        JButton botaoSalvar = new JButton("Salvar");
        JButton botaoExcluir = new JButton("Excluir");
        botaoNovo.addActionListener(e -> limparFormulario());
        botaoSalvar.addActionListener(e -> salvarInstrutor());
        botaoExcluir.addActionListener(e -> excluirInstrutor());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(botaoNovo);
        botoes.add(botaoSalvar);
        botoes.add(botaoExcluir);

        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Instrutor"));
        painel.add(campos, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel criarPainelTabela() {
        tabelaInstrutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaInstrutores.setRowHeight(24);
        tabelaInstrutores.setFillsViewportHeight(true);
        tabelaInstrutores.getTableHeader().setReorderingAllowed(false);
        tabelaInstrutores.setRowSorter(ordenadorTabela);
        tabelaInstrutores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizandoTabela) {
                selecionarInstrutor();
            }
        });

        campoBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabela();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabela();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabela();
            }
        });

        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busca.add(new JLabel("Consultar por nome ou CPF:"));
        busca.add(campoBusca);

        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Instrutores Cadastrados"));
        painel.add(busca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaInstrutores), BorderLayout.CENTER);
        return painel;
    }

    private void filtrarTabela() {
        String busca = campoBusca.getText().trim();
        if (busca.isEmpty()) {
            ordenadorTabela.setRowFilter(null);
        } else {
            ordenadorTabela.setRowFilter(RowFilter.regexFilter(
                    "(?i)" + java.util.regex.Pattern.quote(busca), 1, 2));
        }
    }

    private void atualizarTabela() {
        List<Instrutor> instrutores = instrutorService.listarTodos();
        atualizandoTabela = true;
        try {
            modeloTabela.setRowCount(0);
            int linhaSelecionada = -1;

            for (Instrutor instrutor : instrutores) {
                modeloTabela.addRow(new Object[]{
                        instrutor.getId(),
                        instrutor.getNome(),
                        instrutor.getCpf(),
                        instrutor.getTelefone(),
                        instrutor.getEndereco(),
                        instrutor.getEspecialidade(),
                        instrutor.getCref()
                });
                if (idInstrutorSelecionado != null
                        && instrutor.getId() == idInstrutorSelecionado) {
                    linhaSelecionada = modeloTabela.getRowCount() - 1;
                }
            }

            if (linhaSelecionada >= 0) {
                int linhaVisivel = tabelaInstrutores.convertRowIndexToView(linhaSelecionada);
                if (linhaVisivel >= 0) {
                    tabelaInstrutores.setRowSelectionInterval(linhaVisivel, linhaVisivel);
                }
            } else if (idInstrutorSelecionado != null) {
                limparFormulario();
            }
        } finally {
            atualizandoTabela = false;
        }
    }

    private void selecionarInstrutor() {
        int linhaSelecionada = tabelaInstrutores.getSelectedRow();
        if (linhaSelecionada < 0) {
            return;
        }

        int linhaModelo = tabelaInstrutores.convertRowIndexToModel(linhaSelecionada);
        int id = (int) modeloTabela.getValueAt(linhaModelo, 0);
        Instrutor instrutor = instrutorService.buscarPorId(id);

        idInstrutorSelecionado = instrutor.getId();
        campoNome.setText(instrutor.getNome());
        campoCpf.setText(instrutor.getCpf());
        campoTelefone.setText(instrutor.getTelefone());
        campoEndereco.setText(instrutor.getEndereco());
        campoEspecialidade.setText(instrutor.getEspecialidade());
        campoCref.setText(instrutor.getCref());
    }

    private void salvarInstrutor() {
        try {
            if (idInstrutorSelecionado == null) {
                Instrutor instrutor = instrutorService.cadastrar(
                        campoNome.getText(),
                        campoCpf.getText(),
                        campoTelefone.getText(),
                        campoEndereco.getText(),
                        campoEspecialidade.getText(),
                        campoCref.getText());
                JOptionPane.showMessageDialog(this,
                        "Instrutor cadastrado com sucesso! ID: " + instrutor.getId());
            } else {
                Instrutor instrutor = instrutorService.buscarPorId(idInstrutorSelecionado);
                instrutor.setNome(campoNome.getText());
                instrutor.setCpf(campoCpf.getText());
                instrutor.setTelefone(campoTelefone.getText());
                instrutor.setEndereco(campoEndereco.getText());
                instrutor.setEspecialidade(campoEspecialidade.getText());
                instrutor.setCref(campoCref.getText());
                instrutorService.atualizar(instrutor);
                JOptionPane.showMessageDialog(this, "Instrutor atualizado com sucesso.");
            }

            atualizarTabela();
            limparFormulario();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível salvar o instrutor", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirInstrutor() {
        if (idInstrutorSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um instrutor para excluir.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este instrutor?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            instrutorService.excluir(idInstrutorSelecionado);
            atualizarTabela();
            limparFormulario();
            JOptionPane.showMessageDialog(this, "Instrutor excluído com sucesso.");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Não foi possível excluir o instrutor", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        idInstrutorSelecionado = null;
        tabelaInstrutores.clearSelection();
        campoNome.setText("");
        campoCpf.setText("");
        campoTelefone.setText("");
        campoEndereco.setText("");
        campoEspecialidade.setText("");
        campoCref.setText("");
    }
}
