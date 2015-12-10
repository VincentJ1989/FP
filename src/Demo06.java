import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Java函数式编程(六):Optional-Java里面官方认证的null变量的除臭剂。
 *
 * @author Ethan
 * @date 2015/12/7 8:56
 */
public class Demo06 {
    public static void main(String[] args) {
        //需求：选取单个元素
        final List<String> friends =
                Arrays.asList("Brian", "Nate", "Neal", "Raju", "Sara", "Scott");
        //以往的做法
        pickName(friends, "S");
        //函数式编程
        //分析:能选出第一个匹配的元素，并且能安全的处理不存在这样一个元素的情况.
        pickNameByL(friends, "S");
        pickNameByL(friends, "C");
    }

    /**
     * 以往的做法
     * 缺点:
     * 这个方法简直跟刚过去的垃圾车一样臭不可闻。我们先是新建了一个foundName的变量，
     * 然后初始化成null——这个就是恶臭之源。我们不得不 检查是否为空，
     * 不然的话就会抛出一个NullPointerException或者一个错误响应。我们还用了一个外部迭代器来循环列表，
     * 如果找到了想要的 元素之后还得跳出这个循环，这又加重了原来的臭味：基本类型偏执，命令式风格，可变性，
     * 全齐活了。一旦退出循环后，我们还得先检查下结果，然后才能进行打印.
     */
    public static void pickName(
            final List<String> names, final String startingLetter) {
        String foundName = null;
        for (String name : names) {
            if (name.startsWith(startingLetter)) {
                foundName = name;
                break;
            }
        }
        System.out.println("第一个以" + startingLetter + "开头的名字为:" + foundName);
    }

    /**
     * 函数式编程做法
     */
    public static void pickNameByL(
            final List<String> names, final String startingLetter) {
        final Optional<String> foundName = names.stream()
                        .filter(name -> name.startsWith(startingLetter))
                        .findFirst();//第一个
        System.out.println(String.format("First name starting with %s: %s",
                startingLetter, foundName.orElse("No name found")));
    }
}