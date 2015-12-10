import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Java函数式编程(四):在集合中查找元素
 *
 * @author Ethan
 * @date 2015/12/3 0003 11:12
 */
public class Demo04 {
    public static void main(String[] args) {
        //需求:获取其中以N开头的名字(好吧，其实Demo03已经实现了)
        //创建一个不可变的名字的列表
        final List<String> friends = Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott");
        //使用filter过滤
        friends.stream()
                .filter(name -> name.startsWith("N"))
                .forEach(System.out::println);

        //为以N开头的新建列表--老方法
        final List<String> startsWithN = new ArrayList<String>();
        for (String name : friends) {
            if (name.startsWith("N")) {
                startsWithN.add(name);
            }
        }
        //为以N开头的新建列表--新方法
        /**
         * filter的实现原理:
         * filter方法接收一个返回boolean值的lambda表达式。如果为true,
         * 运行的上下文的那个元素就会被添加到结果的集合中；反之就跳过。
         * 最终返回的是一个Stream，它里面只包含八下表达式返回true的元素。
         * 最后用collect方法将这个集合转化为一个列表。
         * */
        final List<String> startN = friends.stream()
                .filter(name -> name.startsWith("N"))
                .collect(Collectors.toList());
        System.out.println(String.format("FOUND %d names", startN.size()));
        /**
         * filter与map的区别:
         * filter方法和map方法一样，也返回了一个迭代器，不过它们也就这点相同而已了。
         * map返回的集合和输入集合大小是一样的，而filter返回的可不好说。
         * 它返回的集合的大小区间，从0一直到输入集的元素个数。
         * 和map不一样的是，filter返回的是输入集的一个子集。
         * */


        /**
         * 即将面临的代码冗余的问题
         */
        //需求:获取以下所有人中名字以N开头的人员
        final List<String> myfriends =
                Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott");
        final List<String> editors =
                Arrays.asList("Brian", "Jackie", "John", "Mike");
        final List<String> comrades =
                Arrays.asList("Kate", "Ken", "Nick", "Paula", "Zach");
        //先用filter来简单的实现一下
        final long countFriendsStartN1 =
                myfriends.stream()
                        .filter(name -> name.startsWith("N")).count();
        final long countEditorsStartN1 =
                editors.stream()
                        .filter(name -> name.startsWith("N")).count();
        final long countComradesStartN1 =
                comrades.stream()
                        .filter(name -> name.startsWith("N")).count();
        //以上暴露的问题是:如果要改Lambda的过滤条件,则需要对所有的Lambda进行修改。---繁琐!
        //如何解决呢?--可以把lambda表达式赋值给变量，然后对它们进行重用，就像使用对象一样。
        /**
         * 具体实现:
         * filter方法，即lambda表达式的接收方，接收的是一个java.util.function.Predicate函数式接口的引用。
         * 在这里，Java编译器又派上用场了，它用指定的lambda表达式生成了Predicate的test方法的一个实现。
         * 现在我们可以更明确的让Java编译器去生成这个方法，而不是在参数定义的地方再生成。
         * 在上面例子中，我们可以明确的把lambda表达式存储到一个Predicate类型的引用里面，
         * 然后再把这个引用传递给filter方法;这样做很容易就避免了代码冗余。
         * 但是，这并不能完完全全的解决代码冗余的问题!!!
         */
        //重构代码符合DRY原则--Don't Repeat Yourself.
        final Predicate<String> startWithN = name -> name.startsWith("N");
        //需要更改条件只需要修改此句代码即可
        final long countFriendsStartN =
                friends.stream()
                        .filter(startWithN)
                        .count();
        final long countEditorsStartN =
                editors.stream()
                        .filter(startWithN)
                        .count();
        final long countComradesStartN =
                comrades.stream()
                        .filter(startWithN)
                        .count();

    }
}
