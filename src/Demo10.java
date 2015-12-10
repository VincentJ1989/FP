import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * Java函数式编程(十)：收集器
 *
 * @author Ethan
 * @date 2015/12/7 10:34
 */
public class Demo10 {
    public static void main(String[] args) {
        /**
         * 前言：
         *
         * 前面我们已经用过几次collect()方法来将Stream返回的元素拼成ArrayList了。
         * 这是一个reduce操作，它对于将一个集合转化成另一种类型（通常是一个可变的集合）非常有用。
         * collect()函数，如果和Collectors工具类里的一些方法结合起来使用的话，能提供极大的便利性，本节我们将会介绍到。
         */
        final List<Student> people = Arrays.asList(
                new Student("John", 20),
                new Student("Sara", 21),
                new Student("Jane", 21),
                new Student("Greg", 35));
        //从原始列表中找出所有大于20岁的人
        //方法1：可变性和forEach()方法实现
        List<Student> olderThan20 = new ArrayList<>();
        people.stream()
                .filter(stu -> stu.getAge() > 20)
                .forEach(stu -> olderThan20.add(stu));
        System.out.println("Student older than 20: " + olderThan20);
        /**
         * 程序输出的结果是对的，不过还有点小问题。首先，把元素添加到集合中，这种属于低级操作——它是命令式的，而非声明式的。
         * 如果我们想把这个迭代改造成并发的，还得去考虑线程安全的问题——可变性使得它难以并行化。
         * 幸运的是，使用collect()方法可以很容易解决掉这个问题。来看下如何实现的。
         *
         * collect()方法接受一个Stream并将它们收集到一个结果容器中。要完成这个工作，它需要知道三个东西：+
         * 1、如何创建结果容器（比如说，使用ArrayList::new方法）
         * 2、如何把单个元素添加到容器中(比如使用ArrayList::add方法）
         * 3、如何把一个结果集合并到另一个中（比如使用ArrayList::addAll方法）
         * 对于串行操作而言，最后一条不是必需的；代码设计的目标是能同时支持串行和并行的。
         * 我们把这些操作提供给collect方法，让它来把过滤后的流给收集起来。
         */

        List<Student> _olderThan20 = people.stream()
                .filter(stu -> stu.getAge() > 20)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        System.out.println("Student older than 20: " + _olderThan20);
        /**
         * 上面代码说明：
         * 首先，我们编程的方式更聚焦了，表述性也更强，清晰的传达了你要把结果收集到一个ArrayList里去的目的。
         * collect()的第一个参数是一工厂或者生产者，后面的参数是一个用来收集元素的操作。
         * 第二，由于我们没有在代码中个执行显式的修改操作，可以很容易并行地执行这个迭代。我们让底层库来完成修改操作，
         * 它自己会处理好协作及线程安全的问题，尽管ArrayList本身不是线程安全的——干的漂亮。如果条件允许的话，
         * collect()方法可以并行地将元素添加到不同的子列表中，然后再用一个线程安全的方式将它们合并到一个大列表里
         * （最后一个参数就是用来进行合并操作的）。
         */

        /**
         * 我们已经看到，相对于手动把元素添加到列表而言，使用collect()方法的好处真是太多了。
         * 下面我们来看下这个方法的一个重载的版本——它更简单也更方便——它是使用一个Collector作为参数。
         * 这个Collector是一个包含了生产者，添加器，以及合并器在内的接口——在前面的版本中这些操作是作为独立的参数
         * 分别传入方法中的——使用Collector则更简单并且可以复用。Collectors工具类提供了一个toList方法，
         * 可以生成一个Collector的实现，用来把元素添加到ArrayList中。我们来修改下前面那段代码，使用一下这个collect()方法。
         */
        List<Student> __olderThan20 = people.stream()
                .filter(stu -> stu.getAge() > 20)
                .collect(Collectors.toList());
        System.out.println("Student older than 20: " + __olderThan20);
        /**
         * 上面代码说明:
         * 使用了Collectors工具类的简洁版的collect()方法，可不止这一种用法。
         * Collectors工具类中还有好几种不同的方法来可以进行不同的收集和添加的操作。
         * 比如说，除了toList()方法，还有toSet()方法，可以添加到一个Set中，
         * toMap()方法可以用来收集到一个key-value的集合中，还有joining()方法，可以拼接成一个字符串。
         * 我们还可以将mapping(),collectingAndThen()，minBy(), maxBy()和groupingBy()等方法组合起来进行使用。
         */
        //用下groupingBy()方法来将人群按年龄进行分组
        Map<Integer, List<Student>> studentByAge = people.stream()
                .collect(Collectors.groupingBy(Student::getAge));
        System.out.println("Grouped by age: " + studentByAge);
        /**
         * 上面代码说明：
         * groupingBy()接受一个lambda表达式或者方法引用——这种叫分类函数——它返回需要分组的对象的某个属性的值。
         * 根据我们这个函数返回的值，来把调用上下文中的元素放进某个分组中
         */


        /**
         * groupingBy()方法的一个变种可以按多个条件进行分组。简单的groupingBy()方法使用了分类器进行元素收集。
         * 而通用的groupingBy()收集器，则可以为每一个分组指定一个收集器。也就是说，元素在收集的过程中会途经不同的分类器和集合
         */
        //只获取人的名字，按他们的年龄进行排序
        Map<Integer, List<String>> nameOfStudentByAge = people.stream()
                .collect(
                        Collectors.groupingBy(Student::getAge, Collectors.mapping(Student::getName, Collectors.toList())));
        System.out.println("Student grouped by age: " + nameOfStudentByAge);

        //按名字的首字母进行分组，然后选出每个分组中年纪最大的那位

        Comparator<Student> byAge = Comparator.comparing(Student::getAge);
        Map<Character, Optional<Student>> oldestStudentOfEachLetter = people.stream()
                .collect(Collectors.groupingBy(person -> person.getName().charAt(0),
                        Collectors.reducing(BinaryOperator.maxBy(byAge))));
        System.out.println("Oldest person of each letter:" + oldestStudentOfEachLetter);
        /**
         * 上面代码的说明:
         * 我们先是按名字的首字母进行了排序。为了实现这个，我们把一个lambda表达式作为groupingBy()的第一个参数传了进去。
         * 这个lambda表达式是用来返回名字的首字母的，以便进行分组。第二个参数不再是mapping()了，而是执行了一个reduce操作。
         * 在每个分组内，它使用maxBy()方法，从所有元素中递推出最年长的那位。由于组合了许多操作，这个语法看起来有点臃肿，
         * 不过整个读起来是这样的：按名字首字母进行分组，然后递推出分组中最年长的那位。
         */
    }
}

class Student {
    private final String name;
    private final int age;

    public Student(final String theName, final int theAge) {
        name = theName;
        age = theAge;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int ageDifference(final Student other) {
        return age - other.age;
    }

    public String toString() {
        return String.format("%s - %d", name, age);
    }
}