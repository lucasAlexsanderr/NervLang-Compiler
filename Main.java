import java.io.*;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        String inputFile = (args.length > 0) ? args[0] : "input.txt";

        System.out.println("========================================================");
        System.out.println("  NervLang Compiler Frontend v1.0");
        System.out.println("  EVA-themeded compiler - JFlex + JCup integration");
        System.out.println("========================================================");
        System.out.println("Input file: " + inputFile);
        System.out.println("--------------------------------------------------------");
        System.out.println();

        try {
            FileReader fr = new FileReader(inputFile);
            Scanner scanner = new Scanner(fr);
            parser p = new parser(scanner);

            System.out.println(">>> Starting Compilation <<<");
            System.out.println();

            p.parse();

            System.out.println();
            System.out.println("========================================================");
            System.out.println("  Compilation completed successfully!");
            System.out.println("========================================================");

        } catch (FileNotFoundException e) {
            System.err.println("Error: File '" + inputFile + "' not found.");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Compilation failed:");
            e.printStackTrace();
        }
    }
}
