package academia.interfacegrafica;

import academia.modelo.Aluno;
import academia.servico.AlunoService;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

public class TelaAlunos extends JDialog {

	private static final DateTimeFormatter FORMATO_DATA =
			DateTimeFormatter.ofPattern("dd/MM/uuuu")
					.withResolverStyle(ResolverStyle.STRICT);

	private final AlunoService alunoService;

	private final JTextField campoNome = new JTextField();
	private final JTextField campoCpf = new JTextField();
	private final JTextField campoTelefone = new JTextField();
	private final JTextField campoEndereco = new JTextField();
	private final JTextField campoDataNascimento = new JTextField();
	private final JTextField campoBusca = new JTextField(22);

	private final DefaultTableModel modeloTabela = new DefaultTableModel(
			new String[]{"ID", "Nome", "CPF", "Telefone", "Endereço", "Nascimento", "Status"}, 0) {
		@Override
		public boolean isCellEditable(int linha, int coluna) {
			return false;
		}
	};

	private final JTable tabelaAlunos = new JTable(modeloTabela);
	private final TableRowSorter<DefaultTableModel> ordenadorTabela =
			new TableRowSorter<>(modeloTabela);

	private Integer idAlunoSelecionado;
	private boolean atualizandoTabela;

	public TelaAlunos(JFrame telaPrincipal, AlunoService alunoService) {
		super(telaPrincipal, "Gerenciamento de Alunos", false);
		this.alunoService = alunoService;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(1100, 650);
		setLocationRelativeTo(telaPrincipal);

		criarComponentes();

		addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				atualizarTabela();
			}
		});
	}

	private void criarComponentes() {
		JPanel conteudo = new JPanel(new BorderLayout(10, 10));
		conteudo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		conteudo.add(criarFormulario(), BorderLayout.NORTH);
		conteudo.add(criarPainelTabela(), BorderLayout.CENTER);
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
		campos.add(new JLabel("Nascimento (dd/MM/aaaa):"));
		campos.add(campoDataNascimento);
		campos.add(new JLabel());
		campos.add(new JLabel());

		JButton botaoNovo = new JButton("Novo");
		JButton botaoSalvar = new JButton("Salvar");
		JButton botaoExcluir = new JButton("Excluir");

		botaoNovo.addActionListener(e -> limparFormulario());
		botaoSalvar.addActionListener(e -> salvarAluno());
		botaoExcluir.addActionListener(e -> excluirAluno());

		JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		botoes.add(botaoNovo);
		botoes.add(botaoSalvar);
		botoes.add(botaoExcluir);

		JPanel painel = new JPanel(new BorderLayout(0, 10));
		painel.setBorder(BorderFactory.createTitledBorder("Dados do Aluno"));
		painel.add(campos, BorderLayout.CENTER);
		painel.add(botoes, BorderLayout.SOUTH);
		return painel;
	}

	private JPanel criarPainelTabela() {
		tabelaAlunos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabelaAlunos.setRowHeight(24);
		tabelaAlunos.setFillsViewportHeight(true);
		tabelaAlunos.getTableHeader().setReorderingAllowed(false);
		tabelaAlunos.setRowSorter(ordenadorTabela);
		tabelaAlunos.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && !atualizandoTabela) {
				selecionarAluno();
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
		painel.setBorder(BorderFactory.createTitledBorder("Alunos Cadastrados"));
		painel.add(busca, BorderLayout.NORTH);
		painel.add(new JScrollPane(tabelaAlunos), BorderLayout.CENTER);
		return painel;
	}

	private void filtrarTabela() {
		String busca = campoBusca.getText().trim();
		if (busca.isEmpty()) {
			ordenadorTabela.setRowFilter(null);
		} else {
			ordenadorTabela.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(busca), 1, 2));
		}
	}

	private void atualizarTabela() {
		List<Aluno> alunos = alunoService.listarTodos();
		atualizandoTabela = true;
		try {
			modeloTabela.setRowCount(0);
			int linhaSelecionada = -1;

			for (Aluno aluno : alunos) {
				modeloTabela.addRow(new Object[]{
						aluno.getId(),
						aluno.getNome(),
						aluno.getCpf(),
						aluno.getTelefone(),
						aluno.getEndereco(),
						aluno.getDataNascimento().format(FORMATO_DATA),
						aluno.isAtivo() ? "Ativo" : "Inativo"
				});
				if (idAlunoSelecionado != null && aluno.getId() == idAlunoSelecionado) {
					linhaSelecionada = modeloTabela.getRowCount() - 1;
				}
			}

			// Restaura a seleção respeitando a busca e a ordenação, sem sobrescrever a edição.
			if (linhaSelecionada >= 0) {
				int linhaVisivel = tabelaAlunos.convertRowIndexToView(linhaSelecionada);
				if (linhaVisivel >= 0) {
					tabelaAlunos.setRowSelectionInterval(linhaVisivel, linhaVisivel);
				}
			} else if (idAlunoSelecionado != null) {
				limparFormulario();
			}
		} finally {
			atualizandoTabela = false;
		}
	}

	private void selecionarAluno() {
		int linhaSelecionada = tabelaAlunos.getSelectedRow();
		if (linhaSelecionada < 0) {
			return;
		}

		int linhaModelo = tabelaAlunos.convertRowIndexToModel(linhaSelecionada);
		int id = (int) modeloTabela.getValueAt(linhaModelo, 0);
		Aluno aluno = alunoService.buscarPorId(id);

		idAlunoSelecionado = aluno.getId();
		campoNome.setText(aluno.getNome());
		campoCpf.setText(aluno.getCpf());
		campoTelefone.setText(aluno.getTelefone());
		campoEndereco.setText(aluno.getEndereco());
		campoDataNascimento.setText(aluno.getDataNascimento().format(FORMATO_DATA));
	}

	private void salvarAluno() {
		try {
			LocalDate dataNascimento = LocalDate.parse(campoDataNascimento.getText().trim(), FORMATO_DATA);

			if (idAlunoSelecionado == null) {
				Aluno aluno = alunoService.cadastrar(
						campoNome.getText(),
						campoCpf.getText(),
						campoTelefone.getText(),
						campoEndereco.getText(),
						dataNascimento);
				JOptionPane.showMessageDialog(this,
						"Aluno cadastrado com sucesso! ID: " + aluno.getId());
			} else {
				Aluno aluno = alunoService.buscarPorId(idAlunoSelecionado);
				aluno.setNome(campoNome.getText());
				aluno.setCpf(campoCpf.getText());
				aluno.setTelefone(campoTelefone.getText());
				aluno.setEndereco(campoEndereco.getText());
				aluno.setDataNascimento(dataNascimento);
				alunoService.atualizar(aluno);
				JOptionPane.showMessageDialog(this, "Aluno atualizado com sucesso.");
			}

			atualizarTabela();
			limparFormulario();
		} catch (DateTimeParseException e) {
			JOptionPane.showMessageDialog(this,
					"Data inválida. Digite no formato dd/MM/aaaa.",
					"Dados inválidos", JOptionPane.WARNING_MESSAGE);
		} catch (RuntimeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"Não foi possível salvar o aluno", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void excluirAluno() {
		if (idAlunoSelecionado == null) {
			JOptionPane.showMessageDialog(this, "Selecione um aluno para excluir.");
			return;
		}

		int confirmacao = JOptionPane.showConfirmDialog(this,
				"Deseja realmente excluir este aluno?",
				"Confirmar exclusão", JOptionPane.YES_NO_OPTION);
		if (confirmacao != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			alunoService.excluir(idAlunoSelecionado);
			atualizarTabela();
			limparFormulario();
			JOptionPane.showMessageDialog(this, "Aluno excluído com sucesso.");
		} catch (RuntimeException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"Não foi possível excluir o aluno", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void limparFormulario() {
		idAlunoSelecionado = null;
		tabelaAlunos.clearSelection();
		campoNome.setText("");
		campoCpf.setText("");
		campoTelefone.setText("");
		campoEndereco.setText("");
		campoDataNascimento.setText("");
	}
}
