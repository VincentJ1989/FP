import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java函数式编程(三):列表的转化
 *
 * @author Ethan
 * @date 2015/12/3 0003 10:25
 */
public class Demo03 {
    public static void main(String[] args) {
        //需求：将列表中的名字转化成全大写的
        //创建一个不可变的名字的列表
        final List<String> friends = Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott");
        //生成另一个全部大写的列表
        final List<String> uppercaseNames = new ArrayList<>();
        for (String name : friends) {
            uppercaseNames.add(name.toUpperCase());
        }

        //改进1--虽然用了内部迭代器，但扔新建了一个列表，继续改进...
        final List<String> uppercaseNames2 = new ArrayList<>();
        friends.forEach(name -> uppercaseNames2.add(name.toUpperCase()));
        System.out.println("-------------Stream的map过滤简化-------------");
        //改进2---Stream类的map方法
        /*map方法很适合把一个输入集合转化成一个新的输出集合。*/
        friends.stream()
                .map(name -> name.toUpperCase())
                .filter(name -> name.endsWith("E"))
                .forEach(name -> System.out.println(name));
        System.out.println("----------------方法引用再简化-------------------");
        friends.stream()
                .map(String::toUpperCase)
                .forEach(System.out::println);

        System.out.println("------------输出各个名字的长度------------");
        friends.stream()
                .map(String::length)
                .forEach(System.out::println);
        /*
        * 什么时候应该使用方法引用？
        * 当使用Java编程的时候，通常我们用lambda表达式的时候要比方法引用多得多。但这并不意味着方法引用不重要或者没啥用处。当lambda
        * 表达式非常简短的时候，它是一个很好的替代方案，它直接调用了实例方法或者静态方法。
        * 也就是说，如果lambda表达式只是传递了一下参数给方法调用的话，我们应该改用方法引用。
        * 像这样的lambda表达式，有点像Tom Smykowski在电影上班一条虫中讲的那样，
        * 它的工作就是"从客户那把需求拿给软件工程师"。
        * 因为这个，我把这种重构成方法引用的模式叫做上班一条虫模式。
        * 除了简洁外，使用方法引用，方法名字本身的含义和作用可以更好的体现出来。
        * 使用方法引用背后，编译器起到了很关键的作用。方法引用的目标对象和参数都会从这个生成的方法里传进来的参数那推导出来。
        * 这才使得你可以使用方法引用写出比使用lambda表达式更简洁的代码。
        * 不过，如果参数在传递给方法之前或者调用结果在返回之后要被修改的话，这种便利的写法我们就用不了了。
        * */
    }
}
