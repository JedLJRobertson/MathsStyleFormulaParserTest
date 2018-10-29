package net.bbqroast.formulaParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

public class Main {

    public static void main(String[] args) {
        String formula = prompt("Input formula (^,>,.,!,a-z) :");

        try {
            Parser parser = new Parser(formula);
            if (parser.solve()) {
                System.out.println("true");
            } else {
                System.out.println("false");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static String prompt(String message)	{
        System.out.print("\n"+message);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        try {
            return buffer.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
