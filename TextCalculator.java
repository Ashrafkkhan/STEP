import java.util.*;

public class TextCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Text-Based Calculator");
        System.out.println("Enter mathematical expressions or 'quit' to exit");

        while (true) {
            System.out.print("\nEnter expression: ");
            String expression = scanner.nextLine().trim();

            if (expression.equalsIgnoreCase("quit")) {
                break;
            }

            if (expression.isEmpty()) {
                System.out.println("Please enter a valid expression.");
                continue;
            }

            try {
                double result = evaluateExpression(expression);
                System.out.println("Final result: " + result);
            } catch (Exception e) {
                System.out.println("Error evaluating expression: " + e.getMessage());
            }
        }

        scanner.close();
    }

    public static double evaluateExpression(String expression) {
        System.out.println("\nEvaluating: " + expression);
        List<String> postfix = infixToPostfix(expression);
        System.out.println("Postfix: " + postfix);
        double result = evaluatePostfix(postfix);
        System.out.println("Calculation complete!");
        return result;
    }

    private static List<String> infixToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Stack<Character> operators = new Stack<>();
        StringBuilder number = new StringBuilder();
        char[] chars = expression.replaceAll("\\s+", "").toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    output.add(number.toString());
                    number.setLength(0);
                }

                if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        output.add(String.valueOf(operators.pop()));
                    }
                    if (operators.isEmpty() || operators.pop() != '(') {
                        throw new IllegalArgumentException("Mismatched parentheses");
                    }
                } else if (isOperator(c)) {
                    if (c == '-' && (i == 0 || chars[i - 1] == '(' || isOperator(chars[i - 1]))) {
                        number.append(c);
                    } else {
                        while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                            output.add(String.valueOf(operators.pop()));
                        }
                        operators.push(c);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid character: " + c);
                }
            }
        }

        if (number.length() > 0) {
            output.add(number.toString());
        }

        while (!operators.isEmpty()) {
            char op = operators.pop();
            if (op == '(' || op == ')') {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(String.valueOf(op));
        }

        return output;
    }

    private static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                if (stack.size() < 2) throw new IllegalArgumentException("Invalid expression");
                double b = stack.pop();
                double a = stack.pop();
                double result = applyOperation(a, b, token.charAt(0));
                System.out.println("Step: " + a + " " + token + " " + b + " = " + result);
                stack.push(result);
            }
        }

        if (stack.size() != 1) throw new IllegalArgumentException("Invalid expression");
        return stack.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private static int precedence(char c) {
        switch (c) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
        }
        return -1;
    }

    private static double applyOperation(double a, double b, char operator) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
        }
        throw new IllegalArgumentException("Unknown operator: " + operator);
    }

    private static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

