import java.io.RandomAccessFile;

public class SongBST {
    private class TreeNode {
        Song song;
        TreeNode left, right;
        TreeNode(Song s) { this.song = s; left = right = null; }
    }
    
    private TreeNode root;
    public SongBST() { root = null; }

    // Provided
    public void helper_fn(Song song) {
        if (root == null) {
            root = new TreeNode(song);
            return;
        }
        TreeNode current = root;
        TreeNode parent = null;
        while (current != null) {
            parent = current;
            int cmp = song.songId.compareTo(current.song.songId);
            if (cmp > 0) {
                current = current.left;
            } else if (cmp < 0) {
                current = current.right;
            } else {
                return;
            }
        }
        int cmp = song.songId.compareTo(parent.song.songId);
        if (cmp > 0) {
            parent.left = new TreeNode(song);
        } else {
            parent.right = new TreeNode(song);
        }
    }
    public Song search_f1(String songId) {
        TreeNode current = root;
        while (current != null) {
            int cmp = songId.compareTo(current.song.songId);
            if (cmp == 0) return current.song;
            if (cmp > 0) current = current.left;
            else current = current.right;
        }
        return null;
    }
    
    public Song search(String songId) {
        TreeNode current = root;
        while (current != null) {
            int cmp = songId.compareTo(current.song.songId);
            if (cmp == 0) return current.song;
            if (cmp < 0) current = current.left;
            else current = current.right;
        }
        return null;
    }
    
    // Provided
    public void ftraverse(RandomAccessFile f) throws Exception {
        f.writeBytes("--- Song Library (In-Order) ---\r\n");
        if (root == null) {
            f.writeBytes("Empty\r\n");
        } else {
            inOrder(root, f);
        }
    }
    
    private void inOrder(TreeNode node, RandomAccessFile f) throws Exception {
        if (node == null) return;
        inOrder(node.left, f);
        f.writeBytes(node.song.toString() + "\r\n");
        inOrder(node.right, f);
    }

    // =======================================================
    // === STUDENT IMPLEMENTATION AREA                     ===
    // =======================================================

    // f3: Insert a Song into the Library
    public void insert(Song song) {
        // ---------- Student's code starts from here ----------
        // Students are welcomed to use any helper function(s)
        if (song == null) {
            return;
        }
        TreeNode newNode = new TreeNode(song);
        if (root == null) {
            root = newNode;
            return;
        }
        TreeNode current = root;
        TreeNode parent = null;
        while (current != null) {
            parent = current;
            int cmp = song.songId.compareTo(current.song.songId);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return; // duplicate key
            }
        }
        int cmp = song.songId.compareTo(parent.song.songId);
        if (cmp < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        // -----------------------------------------------------
    }

    // f4: Count Songs within an ID Range
    public int countInRange(String minId, String maxId) {
        int count = 0;
        // ---------- Student's code starts from here ----------
        // Students are welcomed to use any helper function(s)
        if (minId != null && maxId != null && minId.compareTo(maxId) > 0) {
            return 0;
        }
        count = countInRangeHelper(root, minId, maxId);
        // -----------------------------------------------------
        return count;
    }

    private int countInRangeHelper(TreeNode node, String minId, String maxId) {
        if (node == null) {
            return 0;
        }
        if (minId != null && node.song.songId.compareTo(minId) < 0) {
            return countInRangeHelper(node.right, minId, maxId);
        }
        if (maxId != null && node.song.songId.compareTo(maxId) > 0) {
            return countInRangeHelper(node.left, minId, maxId);
        }
        return 1 + countInRangeHelper(node.left, minId, maxId) + countInRangeHelper(node.right, minId, maxId);
    }
    // =======================================================
}
