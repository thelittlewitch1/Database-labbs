package witch.hiorm;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception
    {
        DBHelper help = new DBHelper();
        help.insertTrack("Primo Victoria", new Singer("Sabaton"), "Primo Victoria", 251);
        help.insertTrack("Primo Victoria", new Singer("RADIO TAPOK"), null,  251);
        help.insertTrack("RADIO", new Singer("Rammstein"), "RADIO", 277);

        help.insertSinger("Imagine Dragons", 2008, "indie rock");
        help.insertSinger("Sabaton", 1999, "heavy power metal");
        help.insertSinger("Rammstein", 1994, "indastrial metal");

        help.insertTrack("Ghost Division", new Singer("Sabaton"), "The Art of War", 231);
        help.insertTrack("So What", new Singer("Metallica"), "Garage Inc", 188);

        System.out.print("\nAll tracks:\n");
        help.selectAllTracks();

        System.out.print("\nAll singers:\n");
        help.selectAllSingers().forEach(e->{
            System.out.println (e.getName() +"\t"+ e.getGenre() + "\t" + e.getYear());
        });

        System.out.print("\nAll tracks with \"Primo Victoria\" in name:\n");
        help.selectTracksByName("Primo Victoria");

        System.out.print("\nAll tracks longer than 4:12 (252 sec)\n");
        help.selectTracksByLength(0, 252);

        System.out.print("\nSabaton's tracks:\n");
        help.selectTracksBySinger("Sabaton");

        help.deleteDB();
    }
}

