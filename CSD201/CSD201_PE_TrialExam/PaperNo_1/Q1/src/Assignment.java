import java.io.File;
import java.io.RandomAccessFile;

public class Assignment {
    SongBST library;
    PlaylistCLL playlist;
    String dataFile = "data.txt";

    public Assignment() {
        library = new SongBST();
        playlist = new PlaylistCLL();
    }

    public void loadData(int choice) {
        int numSongs = Integer.parseInt(Lib.readLineToStr(dataFile, 0));
        String[] ids = Lib.readLineToStrArray(dataFile, 1);
        String[] titles = Lib.readLineToStrArray(dataFile, 2);
        String[] artists = Lib.readLineToStrArray(dataFile, 3);
        
        if(choice == 1){
            for (int i = 0; i < numSongs; i++) {
                library.insert(new Song(ids[i], titles[i], artists[i]));
            }
        } else if (choice == 0){
            for (int i = 0; i < numSongs; i++) {
                library.helper_fn(new Song(ids[i], titles[i], artists[i]));
            }
        }
    }

    public void f1() throws Exception {
        String fname = "f1.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");
        
        loadData(0);
        playlist.helper_fn(library.search_f1("S01"));
        playlist.helper_fn(library.search_f1("S03"));

        rf.writeBytes(">>> Testing f1: Counting songs in a playlist...\r\n");
        playlist.ftraverse(rf);
        int count = playlist.countSongs();
        rf.writeBytes("\r\nTotal songs in playlist: " + count + "\r\n");
        
        rf.close();
    }

    public void f2() throws Exception {
        String fname = "f2.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");
        
        rf.writeBytes("--- Initial Playlist State ---\r\n");
        playlist.ftraverse(rf);
        
        rf.writeBytes("\r\n>>> Testing f2: Adding a new song to the playlist...\r\n");
        playlist.addToPlaylist(new Song("S99", "New_Song", "Test_Artist"));
        
        rf.writeBytes("\r\n--- Final Playlist State ---\r\n");
        playlist.ftraverse(rf);
        rf.close();
    }
    
    public void f3() throws Exception {
        String fname = "f3.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");

        rf.writeBytes(">>> Testing f3: Inserting songs into the library...\r\n");
        loadData(1);
        
        rf.writeBytes("\r\n--- Final State of Library ---\r\n");
        library.ftraverse(rf);
        rf.close();
    }
    
    public void f4() throws Exception {
        String fname = "f4.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");

        loadData(1);
        rf.writeBytes("--- Initial Library State ---\r\n");
        library.ftraverse(rf);

        String minId = Lib.readLineToStr(dataFile, 5);
        String maxId = Lib.readLineToStr(dataFile, 6);
        
        rf.writeBytes("\r\n>>> Testing f4: Counting songs between " + minId + " and " + maxId + "...\r\n");
        
        int count = library.countInRange(minId, maxId);

        rf.writeBytes("\r\nResult: Found " + count + " songs in the specified range.\r\n");
        rf.close();
    }
}