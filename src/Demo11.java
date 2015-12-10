import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Java函数式编程(十一)：遍历目录
 *
 * @author Ethan
 * @date 2015/12/7 11:01
 */
public class Demo11 {
    public static void main(String[] args) throws IOException {
        /**
         * 1、列出目录中的文件
         * 用File类的list()方法可以很容易的列出目录中的所有文件的文件名。如果想要获取文件而不止是文件名的话，
         * 可以使用它的listFiles()方法。这很简单，难的是怎么去处理这个返回的列表。
         * 我们不再使用传统的冗长的外部迭代器，而是使用优雅的函数式来实遍历这个列表。
         * 这里我们还得用到JDK的新的CloseableStream接口以及一些相关的高阶函数。
         */
        //如果想列出别的目录的话，可以把”.”替换成想要访问的目录的完整路径。
        Files.list(Paths.get("."))
                .forEach(System.out::println);
        /**
         * 上面代码说明:
         * 先是使用了Paths的get()方法，通过一个字符串创建了一个Path实例。
         * 然后通过Files工具类的list()方法获取到了一个ClosableStream对象，我们可以用它来遍历目录下的所有文件。
         * 然后我们使用内部迭代器forEach()来打印出文件名.
         */
        //只想获取当前目录的子目录，而不要文件的话，可以使用filter()方法
        Files.list(Paths.get("."))
                .filter(Files::isDirectory)
                .forEach(System.out::println);
        /**
         * 2、列出目录下指定的文件
         */
        /**
         * 以前的做法：
         * Java很早前就提供了一个list()方法的变种，用来筛选文件名。这个版本的list()方法接受一个FilenameFilter类型的参数。
         * 这个接口只有一个accept()方法，它接受两个参数：File dir(代表目录)，以及String name(代表文件名)。
         * accept()方法返回true的话这个文件名就会出现在返回的列表中，返回false则不在。我们来实现一下这个方法。
         * 习惯性的做法是将一个实现了FilenameFilter接口的匿名内部类的实例传给list()方法。比如说，
         * 我们来看下如何用这种方式来返回src目录下的.txt文件
         */

        final String[] files =
                new File("src").list(new java.io.FilenameFilter() {
                    public boolean accept(final File dir, final String name) {
                        return name.endsWith(".txt");
                    }
                });
        final String[] files2 =
                new File("src").list((dir, name) -> name.endsWith(".txt"));//上面的简化
        Arrays.asList(files).stream().forEach(System.out::println);
        /**
         * 这着实得费些工夫写几行代码。这样的代码太聒噪了：创建对象，调用函数，定义匿名内部类，在类里面嵌入方法等等。
         * 我们不用再忍受这样的痛苦了，只需传一个接受两个参数并返回bollean的lambda表达式进去就好了。Java编译器会搞定剩下的事。
         * 前面那个例子可以简单的用一个lambda表达式替换掉匿名内部就好了，但是还有进一步优化的空间。
         * 新的DirectoryStream工具可以帮助我们更高效的遍历大的目录结构。我们来试下这种方法。
         * 这是newDirectoryStream()方法的一个变种，它接受一个额外的过滤器。
         */
        Files.newDirectoryStream(
                Paths.get("src"), path -> path.toString().endsWith(".txt"))
                .forEach(System.out::println);

        /**
         * 我们基于文件名来筛选文件，同样也可以很容易通过文件属性，比如文件是不是可执行文件，是否可读，可写等来进行筛选。
         * 这么做的话得需要一个listFiles()方法，它接受一个FileFilter类型的参数。
         * 我们仍然使用lambda表达式来实现而不是去创建匿名内部类。现在来看一个列出当前目录下所有隐藏文件的例子。
         */
        final File[] _files = new File(".").listFiles(file -> file.isHidden());
        /**
         * 如果我们操作的是一个很大的目录，可以使用DirectoryStream而不是直接调用File上面的方法。
         * 我们传给listFiles()方法的lambda表达式的签名和FileFilter接口的accept()方法的签名是一样的。
         * 这个lambda表达式接受的是一个File实例的参数，在这个例子中参数名是file。
         * 如果文件是隐藏属性的话，刚返回true，否则返回false.
         * 这里其实还可以再精简下代码，我们不传lambda表达式了，传一个方法引用会让代码看起来会更简洁一些
         */
        new File(".").listFiles(File::isHidden);

        /**
         * 小结：
         *
         * 我们先用lambda表达式实现，随后又使用方法引用将它重构得更加简洁。如果我们再写新的代码的话，
         * 当然应该采用这种简洁的方式。如果可以早点发现这种简洁的实现，我们当然要优先使用它。
         * 有一句话叫做”先让它能工作，然后再去优化（make it work, then make it better）"，
         * 先让代码能跑起来，等我们理清楚了，才去考虑简洁性和性能等进行优化。
         */
    }
}
