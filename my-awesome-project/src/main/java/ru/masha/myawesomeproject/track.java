package ru.masha.myawesomeproject;

public class track {
    int id;
    String name;
    String singer;
    String album;
    int length;

    track(int _id, String _name, String _singer, String _album, int _length)
    {
        id = _id;
        name = _name;
        singer = _singer;
        album = _album;
        length = _length;
    }
    track(String _name, String _singer, String _album, int _length)
    {
        this (0, _name, _singer,_album, _length);
    }
    track(int _id, String _name, String _singer, int _length)
    {
        this (_id, _name, _singer, null, _length);
    }
    track(String _name, String _singer, int _length)
    {
        this (0, _name, _singer, null, _length);
    }
}

