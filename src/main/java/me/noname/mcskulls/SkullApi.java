package me.noname.mcskulls;

import org.bukkit.OfflinePlayer;

import java.sql.*;

public class SkullApi {

    private Connection connection;

    public SkullApi(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL, " +
                    "skull BOOLEAN NOT NULL DEFAULT false)");
        }
    }

    public void closeCon() throws SQLException{
        if(connection != null && ! connection.isClosed()){
            connection.close();
        }
    }

    public void addPlayer(OfflinePlayer player) throws SQLException {
        //this should error if the player already exists
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, username) VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerExists(OfflinePlayer player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void setPlayerSkull(OfflinePlayer player, boolean skull) throws SQLException{
        if (!playerExists(player)){
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET skull = ? WHERE uuid = ?")) {
            preparedStatement.setBoolean(1, skull);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public boolean getPlayerSkull(OfflinePlayer player) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT skull FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("skull");
            } else {
                return false;
            }
        }
    }

    public boolean hasSkull(OfflinePlayer player){
        try {
            boolean isSkull = getPlayerSkull(player);
            if (isSkull) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public String tab(OfflinePlayer player){
        if(hasSkull(player)){
            return " §f☠";
        }
        return "";
    }

    public void giveSkull(OfflinePlayer player) throws SQLException {
        setPlayerSkull(player, true);
        //player.sendTitle("☠", "", 10, 10, 10);
    }

    public void removeSkull(OfflinePlayer player) throws SQLException {
        setPlayerSkull(player, false);

    }
}
