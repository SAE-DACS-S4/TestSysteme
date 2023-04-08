package test;

import fr.umontpellier.SSLThread;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.security.NoSuchAlgorithmException;

import static java.lang.System.exit;
import static java.lang.Thread.activeCount;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testSysteme {

    @BeforeAll
    public static void setUp() throws Exception {
        try {
            // Charger le fichier docker-compose.yml depuis les ressources du classpath
            InputStream inputStream = SSLThread.class.getResourceAsStream("/docker-compose.yml");
            if (inputStream == null) {
                throw new RuntimeException("docker-compose.yml not found in classpath resources");
            }

            // Créer un fichier temporaire pour stocker le docker-compose.yml extrait du JAR
            File tempFile = File.createTempFile("docker-compose", ".yml");
            tempFile.deleteOnExit();
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Exécuter Docker Compose en utilisant le fichier temporaire et en mode détaché
            ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "-f", tempFile.getAbsolutePath(), "up", "-d");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
            sleep(100);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testNbConnections() {
        boolean continuer = true;
        while (continuer) {
            try {
                SSLThread sslThread = new SSLThread();
                sslThread.run();
            } catch (Exception e) {
                continuer = false;
            }
            assertTrue(activeCount()>100);
        }
    }


}
