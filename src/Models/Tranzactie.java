package Models;

import DataAccess.TranzactieRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Clasa Tranzactie reprezintă o tranzacție efectuată în cadrul unui cont bancar.
 */
public class Tranzactie {
    private static int idTranzactieCounter = 0;
    private int idTranzactie;
    private Date dataTranzactie;
    private float sumaTranzactie;
    private TipTranzactie tipTranzactie;
    private String descrie;
    private String IBANDestinatar;
    private String userId;
    private String IBANExpeditor;


    public Tranzactie() {
        this.dataTranzactie = null;
        this.sumaTranzactie = 0f;
        this.tipTranzactie = TipTranzactie.DEPOZIT;
        this.descrie = null;
        this.IBANDestinatar = null;
        this.userId = null;
        this.IBANExpeditor =null;
        idTranzactie = ++idTranzactieCounter;
    }
    public Tranzactie(String dataTranzactie, float sumaTranzactie, TipTranzactie tipTranzactie, String descrie, String IBANDestinatar , String userId, String IBANExpeditor) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date data = dateFormat.parse(dataTranzactie);
            this.dataTranzactie = data;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.sumaTranzactie = sumaTranzactie;
        this.tipTranzactie = tipTranzactie;
        this.descrie = descrie;
        this.IBANDestinatar = IBANDestinatar;
        this.userId = userId;
        idTranzactie = ++idTranzactieCounter;
        this.IBANExpeditor = IBANExpeditor;
    }

    public Tranzactie(Tranzactie other) {
        this.idTranzactie = ++idTranzactieCounter;
        this.dataTranzactie = new Date();
        this.sumaTranzactie = other.sumaTranzactie;
        this.tipTranzactie = other.tipTranzactie;
        this.descrie = other.descrie;
        this.IBANDestinatar = other.IBANDestinatar;
        this.userId = other.userId;
    }

    public String getIBANExpeditor() {
        return IBANExpeditor;
    }

    public void setIBANExpeditor(String IBANExpeditor) {
        this.IBANExpeditor = IBANExpeditor;
    }

    public  void setIdTranzactie(int idTranzactie) {
        this.idTranzactie = idTranzactie;
        idTranzactieCounter = idTranzactie;
    }

    public String getDataTranzactieFormatted() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(dataTranzactie);
    }



    public int getIdTranzactie() {
        return idTranzactie;
    }

    public Date getDataTranzactie() {
        return dataTranzactie;
    }

    public void setDataTranzactie(Date dataTranzactie) {
        this.dataTranzactie = dataTranzactie;
    }

    public float getSumaTranzactie() {
        return sumaTranzactie;
    }

    public void setSumaTranzactie(float sumaTranzactie) {
        this.sumaTranzactie = sumaTranzactie;
    }

    public TipTranzactie getTipTranzactie() {
        return tipTranzactie;
    }

    public void setTipTranzactie(TipTranzactie tipTranzactie) {
        this.tipTranzactie = tipTranzactie;
    }

    public String getDescrie() {
        return descrie;
    }

    public void setDescrie(String descrie) {
        this.descrie = descrie;
    }

    public String getIBANDestinatar() {
        return IBANDestinatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIBANDestinatar(String IBANDestinatar) {
        this.IBANDestinatar = IBANDestinatar;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateFormatted = dateFormat.format(dataTranzactie);
        return "Tranzactia cu ID-ul: " + idTranzactie +
                ", data: " + getDataTranzactieFormatted() +
                ", suma = " + sumaTranzactie +
                ", tip: " + tipTranzactie +
                ", descrie '" + descrie + '\'' +
                ", IBANDestinatar= " + IBANDestinatar;
    }
}

