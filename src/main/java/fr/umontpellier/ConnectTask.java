package fr.umontpellier;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;


public class ConnectTask implements Runnable {
    private String host = "localhost";
    private int port = 5056;
    private AtomicInteger successCounter;

    public ConnectTask(AtomicInteger successCounter) {
        this.successCounter = successCounter;
    }

    SSLSocketFactory createCustomSSLSocketFactory() throws Exception {
        InputStream truststoreInputStream = ConnectTask.class.getResourceAsStream("/certificat/truststore.jks");
        if (truststoreInputStream == null) {
            throw new FileNotFoundException("Le fichier 'truststore.jks' est introuvable.");
        }
        String truststorePassword = "testtest";
        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(truststoreInputStream, truststorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }

    @Override
    public void run() {
        try {
            SSLSocketFactory sslSocketFactory = createCustomSSLSocketFactory();
            SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);
            sslSocket.startHandshake();
            successCounter.incrementAndGet();
            sslSocket.close();
        } catch (Exception e) {
            System.out.println("Connexion échouée.");
        }
    }
}
