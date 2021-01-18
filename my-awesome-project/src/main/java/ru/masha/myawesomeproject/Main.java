package ru.masha.myawesomeproject;

import java.sql.*;
import java.util.*;

import static java.lang.System.exit;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        System.out.print("\nHello!\n");

        Scanner in = new Scanner(System.in);
        musicDB DB = new musicDB();
        DB.newMusTable();
        DB.newSingTable();
        singersTable mySing = new singersTable(DB.getInstance());
        musTable myPlayList = new musTable(DB.getInstance(), mySing);


        myPlayList.insertTrack(new track("Primo Victoria", "Sabaton", "Primo Victoria", 251));
        myPlayList.insertTrack(new track("Primo Victoria", "RADIO TAPOK", 251));
        myPlayList.insertTrack(new track("RADIO", "Rammstein", "RADIO", 277));
        myPlayList.insertTrack(new track("Stalingrad", "Sabaton", "Primo Victoria", 318));
        myPlayList.insertTrack(new track("Links 2 3 4", "Rammstein", "Mutter", 217));

        mySing.insertSinger(new singer ("Imagine Dragons", 2008, "indie rock"));
        mySing.insertSinger(new singer ("Sabaton", 1999, "heavy power metal"));
        mySing.insertSinger(new singer ("Rammstein", 1994, "indastrial metal"));
        mySing.insertSinger(new singer ("RADIO TAPOK", 2016));

        System.out.print("\nNow in DB tracks:\n");
        myPlayList.showList(myPlayList.getMusic());
        System.out.print("\nNow in DB singers:\n");
        mySing.showList(mySing.getSinger());



        System.out.print("\nAll tracks, by 'Sabaton':\n");
        myPlayList.showList(myPlayList.getMusic('s', "Sabaton"));

        System.out.print("\nAll tracks, named 'Primo Victoria':\n");
        myPlayList.showList(myPlayList.getMusic('n', "Primo Victoria"));

        System.out.print("\nAll tracks, less than 4 min and longer than 5 min:\n");
        myPlayList.showList(myPlayList.getMusic(240, 300));

        System.out.print("\nAll singers, that was create at 1990s:\n");
        mySing.showList(mySing.getSinger(2000, 1989));

        System.out.print("\nInfo about \"Imagine dragons\":\n");
        mySing.showList(mySing.getSinger ('n', "Imagine Dragons"));

        mySing.deleteSinger("RADIO TAPOK");
        System.out.print("\nNow in DB tracks:\n");
        myPlayList.showList(myPlayList.getMusic());
        System.out.print("\nNow in DB singers:\n");
        mySing.showList(mySing.getSinger());

        myPlayList.dropTable();
        mySing.dropTable();
    }
}