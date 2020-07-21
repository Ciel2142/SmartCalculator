
import java.math.BigInteger;
import java.util.*;

public class Main {
    https://github.com/Parasite2142/SmartCalculator.git
    public static void main(String[] args) {

        String input;
        HashMap<String, Integer> variables = new HashMap<>();
        HashMap<String, String> BigIntVars = new HashMap<>();

        while (true) {
            input = assignVariables(variables, BigIntVars);

            if (input.isBlank()) {
                continue;
            }
            if ("/help".equals(input)) {
                System.out.println("The program calculates the sum of numbers");
                continue;
            }
            if ("/exit".equals(input)) {
                System.out.println("Bye !");
                break;
            }
            if (input.matches("/[a-zA-Z]+")) {
                System.out.println("Unknown command");
                continue;
            }
            makePrefix(input, variables, BigIntVars);
        }
    }

    public static boolean checkInt(String n) {
        return n.matches("^[+-]?(0|[1-9]\\d*)([.,](0|\\d*[1-9]))?$");
    }

    private static boolean checkVariable(String n, HashMap<String, Integer> variables) {
        return variables.containsKey(n);
    }

    private static boolean checkBigIntVar(String n, HashMap<String, String> bigIntVars) {
        return bigIntVars.containsKey(n);
    }


