package witch.hiorm;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DBHelper {
    private ConnectionSource connectionSource;
    Dao<Track, Integer> trackDao;
    Dao<Singer, Integer> singerDao;

    public DBHelper()
    {
        try
        {
            connectionSource = new JdbcConnectionSource("jdbc:h2:mem:myDb");
            TableUtils.createTableIfNotExists(connectionSource, Track.class);
            TableUtils.createTableIfNotExists(connectionSource, Singer.class);
            trackDao = DaoManager.createDao(connectionSource, Track.class);
            singerDao = DaoManager.createDao(connectionSource, Singer.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteDB() {
        try {
            connectionSource = new JdbcConnectionSource("jdbc:h2:mem:myDb");
            TableUtils.dropTable(connectionSource, Track.class, false);
            TableUtils.dropTable(connectionSource, Singer.class, false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insertTrack(String name, Singer singer, String album, int length) throws SQLException
    {
        Dao.CreateOrUpdateStatus cr = trackDao.createOrUpdate(new Track(name, singer, album, length));
        if (cr.isCreated()) {
            System.out.println("track, named " + name + " by " + singer.getName() + " was inserted.");
        } else if (cr.isUpdated())
            {
                System.out.println("track, named " + name + " by " + singer.getName() + " was updated.");
            } else
                {
                    System.out.println("ERROR");
                }


        if (singerDao.queryBuilder()
                .where()
                .eq("name", singer.getName())
                .query()
                .isEmpty())
        {
            singerDao.createOrUpdate(singer);
        }
    }

    public void insertSinger(String name, int year, String genre) throws SQLException
    {
        singerDao.createOrUpdate(new Singer(name, year, genre));
    }

    public List<Singer> selectAllSingers() throws SQLException
    {
        return singerDao.queryForAll();
    }

    public void selectAllTracks() throws SQLException
    {
        CloseableIterator<Track> itr = trackDao.iterator();
        itr.forEachRemaining(e->{
            System.out.println (e.getId() + "\t"+ e.getName() +"\t"+ e.getSinger().getName() +
                    "\t"+e.getAlbum()+"\t" + e.getLength());
        });
    }

    public void selectTracksBySinger(String singerName) throws SQLException {
        trackDao.queryBuilder()
                .where()
                .eq("singer_id", singerName)
                .iterator()
                .forEachRemaining(e->{
                    System.out.println (e.getId() + "\t"+ e.getName() +"\t"
                            + e.getSinger().getName() + "\t"+e.getAlbum()+"\t" + e.getLength());
        });
    }

    public void selectTracksByName(String name) throws SQLException
    {
        trackDao.queryBuilder()
                .where()
                .eq("name", name)
                .iterator()
                .forEachRemaining(e->{
                    System.out.println (e.getId() + "\t"+ e.getName() +"\t"
                            + e.getSinger().getName() + "\t"+e.getAlbum()+"\t" + e.getLength());
                });
    }

    public void selectTracksByLength(int lessThan, int moreThan) throws SQLException {
        if (lessThan <= 0) {
            if (moreThan <= 0)
            {
                System.out.printf("Error. Unvalide lengths");
            }
            else
            {
                trackDao.queryBuilder()
                        .where()
                        .ge("length", moreThan)
                        .iterator()
                        .forEachRemaining(e->{
                            System.out.println (e.getId() + "\t"+ e.getName() +"\t"
                                    + e.getSinger().getName() + "\t"+e.getAlbum()+"\t" + e.getLength());
                        });
            }
        }
        else
        {
            if (moreThan <= 0)
            {
                trackDao.queryBuilder()
                        .where()
                        .le("length", lessThan)
                        .iterator()
                        .forEachRemaining(e->{
                            System.out.println (e.getId() + "\t"+ e.getName() +"\t"
                                    + e.getSinger().getName() + "\t"+e.getAlbum()+"\t" + e.getLength());
                        });
            }
            else
            {
                if (moreThan < lessThan)
                {
                    trackDao.queryBuilder()
                            .where()
                            .between("length", lessThan, moreThan)
                            .iterator()
                            .forEachRemaining(e->{
                                System.out.println (e.getId() + "\t"+ e.getName() +"\t"
                                        + e.getSinger().getName() + "\t"+e.getAlbum()+"\t" + e.getLength());
                            });
                }
                else
                {
                    trackDao.queryBuilder()
                            .where()
                            .or(trackDao.queryBuilder().where().le("length", lessThan),
                                    trackDao.queryBuilder().where().ge("length", moreThan))
                            .iterator()
                            .forEachRemaining(e->{
                                System.out.println (e.getId() + "\t"+ e.getName() +"\t"
                                        + e.getSinger().getName() + "\t"+e.getAlbum()+"\t" + e.getLength());
                            });
                }
            }
        }
    }
}
