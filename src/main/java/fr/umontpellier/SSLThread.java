package fr.umontpellier;

import fr.umontpellier.utils.Hachage;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SSLThread extends Thread {
    public void run() {

        new Thread(() -> {
            SSLSocket connectionAuServeur = null;
            try {
                connectionAuServeur = makeSSL();
                ObjectInputStream ois = new ObjectInputStream(connectionAuServeur.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(connectionAuServeur.getOutputStream());
                oos.writeUTF("login");
                oos.flush();
                oos.writeUTF("axelf:" + Hachage.hachage("123"));
                oos.flush();
                System.out.println("Login envoyé");
                String line = ois.readUTF();
                System.out.println("serveur: " + line);

            } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException |
                     KeyManagementException e) {
                this.interrupt();
            }
        }).start();

    }

    static SSLSocket makeSSL() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, KeyManagementException {
        // Récupérer le chemin du fichier truststore.jks

        InputStream truststoreInputStream = SSLThread.class.getResourceAsStream("/certificat/truststore.jks");
        if (truststoreInputStream == null) {
            throw new FileNotFoundException("Le fichier 'truststore.jks' est introuvable.");
        }

        String truststorePassword = "testtest";

        // Charger le truststore
        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(truststoreInputStream, truststorePassword.toCharArray());


        // Initialiser le TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        // Initialiser le SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // Créer le SSLSocket
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        return (SSLSocket) sslSocketFactory.createSocket("localhost", 5056);
    }
}

