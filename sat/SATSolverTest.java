package sat;


import sat.env.*;
import sat.formula.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.OutputStreamWriter;

import java.util.Arrays;
//import java.util.*;

public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();


    public static void main(String[] args) throws FileNotFoundException{
        // to use during file creation to print filename
        String filename = "_____"; //INSERT YOUR FILENAME

        File file = new File("______" + filename); //INSERT YOUR FILE DIRECTORY

        Formula formula = new Formula();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String string;

        try {
            System.out.println("Parsing file and creating Formula instance...");

            while ((string = bufferedReader.readLine()) != null){
                String[] splitted = string.split("\\s+");
                if (splitted[0].equals("c") || splitted[0].equals("p") || splitted[0].equals("")){

                    continue;
                }
                else {
                    Literal literal;
                    Clause newClause = new Clause();
                    for (String s: splitted){
                        if (Integer.parseInt(s) > 0) {
                            literal = PosLiteral.make(s);
                        }
                        else if (Integer.parseInt(s) < 0){
                            String s_input = s.substring(1);
                            literal = NegLiteral.make(s_input);
                        }
                        else {
                            continue;
                        }
                        newClause = newClause.add(literal);
                    }
                    if (newClause != null){
                        formula = formula.addClause(newClause);
                    }
                }
            }
        }
        catch(IOException e){
            System.out.println(e + "Error95");
        }





        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();
//        Test case
//        System.out.println(formula);
        Environment output = SATSolver.solve(formula);
        //SATSolver returns a null output if the formula is unsatisfiable

//      make sure time in milliseconds
        long time = System.nanoTime();
        long timeElapsed = time - started;
        System.out.println("Time taken: " + timeElapsed/1000000.0 + " ms");

        //if output is not empty
        if (output != null){
            System.out.println("Satisfiable");

//            Creating a text file for results

            System.out.println("Writing File...");
            // buffered writer is allocating a memory space for write in
            try (Writer writefile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\tjuny\\AndroidStudioProjects\\Notebook\\SATSolver\\src\\main\\java\\sat\\BoolAssignment.txt", false), "utf-8"))) {
                String line = "File: " + filename + "\n";
                writefile.write(line);

            }
            catch(IOException e) {

                System.out.println(e + "Error125");

            }

            try (Writer writefile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\tjuny\\AndroidStudioProjects\\Notebook\\SATSolver\\src\\main\\java\\sat\\BoolAssignment.txt", true), "utf-8"))) {
                String outputString = output.toString();
                outputString  = outputString.substring(outputString.indexOf("[") + 1, outputString.indexOf("]"));
                String[] splitted = outputString.split(",");

                for (String s: splitted){
                    //Remove the redundant -> in the output file
                    String[] small_split = s.split("->");
                    //small_split still has whitespaces before and after i0 and i1 so we want to trim it
                    String i0 = small_split[0].trim();
                    String i1 = small_split[1].trim();
                    //System.out.println(Arrays.toString(small_split));
                    String line = "Case " + i0 + " : " +  i1 + "\n";
                    writefile.write(line);
                }


            }
            catch(IOException e) {

                System.out.println(e + "Error149");

            }
            System.out.println("File written");
        }

        else{

            System.out.println("Unsatisfiable");
        }


    }





    public void testSATSolver1(){
        // (a v b)
        Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
    }


    public void testSATSolver2(){
        // (~a)
        Environment e = SATSolver.solve(makeFm(makeCl(na)));
    }

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }

}