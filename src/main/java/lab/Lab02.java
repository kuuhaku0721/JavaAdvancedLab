package lab;


import javax.swing.*;
import java.util.*;

/**
 * (1) 分别建立一个使用Set接口、List接口和Map接口的对象，使用接口方法对其中的数据进行增、删、改、查，使用迭代器输出全部数据。（基础） (clear)
 * (2) 使用工具类Collections和Arrays。 (clear)
 * (3) 使用泛型。    (clear)
 * (4) 使用包装类。   (clear)
 */
public class Lab02 {
    // (1) 使用接口方法对数据进行增、删、改、查，并使用迭代器输出全部数据
    private void SetExample() {
        System.out.println("-------------Set示例----------------");
        Set<String> set = new HashSet<>();
        // 增
        set.add("琪亚娜·卡斯兰娜");
        set.add("雷电芽衣");
        set.add("布洛妮娅·等待修改");
        set.add("等待删除");

        // 使用迭代器输出
        System.out.println("初始化:");
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // 删
        set.remove("等待删除");

        // 改
        if(set.contains("布洛妮娅·等待修改")){
            set.remove("布洛妮娅·等待修改");
            set.add("布洛妮娅·扎伊切克");
        }

        // 查
        System.out.println("最终结果:");
        iterator = set.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    private void ListExample() {
        System.out.println("-------------List示例----------------");
        List<String> list = new ArrayList<>();
        // 增
        list.add("琪亚娜·卡斯兰娜");
        list.add("雷电芽衣");
        list.add("布洛妮娅·等待修改");
        list.add("等待删除");

        // 使用迭代器输出
        System.out.println("初始化:");
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        // 删
        list.remove(3);

        // 改
        list.set(2, "布洛妮娅·扎伊切克");

        // 查
        System.out.println("最终结果:");
        iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    private void MapExample() {
        System.out.println("-------------Map示例----------------");
        Map<Integer, String> map = new HashMap<>();
        // 增
        map.put(1, "琪亚娜·卡斯兰娜");
        map.put(2, "雷电芽衣");
        map.put(3, "布洛妮娅·等待修改");
        map.put(4, "等待删除");

        // 使用迭代器输出
        System.out.println("初始化:");
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        Iterator<Map.Entry<Integer, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> entry = iterator.next();
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        // 删
        map.remove(4);

        // 改
        map.replace(3, "布洛妮娅·扎伊切克");

        // 查
        System.out.println("最终结果：");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }

    private void CollectionUtilsExample() {
        System.out.println("-------------使用工具类Collections和Arrays----------------");
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1,4,2,43,5,6,43,2,5,7,8,6,65,3,56,99));
        System.out.println("原生结果：" + numbers);
        // 排序
        Collections.sort(numbers);
        System.out.println("排序结果: " + numbers);
        // 反转
        Collections.reverse(numbers);
        System.out.println("反转结果: " + numbers);
        // 打乱
        Collections.shuffle(numbers);
        System.out.println("随机打乱: " + numbers);
        // 查找
        int num = Collections.binarySearch(numbers, 5);
        System.out.println("查找下标5对应数字: " + num);
    }

    private <T> T MyMax(T[] array) {
        T max = array[0];
        for (T item : array) {
            if (((Comparable<T>) item).compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    private void TemplateExample() {
        System.out.println("-------------使用泛型----------------");
        Integer[] intArray = {1,0,7,2,1};
        String[] stringArray = {"琪亚娜·卡斯兰娜", "雷电芽衣", "布洛妮娅·扎伊切克"};

        System.out.println("使用模板类自定义实现查找数组最大值: ");
        System.out.println("int数组最大值: " + MyMax(intArray));
        System.out.println("string数组最大值: " + MyMax(stringArray));
    }

    private void PackagingExample() {
        System.out.println("-------------使用包装类----------------");
        int value = 10;
        Integer integerValue = null;
        System.out.println("未包装: " + integerValue);
        integerValue = Integer.valueOf(value);
        System.out.println("Integer 包装 int: " + integerValue);

        double dvalue = 3.14;
        Double doubleValue = Double.valueOf(dvalue);
        System.out.println("Double 包装 double: " + doubleValue);
    }

    public void RunLab02() {
        SetExample();

        ListExample();

        MapExample();

        CollectionUtilsExample();

        TemplateExample();

        PackagingExample();
    }

}
