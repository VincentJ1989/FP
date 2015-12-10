import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Java函数式编程(二):集合的使用
 *
 * @author Ethan
 * @date 2015/12/3 0003 9:57
 */
public class Demo02 {
    public static void main(String[] args) {
        //创建一个不可变的名字的列表
        final List<String> friends = Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott");
        System.out.println("---------------自虐型遍历---------------");
        //以前的遍历方法
        for (int i = 0, length = friends.size(); i < length; i++) {
            System.out.println(friends.get(i));
        }
        System.out.println("------------forEach遍历-----------");
        //for-each
        for (String name : friends) {
            System.out.println(name);
        }
        System.out.println("-------------------Lambda遍历-----------------");
        friends.forEach(new Consumer<String>() {
            @Override
            public void accept(final String s) {//使用final避免变量的修改
                System.out.println(s);
            }
        });
        System.out.println("------------------一次简化Lambda------------------");
        friends.forEach(s -> System.out.println(s));//已经简化了，去除了s类型指定
        System.out.println("-------------最简Lambda--------------");
        friends.forEach(System.out::println);

    }
}
