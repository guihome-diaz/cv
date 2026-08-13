package eu.daxiongmao.prv.cv.business;

import java.util.ArrayList;
import java.util.List;

public class TaskParser {

    public static List<TaskItem> parse(String rawTasks) {
        List<TaskItem> result = new ArrayList<>();
        TaskItem currentMain = null;

        for (String line : rawTasks.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) { continue; }

            String clean = trimmed.replaceFirst("^[▪\\-]\\s*", "");
            if (trimmed.startsWith("▪")) {
                // Category (ex: administration, accounting)
                currentMain = new TaskItem(clean, new ArrayList<>());
                result.add(currentMain);
            } else {
                // Task
                if (currentMain != null) {
                    // task belong to a category
                    currentMain.subItems.add(clean);
                } else {
                    // standalone task
                    result.add(new TaskItem(clean, new ArrayList<>()));
                }
            }
        }
        return result;
    }

    public record TaskItem(String text, List<String> subItems) {}

}
