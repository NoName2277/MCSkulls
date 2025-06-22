package me.noname.mcskulls;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.sql.*;

public final class SkullApi {

    private Connection connection;

    public SkullApi(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL, " +
                    "skull BOOLEAN NOT NULL DEFAULT false, " +
                    "dead BOOLEAN NOT NULL DEFAULT false)");
        }
    }

    public boolean isPlayerDead(OfflinePlayer player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT dead FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getBoolean("dead");
        }
    }

    public void markPlayerDead(OfflinePlayer player) throws SQLException {
        if (!playerExists(player)) {
            addPlayer(player);
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET dead = ? WHERE uuid = ?")) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public void unmarkPlayerDead(OfflinePlayer player) throws SQLException {
        if (!playerExists(player)) {
            return;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET dead = ? WHERE uuid = ?")) {
            preparedStatement.setBoolean(1, false);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }


    public void closeCon() throws SQLException{
        if(connection != null && ! connection.isClosed()){
            connection.close();
        }
    }

    public void addPlayer(OfflinePlayer player) throws SQLException {
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
            return "§f☠";
        }
        return " ";
    }

    public void giveSkull(OfflinePlayer player) throws SQLException {
        setPlayerSkull(player, true);
        if (player.isOnline()) {
            updateTabList(player.getPlayer());
        }
    }

    public void removeSkull(OfflinePlayer player) throws SQLException {
        setPlayerSkull(player, false);
        if (player.isOnline()) {
            updateTabList(player.getPlayer());
        }
    }

    public void updateTabList(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team currentTeam = scoreboard.getEntryTeam(player.getName());
        if (currentTeam != null) {
            return;
        }
        Team team = scoreboard.getTeam("skull_team");
        if (team == null) {
            team = scoreboard.registerNewTeam("skull_team");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        }
        if (hasSkull(player)) {
            team.setSuffix(" §f☠");
        } else {
            team.setSuffix("");
        }
        team.addEntry(player.getName());
        player.setScoreboard(scoreboard);
    }

}
