package com.mycompany;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    static void show(ResultSet res) throws SQLException {
        while (res.next())
        {
            int id = res.getInt("id");
            String track = res.getString("track");
            String singer = res.getString("singer");
            String album = res.getString("album");
            int leng = res.getInt("tracklength");
            int min = leng / 60;
            int sec = leng - min * 60;

            System.out.printf("%d. '%s' by %s", id, track, singer);
            if (album != null)
                System.out.printf(" from '%s'", album);
            System.out.printf(", %d:%d \n", min, sec);
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello H2!");

        Connection connection = DriverManager.getConnection("jdbc:h2:mem:");

        connection
                .prepareStatement("""
                         CREATE TABLE IF NOT EXISTS MyMusic(
                            id INTEGER PRIMARY KEY AUTO_INCREMENT,
                            track VARCHAR (255) NOT NULL, 
                            singer VARCHAR (225) NOT NULL, 
                            album VARCHAR (225), 
                            tracklength INTEGER NOT NULL
                        );
                        """)
                .execute();

        connection
                .prepareStatement(" INSERT INTO MyMusic (track, singer, album, tracklength) " +
                        "values ('Primo Victoria', 'Sabaton', 'Primo Victoria', 251);" +
                        "               INSERT INTO MyMusic (track, singer,        tracklength) " +
                        "values ('Primo Victoria', 'RADIO TAPOK', 251);" +
                        "               INSERT INTO MyMusic (track, singer, album, tracklength) " +
                        "values ('RADIO', 'Rammstein', 'RADIO', 277);" +
                        "               INSERT INTO MyMusic (track, singer, album, tracklength) " +
                        "values ('Stalingrad', 'Sabaton', 'Primo Victoria', 318);" +
                        "               INSERT INTO MyMusic (track, singer, album, tracklength) " +
                        "values ('Links 2 3 4', 'Rammstein', 'Mutter', 217);")
                .execute();

        System.out.println("All tracks:");
        show (connection
                .createStatement()
                .executeQuery("SELECT * FROM MyMusic"));

        System.out.printf("Sabaton's tracks:\n");
        show (connection
                .createStatement()
                .executeQuery("SELECT * FROM MyMusic WHERE singer = 'Sabaton'"));

        System.out.printf("Tracks, named 'Primo Victoria':\n");
        show (connection
                .createStatement()
                .executeQuery("SELECT * FROM MyMusic WHERE track = 'Primo Victoria'"));

        System.out.printf("\nTracks, less than 4 min and longer than 5 min:\n");
        show (connection
                .createStatement()
                .executeQuery("SELECT * FROM MyMusic WHERE (tracklength< 240) or (tracklength > 300)"));

        System.out.printf("\nTracks, that are between 4 and 5 minutes long:\n");
        show (connection
                .createStatement()
                .executeQuery("SELECT * FROM MyMusic WHERE tracklength BETWEEN 240 AND 300"));

        connection.close();
    }
}
