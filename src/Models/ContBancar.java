package Models;

import DataAccess.ContBancarRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class ContBancar {

    private int nrContBancar;
    private float soldCont;
    private String userID;
    private int contor = 3000;
    private String IBAN;
    private TipContBancar tipContBancar;
    private List<Tranzactie> tranzactiiIstoric = new ArrayList<>();

    static ContBancarRepository contBancarRepository = new ContBancarRepository();
    private static List<String> ibansExistente = new ArrayList<>();


    public ContBancar() {
        this.nrContBancar = ++contor;
        this.soldCont = 0;
        this.userID = null;
        this.tranzactiiIstoric = new ArrayList<>();
        this.IBAN = generateIBANUnic();
    }


    public ContBancar(float soldCont, String userID, List<Tranzactie> tranzactiiIstoric, TipContBancar tipContBancar) {
        this.nrContBancar = ++contor;
        this.soldCont = soldCont;
        this.userID = userID;
        this.tranzactiiIstoric = tranzactiiIstoric;
        this.tipContBancar = tipContBancar;
        this.IBAN = generateIBANUnic();
    }

    public int getNrContBancar() {
        return nrContBancar;
    }

    public void setNrContBancar(int nrContBancar) {
        this.nrContBancar = nrContBancar;
    }

    public float getSoldCont() {
        return soldCont;
    }

    public void setSoldCont(float soldCont) {
        this.soldCont = soldCont;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public List<Tranzactie> getTranzactiiIstoric() {
        return tranzactiiIstoric;
    }


    public void setTranzactiiIstoric(List<Tranzactie> tranzactiiIstoric) {

        for(Tranzactie t : tranzactiiIstoric){
         addTranzactie(t);
        }
    }

    public String getIBAN() {
        return IBAN;
    }

    public TipContBancar getTipContBancar() {
        return tipContBancar;
    }

    public Tranzactie getTranzactie(int index){
        if (index >= 0 && index < tranzactiiIstoric.size()) {
            return tranzactiiIstoric.get(index);
        } else {
            return null;
        }
    }
    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public void setTipContBancar(TipContBancar tipContBancar) {
        this.tipContBancar = tipContBancar;
    }

    public static List<String> getIbansExistente() {
        return ibansExistente;
    }

    public static void setIbansExistente(List<String> ibansExistente) {
        ContBancar.ibansExistente = ibansExistente;
    }

    public static void addINIbansExistente(String iban) {
        ContBancar.ibansExistente.add(iban);
    }


    public void afiseazaTranzactii() {
        if (tranzactiiIstoric.isEmpty()) {
            System.out.println("Nu s-a facut nicio tranzactie momentan!");
        } else {
            System.out.println("Istoric Tranzactii: ");
            for (Tranzactie tranzactie : tranzactiiIstoric) {
               if(tranzactie.getIBANExpeditor().equals(IBAN)){
                   if(!(tranzactie.getTipTranzactie().equals(TipTranzactie.PRIMIT))){
                       System.out.println(tranzactie);
                   }
               }
               else if((tranzactie.getIBANDestinatar().equals(IBAN))){
                   System.out.println(tranzactie);
               }
            }
        }
    }



    public void addTranzactie(Tranzactie t) {
        tranzactiiIstoric.add(t);
        List<ContBancar> contBancar = contBancarRepository.readAll();
        if (t.getTipTranzactie().equals(TipTranzactie.DEPOZIT) || (t.getTipTranzactie().equals(TipTranzactie.PRIMIT) && t.getIBANDestinatar().equals(this.IBAN))) {
            this.soldCont += t.getSumaTranzactie();
        }
        else if(t.getTipTranzactie().equals(TipTranzactie.RETRAGERE) || t.getTipTranzactie().equals(TipTranzactie.TRANSFER)){
            if (soldCont >= t.getSumaTranzactie()) {
                soldCont -= t.getSumaTranzactie();
            } else {
                System.out.println("Tranzactie nefinalizata! Fonduri insuficiente!");
                return;
            }

            for (ContBancar c : contBancar) {
                if (c.getIBAN().equals(t.getIBANDestinatar()) && !(t.getIBANDestinatar().equals(t.getIBANExpeditor()))) {
                    Tranzactie tranzactieDestinatar = new Tranzactie();
                    tranzactieDestinatar.setDataTranzactie(new Date());
                    tranzactieDestinatar.setSumaTranzactie(t.getSumaTranzactie());
                    tranzactieDestinatar.setTipTranzactie(TipTranzactie.PRIMIT);
                    tranzactieDestinatar.setDescrie(t.getDescrie());
                    tranzactieDestinatar.setIBANDestinatar(t.getIBANDestinatar());
                    tranzactieDestinatar.setUserId(t.getUserId());
                    tranzactieDestinatar.setIBANExpeditor(t.getIBANExpeditor());
                    c.getTranzactiiIstoric().add(tranzactieDestinatar);
                    c.setSoldCont(c.getSoldCont() + t.getSumaTranzactie());
                }
            }
        }
    }


    private String generateIBANUnic () {
            Random rand = new Random();
            StringBuilder ibanBuilder = new StringBuilder("RO");
            while (true) {
                for (int i = 0; i < 18; i++) {
                    ibanBuilder.append(rand.nextInt(10));
                }
                String iban = ibanBuilder.toString();
                if (!ibansExistente.contains(iban)) {
                    ibansExistente.add(iban);
                    return iban;
                }
            }
        }

        @Override
        public String toString () {
            return "Detalii cont bancar: " +
                    " numar cont: " + nrContBancar +
                    ", sold curent: " + soldCont +
                    ", tip cont: " + tipContBancar +
                    ", IBAN: " + IBAN;
        }
    }
