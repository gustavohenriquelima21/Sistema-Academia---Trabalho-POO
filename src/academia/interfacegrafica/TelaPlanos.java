package academia.interfacegrafica;

import academia.dados.PlanoDAO;
import academia.modelo.Plano;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class TelaPlanos extends JDialog {
    private final PlanoDAO planoDAO;
    private final JTextField campoNome = new JTextField();
    private final JTextField campoValor = new JTextField();
    private final JTextField campoDuracao = new JTextField();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nome", "Valor mensal", "Duração (meses)", "Total", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabela = new JTable(modelo);
    private Integer idSelecionado;
    private boolean atualizando;

    public TelaPlanos(JFrame owner, PlanoDAO planoDAO) {
        super(owner, "Gerenciamento de Planos", false);
        this.planoDAO = planoDAO;
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(owner);
        montarTela();
        atualizarTabela();
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override public void windowGainedFocus(java.awt.event.WindowEvent e) { atualizarTabela(); }
        });
    }

    private void montarTela() {
        JPanel conteudo = new JPanel(new BorderLayout(10, 10));
        conteudo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel campos = new JPanel(new GridLayout(2, 4, 10, 8));
        campos.add(new JLabel("Nome:")); campos.add(campoNome);
        campos.add(new JLabel("Valor mensal (R$):")); campos.add(campoValor);
        campos.add(new JLabel("Duração (meses):")); campos.add(campoDuracao);
        campos.add(new JLabel()); campos.add(new JLabel());
        JPanel formulario = new JPanel(new BorderLayout(0, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Dados do Plano"));
        formulario.add(campos, BorderLayout.CENTER);

        JButton novo = new JButton("Novo");
        JButton salvar = new JButton("Salvar");
        JButton excluir = new JButton("Excluir");
        novo.addActionListener(e -> limparFormulario());
        salvar.addActionListener(e -> salvarPlano());
        excluir.addActionListener(e -> excluirPlano());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.add(novo); botoes.add(salvar); botoes.add(excluir);
        formulario.add(botoes, BorderLayout.SOUTH);

        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !atualizando) selecionarPlano();
        });
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("Planos Cadastrados"));
        painelTabela.add(new JScrollPane(tabela), BorderLayout.CENTER);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(painelTabela, BorderLayout.CENTER);

        JButton voltar = new JButton("Voltar ao menu");
        voltar.addActionListener(e -> { dispose(); getOwner().toFront(); getOwner().requestFocus(); });
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.add(voltar);
        conteudo.add(rodape, BorderLayout.SOUTH);
        setContentPane(conteudo);
    }

    private void atualizarTabela() {
        int linhaSelecionada = -1;
        atualizando = true;
        try {
            modelo.setRowCount(0);
            List<Plano> planos = planoDAO.listarTodos();
            for (Plano plano : planos) {
                modelo.addRow(new Object[]{plano.getId(), plano.getNome(),
                        String.format(new Locale("pt", "BR"), "R$ %.2f", plano.getValorMensal()),
                        plano.getDuracaoMeses(),
                        String.format(new Locale("pt", "BR"), "R$ %.2f", plano.calcularValorTotal()),
                        plano.isAtivo() ? "Ativo" : "Inativo"});
                if (idSelecionado != null && plano.getId() == idSelecionado) linhaSelecionada = modelo.getRowCount() - 1;
            }
            if (linhaSelecionada >= 0) tabela.setRowSelectionInterval(linhaSelecionada, linhaSelecionada);
            else if (idSelecionado != null) limparFormulario();
        } finally { atualizando = false; }
    }

    private void selecionarPlano() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = ((Number) modelo.getValueAt(row, 0)).intValue();
        Plano plano = planoDAO.buscarPorId(idSelecionado);
        if (plano == null) { limparFormulario(); return; }
        campoNome.setText(plano.getNome());
        campoValor.setText(String.format(Locale.US, "%.2f", plano.getValorMensal()));
        campoDuracao.setText(String.valueOf(plano.getDuracaoMeses()));
    }

    private void salvarPlano() {
        try {
            String nome = campoNome.getText().trim();
            double valor = Double.parseDouble(campoValor.getText().trim().replace(',', '.'));
            int duracao = Integer.parseInt(campoDuracao.getText().trim());
            if (nome.isEmpty() || !Double.isFinite(valor) || valor < 0 || duracao <= 0)
                throw new IllegalArgumentException("Informe nome, valor válido e duração maior que zero.");
            Plano plano = idSelecionado == null ? new Plano(planoDAO.proximoId(), nome, valor, duracao)
                    : planoDAO.buscarPorId(idSelecionado);
            if (plano == null) throw new IllegalStateException("O plano selecionado não existe mais.");
            plano.setNome(nome); plano.setValorMensal(valor); plano.setDuracaoMeses(duracao);
            if (idSelecionado == null) planoDAO.inserir(plano); else planoDAO.atualizar(plano);
            idSelecionado = plano.getId();
            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Plano salvo com sucesso.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage() == null ? "Informe valores numéricos válidos." : e.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Não foi possível salvar o plano", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPlano() {
        if (idSelecionado == null) { JOptionPane.showMessageDialog(this, "Selecione um plano para excluir."); return; }
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este plano?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) return;
        try {
            planoDAO.excluir(idSelecionado);
            limparFormulario(); atualizarTabela();
            JOptionPane.showMessageDialog(this, "Plano excluído com sucesso.");
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Não foi possível excluir o plano", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        idSelecionado = null; tabela.clearSelection(); campoNome.setText("");
        campoValor.setText(""); campoDuracao.setText("");
    }
}
