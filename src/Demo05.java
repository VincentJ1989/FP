import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Java函数式编程(五):闭包
 *
 * @author Ethan
 * @date 2015/12/3 0003 11:54
 */
public class Demo05 {
    public static void main(String[] args) {
        /**
         * 前言:
         * 很多开发人员都存在这种误解，认为使用lambda表达式会导致代码冗余，降低代码质量。
         * 恰恰相反，就算代码变得再复杂，我们也不会为了代码的简洁性而在代码质量上做任何妥协，
         */
        //需求:获取所有以N或者B开头的名字
        final List<String> friends =
                Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott");

        final Predicate<String> startWithN = name -> name.startsWith("N");//N开头
        final Predicate<String> startWithB = name -> name.startsWith("B");//B开头

        final long countFriendsStartN = friends.stream().filter(startWithN).count();
        final long countFriendsStartB = friends.stream().filter(startWithB).count();
        System.out.println("符合条件的人数为:" + (countFriendsStartB + countFriendsStartN));

        //以上2个Predicate产生了代码的冗余，下面就来解决。可如何解决呢?

        //方法1---使用词法作用域来避免冗余
        /**
         * 第一个方案，我们可以把字母抽出来作为函数的参数，同时把这个函数传递给filter方法。
         * 这是个不错的方法，不过filter可不是什么函数都接受的。它只接受只有一个参数的函数，
         * 那个参数对应的就是集合中的元素，返回一个boolean值，它希望传进来的是一个Predicate。
         * 我们希望有一个地方能把这个字母先缓存起来，一直到参数传递过来（在这里就是name这个参数）。
         * 下面来新建一个这样的函数。
         */

        final long countFriendsStartN2 =
                friends.stream().filter(checkIfStartWith("N")).count();
        final long countFriendsStartB2 =
                friends.stream().filter(checkIfStartWith("B")).count();

        //方法2--重构，缩小作用域
        /**
         * 在前面的例子中我们用了一个static方法，不过我们不希望用static方法来缓存变量，
         * 这样把我们的代码搞乱了。最好能把这个函数的作用域缩小到使用它的地方。
         * 我们可以用一个Function接口来实现这个。
         */
        final Function<String, Predicate<String>> startsWithLetter = (String letter) -> {
            Predicate<String> checkStarts = (String name) -> name.startsWith(letter);
            return checkStarts;
        };
        //进一步简化
        final Function<String, Predicate<String>> startsWithLetter2 =
                (String letter) -> (name) -> name.startsWith(letter);


        final long countFriendsStartN3 = friends.stream()
                .filter(startsWithLetter.apply("N")).count();
        final long countFriendsStartB3 = friends.stream()
                .filter(startsWithLetter.apply("B")).count();
    }

    /**
     * 方案1构造的函数</br>
     * 接收一个String参数，并且返回一个Predicate对象，它正好可以传递给filter方法，以便后面进行使用.
     * heckIfStartsWith方法返回的Predicate对象和其它lambda表达式有些不同。
     * 在 return name -> name.startsWith(letter)语句中，我们很清楚name是什么，
     * 它是传入到lambda表达式中的参数。不过变量letter到底是什么？
     * 它是在这个匿名函数的域外边的，Java找到了定义这个lambda表达式的域，并发现了这个变量letter。
     * 这个就叫做词法作用域。词法作用域是个很有用的东西，它使得我们可以在一个作用域中缓存一个变量，
     * 以便后面在另一个上下文中进行使用。由于这个lambda表达式使用了它的定义域中的变量，这种情况也被称作闭包。
     */
    public static Predicate<String> checkIfStartWith(final String letter) {
        return name -> name.startsWith(letter);
    }

    /**
     * Predicate和Function的区别:
     *
     * Predicate接受一个类型为T的参数，返回一个boolean值来代表它对应的判断条件的真假。
     * 当我们需要做条件判断 的时候，我们可以使用Predicateg来完成。
     * 像filter这类对元素进行筛选的方法都接收Predicate作为参数。而Funciton代表的是一个函数，
     * 它的入参是类型为T的变量，返回的是R类型的一个结果。它和只能返回boolean的Predicate相比要更加通用。
     * 只要是将输入转化成一个输出的，我们都可以使用Function，因此map使用Function作为参数也是情理之中的事情了。
     */

}
