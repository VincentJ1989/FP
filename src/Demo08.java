/**
 * Java函数式编程(八)：字符串及方法引用
 *
 * @author Ethan
 * @date 2015/12/7 9:25
 */
public class Demo08 {
    public static void main(String[] args) {
        /**
         * 遍历字符串
         * chars()方法是String类里的一个新方法，它是CharSequence接口的一部分。想要快速遍历String的字符序列的话，
         * 它是一个很有用的工具。有了这个内部迭代器，我们可以方便的操作字符串中的各个字符。
         */
        //输出都是转化后的int
        final String str = "w00t";
        str.chars().forEach(ch -> System.out.println(ch));
        str.chars().forEach(System.out::println);//上面的简化--由此可自定义括号内部的实现
        str.chars()
                .mapToObj(ch -> Character.valueOf((char)ch))
                .forEach(System.out::println);
        //函数式做法
        str.chars().forEach(Demo08::printChar);
        //获取字符串中的数字--还有map、reduce等
        str.chars()
                .filter(ch -> Character.isDigit(ch))
                .forEach(ch -> printChar(ch));
        str.chars()
                .filter(Character::isDigit)
                .forEach(Demo08::printChar);//上面的简化
    }

    /**
     * 自定义打印char函数
     */
    private static void printChar(int aChar) {
        System.out.println((char)(aChar));
    }

    /**
     * 小结:
     * 实例方法和静态方法的引用看起来都一样：比方说String::toUpperCase和Character::isDigit。
     * 编译器会判断方法是实例方法还是静态方法，来决定如何路由参数。如果是实例方法，
     * 它会将生成方法的入参用作方法调用的目标对象，比如 parameter,toUpperCase();
     * (当然也有例外，比如方法调用的目标对象已经指定了，像System::out.println())。
     * 另外如果是静态方法的话，生成方法的入参就会作为这个引用的方法的参数，比如Character.isDigit(parameter)。
     * 尽管方法引用用起来很方便，但还有一个问题——方法命名冲突导致的二义性 。如果匹配的方法既有实例方法也有静态方法，
     * 由于方法存在歧义编译器会报错。比如这么写，Double::toString，我们其实是想要把一个double类型转化成字符串，
     * 但编译器就不知道到底是该调用public String toString()的实例方法好，
     * 还是去调用public static String toString(double)方法，因为两个方法都是Double类的。
     * 如果你碰到这样的情况，别灰心，就用lambda表达式来完成就好了。一旦我们适应了函数式编程，
     * 我们就可以在lambda表达式和方法引用之间随心所欲地来回切换了。
     * 本节中我们用了Java 8中的一个新方法来遍历字符串。下面我们来看下Comparator接口又有了哪些改进。
     */
}
