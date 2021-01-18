package witch.hiorm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable()
public class Singer {
    @DatabaseField(columnName = "name", id = true, canBeNull = false, unique = true)
    private String name;
    @DatabaseField(columnName = "creation year")
    private int year;
    @DatabaseField(columnName = "main genre")
    private String genre;

    Singer (String name, int year, String genre)
    {
        this.name  = name;
        this.year  = year;
        this.genre = genre;
    }
    Singer (String name)
    {
        this (name, 0, null);
    }
    public Singer () {}

    public String getName() {
        return name;
    }
    public int getYear() {
        return year;
    }
    public String getGenre() {
        return genre;
    }
}
