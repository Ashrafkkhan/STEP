import java.util.Scanner;

public class TextCompression {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter text to compress: ");
        String originalText = scanner.nextLine();
        
        if (originalText.isEmpty()) {
            System.out.println("No text entered!");
            return;
        }
        
        char[] uniqueChars;
        int[] frequencies;
        String[][] mappingTable;
        String compressedText;
        String decompressedText;
        
        Object[] freqResult = countCharacterFrequency(originalText);
        uniqueChars = (char[]) freqResult[0];
        frequencies = (int[]) freqResult[1];
        
        mappingTable = createCompressionCodes(uniqueChars, frequencies);
        
        compressedText = compressText(originalText, mappingTable);
        
        decompressedText = decompressText(compressedText, mappingTable);
        
        displayCompressionAnalysis(originalText, compressedText, decompressedText, 
                                 uniqueChars, frequencies, mappingTable);
        
        scanner.close();
    }
    
    public static Object[] countCharacterFrequency(String text) {
        char[] tempChars = new char[text.length()];
        int[] tempFreqs = new int[text.length()];
        int uniqueCount = 0;
        
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            boolean found = false;
            
            for (int j = 0; j < uniqueCount; j++) {
                if (tempChars[j] == currentChar) {
                    tempFreqs[j]++;
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                tempChars[uniqueCount] = currentChar;
                tempFreqs[uniqueCount] = 1;
                uniqueCount++;
            }
        }
        
        char[] resultChars = new char[uniqueCount];
        int[] resultFreqs = new int[uniqueCount];
        
        for (int i = 0; i < uniqueCount; i++) {
            resultChars[i] = tempChars[i];
            resultFreqs[i] = tempFreqs[i];
        }
        
        sortByFrequency(resultChars, resultFreqs);
        
        return new Object[]{resultChars, resultFreqs};
    }
    
    private static void sortByFrequency(char[] chars, int[] freqs) {
        for (int i = 0; i < chars.length - 1; i++) {
            for (int j = i + 1; j < chars.length; j++) {
                if (freqs[j] > freqs[i]) {
                    int tempFreq = freqs[i];
                    freqs[i] = freqs[j];
                    freqs[j] = tempFreq;
                    
                    char tempChar = chars[i];
                    chars[i] = chars[j];
                    chars[j] = tempChar;
                }
            }
        }
    }
    
    public static String[][] createCompressionCodes(char[] uniqueChars, int[] frequencies) {
        String[][] mappingTable = new String[uniqueChars.length][2];
        
        String[] codes = {"@", "#", "$", "%", "&", "*", "+", "-", "=", 
                         "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        
        for (int i = 0; i < uniqueChars.length; i++) {
            mappingTable[i][0] = String.valueOf(uniqueChars[i]);
            if (i < codes.length) {
                mappingTable[i][1] = codes[i];
            } else {
                mappingTable[i][1] = "~" + (i - codes.length + 1);
            }
        }
        
        return mappingTable;
    }
    
    public static String compressText(String text, String[][] mappingTable) {
        StringBuilder compressedBuilder = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char currentChar = text.charAt(i);
            String code = findCodeForChar(currentChar, mappingTable);
            compressedBuilder.append(code);
        }
        
        return compressedBuilder.toString();
    }
    
    private static String findCodeForChar(char c, String[][] mappingTable) {
        for (String[] mapping : mappingTable) {
            if (mapping[0].charAt(0) == c) {
                return mapping[1];
            }
        }
        return String.valueOf(c);
    }
    
    private static char findCharForCode(String code, String[][] mappingTable) {
        for (String[] mapping : mappingTable) {
            if (mapping[1].equals(code)) {
                return mapping[0].charAt(0);
            }
        }
        return '?';
    }
    
    public static String decompressText(String compressedText, String[][] mappingTable) {
        StringBuilder decompressedBuilder = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();
        
        for (int i = 0; i < compressedText.length(); i++) {
            char currentChar = compressedText.charAt(i);
            
            if (currentChar == '@' || currentChar == '#' || currentChar == '$' || 
                currentChar == '%' || currentChar == '&' || currentChar == '*' || 
                currentChar == '+' || currentChar == '-' || currentChar == '=') {
                decompressedBuilder.append(findCharForCode(String.valueOf(currentChar), mappingTable));
            } 
            else if (currentChar == '~') {
                i++;
                StringBuilder numberBuilder = new StringBuilder();
                while (i < compressedText.length() && Character.isDigit(compressedText.charAt(i))) {
                    numberBuilder.append(compressedText.charAt(i));
                    i++;
                }
                i--;
                String code = "~" + numberBuilder.toString();
                decompressedBuilder.append(findCharForCode(code, mappingTable));
            }
            else if (Character.isDigit(currentChar)) {
                decompressedBuilder.append(findCharForCode(String.valueOf(currentChar), mappingTable));
            }
            else {
                decompressedBuilder.append(currentChar);
            }
        }
        
        return decompressedBuilder.toString();
    }
    
    public static void displayCompressionAnalysis(String originalText, String compressedText, 
                                                String decompressedText, char[] uniqueChars, 
                                                int[] frequencies, String[][] mappingTable) {
        
        System.out.println("\n=== COMPRESSION ANALYSIS ===");
        
        System.out.println("\n1. CHARACTER FREQUENCY TABLE:");
        System.out.println("Char\tFrequency");
        System.out.println("----\t---------");
        for (int i = 0; i < uniqueChars.length; i++) {
            System.out.println("'" + uniqueChars[i] + "'\t" + frequencies[i]);
        }
        
        System.out.println("\n2. COMPRESSION MAPPING:");
        System.out.println("Original\tCode");
        System.out.println("--------\t----");
        for (String[] mapping : mappingTable) {
            String displayChar = mapping[0].equals(" ") ? "[space]" : 
                               mapping[0].equals("\t") ? "[tab]" : mapping[0];
            System.out.println("'" + displayChar + "'\t\t" + mapping[1]);
        }
        
        System.out.println("\n3. TEXT COMPARISON:");
        System.out.println("Original:    " + formatForDisplay(originalText));
        System.out.println("Compressed:  " + formatForDisplay(compressedText));
        System.out.println("Decompressed: " + formatForDisplay(decompressedText));
        
        System.out.println("\n4. COMPRESSION STATISTICS:");
        int originalSize = originalText.length();
        int compressedSize = compressedText.length();
        double compressionRatio = (double) compressedSize / originalSize;
        double efficiencyPercentage = (1 - compressionRatio) * 100;
        
        System.out.println("Original size: " + originalSize + " characters");
        System.out.println("Compressed size: " + compressedSize + " characters");
        System.out.println("Compression ratio: " + String.format("%.2f", compressionRatio) + 
                          " (" + compressedSize + "/" + originalSize + ")");
        System.out.println("Efficiency: " + String.format("%.1f", efficiencyPercentage) + "% reduction");
        
        System.out.println("\n5. VALIDATION:");
        if (originalText.equals(decompressedText)) {
            System.out.println("✓ SUCCESS: Decompressed text matches original!");
        } else {
            System.out.println("✗ ERROR: Decompressed text doesn't match original!");
            System.out.println("Difference at position: " + findFirstDifference(originalText, decompressedText));
        }
    }
    
    private static String formatForDisplay(String text) {
        return text.replace(" ", "[space]")
                  .replace("\t", "[tab]")
                  .replace("\n", "[newline]");
    }
    
    private static int findFirstDifference(String s1, String s2) {
        int minLength = Math.min(s1.length(), s2.length());
        for (int i = 0; i < minLength; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return i;
            }
        }
        return minLength;
    }
}
