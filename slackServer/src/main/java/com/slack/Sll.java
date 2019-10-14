package com.slack;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.stream.IntStream;

public class Sll {

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (FileWriter fw = new FileWriter("hook.log")) {
                // 假设记录日志/或者发送消息
                fw.write("完成销毁操作,回收内存! " + (new Date()).toString());
                System.out.println("退出程序...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        IntStream.range(0, 10).forEach(i -> {
            try {
                System.out.println("正在工作...");
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
	}
}
