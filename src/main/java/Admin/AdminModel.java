package Admin;

import com.digitalpersona.uareu.Fmd;
import java.util.Date;

public class AdminModel {

    private byte[] fingerprint;
    private byte[] fingerprintImage;
    private String staff_id;
    private String stFname;
    private String stMname;
    private String stLname;
//    private String position;
    private String sx;
    private String conNum;
    private String email;
    private String barangay;
    private String municipal;
    private Date bday;
    private byte[] image;
    private String collge;
    private String username;
    private String pass;

    private byte[] imageData;
    private byte[] fingerprintData;
    private byte[] fngrprntImageData;

    private Fmd fingerprintTemplate;

    public Fmd getFingerprintTemplate() {
        return fingerprintTemplate;
    }

    public void setFingerprintTemplate(Fmd fingerprintTemplate) {
        this.fingerprintTemplate = fingerprintTemplate;
    }

    public byte[] getFingerprintImage() {
        return fingerprintImage;
    }

    public void setFingerprintImage(byte[] fingerprintImage) {
        this.fingerprintImage = fingerprintImage;
    }

    public byte[] getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(byte[] fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getStFname() {
        return stFname;
    }

    public void setStFname(String stFname) {
        this.stFname = stFname;
    }

    public String getStMname() {
        return stMname;
    }

    public void setStMname(String stMname) {
        this.stMname = stMname;
    }

    public String getStLname() {
        return stLname;
    }

    public void setStLname(String stLname) {
        this.stLname = stLname;
    }

//    public String getPosition() {
//        return position;
//    }
//
//    public void setPosition(String position) {
//        this.position = position;
//    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getConNum() {
        return conNum;
    }

    public void setConNum(String conNum) {
        this.conNum = conNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBday() {
        return bday;
    }

    public void setBday(Date bday) {
        this.bday = bday;
    }

    public String getCollge() {
        return collge;
    }

    public void setCollge(String collge) {
        this.collge = collge;
    }

    public String getSx() {
        return sx;
    }

    public void setSx(String sx) {
        this.sx = sx;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getMunicipal() {
        return municipal;
    }

    public void setMunicipal(String municipal) {
        this.municipal = municipal;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public byte[] getFingerprintData() {
        return fingerprintData;
    }

    public void setFingerprintData(byte[] fingerprintData) {
        this.fingerprintData = fingerprintData;
    }

    public byte[] getFngrprntImageData() {
        return fngrprntImageData;
    }

    public void setFngrprntImageData(byte[] fngrprntImageData) {
        this.fngrprntImageData = fngrprntImageData;
    }
}
