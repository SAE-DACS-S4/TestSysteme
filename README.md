### Configuration SSL/TLS :
- Vérifier les versions, les algorithmes de chiffrement et les certificats utilisés.
- Tester la résistance aux attaques « man-in-the-middle ».


Avant :
![Before SSL.png](src%2Ftest%2Fresources%2FBefore%20SSL.png)

Après :

![captureSSL.png](src%2Ftest%2Fresources%2FcaptureSSL.png)
Client Hello entre l'application cliente et le serveur, capturée via WireShark.

Comme l'indique la capture de trame les données sont bien chiffrées entre composantes applicatives.
### Performance et la charge :
- Mesurer la capacité maximale du serveur pour gérer les connexions simultanées et les requêtes.
- Simuler des scénarios de forte charge pour vérifier la robustesse et la stabilité du serveur.

Suite de tests disponible :
[testSysteme.java](src%2Ftest%2Fjava%2Ftest%2FtestSysteme.java)
### Authentification et l'autorisation :
- Tester les mécanismes d'authentification pour les utilisateurs autorisés.
- Vérifier l'accès restreint pour les utilisateurs non autorisés.

Suite de tests disponible :
[testSysteme.java](src%2Ftest%2Fjava%2Ftest%2FtestSysteme.java)