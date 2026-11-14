import java.io.File;
import java.io.RandomAccessFile;

public class Assignment {
    LuggageStack ls;
    FlightDLL fl;
    String dataFile = "data.txt";

    public Assignment() {
        ls = new LuggageStack();
        fl = new FlightDLL();
    }

    public void loadDataLuggage() {
        String[] ids     = Lib.readLineToStrArray(dataFile, 1);
        String[] owners  = Lib.readLineToStrArray(dataFile, 2);
        int[] weights    = Lib.readLineToIntArray(dataFile, 3);

        for (int i = 0; i < ids.length; i++) {
            ls.push(new Luggage(ids[i], owners[i], weights[i]));
        }
    }

    public void loadDataFlight() {
        String[] ids   = Lib.readLineToStrArray(dataFile, 5);
        String[] dests = Lib.readLineToStrArray(dataFile, 6);
        int[] prices    = Lib.readLineToIntArray(dataFile, 7);

        for (int i = 0; i < ids.length; i++) {
            fl.addSorted(new Flight(ids[i], dests[i], prices[i]));
        }
    }

    public void f1() throws Exception {
        String fname = "f1.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");
        loadDataLuggage();
        rf.writeBytes("Current Luggage (Top -> Bottom):\r\n");
        ls.ftraverse(rf);
        Luggage x = ls.peek();
        if (x == null) {
            rf.writeBytes("\r\nTop luggage: Empty");
        } else {
            rf.writeBytes("\r\nTop luggage: (" + x.id + "," + x.owner + "," + x.weight + ")");
        }
        rf.close();
    }

    public void f2() throws Exception {
        String fname = "f2.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");
        loadDataLuggage();
        int k = Integer.parseInt(Lib.readLineToStr(dataFile, 8));
        rf.writeBytes("Current Luggage (Top -> Bottom):\r\n");
        ls.ftraverse(rf);
        rf.writeBytes("\r\nUnloading " + k + " luggage:\r\n");
        int total = ls.unload(k);
        rf.writeBytes("Unloaded weight: " + total + "\r\n");
        rf.writeBytes("Remaining Luggage (Top -> Bottom):\r\n");
        ls.ftraverse(rf);
        rf.close();
    }

    public void f3() throws Exception {
        String fname = "f3.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");
        rf.writeBytes("Flight List (Ordered by Price):\r\n");
        loadDataFlight();
        fl.ftraverse(rf);
        rf.writeBytes("\r\nReverse Order:\r\n");
        fl.btraverse(rf);
        rf.close();
    }

    public void f4() throws Exception {
        String fname = "f4.txt";
        File f = new File(fname);
        if (f.exists()) f.delete();
        RandomAccessFile rf = new RandomAccessFile(fname, "rw");
        rf.writeBytes("Flight List:\r\n");
        loadDataFlight();
        fl.ftraverse(rf);
        String flightID = Lib.readLineToStr(dataFile, 9);
        int newPrice = Integer.parseInt(Lib.readLineToStr(dataFile, 10));
        rf.writeBytes("\r\nUpdating price of flight " + flightID + " to " + newPrice + ":\r\n");
        fl.updatePrice(flightID, newPrice);
        fl.ftraverse(rf);
        rf.writeBytes("\r\nReverse Order:\r\n");
        fl.btraverse(rf);
        rf.close();
    }
}
