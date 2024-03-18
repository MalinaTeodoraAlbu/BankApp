package Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class User {
    private String token;
    private String nume;
    private String numarTelefon;
    private String adresa;
    private Date ziNastere;
    private String CNP;
    private ContBancar[] conturiBancare;
    private String password;

    public User(String nume, String numarTelefon, String adresa, String CNP, String password, String ziNastere,ContBancar[] conturiBancare) {
        this.nume = nume;
        this.token = generateUserID();;
        this.numarTelefon = numarTelefon;
        this.adresa = adresa;
        this.CNP = CNP;
        this.password = password;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date data = dateFormat.parse(ziNastere);
            this.ziNastere = data;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.conturiBancare = conturiBancare;
    }

    public User() {
        this.nume = "";
        this.token = generateUserID();;
        this.numarTelefon = "";
        this.adresa = "";
        this.CNP = "";
        this.password = "password";
        this.ziNastere = null;
        this.conturiBancare = null;
    }


    private String generateUserID() {
        Random rand = new Random();
        StringBuilder idBuilder = new StringBuilder("RO");
        for (int i = 0; i < 5; i++) {
            idBuilder.append(rand.nextInt(10));
        }
        return idBuilder.toString();
    }
    public String getNume() {
        return nume;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getTokenUser() {
        return token;
    }

    public void setTokenUser(String idUser) {
        this.token = idUser;
    }

    public String getNumarTelefon() {
        return numarTelefon;
    }

    public void setNumarTelefon(String numarTelefon) {
        this.numarTelefon = numarTelefon;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public Date getZiNastere() {
        return ziNastere;
    }

    public void setZiNastere(Date ziNastere) {
        this.ziNastere = ziNastere;
    }

    public ContBancar[] getConturiBancare() {
        return conturiBancare;
    }


    public void setConturiBancare(ContBancar[] conturiBancare) {
        ContBancar[] copy = new ContBancar[conturiBancare.length];
        for(int i = 0; i<copy.length;i++){
            copy[i] =conturiBancare[i];
        }
        this.conturiBancare = copy;
    }


    public String getDataNastereUser() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(ziNastere);
    }

    public void adaugaNouContBancar(ContBancar contBancar) {
        if (conturiBancare == null) {
            conturiBancare = new ContBancar[1];
            conturiBancare[0] = contBancar;
        } else {
            ContBancar[] contNou = new ContBancar[conturiBancare.length + 1];
            for(int i = 0; i < conturiBancare.length; i++) {
                contNou[i] = conturiBancare[i];
            }
            contNou[contNou.length - 1] = contBancar;
            this.conturiBancare = contNou;
        }
    }


    @Override
    public String toString() {
        String result = "Detalii client: " +
                "Nume: " + nume +
                ", token-ul: " + token +
                ", numar de telefon: " + numarTelefon +
                ", adresa: " + adresa +
                ", CNP: " + CNP + ", ziua de nastere " + getDataNastereUser();

        if (conturiBancare != null && conturiBancare.length > 0) {
            result += ", numar cont bancar " + conturiBancare[0].getNrContBancar();
        } else {
            result += ". Nu ai un cont bancar asignat.";
        }

        return result;
    }

    public void afisareDetaliiCont(){
        if(conturiBancare == null){
            System.out.println("Nu ai un cont bancar activ!");
        }
        else {
            for(ContBancar c : conturiBancare){
                System.out.println("Contul bancar: " + c);
                c.afiseazaTranzactii();
            }

        }
    }


}
