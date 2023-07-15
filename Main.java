import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {
    public static void main(String[] args) {
        File directory = new File("C:\\Users\\User\\Downloads");
        long fileSizeThreshold = 1024; // Пороговый размер файла

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        FileSizeFinder fileSizeFinder = new FileSizeFinder(directory, fileSizeThreshold);
        List<File> foundFiles = forkJoinPool.invoke(fileSizeFinder);

        System.out.println("Найдено файлов: " + foundFiles.size());
        for (File file : foundFiles) {
            System.out.println("Файл: " + file.getAbsolutePath());
            System.out.println("Размер: " + file.length() + " байт");

        }
    }

}
class FileSizeFinder extends RecursiveTask<List<File>> {
    private final File directory;
    private final long fileSizeThreshold;

    public FileSizeFinder(File directory, long fileSizeThreshold) {
        this.directory = directory;
        this.fileSizeThreshold = fileSizeThreshold;
    }

    @Override
    protected List<File> compute() {
        List<File> foundFiles = new ArrayList<>();

        if (directory.isDirectory()) {
            List<FileSizeFinder> subTasks = new ArrayList<>();

            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    FileSizeFinder subTask = new FileSizeFinder(file, fileSizeThreshold);
                    subTasks.add(subTask);
                    subTask.fork();
                } else {
                    if (file.length() > fileSizeThreshold) {
                        foundFiles.add(file);
                    }
                }
            }

            for (FileSizeFinder subTask : subTasks) {
                foundFiles.addAll(subTask.join());
            }
        }

        return foundFiles;
    }
}
