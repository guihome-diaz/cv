package eu.daxiongmao.prv.cv.business;

import java.util.ArrayList;
import java.util.List;

public class TaskParser {

    public static List<TaskItem> parse(String rawTasks) {
        List<TaskItem> result = new ArrayList<>();

        TaskItem currentMain = null;
        Boolean previousHasBullet = null;
        for (String line : rawTasks.split("\n")) {
            // Prepare data
            String trimmed = line.trim();
            if (trimmed.isEmpty()) { continue; }
            String clean = trimmed.replaceFirst("^[▪\\-]\\s*", "");

            // Analysis
            if (trimmed.startsWith("-")) {
                // Bullet point => task belong to a category
                if (currentMain == null) {
                    currentMain = new TaskItem("", new ArrayList<>());
                    result.add(currentMain);
                }
                currentMain.subItems.add(clean);
            } else if (trimmed.startsWith("▪")) {
                // New category (ex: administration, accounting)
                currentMain = new TaskItem(clean, new ArrayList<>());
                result.add(currentMain);
            } else {
                // Simple text
                currentMain = new TaskItem(clean, new ArrayList<>());
                result.add(currentMain);
            }
        }
        return result;
    }

    public static String toString(List<TaskItem> tasks) {
        if (tasks == null || tasks.isEmpty()) { return ""; }
        String result = "";
        for (TaskItem task : tasks) {
            result += task.toString();
        }
        return result;
    }

    public record TaskItem(String text, List<String> subItems) {

        @Override
        public String toString() {
            if (subItems == null || subItems.isEmpty()) {
                // Simple text only
                return text;
            }

            // Category / title with bullets points
            String output = "<ul>\n";
            for (String subItem : subItems) {
                output += "<li>" + subItem + "</li>\n";
            }
            output += "</ul>";
            return output;
        }
    }

}
