package ru.masha.myawesomeproject;

public class singer
{
    String name;
    int year;
    String genre;

    singer(String _name, int _year, String _genre)
    {
        name = _name;
        year = _year;
        genre = _genre;
    }
    singer(String _name, String _genre)
    {
        this (_name, 0, _genre);
    }
    singer(String _name, int _year)
    {
        this (_name, _year, null);
    }
    singer(String _name)
    {
        this (_name, 0, null);
    }
}
