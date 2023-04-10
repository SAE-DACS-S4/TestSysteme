package fr.umontpellier;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginTask extends ConnectTask {
    private String host = "localhost";
    private int port = 5056;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    String log;
    String password;

    public LoginTask(String log,String password,AtomicInteger successCounter) {
        super(successCounter);
        this.log=log;
        this.password=password;
    }

    @Override
    public void run() {
        try {
            SSLSocketFactory sslSocketFactory = super.createCustomSSLSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            this.ois = new ObjectInputStream(sslSocket.getInputStream());
            this.oos = new ObjectOutputStream(sslSocket.getOutputStream());
            login(log, password);
        } catch (Exception e) {
            System.out.println("Connexion échouée.");
        }
    }

    private void login(String log, String password) throws NoSuchAlgorithmException {
        try {
            oos.writeUTF("login");
            oos.flush();
            oos.writeUTF(log + ":" + Hachage.hachage(password));
            oos.flush();
            System.out.println("Login envoyé");
            String line = ois.readUTF();
            System.out.println("serveur: " + line);
            if(line.equals("connexionauthorise")){
                super.incrementAndGet();
            }
        } catch (IOException e) {
            System.out.println("EOF error");
        }
    }
}

/**
 * Classe permettant de hacher un mot de passe
 */
class Hachage {
    String hash;

    public Hachage(String password) throws NoSuchAlgorithmException {
        hash = hachage(password);
    }

    public static String hachage(String password) throws NoSuchAlgorithmException {
        MessageDigest msg = MessageDigest.getInstance("SHA-256");
        byte[] hash = msg.digest(password.getBytes(StandardCharsets.UTF_8));

        // convertir bytes en hexadécimal
        StringBuilder s = new StringBuilder();
        for (byte b : hash) {
            s.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return s.toString();
    }
}

