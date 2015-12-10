import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Java函数式编程(七)：MapReduce
 *
 * @author Ethan
 * @date 2015/12/7 9:09
 */
public class Demo07 {
    public static void main(String[] args) {
        /**
         * 前言:
         * 现在为止我们已经介绍了几个操作集合的新技巧了：查找匹配元素，查找单个元素，集合转化。这些操作有一个共同点，
         * 它们都是对集合中的单个元素进行操作。不需要对元素进行比较，或者对两个元素进行运算。
         * 本节中我们来看一下如何比较元素，以及在遍历集合过程中动态维护一个运算结果。
         */
        /**
         * 1、对集合进行规约
         */
        //需求:遍历一下friends集合，计算出所有名字的总字符数。
        final List<String> friends = Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott");
        System.out.println("Total number of characters in all names: " + friends.stream()
                .mapToInt(name -> name.length())
                .sum());
        //除了使用sum方法，还有很多类似的方法可以使用，比如用max()可以求出最大的长度，用min()是最小长度，
        // sorted()对长度进行排序，average()求平均长度，等等

        //需求：遍历所有的名字，然后打印出名字最长的那个。如果最长的名字有好几个，我们就打印出最开始找到的那个
        final Optional<String> aLongName = friends.stream()
                .reduce((name1, name2) ->
                        name1.length() >= name2.length() ? name1 : name2);
        aLongName.ifPresent(name ->
                System.out.println(String.format("A longest name: %s", name)));

        //reduce的结果最多只可能是集合中的一个元素。如果我们希望能返回一个默认值或者基础值的话，
        // 我们可以使用reduce()方法的一个变种，它可以接收一个额外的参数。比如，如果最短的名字是Steve，
        // 我们可以把它传给reduce()方法
        final String steveOrLonger = friends.stream()
                .reduce("Ste", (name1, name2) ->
                        name1.length() >= name2.length() ? name1 : name2);
        System.out.println(steveOrLonger);

        /**
         * 2、合并元素--join()
         */
        //需求:遍历列表并且挨个打印元素
        /**
         * for-each
         * 烦人的最后额逗号
         */

        for (String name : friends) {
            System.out.print(name + ", ");
        }
        System.out.println();
        //for-each改善最后逗号
        for (int i = 0; i < friends.size() - 1; i++) {
            System.out.print(friends.get(i) + ", ");
        }
        if (friends.size() > 0)
            System.out.println(friends.get(friends.size() - 1));
        /**
         * Java8的做法
         * Java 8里的StringJoiner类帮我们搞定了这些难题，不止如此，String类还增加了一个join方法
         * 方便我们可以用一行代码来替代掉上面那坨东西。
         */
        /**
         * 实现原理：
         * 在底层实现中，String.join()方法调用了StringJoiner类来将第二个参数传进来的值（这是个变长参数）
         * 拼接成一个长的字符串，用第一个参数作为分隔符。这个方法当然不止是能拼接逗号这么简单了。
         * ，我们可以传入一堆路径，然后很容易的拼出一个类路径（classpath)，这可真是多亏了这些新增加的方法和类。
         */
        System.out.println(String.join(", ", friends));
        /**
         * 3、元素拼接成字符串
         * 我们可以用reduce()方法将元素拼接成一个字符串，不过这需要我们费点工夫。JDK有一个十分方便的collect()方法，
         * 它也是reduce()的一个变种，我们可以用它来把元素合并成一个想要的值。
         */
        //collect()方法来执行归约操作，不过它把具体的操作委托给一个collector来执行。我们可以把转化后的元素合并成一个ArrayList。
        // 继续刚才那个例子，我们可以将转化后的元素，拼接成一个用逗号分隔的字符串。--可以加过滤等操作
        /**
         * 实现原理:
         * 我们在转化后的列表上调用了collect()方法，给它传入了一个joining()方法返回的collector，
         * joining是Collectors工具类里的一个静态方法。collector就像是个接收器，它接收collect传进来的对象，
         * 并把它们存储成你想要的格式：ArrayList, String等。我们会在52页的collect方法及Collectors类中进一步探索这个方法。
         */
        System.out.println(friends.stream()
                .map(String::toUpperCase)
                .collect(Collectors.joining(", ")));

        /**
         * 小结：
         * 集合在编程中十分常见，有了lambda表达式后，Java的集合操作变得更加简单容易了。
         * 那些拖沓的集合操作的老代码都可以换成这种优雅简洁的新方式。内部迭代器使得集合遍历，转化都变得更加方便，
         * 远离了可变性的烦恼，查找集合元素也变得异常轻松。使用这些新方法可以少写不少代码。
         * 这使得代码更容易维护，更聚焦于业务逻辑，编程中的那些基本操作也变得更少了。
         */
    }
}
