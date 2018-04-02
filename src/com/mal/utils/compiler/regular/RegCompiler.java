package com.mal.utils.compiler.regular;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class RegCompiler extends Thread {

    Queue<File> files = new LinkedList<>();
    Queue<File> errors = new LinkedList<>();
    private Condition newFile = (Condition) new ReentrantLock();
    Semaphore lock = new Semaphore(1);


    @Override
    public void run() {
        while(true){
            while(!files.isEmpty()){
                try {
                    lock.acquire();
                    File tmp = files.poll();
                    lock.release();
                    compile(tmp);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                newFile.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFile(File file) throws InterruptedException {
        lock.acquire();
        files.add(file);
        lock.release();
        newFile.signal();
    }

    private void compile(File file){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler.run(null,null,null,file.getAbsolutePath()) != 0){
            errors.add(file);
        }
    }
}
