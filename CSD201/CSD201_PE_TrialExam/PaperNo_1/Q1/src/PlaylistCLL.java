import java.io.RandomAccessFile;

public class PlaylistCLL {
    private class CLLNode {
        Song song;
        CLLNode next;
        CLLNode(Song s) { this.song = s; this.next = null; }
    }

    private CLLNode tail;
    public PlaylistCLL() { this.tail = null; }
    
    // Provided
    public void helper_fn(Song song) {
        if(song == null){
            return;
        }
        CLLNode newNode = new CLLNode(song);
        if (tail == null) {
            tail = newNode;
            tail.next = tail;
        } else {
            newNode.next = tail.next;
            tail.next = newNode;
        }
    }
    
    public void ftraverse(RandomAccessFile f) throws Exception {
        f.writeBytes("--- Current Playlist ---\r\n");
        if (tail == null) {
            f.writeBytes("Empty\r\n");
            return;
        }
        CLLNode current = tail.next;
        do {
            f.writeBytes(current.song.toString() + "\r\n");
            current = current.next;
        } while (current != tail.next);
    }

    // =======================================================
    // === STUDENT IMPLEMENTATION AREA                     ===
    // =======================================================

    // f1: Count Songs in Playlist
    public int countSongs() {
        int count = 0;
        // ---------- Student's code starts from here ----------
        // Students are welcomed to use any helper function(s)
        if (tail == null) {
            return 0;
        }
        CLLNode current = tail.next;
        do {
            count++;
            current = current.next;
        } while (current != tail.next);
        // -----------------------------------------------------
        return count;
    }

    // f2: Add a Song to the Playlist
    public void addToPlaylist(Song song) {
        // ---------- Student's code starts from here ----------
        // Students are welcomed to use any helper function(s)
        if (song == null) {
            return;
        }
        CLLNode newNode = new CLLNode(song);
        if (tail == null) {
            tail = newNode;
            tail.next = tail;
        } else {
            newNode.next = tail.next;
            tail.next = newNode;
            tail = newNode;
        }
        // -----------------------------------------------------
    }
    // =======================================================
}
