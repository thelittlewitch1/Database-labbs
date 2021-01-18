package witch.hiorm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable()
public class Track {
    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    private static int id;
    @DatabaseField(columnName = "name", canBeNull = false)
    private static String name;
    @DatabaseField(columnName = "singer_id", foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private static Singer singer;
    @DatabaseField(columnName = "album")
    private static String album;
    @DatabaseField(columnName = "length", canBeNull = false)
    private static int length;

    Track (int id, String name, Singer singer, String album, int length)
    {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.album = album;
        this.length = length;
    }
    Track (String name, Singer singer, String album, int length)
    {
        this(0, name, singer, album, length);
    }
    Track (String name, Singer singer, int length)
    {
        this(0, name, singer, null, length);
    }
    Track (int id, String name, Singer singer, int length)
    {
        this(id, name, singer, null, length);
    }
    Track (int id, String name, String singer, String album, int length)
    {
        this (id, name, new Singer(singer, 0, null), album, length);
    }
    Track (String name, String singer, String album, int length)
    {
        this (0, name, new Singer(singer, 0, null), album, length);
    }
    Track (int id, String name, String singer, int length)
    {
        this (id, name, new Singer(singer, 0, null), null, length);
    }
    Track (String name, String singer, int length)
    {
        this (0, name, new Singer(singer), null, length);
    }
    Track (Track T)
    {
        this(T.id, T.name, T.singer, T.album, T.length);
    }
    public Track () {}

    public int getId() {
        return id;
    }
    public int getLength() {
        return length;
    }
    public Singer getSinger() {
        return singer;
    }
    public String getAlbum() {
        return album;
    }
    public String getName() {
        return name;
    }
}
