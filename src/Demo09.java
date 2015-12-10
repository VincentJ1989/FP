import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Java函数式编程(九)：Comparator
 *
 * @author Ethan
 * @date 2015/12/7 9:39
 */
public class Demo09 {
    public static void main(String[] args) {
        /**
         * 1、实现Comparator接口
         * Comparator接口的身影在JDK库中随处可见，从查找到排序，再到反转操作，等等。
         * Java 8里它变成了一个函数式接口，这样的好处就是我们可以使用流式语法来实现比较器了。
         */
        final List<Person> people = Arrays.asList(
                new Person("John", 20),
                new Person("Sara", 21),
                new Person("Jane", 21),
                new Person("Greg", 35));
        //需求:使用Comparator进行排序
        //如果实现Comparator类只能实现一种排序(名字或者年龄等)，而我们希望能针对各个属性进行排序

        //按年龄从小到大对他们进行排序
        /**
         * 实现原理:
         *
         * 通过stream()方法将列表转化成一个Stream对象。然后调用它的sorted()方法。这个方法接受一个Comparator参数。
         * 由于Comparator是一个函数式接口，我们可以传入一个lambda表达式。最后我们调用collect方法，
         * 让它把结果存储到一个列表里。collect方法是一个归约器，它能把迭代过程中的对象输出成某种特定的格式或者类型。
         * toList()方法是Collectors类的一个静态方法。
         *
         * Comparator的抽象方法compareTo()接收两个参数，也就是要比较的对象，并返回一个int类型的结果。为了兼容这个，
         * 我们的lambda表达式也接收两个参数，两个Person对象，它们的类型是由编译器自动推导的。
         * 我们返回一个int类型，表明比较的对象是否相等。
         * 因为要按年龄进行排序，所以我们会比较两个对象的年龄，然后返回比较的结果。如果他们一样大，则返回0。
         * 否则如果第一个人更年轻的话就返回一个负数，更年长的话就返回正数。
         * sorted()方法会遍历目标集合的每个元素并调用指定的Comparator，来确定出元素的排序顺序。
         * sorted()方法的执行方式有点类似前面说到的reduce()方法。reduce()方法把列表逐步归约出一个结果。
         * 而sorted()方法则通过比较的结果来进行排序。
         */
        List<Person> ascendingAge = people.stream().
                sorted((p1, p2) -> p1.ageDifference(p2))
                .collect(Collectors.toList());
        printPeople("Sorted in ascending order by age: ", ascendingAge);

        List<Person> ascendingAge2 = people.stream().
                sorted(Person::ageDifference)//简化--第一个作为调用目标，第二个作为它的参数
                .collect(Collectors.toList());
        /**
         * 2、重用Comparator
         */
        //按年龄从大到小排列---这里不能够简化，就是因为参数顺序意义不同
        printPeople("Sorted in descending order by age: ",
                people.stream()
                        .sorted((person1, person2) -> person2.ageDifference(person1))
                        .collect(Collectors.toList()));


        //以上2个Lambda都是排序，这样显得代码冗余而且违背DRY(Don't Repeat Yourself)原则
        //解决方法:如果我们只是想要调整下排序顺序的话，JDK提供了一个reverse方法，它有一个特殊的方法修饰符，default。
        // 我们会在77页中的default方法来讨论它，这里我们先用下这个reversed()方法来去除冗余性。
        Comparator<Person> compareAscending =
                (person1, person2) -> person1.ageDifference(person2);
        /**
         * 底层原理：
         * 在reversed()方法底层，它创建了一个比较器，来交换了比较的参数的顺序。
         * 这说明reversed也是一个高阶方法——它创建并返回了一个无副作用的函数
         */
        Comparator<Person> compareDescending = compareAscending.reversed();

        printPeople("Sorted in ascending order by age: ",
                people.stream().sorted(compareAscending).collect(Collectors.toList())
        );
        printPeople("Sorted in descending order by age: ",
                people.stream().sorted(compareDescending).collect(Collectors.toList())
        );

        //按名字进行字典序排列
        printPeople("Sorted in ascending order by name: ",
                people.stream()
                        .sorted((person1, person2) -> person1.getName().compareTo(person2.getName()))
                        .collect(Collectors.toList()));

        //选出列表中最年轻的人
        /**
         * 思路：
         *
         * 可以先按年龄从小到大排序然后选中结果中的第一个。不过其实用不着那样，Stream有一个min()方法可以实现这个。
         * 这个方法同样也接受一个Comparator，不过返回的是集合中最小的对象
         */
        people.stream()
                .min(Person::ageDifference)
                .ifPresent(youngest -> System.out.println("Youngest: " + youngest));

        //年纪最大的
        people.stream()
                .max(Person::ageDifference)
                .ifPresent(eldest -> System.out.println("Eldest: " + eldest));

        /**
         * 3、多重比较和流式比较
         */
        //按名字排序的话，我们上面是这么写的
        people.stream().sorted((person1, person2) ->
                person1.getName().compareTo(person2.getName()));
        //函数式编程
        /**
         * 原理：
         * 导入了Comparator类的静态方法comparing()。comparing()方法使用传入的lambda表达式来生成一个Comparator对象。
         * 也就是说，它也是一个高阶函数，接受一个函数入参并返回另一个函数。
         */
        final Function<Person, String> byName = person -> person.getName();
        people.stream().sorted(Comparator.comparing(byName));
        //按名字和年龄多重比较
        /**
         * 代码说明：
         *
         * 我们先是创建了两个lambda表达式，一个返回指定人的年龄，一个返回的是他的名字。
         * 在调用sorted()方法的时候我们把这两个表达式组合 到了一起，这样就能进行多个属性的比较了。
         * comparing()方法创建并返回了一个按年龄比较的Comparator ,我们再调用这个返回的Comparator上面的
         * thenComparing()方法来创建一个组合的比较器，它会对年龄和名字两项进行比较.
         */
        final Function<Person, Integer> byAge = person -> person.getAge();
        final Function<Person, String> byTheirName = person -> person.getName();
        printPeople("Sorted in ascending order by age and name: ",
                people.stream()
                        .sorted(Comparator.comparing(byAge).thenComparing(byTheirName))
                        .collect(Collectors.toList()));
    }

    /**
     * 打印结果
     */
    public static void printPeople(
            final String message, final List<Person> people) {
        System.out.println(message);
        people.forEach(System.out::println);
    }
}

class Person {
    private final String name;
    private final int age;

    public Person(final String theName, final int theAge) {
        name = theName;
        age = theAge;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int ageDifference(final Person other) {
        return age - other.age;
    }

    public String toString() {
        return String.format("%s - %d", name, age);
    }
}
