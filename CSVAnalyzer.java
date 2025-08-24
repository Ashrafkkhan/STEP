import java.util.Scanner;
import java.util.ArrayList;

public class CSVAnalyzer {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("CSV Data Analyzer");
        System.out.println("Enter CSV data (comma-separated values):");
        System.out.println("Enter 'END' on a new line to finish input");
        
        StringBuilder csvInput = new StringBuilder();
        String line;
        
        while (true) {
            line = scanner.nextLine();
            if (line.equals("END")) {
                break;
            }
            csvInput.append(line).append("\n");
        }
        
        if (csvInput.length() == 0) {
            System.out.println("No data entered!");
            return;
        }
        
        String csvData = csvInput.toString().trim();
        
        String[][] parsedData = parseCSVData(csvData);
        String[][] cleanedData = validateAndCleanData(parsedData);
        
        System.out.println("\nFORMATTED DATA TABLE");
        formatTableOutput(cleanedData);
        
        System.out.println("\nDATA ANALYSIS REPORT");
        generateSummaryReport(cleanedData);
        
        scanner.close();
    }
    
    public static String[][] parseCSVData(String csvData) {
        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
        ArrayList<String> currentRow = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < csvData.length(); i++) {
            char c = csvData.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }
            
            if (c == ',' && !inQuotes) {
                currentRow.add(currentField.toString());
                currentField.setLength(0);
                continue;
            }
            
            if ((c == '\n' || c == '\r') && !inQuotes) {
                if (currentField.length() > 0 || !currentRow.isEmpty()) {
                    currentRow.add(currentField.toString());
                    dataList.add(new ArrayList<>(currentRow));
                    currentRow.clear();
                    currentField.setLength(0);
                }
                continue;
            }
            
            currentField.append(c);
        }
        
        if (currentField.length() > 0) {
            currentRow.add(currentField.toString());
        }
        
        if (!currentRow.isEmpty()) {
            dataList.add(currentRow);
        }
        
        String[][] result = new String[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            result[i] = dataList.get(i).toArray(new String[0]);
        }
        
        return result;
    }
    
    public static String[][] validateAndCleanData(String[][] data) {
        if (data.length == 0) return data;
        
        String[][] cleanedData = new String[data.length][data[0].length];
        
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                String field = data[i][j].trim();
                
                if (field.isEmpty()) {
                    cleanedData[i][j] = "[MISSING]";
                } else if (isNumericField(field)) {
                    cleanedData[i][j] = formatNumeric(field);
                } else {
                    cleanedData[i][j] = field;
                }
            }
        }
        
        return cleanedData;
    }
    
    private static boolean isNumericField(String field) {
        if (field.isEmpty()) return false;
        
        boolean hasDecimal = false;
        boolean hasDigit = false;
        
        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (c == '.' && !hasDecimal) {
                hasDecimal = true;
            } else if (c == '-' && i == 0) {
                continue;
            } else {
                return false;
            }
        }
        
        return hasDigit;
    }
    
    private static String formatNumeric(String field) {
        try {
            double value = Double.parseDouble(field);
            if (value == (int) value) {
                return String.valueOf((int) value);
            } else {
                return String.format("%.2f", value);
            }
        } catch (NumberFormatException e) {
            return field;
        }
    }
    
    public static void formatTableOutput(String[][] data) {
        if (data.length == 0) return;
        
        int[] columnWidths = calculateColumnWidths(data);
        
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                String value = data[i][j];
                System.out.print(padRight(value, columnWidths[j]) + "  ");
            }
            System.out.println();
        }
    }
    
    private static int[] calculateColumnWidths(String[][] data) {
        if (data.length == 0) return new int[0];
        
        int[] widths = new int[data[0].length];
        
        for (int col = 0; col < data[0].length; col++) {
            int maxWidth = 0;
            for (String[] row : data) {
                if (col < row.length) {
                    maxWidth = Math.max(maxWidth, row[col].length());
                }
            }
            widths[col] = maxWidth;
        }
        
        return widths;
    }
    
    private static String padRight(String s, int length) {
        if (s.length() >= length) {
            return s;
        }
        return s + " ".repeat(length - s.length());
    }
    
    public static void generateSummaryReport(String[][] data) {
        if (data.length == 0) {
            System.out.println("No data to analyze.");
            return;
        }
        
        int totalRecords = data.length;
        int totalFields = totalRecords * data[0].length;
        int missingFields = 0;
        
        System.out.println("Total records processed: " + totalRecords);
        System.out.println("Total fields: " + totalFields);
        System.out.println("COLUMN ANALYSIS:");
        
        for (int col = 0; col < data[0].length; col++) {
            System.out.println("Column " + (col + 1) + ":");
            
            ArrayList<String> values = new ArrayList<>();
            int colMissing = 0;
            boolean isNumeric = true;
            
            for (int row = 0; row < data.length; row++) {
                if (col < data[row].length) {
                    String value = data[row][col];
                    if (value.equals("[MISSING]")) {
                        colMissing++;
                        missingFields++;
                    } else {
                        values.add(value);
                        if (isNumeric && !isNumericField(value)) {
                            isNumeric = false;
                        }
                    }
                }
            }
            
            System.out.println("  Missing values: " + colMissing);
            System.out.println("  Unique values: " + countUniqueValues(values));
            
            if (isNumeric && !values.isEmpty()) {
                double[] numericValues = new double[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    numericValues[i] = Double.parseDouble(values.get(i));
                }
                
                double min = numericValues[0];
                double max = numericValues[0];
                double sum = 0;
                
                for (double value : numericValues) {
                    min = Math.min(min, value);
                    max = Math.max(max, value);
                    sum += value;
                }
                
                double average = sum / numericValues.length;
                
                System.out.println("  Min: " + min);
                System.out.println("  Max: " + max);
                System.out.println("  Average: " + String.format("%.2f", average));
            }
        }
        
        double completeness = 100.0 * (totalFields - missingFields) / totalFields;
        System.out.println("DATA QUALITY SUMMARY:");
        System.out.println("Missing fields: " + missingFields);
        System.out.println("Data completeness: " + String.format("%.1f", completeness) + "%");
    }
    
    private static int countUniqueValues(ArrayList<String> values) {
        ArrayList<String> unique = new ArrayList<>();
        for (String value : values) {
            if (!unique.contains(value)) {
                unique.add(value);
            }
        }
        return unique.size();
    }
}
