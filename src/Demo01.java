import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Java函数式编程(一):你好，Lambda表达式
 * 2015/12/3 0003
 */
public class Demo01 {
    public static void main(String[] args) {
        //需求:我们定义了一系列价格，并通过不同的方式来计算打折后的总价。
        final List<BigDecimal> prices = Arrays.asList(
                new BigDecimal("10"), new BigDecimal("30"), new BigDecimal("17"),
                new BigDecimal("20"), new BigDecimal("15"), new BigDecimal("18"),
                new BigDecimal("45"), new BigDecimal("12")
        );
        //假设超过20块的话要打九折，我们先用普通的方式实现一遍。
        BigDecimal totalOfDiscountedPrices = BigDecimal.ZERO;
        for (BigDecimal price : prices) {
            if (price.compareTo(BigDecimal.valueOf(20)) > 0) {
                totalOfDiscountedPrices = totalOfDiscountedPrices.add(price.multiply(BigDecimal.valueOf(0.9)));
            }
        }
        System.out.println("Total of discounted prices: " + totalOfDiscountedPrices);


        //缩小业务需求和实现的代码之间的差距，减少了需求被误读的可能性.
        //我们不再让Java去创建一个变量然后没完没了的给它赋值了,我们要从一个更高层次的抽象去与它沟通.
       /**
       * 相对于前者的改进：
       * 1、结构良好而不混乱；
       * 2、没有低级操作；
       * 3.易于增强或者修改逻辑；
       * 4、由方法库来进行迭代；
       * 5.高效；循环体惰性求值；
       * 6、易于并行化。
       * */
        final BigDecimal total = prices.stream()
                .filter(price -> price.compareTo(BigDecimal.valueOf(20)) > 0)//过滤出大于20块的价格
                .map(price -> price.multiply(BigDecimal.valueOf(0.9)))//转化为折扣价
                .reduce(BigDecimal.ZERO, BigDecimal::add);//累加
        System.out.println("Total of discounted prices: " + total);
    }
}
