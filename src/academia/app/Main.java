package academia.app;
import academia.dados.PlanoDAO;
import academia.interfacegrafica.TelaPrincipal;
import academia.servico.AlunoService;
import academia.servico.InstrutorService;
import academia.servico.MatriculaService;
import academia.servico.TreinoService;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciarAplicacao);
    }

    private static void iniciarAplicacao() {
        configurarVisual();

        try {
            TreinoService treinoService = new TreinoService();
            AlunoService alunoService = new AlunoService(treinoService);
            PlanoDAO planoDAO = new PlanoDAO();
            MatriculaService matriculaService = new MatriculaService();
            InstrutorService instrutorService = new InstrutorService(treinoService);

            TelaPrincipal telaPrincipal = new TelaPrincipal(
                    alunoService, planoDAO, matriculaService, instrutorService, treinoService);
            telaPrincipal.setVisible(true);
            if (treinoService.getAvisoCarga() != null) {
                JOptionPane.showMessageDialog(telaPrincipal, treinoService.getAvisoCarga(),
                        "Arquivo de treinos recuperado", JOptionPane.WARNING_MESSAGE);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Não foi possível iniciar o sistema.\n" + e.getMessage(),
                    "Erro de inicialização", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void configurarVisual() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                 | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Não foi possível aplicar o tema do sistema: " + e.getMessage());
        }

        FontUIResource fontePadrao = new FontUIResource(Font.SANS_SERIF, Font.PLAIN, 14);
        for (Object chave : UIManager.getDefaults().keySet().toArray()) {
            if (UIManager.get(chave) instanceof Font) {
                UIManager.put(chave, fontePadrao);
            }
        }
    }
}
