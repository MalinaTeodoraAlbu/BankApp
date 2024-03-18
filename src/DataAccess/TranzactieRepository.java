package DataAccess;


import Models.ContBancar;
import Models.TipTranzactie;
import Models.Tranzactie;
import Models.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TranzactieRepository{
    private static List<Tranzactie> listaTranzactiiCache = new ArrayList<>();
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }
    String url = "jdbc:sqlite:src\\Date\\EBanking.db";

    public List<Tranzactie> readAll() {
        if (listaTranzactiiCache.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(url);
                 Statement statement = connection.createStatement();
                 ResultSet tranzactii = statement.executeQuery("SELECT * FROM Tranzactie")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                while (tranzactii.next()) {
                    Tranzactie tranzactie = new Tranzactie();
                    Date date = dateFormat.parse(tranzactii.getString("dataTranzactie"));
                    tranzactie.setDataTranzactie(date);
                    tranzactie.setIdTranzactie(tranzactii.getInt("id"));
                    tranzactie.setSumaTranzactie(tranzactii.getFloat("sumaTranzactie"));
                    String tipTranzactieStr = tranzactii.getString("tipTranzactie");
                    tranzactie.setTipTranzactie(TipTranzactie.valueOf(tipTranzactieStr));
                    tranzactie.setDescrie(tranzactii.getString("descrie"));
                    tranzactie.setIBANDestinatar(tranzactii.getString("IBANDestinatar"));
                    tranzactie.setUserId(tranzactii.getString("userToken"));
                    tranzactie.setIBANExpeditor(tranzactii.getString("IBANExpeditor"));
                    listaTranzactiiCache.add(tranzactie);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return listaTranzactiiCache;
    }

    public Tranzactie create(Tranzactie entity) {
        try( Connection connection = DriverManager.getConnection(url);
             var statement = connection.prepareStatement("INSERT INTO Tranzactie VALUES (?, ?, ?, ?, ?, ?, ?, ? )");
        ) {
            statement.setInt(1,entity.getIdTranzactie());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String data = dateFormat.format(entity.getDataTranzactie());
            statement.setString(2, data);
            statement.setFloat(3,entity.getSumaTranzactie());
            statement.setString(4, String.valueOf(entity.getTipTranzactie()));
            statement.setString(5,entity.getDescrie());
            statement.setString(6,entity.getIBANDestinatar());
            statement.setString(7,entity.getUserId());
            statement.setString(8,entity.getIBANExpeditor());

            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Tranzactie read(int id) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM tranzactie WHERE id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Tranzactie tranzactie = new Tranzactie();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = dateFormat.parse(resultSet.getString("dataTranzactie"));
                tranzactie.setDataTranzactie(date);
                tranzactie.setSumaTranzactie(resultSet.getFloat("sumaTranzactie"));
                String tipTranzactieStr = resultSet.getString("tipTranzactie");
                tranzactie.setTipTranzactie(TipTranzactie.valueOf(tipTranzactieStr));
                tranzactie.setDescrie(resultSet.getString("descrie"));
                tranzactie.setIBANDestinatar(resultSet.getString("IBANDestinatar"));
                tranzactie.setUserId(resultSet.getString("userToken"));
                tranzactie.setIdTranzactie(resultSet.getInt("id"));

                return tranzactie;
            } else {
                return null;
            }
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Tranzactie update(Tranzactie entity) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE tranzactie SET dataTranzactie=?, sumaTranzactie=?, tipTranzactie=?, descrie=?, IBANDestinatar=?, userToken=?, IBANExpeditor=? WHERE id=?")) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String data = dateFormat.format(entity.getDataTranzactie());
            statement.setString(1, data);
            statement.setFloat(2,entity.getSumaTranzactie());
            statement.setString(3, String.valueOf(entity.getTipTranzactie()));
            statement.setString(4,entity.getDescrie());
            statement.setString(5,entity.getIBANDestinatar());
            statement.setString(6,entity.getUserId());
            statement.setString(7,entity.getIBANExpeditor());
            statement.setInt(8, entity.getIdTranzactie());



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    public void delete(int id) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Tranzactie WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
