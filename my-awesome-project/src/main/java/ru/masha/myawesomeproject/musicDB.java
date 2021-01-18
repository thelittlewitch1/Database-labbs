package ru.masha.myawesomeproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class musicDB
{
    Connection con;
    musicDB() throws SQLException {
        con = DriverManager.getConnection("jdbc:h2:mem:");
    }
    Connection getInstance()
    {
        return con;
    }
    void newMusTable() throws SQLException {
        con
                .prepareStatement("""
                         CREATE TABLE IF NOT EXISTS MyMusic(
                            id INTEGER PRIMARY KEY AUTO_INCREMENT,
                            track VARCHAR (255) NOT NULL, 
                            singer VARCHAR (225) NOT NULL, 
                            album VARCHAR (225), 
                            length INTEGER NOT NULL
                        )
                        """)
                .execute();
    }
    void newSingTable() throws SQLException
    {
        con
                .prepareStatement("""
                         CREATE TABLE IF NOT EXISTS Singers(
                            name VARCHAR (255) NOT NULL PRIMARY KEY,
                            creationyear INT, 
                            maingenre VARCHAR (255)
                        )
                        """)
                .execute();
    }
}
