import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class Flashcards {
    static Map<String, String> cardsDict = new HashMap<>();
    static Map<String, Integer> statistics = new HashMap<>();
    static List<String> logs = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            input = scanner.nextLine();

            switch (input) {
                case "add":
                    add();
                    break;
                case "remove":
                    remove(scanner);
                    break;
                case "import":
                    importCards(scanner);
                    break;
                case "export":
                    export(scanner);
                    break;
                case "ask":
                    ask(scanner);
                    break;
                case "exit":
                    System.out.println("Bye bye!");
                    if (args.length > 0 && args[0].equals("-e")) {
                        cliExport(args[1]);
                    }
                    return;
                case "log":
                    saveLog(scanner);
                    break;
                case "hardest card":
                    hardest();
                    break;
                case "reset stats":
                    statistics.clear();
                    System.out.println("Card statistics have been reset.");
                    break;
                case "stats":
                    System.out.println(statistics);
                    break;
                default:
                    System.out.println("Invalid action.");
            }
        }
    }

    static void add() {
        Scanner scanner = new Scanner(System.in);
        String card, definition;

        System.out.println("The card:");
        card = scanner.nextLine();

        if (cardsDict.containsKey(card)) {
            System.out.println("The term \"" + card + "\" already exists. Try again:");
            return;
        }

        System.out.println("The definition of the card:");
        definition = scanner.nextLine();

        if (cardsDict.containsValue(definition)) {
            System.out.println("The definition \"" + definition + "\" already exists. Try again:");
            return;
        }

        cardsDict.put(card, definition);
        System.out.println("The pair (\"" + card + "\":\"" + definition + "\") has been added.");
    }

    static void remove(Scanner scanner) {
        System.out.println("Which card?");
        String rem = scanner.nextLine();

        if (cardsDict.containsKey(rem)) {
            cardsDict.remove(rem);
            statistics.remove(rem);
            System.out.println("The card has been removed.");
        } else {
            System.out.println("Can't remove \"" + rem + "\": there is no such card.");
        }
    }

    static void importCards(Scanner scanner) {
        try {
            System.out.println("File name:");
            String file = scanner.nextLine();
            File inputFile = new File(file);
            Scanner fileScanner = new Scanner(inputFile);
            int count = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|\\|");
                String c = parts[0].trim();
                String d = parts[1].trim();
                String s = parts[2].trim();
                cardsDict.put(c, d);
                statistics.put(c, Integer.parseInt(s));
                count++;
            }
            System.out.println(count + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    static void export(Scanner scanner) {
        try {
            System.out.println("File name:");
            String file = scanner.nextLine();
            FileWriter writer = new FileWriter(file);
            for (Map.Entry<String, String> entry : cardsDict.entrySet()) {
                writer.write(entry.getKey() + "||" + entry.getValue() + "||" + statistics.get(entry.getKey()) + "\n");
            }
            writer.close();
            System.out.println(cardsDict.size() + " cards have been saved.");
        } catch (IOException e) {
            System.out.println("An error occurred while exporting.");
        }
    }

    static void ask(Scanner scanner) {
        System.out.println("How many times to ask?");
        int inp = Integer.parseInt(scanner.nextLine());
        List<String> keys = new ArrayList<>(cardsDict.keySet());
        for (int i = 0; i < inp; i++) {
            String card = keys.get(new Random().nextInt(keys.size()));
            System.out.println("Print the definition of \"" + card + "\":");
            String input = scanner.nextLine();
            if (input.equals(cardsDict.get(card))) {
                System.out.println("Correct!");
            } else if (cardsDict.containsValue(input)) {
                String correctCard = cardsDict.entrySet().stream()
                        .filter(entry -> Objects.equals(entry.getValue(), input))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList())
                        .get(0);
                System.out.println("Wrong. The right answer is \"" + cardsDict.get(card) + "\", but your definition is correct for \"" + correctCard + "\".");
                statistics.put(card, statistics.getOrDefault(card, 0) + 1);
            } else {
                System.out.println("Wrong. The right answer is \"" + cardsDict.get(card) + "\".");
                statistics.put(card, statistics.getOrDefault(card, 0) + 1);
            }
        }
    }

    static void saveLog(Scanner scanner) {
        try {
            System.out.println("File name:");
            String fileName = scanner.nextLine();
            FileWriter writer = new FileWriter(fileName);
            for (String log : logs) {
                writer.write(log + "\n");
            }
            writer.close();
            System.out.println("The log has been saved.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the log.");
        }
    }

    static void hardest() {
        if (statistics.isEmpty()) {
            System.out.println("There are no cards with errors.");
        } else {
            int maxErrors = Collections.max(statistics.values());
            if (maxErrors == 0) {
                System.out.println("There are no cards with errors.");
            } else {
                List<String> hardestCards = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
                    if (entry.getValue() == maxErrors) {
                        hardestCards.add("\"" + entry.getKey() + "\"");
                    }
                }
                if (hardestCards.size() == 1) {
                    System.out.println("The hardest card is " + hardestCards.get(0) + ". You have " + maxErrors + " errors answering it.");
                } else {
                    System.out.println("The hardest cards are " + String.join(", ", hardestCards));
                }
            }
        }
    }

    static void cliExport(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            for (Map.Entry<String, String> entry : cardsDict.entrySet()) {
                writer.write(entry.getKey() + "||" + entry.getValue() + "||" + statistics.get(entry.getKey()) + "\n");
            }
            writer.close();
            System.out.println(cardsDict.size() + " cards have been saved.");
        } catch (IOException e) {
            System.out.println("An error occurred while exporting.");
        }
    }
}
