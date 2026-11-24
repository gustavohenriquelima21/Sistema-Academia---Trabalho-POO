package academia.dados;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

final class ArquivoTexto {
    private ArquivoTexto() {}

    static void salvarAtomico(Path arquivo, List<String> linhas) throws IOException {
        Path pasta = arquivo.toAbsolutePath().getParent();
        Files.createDirectories(pasta);
        Path temporario = Files.createTempFile(pasta, arquivo.getFileName().toString(), ".tmp");
        try {
            Files.write(temporario, linhas, StandardCharsets.UTF_8);
            try {
                Files.move(temporario, arquivo, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
