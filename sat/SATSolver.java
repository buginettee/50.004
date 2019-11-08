package sat;


import immutable.ImList;
import sat.env.*;
import immutable.EmptyImList;
import sat.formula.*;


/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in classification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     * null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment e = new Environment();

        ImList<Clause> clauses = formula.getClauses();

        Environment result = solve(clauses, e);
        return result;
    }


    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses formula in conjunctive normal form
     * @param env     assignment of some or all variables in clauses to true or
     *                false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     * or null if no such environment exists.
     */
    public static Clause smallest(ImList<Clause> clauses){
        Clause c = clauses.first();
        for (Clause x : clauses){
            if (x.size() < c.size()){
                c = x;
            }
            if (x.isEmpty()){ // having an empty clause is wrong!! means we need to backtrackkk
                return null;
            }
        }
        return c;
    }

    private static Environment solve(ImList<Clause> clauses, Environment env) {

        // if there are no more clauses, means there's a solution!
        if (clauses.isEmpty())
            return env;

        Clause smallest = smallest(clauses);

        if (smallest == null) // failed :-( (we found an empty clause)
            return null;

        Literal l = smallest.chooseLiteral();

        if (smallest.isUnit()) { //one literal left
            return LASTONE(env, clauses, l); //checK THE LAST LITERAL AND WE'RE D O N E :-)
        } else {
            // must try for both postive and negative
            Environment testP = env.putTrue(l.getVariable()); // trying to be positive
            Literal lP = PosLiteral.make(l.getVariable());
            ImList clP = substitute(clauses, lP);
            Environment ef1 = solve(clP, testP);
            if (ef1 == null) { // ok it didn't work time to be negative (empty clause was found)
                Environment envN = env.putFalse(l.getVariable());
                Literal lN = NegLiteral.make(l.getVariable());
                ImList clN = substitute(clauses, lN);
                Environment ef2 = solve(clN, envN);
                return ef2;
            }
            return ef1; // return env not null
        }
    }

    private static Environment LASTONE(Environment env, ImList<Clause> clauses, Literal l){
        // just clear the variable according to its negation since there's only one left!!
        if (l instanceof PosLiteral){
            env = env.put(l.getVariable(), Bool.TRUE);
            ImList<Clause> newC = substitute(clauses,l);
            return solve(newC, env);
        } else {
            env = env.put(l.getVariable(), Bool.FALSE);
            ImList<Clause> newC = substitute(clauses,l);
            return solve(newC, env);
        }
    }



    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses , a list of clauses
     * @param l       , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
                                             Literal l) {
        ImList<Clause> new_clauses = new EmptyImList<>();

        for (Clause c : clauses) {
            if (l != null) {
                Clause new_c = c.reduce(l); // set literal to true or false
                if (new_c != null) {
                    new_clauses = new_clauses.add(new_c); // append to new clauses
                }
            }
        }
        return new_clauses;
    }
}