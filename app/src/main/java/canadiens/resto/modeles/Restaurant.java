package canadiens.resto.modeles;

public class Restaurant {
    private String idRestaurant;
    private String nom;
    private String adresse;
    private double longitude;
    private double latitude;
    private String telephone;
    private String mail;
    private String motDePasse;
    private String description;

    public Restaurant() {
    }

    public Restaurant(String idRestaurant, String nom, String adresse, double longitude, double latitude, String telephone, String mail, String motDePasse, String description) {
        this.idRestaurant = idRestaurant;
        this.nom = nom;
        this.adresse = adresse;
        this.longitude = longitude;
        this.latitude = latitude;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.description = description;
    }

    public Restaurant(String idRestaurant, String nom, double longitude, double latitude, String description) {
        this.idRestaurant = idRestaurant;
        this.nom = nom;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    public Restaurant(String nom, String adresse, double longitude, double latitude, String telephone, String mail, String motDePasse, String description) {
        this.nom = nom;
        this.adresse = adresse;
        this.longitude = longitude;
        this.latitude = latitude;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.description = description;
    }

    public Restaurant(String idRestaurant, String nom, String adresse, String telephone, String mail, String motDePasse, String description) {
        this.idRestaurant = idRestaurant;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.mail = mail;
        this.motDePasse = motDePasse;
        this.description = description;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "idRestaurant='" + idRestaurant + '\'' +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", telephone='" + telephone + '\'' +
                ", mail='" + mail + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
