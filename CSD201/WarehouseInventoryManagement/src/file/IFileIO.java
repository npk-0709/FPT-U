package file;

import java.util.List;

public interface IFileIO<E> {

    List<E> readFromFile();

    boolean saveToFile(List<E> list);
}
