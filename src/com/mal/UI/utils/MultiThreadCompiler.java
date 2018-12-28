package com.mal.UI.utils;

import bb_framework.interfaces.Bound;
import utils.MemoryCompiler;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiThreadCompiler {
    //TODO: Add Compile Exception Handling
    private final int MAX_NR_OF_THREADS = 5;

    private MemoryCompiler compiler;
    private LinkedBlockingDeque<JavaFile> files = new LinkedBlockingDeque<>();
    private ConcurrentHashMap<String,LinkedBlockingDeque<Integer>> filesUsedIndex = new ConcurrentHashMap<>();
    private HashMap<Integer, ProblemInstance> problemInstances;
    private CompilerThread[] threads = new CompilerThread[MAX_NR_OF_THREADS];

    private final Object lock = new Object();
    private final ReentrantReadWriteLock indexLock = new ReentrantReadWriteLock(true);
    private final Lock read = indexLock.readLock();
    private final Lock write = indexLock.writeLock();


    public MultiThreadCompiler(HashMap<Integer, ProblemInstance> pi){
        compiler = MemoryCompiler.newInstance();
        problemInstances = pi;

        for(int i = 0; i < MAX_NR_OF_THREADS; i++){
            threads[i] = new CompilerThread();
            threads[i].start();
        }
    }

    public void addFile(int index,JavaFile file){
        read.lock();
        if(!filesUsedIndex.containsKey(file.filename)){
            read.unlock();
            LinkedBlockingDeque<Integer> tmp = new LinkedBlockingDeque<>();
            tmp.add(index);
            write.lock();
            filesUsedIndex.put(file.filename,tmp);
            write.unlock();
            files.add(file);
        }
        else{
            read.unlock();
            write.lock();
            filesUsedIndex.get(file.filename).add(index);
            write.unlock();
        }
    }

    public void compileAll(){
        synchronized (lock){
            lock.notifyAll();
        }
    }

    public void compile(int index, JavaFile file){
        synchronized (lock) {
            addFile(index, file);
            lock.notify();
        }
    }

    private class CompilerThread extends Thread {
        @Override
        public void run() {
            while(true){
                try {
                    synchronized (lock){
                        lock.wait();
                        System.out.println("Compiling file");
                        while (!files.isEmpty()){
                            JavaFile tmp = files.takeFirst();
                            if (tmp != null){
                                String cname = tmp.filename.split("\\.")[0];
                                Class<?> boundClass = compiler.compile(cname,tmp.content);
                                Bound bound = (Bound) boundClass.getDeclaredConstructor().newInstance();
                                read.lock();
                                for(Integer i : filesUsedIndex.get(tmp.filename)){
                                    problemInstances.get(i).setBound(bound);
                                }
                                read.unlock();
                                write.lock();
                                filesUsedIndex.remove(tmp.filename);
                                write.unlock();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
