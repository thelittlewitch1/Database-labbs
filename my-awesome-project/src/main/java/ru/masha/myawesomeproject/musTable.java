package ru.masha.myawesomeproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class musTable
{
    Connection con;
    singersTable S;
    musTable(Connection _con, singersTable _s)
    {
        con = _con;
        S = _s;
    }
    void showList (List<track> L)
    {
        for (int i=0; i<L.size(); i++)
        {
            int min = L.get(i).length / 60;
            int sec = L.get(i).length - min * 60;
            System.out.printf("%d. '%s' by %s", L.get(i).id, L.get(i).name, L.get(i).singer);
            if (L.get(i).album != null)
                System.out.printf(" from '%s'", L.get(i).album);
            System.out.printf(", %d:%d \n", min, sec);
        }
    }

    void insertTrack(track M) throws SQLException
    {
        PreparedStatement statement;
        if (M.album == null)
        {
            String query = "INSERT INTO MyMusic (track, singer, length) values (?, ?, ?);";
            statement = con.prepareStatement(query);
            statement.setString(1, M.name);
            statement.setString(2, M.singer);
            statement.setInt   (3, M.length);
        }
        else
        {
            String query = "INSERT INTO MyMusic (track, singer, album, length) values (?, ?, ?, ?);";
            statement = con.prepareStatement(query);
            statement.setString(1, M.name);
            statement.setString(2, M.singer);
            statement.setString(3, M.album);
            statement.setInt   (4, M.length);
        }
        statement.execute();

        String query = "SELECT * FROM Singers where name = ?;";
        statement = con.prepareStatement(query);
        statement.setString(1,M.singer);
        ResultSet res = statement.executeQuery();
        if (!res.next())
        {
            S.insertSinger(new singer(M.singer));
        }
    }

    void deleteTrack(int _id) throws SQLException
    {
        PreparedStatement st = con.prepareStatement("""
                    SELECT * FROM MyMusic WHERE 'id' = ?
                    """);
        st.setInt(1, _id);
        ResultSet res = st.executeQuery();
        if (res.next())
        {
            st = con.prepareStatement("""
                    DELETE FROM MyMusic WHERE id = ?;
                    """);
            st.setInt(1, _id);
            st.execute();
        } else
        {
            System.out.print("There is no track with this ID.");
        }
    }

    List<track> getMusic() throws SQLException
    {
        ResultSet res = con.createStatement().executeQuery("SELECT * FROM MyMusic");
        List<track> L = new ArrayList<>();
        while (res.next())
        {
            L.add(new track(res.getInt("id"),
                    res.getString("track"),
                    res.getString("singer"),
                    res.getString("album"),
                    res.getInt("length")));
        }
        return L;
    }

    /*
     * char c is
     *           n for name
     *           s for singer
     *           a for album
     *    any char for all collums
     */
    List<track> getMusic(char c, String _key) throws SQLException
    {
        PreparedStatement st = null;
        switch (c)
        {
            case ('n'):
                st = con.prepareStatement("SELECT * FROM MyMusic where track = ?;");
                break;
            case ('s'):
                st = con.prepareStatement("SELECT * FROM MyMusic where singer = ?");
                break;
            case ('a'):
                st = con.prepareStatement("SELECT * FROM MyMusic where album = ?");
                break;
            default:
                st = con.prepareStatement("SELECT * FROM MyMusic where track = ? or singer = ? or album = ?");
                st.setString(2, _key);
                st.setString(3, _key);
                break;
        }
        st.setString(1, _key);
        ResultSet res = st.executeQuery();
        List<track> L = new ArrayList<>();
        while (res.next())
        {
            L.add(new track(res.getInt("id"),
                    res.getString("track"),
                    res.getString("singer"),
                    res.getString("album"),
                    res.getInt("length")));
        }
        return L;

    }

    List<track> getMusic(int lessThan, int moreThan) throws SQLException
    {
        PreparedStatement st = null;
        if (lessThan<moreThan)
        {
            st = con.prepareStatement("SELECT * FROM MyMusic WHERE (length<?) or (length>?)");
            st.setInt(1, lessThan);
            st.setInt(2, moreThan);
        } else
        {
            if (lessThan>moreThan)
            {
                st = con.prepareStatement("SELECT * FROM MyMusic WHERE (length<?) and (length>?)");
                st.setInt(1, lessThan);
                st.setInt(2, moreThan);
            } else
            {
                st = con.prepareStatement("SELECT * FROM MyMusic WHERE (length=?)");
                st.setInt(1, lessThan);
            }
        }
        ResultSet res = st.executeQuery();
        List<track> L = new ArrayList<>();
        while (res.next())
        {
            L.add(new track(res.getInt("id"),
                    res.getString("track"),
                    res.getString("singer"),
                    res.getString("album"),
                    res.getInt("length")));
        }
        return L;
    }

    List<track> getMusic(int _len) throws SQLException
    {
        return getMusic(_len, _len);
    }

    void dropTable () throws SQLException
    {
        con.createStatement().execute("DROP TABLE MyMusic;");
        System.out.print("\nMusic table was droped.\n");
    }
}
