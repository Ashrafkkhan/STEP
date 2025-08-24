import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class FileOrganizer {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("File Organizer");
        System.out.println("Enter file names with extensions (one per line):");
        System.out.println("Enter 'DONE' to finish");
        
        ArrayList<String> fileList = new ArrayList<>();
        String fileName;
        
        while (true) {
            fileName = scanner.nextLine().trim();
            if (fileName.equalsIgnoreCase("DONE")) {
                break;
            }
            if (!fileName.isEmpty()) {
                fileList.add(fileName);
            }
        }
        
        if (fileList.isEmpty()) {
            System.out.println("No files entered!");
            return;
        }
        
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();
        for (String file : fileList) {
            FileInfo info = extractFileInfo(file);
            fileInfoList.add(info);
        }
        
        categorizeFiles(fileInfoList);
        generateNewNames(fileInfoList);
        analyzeContent(fileInfoList);
        
        System.out.println("\nFILE ORGANIZATION REPORT");
        displayReport(fileInfoList);
        
        System.out.println("\nBATCH RENAME COMMANDS");
        generateRenameCommands(fileInfoList);
        
        scanner.close();
    }
    
    static class FileInfo {
        String originalName;
        String fileName;
        String extension;
        String category;
        String newName;
        String subcategory;
        int priority;
        boolean isValid;
        
        FileInfo(String originalName, String fileName, String extension, boolean isValid) {
            this.originalName = originalName;
            this.fileName = fileName;
            this.extension = extension;
            this.isValid = isValid;
            this.category = "Unknown";
            this.subcategory = "General";
            this.priority = 3;
        }
    }
    
    public static FileInfo extractFileInfo(String fullFileName) {
        if (fullFileName == null || fullFileName.isEmpty()) {
            return new FileInfo("", "", "", false);
        }
        
        int lastDotIndex = fullFileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == 0 || lastDotIndex == fullFileName.length() - 1) {
            return new FileInfo(fullFileName, fullFileName, "", false);
        }
        
        String fileName = fullFileName.substring(0, lastDotIndex);
        String extension = fullFileName.substring(lastDotIndex + 1);
        
        boolean isValid = validateFileName(fileName) && validateExtension(extension);
        
        return new FileInfo(fullFileName, fileName, extension, isValid);
    }
    
    private static boolean validateFileName(String fileName) {
        if (fileName.isEmpty()) return false;
        
        String invalidChars = "<>:\"/\\|?*";
        for (char c : fileName.toCharArray()) {
            if (invalidChars.indexOf(c) != -1) {
                return false;
            }
            if (c < 32) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean validateExtension(String extension) {
        if (extension.isEmpty()) return false;
        
        for (char c : extension.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
    public static void categorizeFiles(ArrayList<FileInfo> fileInfoList) {
        for (FileInfo info : fileInfoList) {
            if (!info.isValid) {
                info.category = "Invalid";
                continue;
            }
            
            String ext = info.extension.toLowerCase();
            if (ext.equals("txt") || ext.equals("doc") || ext.equals("docx") || ext.equals("pdf")) {
                info.category = "Documents";
            } else if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif")) {
                info.category = "Images";
            } else if (ext.equals("mp3") || ext.equals("wav") || ext.equals("flac")) {
                info.category = "Audio";
            } else if (ext.equals("mp4") || ext.equals("avi") || ext.equals("mov")) {
                info.category = "Video";
            } else if (ext.equals("java") || ext.equals("py") || ext.equals("cpp") || ext.equals("js")) {
                info.category = "Code";
            } else if (ext.equals("zip") || ext.equals("rar") || ext.equals("7z")) {
                info.category = "Archives";
            } else {
                info.category = "Other";
            }
        }
    }
    
    public static void generateNewNames(ArrayList<FileInfo> fileInfoList) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        
        ArrayList<String> usedNames = new ArrayList<>();
        
        for (FileInfo info : fileInfoList) {
            if (!info.isValid) {
                info.newName = info.originalName + "_INVALID";
                continue;
            }
            
            String baseName = info.category + "_" + dateStr + "_" + info.fileName;
            String newName = baseName + "." + info.extension;
            
            int counter = 1;
            while (usedNames.contains(newName)) {
                newName = baseName + "_" + counter + "." + info.extension;
                counter++;
            }
            
            usedNames.add(newName);
            info.newName = newName;
        }
    }
    
    public static void analyzeContent(ArrayList<FileInfo> fileInfoList) {
        for (FileInfo info : fileInfoList) {
            if (!info.isValid) continue;
            
            String name = info.fileName.toLowerCase();
            
            if (info.category.equals("Documents")) {
                if (name.contains("resume") || name.contains("cv")) {
                    info.subcategory = "Resume";
                    info.priority = 1;
                } else if (name.contains("report")) {
                    info.subcategory = "Report";
                    info.priority = 2;
                } else if (name.contains("letter")) {
                    info.subcategory = "Letter";
                    info.priority = 2;
                }
            } else if (info.category.equals("Code")) {
                if (name.contains("project") || name.contains("main")) {
                    info.priority = 1;
                }
            } else if (info.category.equals("Images")) {
                if (name.contains("photo") || name.contains("img")) {
                    info.priority = 1;
                }
            }
            
            if (name.contains("important") || name.contains("urgent")) {
                info.priority = Math.max(1, info.priority - 1);
            }
        }
    }
    
    public static void displayReport(ArrayList<FileInfo> fileInfoList) {
        int totalFiles = fileInfoList.size();
        int validFiles = 0;
        int invalidFiles = 0;
        
        System.out.println("CATEGORY WISE FILE COUNTS:");
        System.out.println("Category     Count");
        System.out.println("------------------");
        
        java.util.HashMap<String, Integer> categoryCounts = new java.util.HashMap<>();
        for (FileInfo info : fileInfoList) {
            categoryCounts.put(info.category, categoryCounts.getOrDefault(info.category, 0) + 1);
            if (info.isValid) validFiles++; else invalidFiles++;
        }
        
        for (String category : categoryCounts.keySet()) {
            System.out.printf("%-12s %d%n", category, categoryCounts.get(category));
        }
        
        System.out.println("\nFILE DETAILS:");
        System.out.println("Original Name         Category     New Name");
        System.out.println("--------------------------------------------");
        
        for (FileInfo info : fileInfoList) {
            String origDisplay = info.originalName.length() > 20 ? info.originalName.substring(0, 17) + "..." : info.originalName;
            String newDisplay = info.newName.length() > 20 ? info.newName.substring(0, 17) + "..." : info.newName;
            System.out.printf("%-20s %-12s %s%n", origDisplay, info.category, newDisplay);
        }
        
        System.out.println("\nFILES NEEDING ATTENTION:");
        boolean hasAttentionNeeded = false;
        for (FileInfo info : fileInfoList) {
            if (!info.isValid || info.category.equals("Unknown") || info.category.equals("Other")) {
                System.out.println("- " + info.originalName + " (" + info.category + ")");
                hasAttentionNeeded = true;
            }
        }
        if (!hasAttentionNeeded) {
            System.out.println("No files need special attention");
        }
        
        System.out.println("\nORGANIZATION STATISTICS:");
        System.out.println("Total files: " + totalFiles);
        System.out.println("Valid files: " + validFiles);
        System.out.println("Invalid files: " + invalidFiles);
        System.out.printf("Organization score: %.1f%%%n", (validFiles * 100.0 / totalFiles));
    }
    
    public static void generateRenameCommands(ArrayList<FileInfo> fileInfoList) {
        System.out.println("RENAME OPERATIONS:");
        System.out.println("Original -> New");
        System.out.println("----------------");
        
        for (FileInfo info : fileInfoList) {
            if (info.isValid) {
                System.out.println(info.originalName + " -> " + info.newName);
            }
        }
        
        int organizedFiles = 0;
        for (FileInfo info : fileInfoList) {
            if (info.isValid && !info.category.equals("Unknown") && !info.category.equals("Other")) {
                organizedFiles++;
            }
        }
        
        System.out.println("\nORGANIZATION IMPROVEMENT:");
        System.out.println("Files properly organized: " + organizedFiles + "/" + fileInfoList.size());
        System.out.printf("Organization improvement: %.1f%%%n", (organizedFiles * 100.0 / fileInfoList.size()));
        
        System.out.println("\nRECOMMENDATIONS:");
        if (organizedFiles == fileInfoList.size()) {
            System.out.println("All files are properly organized!");
        } else {
            System.out.println("Review files marked as Invalid/Unknown/Other");
            System.out.println("Consider adding support for new file types");
        }
    }
}
