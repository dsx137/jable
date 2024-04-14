package com.github.dsx137.jable.random;

import java.util.Random;

public class RandomNickname extends RandomGenerator<String> {

    private static final String[] WORDS = {
            "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的", "的",
            "帧", "流", "块", "门", "熵", "核", "类", "锁", "链", "模", "位", "堆", "秩",
            "闭包", "复用", "核心", "协议", "路由", "并行", "信道", "正则", "函数", "谓词", "异步", "管道", "指针", "递归", "向量", "接口", "命令", "交换", "缓存",
            "柯里化", "反混淆", "二叉树", "全双工", "分布式", "最短路", "图灵", "归并", "序列化", "多重表", "哈夫曼", "傅里叶", "虚拟化", "轻量级", "客户端",
            "欧几里得", "模式匹配", "完全应用", "控制平面", "敏捷开发", "反向传播", "负载均衡", "神经网络", "微服务化",
            "空间复杂度", "安全上下文", "支持传递到", "标准表达式", "图数据挖掘", "自底向上", "通信协议栈", "面向对象",
            "身份验证机制", "资源调度策略", "可移植性编程",
            "具有层次关系", "不特定于给定", "高性能计算集群", "用户体验友好",
            "自适应实时操作系统内核"
    };

    @Override
    protected String generate(byte[] seed, int length, Random random) {
        StringBuilder nickname = new StringBuilder(length);

        int counter = 0;
        String latestWord = "棍斤拷棍斤拷棍斤拷棍斤拷棍斤拷棍斤拷棍斤拷";

        while (nickname.length() < length) {
            if (counter == 1000) break;
            counter++;

            String word = WORDS[random.nextInt(WORDS.length)];
            if ((
                    nickname.length() == 0 && word.equals("的")) ||
                    (nickname.length() + word.length() > length) ||
                    (latestWord.contains(word) || word.contains(latestWord))
            ) {
                continue;
            }

            nickname.append(word);
            latestWord = word;
        }

        return nickname.toString();
    }
}
