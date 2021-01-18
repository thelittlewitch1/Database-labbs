package ru.masha.myawesomeproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class singersTable
{
    Connection con;
    singersTable (Connection _con)
    {
        con = _con;
    }

    void showList (List<singer> L)
    {
        if (L.isEmpty())
        {
            System.out.print("Singers table is empty.\n");
        }
        else
        {
            for (ru.masha.myawesomeproject.singer singer : L) {
                if (singer.genre == null)
                {
                    if (singer.year == 0) {
                        System.out.print(singer.name + ";\n");
                    } else {
                        System.out.print(singer.name + "was created at " + singer.year + ";\n");
                    }
                } else {
                    if (singer.year == 0)
                    {
                        System.out.print(singer.name + " (" + singer.genre + ");\n");
                    } else {
                        System.out.print(singer.name + " (" + singer.genre + ") was created at " + singer.year + ";\n");
                    }
                }
            }
        }
    }
    boolean isExist (String _name) throws SQLException {
        PreparedStatement statement = con.prepareStatement("SELECT * FROM Singers WHERE name = ?;");
        statement.setString(1, _name);
        ResultSet res = statement.executeQuery();
        if (res.next())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    void insertSinger (singer S) throws SQLException
    {
        if (!isExist(S.name))
        {
            PreparedStatement statement = null;
            if (S.year == 0) {
                if (S.genre == null) {
                    String query = "INSERT INTO Singers (name) values (?);";
                    statement = con.prepareStatement(query);
                    statement.setString(1, S.name);
                } else {
                    String query = "INSERT INTO Singers (name, maingenre) values (?, ?);";
                    statement = con.prepareStatement(query);
                    statement.setString(1, S.name);
                    statement.setString(2, S.genre);
                }
            } else
            {
                if (S.genre == null)
                {
                    String query = "INSERT INTO Singers (name, creationyear) values (?, ?);";
                    statement = con.prepareStatement(query);
                    statement.setString(1, S.name);
                    statement.setInt(2, S.year);
                } else {
                    String query = "INSERT INTO Singers (name,creationyear, maingenre) values (?, ?, ?);";
                    statement = con.prepareStatement(query);
                    statement.setString(1, S.name);
                    statement.setInt(2, S.year);
                    statement.setString(3, S.genre);
                }
            }
            statement.execute();
        }
        else
        {
            if (S.year == 0) {
                if (!(S.genre == null))
                {
                    correctSinger(S.name, S.genre);
                }
            } else
            {
                if (S.genre == null)
                {
                    correctSinger(S.name, S.year);
                } else
                {
                    correctSinger(S.name, S.year, S.genre);
                }
            }
        }
    }

    void correctSinger (String _name, String _genre) throws SQLException
    {
        if (isExist(_name))
        {
            String query = "UPDATE Singers SET maingenre = ? WHERE name = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, _genre);
            statement.setString(2, _name);
            statement.execute();
        }
        else
        {
            insertSinger(new singer(_name, _genre));
        }
    }
    void correctSinger (String _name, int _year) throws SQLException
    {
        if (isExist(_name))
        {
            String query = "UPDATE Singers SET creationyear = ? WHERE name = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt   (1, _year);
            statement.setString(2, _name);
            statement.execute();
        }
        else
        {
            insertSinger(new singer(_name, _year));
        }
    }
    void correctSinger (String _name, int _year, String _genre) throws SQLException
    {
        correctSinger(_name, _genre);
        correctSinger(_name, _year);
    }


    void deleteSinger (String _name) throws SQLException
    {
        if (isExist(_name))
        {
            PreparedStatement st = con.prepareStatement("DELETE FROM Singers WHERE name = ?;");
            st.setString(1, _name);
            st.execute();

            st = con.prepareStatement("DELETE FROM MyMusic WHERE singer = ?;");
            st.setString(1, _name);
            st.execute();
            System.out.printf("\nSinger %s was deleted.\n", _name);
        } else
        {
            System.out.print("There is no singer, named " + _name);
        }
    }

    List<singer> getSinger () throws SQLException
    {
        ResultSet res = con.createStatement().executeQuery("SELECT * FROM Singers");
        List<singer> L = new ArrayList<>();
        while (res.next())
        {
            L.add(new singer(res.getString("name"),
                    res.getInt("creationyear"),
                    res.getString("maingenre")));
        }
        return L;
    }

    /*
     * char c is
     *           n for name
     *           g for genre
     *    any char for all collums
     */
    List<singer> getSinger(char c, String _key) throws SQLException
    {
        PreparedStatement st = null;
        switch (c)
        {
            case ('n'):
                st = con.prepareStatement("SELECT * FROM Singers where name = ?;");
                break;
            case ('g'):
                st = con.prepareStatement("SELECT * FROM Singers where maingenre = ?;");
                break;
            default:
                st = con.prepareStatement("SELECT * FROM Singers where name = ? or maingenre = ?;");
                st.setString(2, _key);
                break;
        }
        st.setString(1, _key);
        ResultSet res = st.executeQuery();
        List<singer> L = new ArrayList<>();
        while (res.next())
        {
            L.add(new singer(res.getString("name"),
                    res.getInt("creationyear"),
                    res.getString("maingenre")));
        }
        return L;
    }

    List<singer> getSinger(int lessThan, int moreThan) throws SQLException
    {
        PreparedStatement st = null;
        if (lessThan<moreThan)
        {
            st = con.prepareStatement("SELECT * FROM Singers WHERE (creationyear<?) or (creationyear>?)");
            st.setInt(1, lessThan);
            st.setInt(2, moreThan);
        } else
        {
            if (lessThan>moreThan)
            {
                st = con.prepareStatement("SELECT * FROM Singers WHERE (creationyear<?) and (creationyear>?)");
                st.setInt(1, lessThan);
                st.setInt(2, moreThan);
            } else
            {
                st = con.prepareStatement("SELECT * FROM Singers WHERE (creationyear=?)");
                st.setInt(1, lessThan);
            }
        }
        ResultSet res = st.executeQuery();
        List<singer> L = new ArrayList<>();
        while (res.next())
        {
            L.add(new singer(res.getString("name"),
                    res.getInt("creationyear"),
                    res.getString("maingenre")));
        }
        return L;
    }
    List<singer> getSinger(int _year) throws SQLException
    {
        return getSinger(_year, _year);
    }

    void dropTable () throws SQLException
    {
        con.createStatement().execute("DROP TABLE Singers;");
        System.out.print("\nSingers table was droped.\n");
    }
}
