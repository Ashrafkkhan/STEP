import java.util.Scanner;

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
            
            if (!validateExpression(expression)) {
                System.out.println("Invalid expression format!");
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
    
    public static boolean validateExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            return false;
        }
        
        int parenthesesCount = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            
            if (!isValidCharacter(c)) {
                return false;
            }
            
            if (c == '(') {
                parenthesesCount++;
            } else if (c == ')') {
                parenthesesCount--;
                if (parenthesesCount < 0) {
                    return false;
                }
            }
            
            if (isOperator(c)) {
                if (i == 0 || i == expression.length() - 1) {
                    return false;
                }
                
                char nextChar = expression.charAt(i + 1);
                if (isOperator(nextChar) || nextChar == ')') {
                    return false;
                }
            }
        }
        
        if (parenthesesCount != 0) {
            return false;
        }
        
        return true;
    }
    
    private static boolean isValidCharacter(char c) {
        return Character.isDigit(c) || isOperator(c) || c == ' ' || c == '(' || c == ')' || c == '.';
    }
    
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    
    public static double evaluateExpression(String expression) {
        System.out.println("\nEvaluating: " + expression);
        
        String simplified = expression.replaceAll(" ", "");
        
        while (simplified.contains("(")) {
            int openParen = simplified.lastIndexOf("(");
            int closeParen = simplified.indexOf(")", openParen);
            
            if (closeParen == -1) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            
            String innerExpr = simplified.substring(openParen + 1, closeParen);
            double innerResult = evaluateSimpleExpression(innerExpr);
            
            System.out.println("Parentheses evaluation: (" + innerExpr + ") = " + innerResult);
            
            String before = simplified.substring(0, openParen);
            String after = simplified.substring(closeParen + 1);
            simplified = before + innerResult + after;
            
            System.out.println("Updated expression: " + simplified);
        }
        
        double finalResult = evaluateSimpleExpression(simplified);
        System.out.println("Calculation complete!");
        return finalResult;
    }
    
    private static double evaluateSimpleExpression(String expression) {
        double[] numbers = parseNumbers(expression);
        char[] operators = parseOperators(expression);
        
        System.out.println("Numbers: " + java.util.Arrays.toString(numbers));
        System.out.println("Operators: " + java.util.Arrays.toString(operators));
        
        double result = numbers[0];
        StringBuilder steps = new StringBuilder();
        steps.append(expression).append(" = ");
        
        for (int i = 0; i < operators.length; i++) {
            if (operators[i] == '*' || operators[i] == '/') {
                double temp = applyOperation(numbers[i], numbers[i + 1], operators[i]);
                System.out.println("Step: " + numbers[i] + " " + operators[i] + " " + numbers[i + 1] + " = " + temp);
                numbers[i + 1] = temp;
                if (i > 0) {
                    numbers[i] = numbers[i - 1];
                }
            }
        }
        
        result = numbers[0];
        for (int i = 0; i < operators.length; i++) {
            if (operators[i] == '+' || operators[i] == '-') {
                result = applyOperation(result, numbers[i + 1], operators[i]);
                System.out.println("Step: " + result + " after " + operators[i] + " " + numbers[i + 1]);
            } else if (operators[i] == '*' || operators[i] == '/') {
                result = applyOperation(result, numbers[i + 1], operators[i]);
                System.out.println("Step: " + result + " after " + operators[i] + " " + numbers[i + 1]);
            }
        }
        
        return result;
    }
    
    private static double[] parseNumbers(String expression) {
        String[] numberStrings = expression.split("[+\\-*/]");
        double[] numbers = new double[numberStrings.length];
        
        for (int i = 0; i < numberStrings.length; i++) {
            try {
                numbers[i] = Double.parseDouble(numberStrings[i]);
            } catch (NumberFormatException e) {
                numbers[i] = 0;
            }
        }
        
        return numbers;
    }
    
    private static char[] parseOperators(String expression) {
        StringBuilder operators = new StringBuilder();
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (isOperator(c)) {
                operators.append(c);
            }
        }
        
        return operators.toString().toCharArray();
    }
    
    private static double applyOperation(double a, double b, char operator) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': 
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            default: throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
