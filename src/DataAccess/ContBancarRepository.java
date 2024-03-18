package DataAccess;

import Models.ContBancar;
import Models.TipContBancar;
import Models.Tranzactie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContBancarRepository{

    private static List<ContBancar> listaConturiCache = new ArrayList<>();

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }

    String url = "jdbc:sqlite:src\\Date\\EBanking.db";

    public List<ContBancar> readAll() {
        if (listaConturiCache.isEmpty()) {
        TranzactieRepository tranzactieRepository = new TranzactieRepository();
        List<Tranzactie> listaTranzactii = tranzactieRepository.readAll();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet conturiResult  = statement.executeQuery("SELECT * FROM ContBancar")) {
            while (conturiResult.next()) {
                ContBancar contBancar = new ContBancar();
                contBancar.setNrContBancar(conturiResult.getInt("nrContBancar"));
                contBancar.setSoldCont(0);
                contBancar.setIBAN(conturiResult.getString("IBAN"));
                contBancar.setUserID(conturiResult.getString("userToken"));
                String tipContBancarStr = conturiResult.getString("tipContBancar");
                contBancar.setTipContBancar(TipContBancar.valueOf(tipContBancarStr));

                listaConturiCache.add(contBancar);
            }
            for(ContBancar contBancar : listaConturiCache){
                List<Tranzactie> tranzactiiCont = new ArrayList<>();
                for (Tranzactie tranzactie : listaTranzactii) {
                    if (tranzactie.getIBANExpeditor().equals(contBancar.getIBAN())) {
                        tranzactiiCont.add(tranzactie);
                    }
                }
                contBancar.setTranzactiiIstoric(tranzactiiCont);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }
        return listaConturiCache;
    }


    public ContBancar create(ContBancar entity) {
        try( Connection connection = DriverManager.getConnection(url);
             var statement = connection.prepareStatement("INSERT INTO ContBancar VALUES (?, ?, ?, ?, ?)");) {
            statement.setInt(1,entity.getNrContBancar());
            statement.setFloat(2,entity.getSoldCont());
            statement.setString(3,entity.getUserID());
            statement.setString(4,entity.getIBAN());
            statement.setString(5, String.valueOf(entity.getTipContBancar()));

            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;

    }


    public ContBancar read(int token) {
        TranzactieRepository  tranzactieRepository = new TranzactieRepository();
        List<Tranzactie> listaTranzactii = tranzactieRepository.readAll();

        try(Connection connection = DriverManager.getConnection(url);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM ContBancar where nrContBancar = ?")) {
            statement.setInt(1,token);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                ContBancar contBancar = new ContBancar();
                contBancar.setNrContBancar(resultSet.getInt("nrContBancar"));
                contBancar.setSoldCont(resultSet.getFloat("soldCont"));
                contBancar.setIBAN(resultSet.getString("IBAN"));
                contBancar.setUserID(resultSet.getString("userToken"));
                String tipContBancarStr = resultSet.getString("tipContBancar");
                contBancar.setTipContBancar(TipContBancar.valueOf(tipContBancarStr));

                List<Tranzactie> tranzactiiCont = new ArrayList<>();
                for (Tranzactie tranzactie : listaTranzactii) {
                    if (tranzactie.getIBANExpeditor().equals(contBancar.getIBAN())) {
                        tranzactiiCont.add(tranzactie);
                    }
                }
                contBancar.setTranzactiiIstoric(tranzactiiCont);

                return contBancar;
            }
            else{
                return  null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ContBancar[] getConturiBancareByUsers(String token){
        List<ContBancar> userCoturi = new ArrayList<>();
        readAll();
        for(ContBancar contBancar : listaConturiCache){
            if(contBancar.getUserID().equals(token)){
                userCoturi.add(contBancar);
            }
        }
        ContBancar[] conturi = new ContBancar[userCoturi.size()];
        for(int i=0; i < conturi.length; i++){
         conturi[i] = userCoturi.get(i);
        }
        return conturi;
    }

    public ContBancar update(ContBancar entity) {
        TranzactieRepository tranzactieRepository = new TranzactieRepository();
        List<Tranzactie> tranzactiiInBazaDeDate = tranzactieRepository.readAll();
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE ContBancar SET soldCont=?, IBAN=?, userToken=?, tipContBancar=? WHERE nrContBancar=?")) {
            statement.setFloat(1, entity.getSoldCont());
            statement.setString(2, entity.getIBAN());
            statement.setString(3, entity.getUserID());
            statement.setString(4, String.valueOf(entity.getTipContBancar()));
            statement.setInt(5, entity.getNrContBancar());

            for (Tranzactie t : entity.getTranzactiiIstoric()) {
                boolean tranzactieExistaInDb = false;
                for (Tranzactie tranzactieInDb : tranzactiiInBazaDeDate) {
                    if (tranzactieInDb.getIdTranzactie() == t.getIdTranzactie()) {
                        tranzactieExistaInDb = true;
                        tranzactieRepository.update(t);
                        break;
                    }
                }

                if (!tranzactieExistaInDb) {
                    tranzactieRepository.create(t);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }



    public void delete(String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(url);
            String sql = "DELETE FROM ContBancar WHERE nrContBancar = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
