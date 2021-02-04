/*
Developed By Reza Manosuri
std_reza_mansouri@khu.ac.ir
Hosted On GitHub at https://github.com/rezmansouri/CoffeeLEX
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] paths = brewCoffee();
        CoffeeLexer coffeeLexer = new CoffeeLexer(paths[0]);
        Token headOfLinkedList = coffeeLexer.analyze(paths[1]);
        pourCoffee(headOfLinkedList);
        printCredits();
    }

    private static String[] brewCoffee() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the path of Rules File in json format: ");
        String rulesPath = scanner.nextLine();
        System.out.println("Insert the path of program file: ");
        String programPath = scanner.nextLine();
        return new String[]{rulesPath, programPath};
    }

    private static void pourCoffee(Token head) {
        List<List<String>> rows = new ArrayList<>();
        List<String> headers = Arrays.asList("Value", "Type", "Row", "Column");
        List<String> seperators = Arrays.asList("-----", "----", "---", "-----");
        rows.add(headers);
        rows.add(seperators);
        Token p = new Token();
        p.setNextToken(head);
        while (p.hasNext()) {
            p = p.getNextToken();
            rows.add(Arrays.asList(p.getValue(), p.getType(), p.getRowNumber(), p.getColumnNumber()));
        }
        System.out.print(formatAsTable(rows));
    }

    private static String formatAsTable(List<List<String>> rows) {
        int[] maxLengths = new int[rows.get(0).size()];
        for (List<String> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                maxLengths[i] = Math.max(maxLengths[i], row.get(i).length());
            }
        }
        StringBuilder formatBuilder = new StringBuilder();
        for (int maxLength : maxLengths) {
            formatBuilder.append("%-").append(maxLength + 2).append("s");
        }
        String format = formatBuilder.toString();

        StringBuilder result = new StringBuilder();
        for (List<String> row : rows) {
            result.append(String.format(format, row.toArray(new String[0]))).append("\n");
        }
        return result.toString();
    }

    private static void printCredits() {
        System.out.println("CoffeeLex © - 2021");
        System.out.println("Developed By Reza Mansouri");
    }
}