    public static String assignVariables(HashMap<String, Integer> variables, HashMap<String, String> bigIntVars) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] array;
        while (true) {
            array = input.trim().split("(\\s*=\\s*|\\s+)");
            if (input.contains("=") && array.length != 2) {
                System.out.println("Invalid assignment");
            } else if (!input.contains("=")) {
                if (array[0].matches("[a-zA-Z]+") && (!variables.containsKey(array[0]) && !bigIntVars.containsKey(array[0]))) {
                    System.out.println("Unknown variable");
                }
                break;
            } else if (array[0].matches("[a-zA-Z]+") && array[1].matches("[a-zA-Z]+")) {
                if (bigIntVars.containsKey(array[1])) {
                    bigIntVars.put(array[0], bigIntVars.get(array[1]));
                } else if (variables.containsKey(array[1])) {
                    variables.put(array[0], variables.get(array[1]));
                } else {
                    System.out.println("Unknown variable");
                }
            } else if (array[0].matches("[a-zA-Z]+") && checkInt(array[1])){
                BigInteger k = new BigInteger(array[1]);
                if (k.longValue() > Integer.MAX_VALUE || k.longValue() < Integer.MIN_VALUE) {
                    bigIntVars.put(array[0], k.toString());
                } else {
                    variables.put(array[0], Integer.parseInt(array[1]));
                }

            } else {
                System.out.println("Invalid assignment");
            }
            input = scanner.nextLine();
        }
        return input;
    }

    public static void manipulateResult(String[] result, HashMap<String, Integer> variables) {
        Deque<Integer> line = new ArrayDeque<>();
        int a;
        int b;

        for (String n: result) {
            if (checkInt(n)) {
                line.offerLast(Integer.parseInt(n));
            } else if (checkVariable(n, variables)) {
                line.offerLast(variables.get(n));
            } else if (n.matches("[\\-+^*/]")) {
                a = line.pollLast();
                b = line.pollLast();
                switch (n) {
                    case "+":
                        line.offerLast(b + a);
                        break;
                    case "-":
                        line.offerLast(b - a);
                        break;
                    case "*":
                        line.offerLast(b * a);
                        break;
                    case "/":
                        line.offerLast(b / a);
                        break;
                    default:
                        line.offerLast((int) Math.pow(b, a));
                        break;
                }
            }
        }
        System.out.println(line.poll());
    }

    public static void manipulateBigIntResult(String[] result, HashMap<String, String> vars) {
        Deque<String> bigIntLine = new ArrayDeque<>();
        BigInteger k;
        BigInteger j;

        for (String n: result) {
            if (checkInt(n)) {
                bigIntLine.offerLast(n);
            } else if (checkBigIntVar(n, vars)) {
                bigIntLine.offerLast(vars.get(n));
            } else if (n.matches("[\\-+^*/]")) {
                k = new BigInteger(bigIntLine.pollLast());
                j = new BigInteger(bigIntLine.pollLast());
                switch (n) {
                    case "+":
                        bigIntLine.offerLast(j.add(k).toString());
                        break;
                    case "-":
                        bigIntLine.offerLast(j.subtract(k).toString());
                        break;
                    case "*":
                        bigIntLine.offerLast(j.multiply(k).toString());
                        break;
                    default:
                        bigIntLine.offerLast(j.divide(k).toString());
                        break;
                }
            }
        }
        System.out.println(bigIntLine.peekLast().toString());
    }


    public static String parseInput(String input) {
        if (input.matches(".*?[*/]{2,}.*?")) {
            System.out.println("Invalid expression");
            return null;
        }
        return input.replaceAll("-(--)+", "-").
                replaceAll("(--)+", "+").
                replaceAll("\\(", " ( ").
                replaceAll("\\)", " ) ").
                replaceAll("\\*+", " * ").
                replaceAll("\\++", " + ").
                replaceAll("-", " - ").
                replaceAll("/", " / ").
                replaceAll("\\^", " ^ ");
    }

    private static void makePrefix(String input, HashMap<String,
            Integer> variables, HashMap<String, String> BigIntVars) {
        input = parseInput(input);
        if (input == null) {
            return;
        }
        String[] inputS = input.trim().split("\\s+");

        if (inputS.length == 1) {
            if (checkInt(inputS[0])) {
                System.out.println(inputS[0]);
                return;
            } else if (checkVariable(inputS[0], variables)) {
                System.out.println(variables.get(inputS[0]));
                return;
            } else if (checkBigIntVar(inputS[0], BigIntVars)) {
                System.out.println(BigIntVars.get(inputS[0]));
                return;
            } else {
                System.out.println("Invalid expression");
                return;
            }
        }

        Deque<String> operationOrder = new ArrayDeque<>();
        HashMap<String, Integer> priorityOperator = new HashMap<>(
                Map.of("-", 1, "+", 1,
                        "*", 2, "/", 2,
                        "^", 3));
        StringBuilder postFix = new StringBuilder();
        Queue<String> paranthesis = new ArrayDeque<>();
        boolean bigInt = false;
        BigInteger num;

        for (String n : inputS) {
            if (!checkBigIntVar(n, BigIntVars) && !checkInt(n) &&
                    !checkVariable(n, variables) && !n.matches("[/*^\\-+()]")) {
                System.out.println("Invalid expression");
                return;
            }

            if (checkInt(n)) {
                num = new BigInteger(n);
                if (num.longValue() > Integer.MAX_VALUE || num.longValue() < Integer.MIN_VALUE) {
                    bigInt = true;
                    postFix.append(num.toString()).append(" ");
                } else {
                    postFix.append(n).append(" ");
                }

            } else if (checkVariable(n, variables)) {
                postFix.append(variables.get(n)).append(" ");
            } else if (checkBigIntVar(n, BigIntVars)) {
                bigInt = true;
                postFix.append(BigIntVars.get(n)).append(" ");
            } else if ("(".equals(n)) {
                operationOrder.offerLast(n);
                paranthesis.offer(n);
            } else if (")".equals(n)) {
                while (true) {
                    if ("(".equals(operationOrder.peekLast())) {
                        operationOrder.pollLast();
                        paranthesis.poll();
                        break;
                    }
                    if (operationOrder.isEmpty()) {
                        System.out.println("Invalid expression");
                        return;
                    }
                    postFix.append(operationOrder.pollLast()).append(" ");
                }
            } else if (operationOrder.isEmpty() || "(".equals(operationOrder.peekLast()) ||
                    priorityOperator.get(n) > priorityOperator.get(operationOrder.peekLast())) {
                operationOrder.offerLast(n);
            } else if (priorityOperator.get(n) <= priorityOperator.get(operationOrder.peekLast())) {
                while (!operationOrder.isEmpty()) {
                    if ("(".equals(operationOrder.peekLast())) {
                        break;
                    }
                    postFix.append(operationOrder.pollLast()).append(" ");
                }
                operationOrder.offerLast(n);
            }
        }
        while (!operationOrder.isEmpty()) {
            postFix.append(operationOrder.pollLast()).append(" ");
        }
        if (!paranthesis.isEmpty()) {
            System.out.println("Invalid expression");
            return;
        }
        if (bigInt) {
            manipulateBigIntResult(postFix.toString().split("\\s+"), BigIntVars);
        } else {
            manipulateResult(postFix.toString().split("\\s"), variables);
        }

    }
}
