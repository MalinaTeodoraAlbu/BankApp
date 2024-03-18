package DataAccess;

import DataAccess.ContBancarRepository;
import Models.ContBancar;
import Models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;

public class UserRepository{
    ContBancarRepository contBancarRepository = new ContBancarRepository();
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }
    String url = "jdbc:sqlite:src\\Date\\EBanking.db";
    private static List<User> listaUseriCache = new ArrayList<>();


    public List<User> readAll() {
        if (listaUseriCache.isEmpty()) {
            ContBancarRepository contBancarRepository = new ContBancarRepository();
            ContBancar[] conturiBancare;
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet users = statement.executeQuery("SELECT * FROM USER")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            while (users.next()) {
                User user = new User();
                user.setNume(users.getString("nume"));
                user.setNumarTelefon(users.getString("numarTelefon"));
                user.setAdresa(users.getString("adresa"));
                Date date = dateFormat.parse(users.getString("ziNastere"));
                user.setZiNastere(date);
                user.setCNP(users.getString("CNP"));
                user.setPassword(users.getString("password"));
                user.setTokenUser(users.getString("token"));
                conturiBancare = contBancarRepository.getConturiBancareByUsers(users.getString("token"));
                user.setConturiBancare(conturiBancare);
                listaUseriCache.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }}
        return listaUseriCache;
    }


    public User create(User entity) {
        try( Connection connection = DriverManager.getConnection(url);
        var statement = connection.prepareStatement("INSERT INTO User VALUES (?, ?, ?, ?, ?, ?, ?)");) {
            statement.setString(1,entity.getNume());
            statement.setString(2,entity.getNumarTelefon());
            statement.setString(3,entity.getAdresa());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String ziNastereFormatata = dateFormat.format(entity.getZiNastere());
            statement.setString(4, ziNastereFormatata);
            statement.setString(5,entity.getCNP());
            statement.setString(6,entity.getPassword());
            statement.setString(7,entity.getTokenUser());

            for(int i =0; i <entity.getConturiBancare().length;i++){
                contBancarRepository.create(entity.getConturiBancare()[i]);
            }
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    public User read(String token) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER WHERE token = ?")) {
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setNume(resultSet.getString("nume"));
                user.setNumarTelefon(resultSet.getString("numarTelefon"));
                user.setAdresa(resultSet.getString("adresa"));
                Date ziNastere = new SimpleDateFormat("yyyy-MM-dd").parse(resultSet.getString("ziNastere"));
                user.setZiNastere(ziNastere);
                user.setCNP(resultSet.getString("CNP"));
                user.setPassword(resultSet.getString("password"));
                user.setTokenUser(resultSet.getString("token"));

                ContBancarRepository contBancarRepository = new ContBancarRepository();
                ContBancar[] conturiBancare = contBancarRepository.getConturiBancareByUsers(token);
                user.setConturiBancare(conturiBancare);

                return user;
            } else {
                return null;
            }
        } catch (SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public User update(User entity) {
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE USER SET nume=?, numarTelefon=?, adresa=?, ziNastere=?, CNP=?, password=? WHERE token=?")) {
            statement.setString(1, entity.getNume());
            statement.setString(2, entity.getNumarTelefon());
            statement.setString(3, entity.getAdresa());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String ziNastereFormatata = dateFormat.format(entity.getZiNastere());
            statement.setString(4, ziNastereFormatata);
            statement.setString(5, entity.getCNP());
            statement.setString(6, entity.getPassword());
            statement.setString(7, entity.getTokenUser());

            for (ContBancar c : entity.getConturiBancare()) {
                if (contBancarRepository.read(c.getNrContBancar()) == null) {
                    contBancarRepository.create(c);
                } else {
                    contBancarRepository.update(c);
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
            String sql = "DELETE FROM user WHERE token = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1,id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
