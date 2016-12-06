package kz.qobyzbook.C_Lessons;

/**
 * Created by Orenk on 16.08.2016.
 */
public class Lesson {

    public Lesson(){}

    private String name,video,mif,ukazanie,audio,note,liter;



    private int id;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String aboutQobyzItem) {
        this.name = aboutQobyzItem;
    }


    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getMif() {
        return mif;
    }

    public void setMif(String mif) {
        this.mif = mif;
    }

    public String getUkazanie() {
        return ukazanie;
    }

    public void setUkazanie(String ukazanie) {
        this.ukazanie = ukazanie;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLiter() {
        return liter;
    }

    public void setLiter(String liter) {
        this.liter = liter;
    }
}
