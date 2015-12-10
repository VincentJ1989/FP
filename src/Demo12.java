import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java函数式编程(十二)：监控文件修改
 *
 * @author Ethan
 * @date 2015/12/7 11:21
 */
public class Demo12 {
    public static void main(String[] args) throws IOException, InterruptedException {
        /**
         * 1、使用flatMap列出子目录
         */
        /**
         * 前面已经看到如何列出指定目录下的文件了。我们再来看下如何遍历指定目录的直接子目录（深度为1），
         * 先实现一个简单的版本，然后再用更方便的flatMap()方法来实现。
         * 我们先用传统的for循环来遍历一个指定的目录。如果子目录中有文件，就添加到列表里；否则就把子目录添加到列表里。
         * 最后，打印出所有文件的总数。代码在下面——这个是困难模式的。---listTheHardWay()
         */
        listTheHardWay();
        /**
         * 我们先获取当前目录下的文件列表，然后进行遍历。对于每个文件，如果它有子文件，就把它们添加到列表中。
         * 这样做是没问题的，不过它有一些常见的问题：可变性，基本类型偏执，命令式，代码冗长，等等。
         * 一个叫flatMap()的小方法就可以解决掉这些问题。
         * 这个方法在映射后会进行扁平化。它会像map()一样对集合中的元素进行映射。但是和map()方法不同的是，
         * map()方法里面的lambda表达式只是返回一个元素，而这里返回的是一个Stream对象。于是这个方法将多个流压平，
         * 将里面的每个元素映射到一个扁平化的流中。我们可以用flatMap()来执行各种操作，
         * 不过现在手头的这个问题就正好诠释了它的价值。每个子目录都有一个文件的列表或者说流，
         * 而我们希望获取当前目录下的所有子目录中的文件列表。有一些目录可能是空的，或者说没有子元素。
         * 这种情况下，我们将这个空目录或者文件包装成一个流对象。如果我们想忽略某个文件，
         * JDK中的flatMap()方法也可以很好的处理空文件；它会把一个空引用作为一个空集合合并到流里。来看下flatMap()方法的使用。
         */
        betterWay();

        /**
         * 2、监控文件修改
         * 我们已经知道如何查找文件及目录，不过如果我们希望在文件创建，修改或删除的时候，能够接收到提示消息的话，这个也非常简单。
         * 这样的机制对于监视一些特殊文件比如配置文件，系统资源的改动非常有用。下面我们来探索下Java 7中引入的这个工具，
         * WatchService，它可以用来监控文件的修改。下面我们看到的许多特性都来自JDK 7，而这里最大的改进就是内部迭代器带来的便利性。
         * 我们先来写个监控当前目录中的文件修改的例子。JDK中的Path类会对应文件系统中的一个实例，
         * 它是一个观察者服务的工厂。我们可以给这个服注册通知事件，就像这样：
         */

        final Path path = Paths.get("src");

        final WatchService watchService = path.getFileSystem().newWatchService();
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        System.out.println("Report any file changed within next 1 minute...");
        /**
         * 我们注册了一个WatchService来观察当前目录的修改。你可以轮询这个WatchService来获取目录下文件的修改操作，
         * 它会通过一个WatchKey将这些改动返回给我们。一旦我们拿到了这个key，可以遍历它的所有事件来获取文件更新的详细信息。
         * 因为可能会有多个文件被同时修改，poll操作可能会返回多个事件。来看下轮询以及遍历的代码。
         */
        final WatchKey watchKey = watchService.poll(1, TimeUnit.MINUTES);
        if (watchKey != null) {
            watchKey.pollEvents()
                    .stream()
                    .forEach(event ->
                            System.out.println(event.context()));
        }
        /**
         * 这里可以看到，Java 7和Java 8的特性同时出场了。我们把pollEvents()返回的集合转化成了一个Java 8的Stream，
         * 然后使用它的内部迭代器来打印出每个文件的详细的更新信息。
         * 我们来运行下这段代码，然后将src目录下的test.txt文件修改一下，看下这个程序是否能察觉这个更新。
         */
    }

    /**
     * 传统列出子目录
     */
    public static void listTheHardWay() {
        List<File> files = new ArrayList<>();
        File[] filesInCurrentDir = new File(".").listFiles();
        for (File file : filesInCurrentDir) {
            File[] filesInSubDir = file.listFiles();
            if (filesInSubDir != null) {
                files.addAll(Arrays.asList(filesInSubDir));
            } else {
                files.add(file);
            }
        }
        System.out.println("Count: " + files.size());
        /**
         * 当我们修改了这个文件的时候，程序会提示说文件被修改了。我们可以用这个功能来监视不同文件的更新，
         * 然后执行相应的任务。当然我们也可以只注册文件新建或者删除的操作。
         */
    }

    /**
     * Java8的做法
     */
    public static void betterWay() {
        List<File> files = Stream.of(new File(".").listFiles())
                .flatMap(file -> file.listFiles() == null ?
                        Stream.of(file) : Stream.of(file.listFiles()))
                .collect(Collectors.toList());
        System.out.println("Count: " + files.size());
    }
    /**
     * 我们先是获取了当前目录的子文件流，然后调用了它的flatMap()方法。然后将一个lambda表达式传给这个方法，
     * 这个表达式会返回指定文件的子文件的流。flatMap()方法返回的的是当前目录所有子目录下的文件的集合。
     * 我们使用collect()方法以及Collectors里面的toList()(方法把它们收集到一个列表中。
     * 我们传给flatMap()的这个lambda表达式，它返回的是一个文件的子文件。 如果没有的话，则返回这个文件的流。
     * flatMap()方法优雅地将这个流映射到一个流的集合中，然后将这个集合扁平化，最终合并到一个流中。
     * flatMap()方法减少了许多开发的工作——它将两个连续的操作很好的结合到了一起，这通常称为元组 ——用一个优雅的操作就完成了。
     * 我们已经知道如何使用flatMap()方法来将直接子目录中的所有文件列出来。
     *
     * 下面我们来监控一下文件的修改操作。
     */

    /**
     * 总结
     *
     * 有了lambda表达式和方法引用后，像字符串及文件的操作，创建自定义比较器这些常见的任务都变得更简单也更简洁了。
     * 匿名内部类也变得优雅起来了，而可变性就像日出后的晨雾一样，也消失得无影无踪了。使用这种新风格进行编码还有一个福利，
     * 就是你可以使用JDK的新设施来高效地遍历庞大的目录。
     * 现在你已经知道如何创建lambda表达式并把它传递给方法了。
     * 下一章我们会介绍如何使用函数式接口及lambda表达式进行软件的设计。
     */
}
