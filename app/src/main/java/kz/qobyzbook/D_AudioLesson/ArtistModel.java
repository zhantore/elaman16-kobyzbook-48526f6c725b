package kz.qobyzbook.D_AudioLesson;

/**
 * Created by Orenk on 31.08.2016.
 */
public class ArtistModel {
    private long id;
    private String img_big;
    private String img_small;
    private String name;
    private int count;

    public ArtistModel(){}
    public ArtistModel(long kuishiId, String songArtist, int songCount, String img_big, String img_small) {
        id=kuishiId;
        this.img_big =img_big;
        this.img_small =img_small;
        name =songArtist;
        count =songCount;
    }

    public long getID(){return id;}
    public String getImg_big(){return img_big;}
    public String getImg_small(){return img_small;}
    public String getName(){return name;}
    public int getCount() {return count; }
}
