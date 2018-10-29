package net.bbqroast.formulaParser;

import java.text.ParseException;
import java.util.HashMap;

/**
 * Takes a formula formed from (), ^ (and), . (or), > (if .. then ..), a-z, and !
 * Then the solve() function can be used to check if the value is true or false
 * TODO: Currently asks for input in constructor, should be done in Main via a field for decoupling
 */
public class Parser {
    HashMap<String, String> simpleSubFormulas = new HashMap<>();
    HashMap<Character, Boolean> propositions = new HashMap<>();

    String formula;

    public Parser(String formula) throws ParseException {
        // Check for illegal characters
        if (formula.matches("[^a-z()^.>]")) {
            throw new ParseException("Contains invalid character!", 0);
        }

        // Prompt for proposition input
        String found = "";
        for (char c : formula.toCharArray())   {
            if (Character.isAlphabetic(c))  {
                if (!found.contains(Character.toString(c)))    {
                    String input = "";
                    while (input.length() != 1 || input.matches("[^01]")) {
                        input = Main.prompt("Enter boolean value (0/1) for "+c+":");
                    }
                    boolean flag;
                    if (input.equals("0")) {
                        flag = false;
                    } else {
                        flag = true;
                    }

                    propositions.put(c, new Boolean(flag));
                    found += c;
                }
            }
        }

        this.formula = formula;
        form();
    }

    public boolean solve() {
        for (String key : simpleSubFormulas.keySet())    {
            String localFormula = simpleSubFormulas.get(key);
            if (localFormula.matches("^\\([a-z][.>^][a-z]\\)$"))   {
                String a = localFormula.substring(1,2);
                String b = localFormula.substring(3,4);
                boolean aV = propositions.get(a.toCharArray()[0]);
                boolean bV = propositions.get(b.toCharArray()[0]);
                String op = localFormula.substring(2,3);
                if (op.equals(".")) {
                    propositions.put(key.toCharArray()[0], aV || bV); //or
                } else if (op.equals(">"))  {
                    propositions.put(key.toCharArray()[0], !aV || bV); // if..then
                } else {
                    propositions.put(key.toCharArray()[0], aV && bV);//and
                }
            } else if (localFormula.matches("^!\\([a-z]\\)$")) { // not
                String a = localFormula.substring(2,3);
                boolean aV = propositions.get(a.toCharArray()[0]);
                propositions.put(key.toCharArray()[0], !aV);
            } else if (localFormula.matches("^\\([a-z]\\)$")) { //(a)
                String a = localFormula.substring(2,3);
                boolean aV = propositions.get(a.toCharArray()[0]);
                propositions.put(key.toCharArray()[0], aV);
            }
        }
        return propositions.get(formula.toCharArray()[0]);
    }

    private void form() throws ParseException {
        while (replaceSubFormula()) {
            System.out.println(formula);
        }
    }

    /**
     * Replaces some sub-formula with a new proposition
     */
    private boolean replaceSubFormula() throws ParseException  {
        for (int i = 0; i < formula.length(); i++)  {
            if (formula.substring(i,i + 1).equals("(")) {
                // Check if simple sub formula
                if (formula.substring(i + 1).indexOf("(") == -1 || formula.substring(i).indexOf("(") > formula.substring(i).indexOf(")"))    {
                    String found = formula.substring(i, formula.substring(i).indexOf(")") + 1 + i);
                    if (found.matches("^\\([a-z][.>^][a-z]\\)$"))   {
                        String key = getNewProp();
                        simpleSubFormulas.put(key, found);
                        formula = formula.replace(found, key);
                        return true;
                    } else if (found.matches("^\\([a-z]\\)$") && formula.substring(i-1,i).equals(("!"))) {
                        String key = getNewProp();
                        simpleSubFormulas.put(key, "!"+found);
                        formula = formula.replace("!"+found, key);
                        return true;
                    } else if (found.matches("^\\([a-z]\\)$"))   {
                        String key = getNewProp();
                        simpleSubFormulas.put(key, found);
                        formula = formula.replace(found, key);
                        return true;
                    } else {
                        throw new ParseException("Couldn't match: "+found, 0);
                    }
                }
            }
        }
        return false;
    }

    private String getNewProp() throws ParseException   {
        for (char c : "abcdefghijklmnopqrstuv".toCharArray())   {
            if (!formula.contains(Character.toString(c)) && !propositions.containsKey(new Character(c)) && !simpleSubFormulas.containsKey(Character.toString(c))) {
                return Character.toString(c);
            }
        }
        throw new ParseException("Maximum of 26 propositions exceeded :(", 0);
    }
}
