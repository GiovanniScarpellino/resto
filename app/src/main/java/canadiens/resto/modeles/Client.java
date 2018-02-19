package canadiens.resto.modeles;

/**
 * Created by nicog on 19/02/2018.
 */

public class Client {

    private String idRestaurant;
    private String nom;
    private String telephone;
    private String mail;
    private String motDePasse;


    public Client(){

    }

    public Client(String idRestaurant, String nom, String telephone, String mail, String motDePasse) {
        this.idRestaurant = idRestaurant;
        this.nom = nom;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
    }

    public Client(String nom, String telephone, String mail, String motDePasse) {
        this.nom = nom;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
    }

    public String getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(String idRestaurant) {
        this.idRestaurant = idRestaurant;
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

    @Override
    public String toString() {
        return "Client{" +
                "idRestaurant='" + idRestaurant + '\'' +
                ", nom='" + nom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", mail='" + mail + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                '}';
    }
}
