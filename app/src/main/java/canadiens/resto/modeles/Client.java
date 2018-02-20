package canadiens.resto.modeles;

/**
 * Created by nicog on 19/02/2018.
 */

public class Client {

    private String idClient;
    private String nom;
    private String prenom;
    private String telephone;
    private String mail;
    private String motDePasse;


    public Client(){

    }

    public Client(String idClient, String nom, String prenom, String telephone, String mail, String motDePasse) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
    }

    public Client(String nom, String telephone, String mail, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
    }

    public String getIdRestaurant() {
        return idClient;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idClient = idClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public String toString() {
        return "Client{" +
                "idClient='" + idClient + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", mail='" + mail + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                '}';
    }
}
