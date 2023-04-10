package test;

import fr.umontpellier.ConnectTask;
import fr.umontpellier.LoginTask;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class testSysteme {

    /**
     * Instanciation de l'image docker de l'application système.
     * La suite de Test suivante requiert que le docker soit installé sur la machine, et en cours d'exécution.
     */
    @BeforeAll
    public static void setUp() {
        try {
            InputStream inputStream = ConnectTask.class.getResourceAsStream("/docker-compose.yml");
            if (inputStream == null) {
                throw new RuntimeException("docker-compose.yml not found in classpath resources");
            }
            File tempFile = File.createTempFile("docker-compose", ".yml");
            tempFile.deleteOnExit();
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            ProcessBuilder pull = new ProcessBuilder("docker-compose", "-f", tempFile.getAbsolutePath(), "pull");
            pull.inheritIO();
            Process pullProcess = pull.start();
            pullProcess.waitFor();
            ProcessBuilder processBuilder = new ProcessBuilder("docker-compose", "-f", tempFile.getAbsolutePath(), "up", "-d");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
            assertTrue(process.exitValue() == 0);
            sleep(1000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testConnectionLoad() throws InterruptedException {
        int concurrentConnections = 100; // Number of concurrent connections to test
        AtomicInteger successCounter = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentConnections);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < concurrentConnections; i++) {
            executorService.execute(new ConnectTask(successCounter));
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Total successful connections: " + successCounter.get());
        System.out.println("Elapsed time: " + elapsedTime + " ms");
    }


    static Stream<Arguments> LogsSource() {
        return Stream.of(
                Arguments.of("dimitric", "123", 1),
                Arguments.of("dimitricopley", "123", 0),
                Arguments.of("dimitric", "1234", 0),
                Arguments.of("simonr", "123", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("LogsSource")
    public void logInTest(String log, String password, int expected) throws InterruptedException {
        AtomicInteger hasLoggedIn = new AtomicInteger(0);
        new LoginTask(log, password, hasLoggedIn).run();
        sleep(1000);
        assertEquals(expected, hasLoggedIn.get());
    }
}

/*  Rappel :
    Assurez-vous d'avoir Docker installé et en daemon sur votre machine avant d'éxucuter les tests.
*/