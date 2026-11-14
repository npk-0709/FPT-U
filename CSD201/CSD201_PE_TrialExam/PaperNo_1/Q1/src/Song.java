public class Song {
    String songId;
    String title;
    String artist;

    public Song(String songId, String title, String artist) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "(" + songId + ", " + title + ", " + artist + ")";
    }
}