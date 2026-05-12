
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

//        Goalkeeper goalkeeper = new Goalkeeper("01", "NVA", 30, "VN", "u30", 500000.0, 3);
//        Defender defender = new Defender("02", "NVB", 31, "VN", "u31", 600000.0, 2);
//        Midfielder midfielder = new Midfielder("03", "NVC", 32, "V2", "u30", 700000.0, 4);
//        Forward forward = new Forward("04", "NVD", 33, "VN", "u33", 800000.0, 5);
//        PlayerManager playermanager = new PlayerManager();
//        playermanager.addPlayer(goalkeeper);
//        playermanager.addPlayer(defender);
//        playermanager.addPlayer(midfielder);
//        playermanager.addPlayer(forward);
//
//        playermanager.displayAllPlayers();
//
//        System.out.println(goalkeeper.calculateTotalIncome());
//        System.out.println(defender.calculateTotalIncome());
//        System.out.println(midfielder.calculateTotalIncome());
//        System.out.println(forward.calculateTotalIncome());
        Scanner myScanner = new Scanner(System.in);
        PlayerManager playermanager = new PlayerManager();
        while (true) {
            System.out.println("------------------------------------");
            System.out.println(" 1. Add Player");
            System.out.println(" 2. Search Player by ID ");
            System.out.println(" 3. Display All Players");
            System.out.println(" 4. Delete Player ");
            System.out.println(" 5. Sort Players ");
            System.out.println(" 6. Generate Reports");
            System.out.println(" 7. Save to File");
            System.out.println(" 8. Load from File");
            System.out.println(" 9. Exit");
            System.out.println("------------------------------------");

            System.out.println("");

            int chon = myScanner.nextInt();

            if (chon == 1) {

                String[] typePlayer = {"Defender", "Goalkeeper", "Midfielder", "Forward"};
                System.out.println("Input Type Player: ");
                myScanner.nextLine();
                String inputTypePlayer = myScanner.nextLine();

                System.out.print("Input playerId: ");
                String playerId = myScanner.nextLine();
                System.out.print("Input fullName: ");
                String fullName = myScanner.nextLine();
                if (fullName.isEmpty()) {
                    System.out.print("Name Can Not Be Empty! ");
                    continue;
                }
                System.out.print("Input age: ");
                int age = myScanner.nextInt();
                if (age <= 0) {
                    System.out.print("Age must be >0 ! ");
                    continue;
                }
                System.out.print("Input nationality: ");
                myScanner.nextLine();
                String nationality = myScanner.nextLine();
                if (nationality.isEmpty()) {
                    System.out.print("nationality Can Not Be Empty! ");
                    continue;
                }
                System.out.print("Input clubName: ");
                String clubName = myScanner.nextLine();
                if (clubName.isEmpty()) {
                    System.out.print("clubName Can Not Be Empty! ");
                    continue;
                }
                System.out.print("Input baseSalary: ");
                int baseSalary = myScanner.nextInt();
                if (baseSalary <= 0) {
                    System.out.print("baseSalary must be >0 ! ");
                    continue;
                }

                if (inputTypePlayer.equals("Defender")) {
                    System.out.print("Input tackles: ");
                    int tackles = myScanner.nextInt();
                    if (tackles <= 0) {
                        System.out.print("tackles must be >0 ! ");
                        continue;
                    }
                    Defender defender = new Defender(playerId, fullName, age, nationality, clubName, baseSalary, tackles);
                    playermanager.addPlayer(defender);
                } else if (inputTypePlayer.equals("Forward")) {
                    System.out.print("Input goals: ");
                    int goals = myScanner.nextInt();
                    if (goals <= 0) {
                        System.out.print("goals must be >0 ! ");
                        continue;
                    }
                    Forward forward = new Forward(playerId, fullName, age, nationality, clubName, baseSalary, goals);
                    playermanager.addPlayer(forward);
                } else if (inputTypePlayer.equals("Midfielder")) {
                    System.out.print("Input assists: ");
                    int assists = myScanner.nextInt();
                    if (assists <= 0) {
                        System.out.print("assists must be >0 ! ");
                        continue;
                    }
                    Midfielder midfielder = new Midfielder(playerId, fullName, age, nationality, clubName, baseSalary, assists);
                    playermanager.addPlayer(midfielder);
                } else if (inputTypePlayer.equals("Goalkeeper")) {
                    System.out.print("Input cleanSheets: ");
                    int cleanSheets = myScanner.nextInt();
                    if (cleanSheets <= 0) {
                        System.out.print("cleanSheets must be >0 ! ");
                        continue;
                    }
                    Goalkeeper goalkeeper = new Goalkeeper(playerId, fullName, age, nationality, clubName, baseSalary, cleanSheets);
                    playermanager.addPlayer(goalkeeper);
                }

            } else if (chon == 2) {

            } else if (chon == 3) {
                playermanager.displayAllPlayers();
            } else if (chon == 4) {
                System.out.print("Input ID to DELETE: ");
                String id = myScanner.nextLine();
                playermanager.deletePlayer(id);

            } else if (chon == 5) {

            } else if (chon == 6) {

            } else if (chon == 7) {

                String filePath = "data.txt";

                // Thêm tham số 'true' vào FileWriter(filePath, true) nếu bạn muốn GHI TIẾP (append) vào file cũ
                // Mặc định nó sẽ ghi đè (overwrite) file hiện tại.
                try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

                    for (Player pl : playermanager) {
                        writer.write(pl.toString());
                        writer.newLine();
                    }
                    System.out.println("Ghi file thành công!");
                } catch (IOException e) {
                    System.err.println("Có lỗi xảy ra khi ghi file: " + e.getMessage());
                }

            } else if (chon == 8) {
                String filePath = "data.txt";
                PlayerManager playermanager2 = new PlayerManager();

                try ( BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] temp = line.split(",");
                        String type = temp[0];
                        if (type.equals("Defender")) {
                            Defender defender = new Defender(temp[1], temp[2], Integer.parseInt(temp[3]), temp[4], temp[5], Double.parseDouble(temp[6]), Integer.parseInt(temp[7]));
                            playermanager2.addPlayer(defender);
                        } else if (type.equals("Forward")) {
                            Forward forward = new Forward(temp[1], temp[2], Integer.parseInt(temp[3]), temp[4], temp[5], Double.parseDouble(temp[6]), Integer.parseInt(temp[7]));
                            playermanager2.addPlayer(forward);
                        } else if (type.equals("Midfielder")) {
                            Midfielder midfielder = new Midfielder(temp[1], temp[2], Integer.parseInt(temp[3]), temp[4], temp[5], Double.parseDouble(temp[6]), Integer.parseInt(temp[7]));
                            playermanager2.addPlayer(midfielder);
                        } else if (type.equals("Goalkeeper")) {
                            Goalkeeper goalkeeper = new Goalkeeper(temp[1], temp[2], Integer.parseInt(temp[3]), temp[4], temp[5], Double.parseDouble(temp[6]), Integer.parseInt(temp[7]));
                            playermanager2.addPlayer(goalkeeper);
                        }

                    }
                    playermanager = playermanager2;
                } catch (IOException e) {
                    System.err.println("Có lỗi xảy ra khi đọc file: " + e.getMessage());
                }

            } else if (chon == 9) {
                System.out.println("Bye !!!! ");
                break;

            }

        }

    }

}
