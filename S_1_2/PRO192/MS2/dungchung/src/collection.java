import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class MyBook implements IBook {

    @Override
    public int f1(List<Book> t) {
        int maxPage = -1;
        for (Book b : t) {
            // Check if author contains at least two words (separated by spaces)
            String[] words = b.getAuthor().trim().split("\\s+");
            if (words.length >= 2) {
                if (b.getPage() > maxPage) {
                    maxPage = b.getPage();
                }
            }
        }
        return maxPage;
    }

    @Override
    public void f2(List<Book> t) {
        // Sort ascendingly by page number
        Collections.sort(t, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return Integer.compare(b1.getPage(), b2.getPage());
            }
        });
    }

    @Override
    public void f3(List<Book> t) {
        if (t.isEmpty()) return;

        // 1. Find the global minimum page value in the list
        int minPage = t.get(0).getPage();
        for (Book b : t) {
            if (b.getPage() < minPage) {
                minPage = b.getPage();
            }
        }

        // 2. Remove Books where author starts with 'N' AND page equals minPage
        // Using removeIf to ensure the list is updated in-place correctly
        final int finalMin = minPage;
        t.removeIf(b -> b.getAuthor().startsWith("N") && b.getPage() == finalMin);
    }
}