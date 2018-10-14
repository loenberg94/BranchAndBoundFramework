package com.mal.UI.utils;

import bb_framework.interfaces.Bound;
import utils.MemoryCompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadCompiler {
    private final int MAX_NR_OF_THREADS = 10;

    private MemoryCompiler compiler;
    private LinkedList<JavaFile> files = new LinkedList<>(); // Used to keep track of files with index
    private HashMap<String, List<Integer>> filesUsedIndex = new HashMap<>(); // Used to keep track of where files are used in

    private HashMap<Integer, ProblemInstance> problemInstances;

    private CompilerThread[] threads = new CompilerThread[MAX_NR_OF_THREADS];
    ReentrantLock compilerLock = new ReentrantLock();

    public MultiThreadCompiler(HashMap<Integer, ProblemInstance> pi){
        compiler = MemoryCompiler.newInstance();
        problemInstances = pi;

        for(int i = 0; i < MAX_NR_OF_THREADS; i++){
            threads[i] = new CompilerThread();
            threads[i].start();
        }
    }

    public void addFile(int index,JavaFile file){
        try {
            compilerLock.lock();
            if(!filesUsedIndex.containsKey(file.filename)){
                ArrayList<Integer> tmp = new ArrayList<>();
                tmp.add(index);
                filesUsedIndex.put(file.filename,tmp);
                files.add(file);
            }
            else{
                filesUsedIndex.get(file.filename).add(index);
            }
        } finally {
            compilerLock.unlock();
        }
    }

    public void compileAll(){
        files.notifyAll();
    }

    public void compile(int index, JavaFile file){
        addFile(index,file);
        compileAll();
    }

    private class CompilerThread extends Thread {
        @Override
        public void run() {
            while(true){
                try{
                    try {
                        files.wait();
                        compilerLock.lock();
                        while (!files.isEmpty()){
                            JavaFile tmp = files.removeFirst();
                            compilerLock.unlock();
                            Class<?> boundClass = compiler.compile(tmp.filename,tmp.content);
                            Bound bound = (Bound) boundClass.getDeclaredConstructor().newInstance();
                            for(Integer i : filesUsedIndex.get(tmp.filename)){
                                problemInstances.get(i).bound = bound;
                            }
                        }
                    } catch (Exception e) {
                        compilerLock.unlock();
                        e.printStackTrace();
                    }
                } finally {
                    compilerLock.unlock();
                }
            }
        }
    }

}
