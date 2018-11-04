package com.ouyangm.tool;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 文件查询 io出来的工具类
 * 依赖于
 *  compile group: 'commons-io', name: 'commons-io', version: '2.6'
 * // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
 *     compile group: 'org.apache.commons', name: 'commons-lang3', version: '3.8'
 * // https://mvnrepository.com/artifact/commons-collections/commons-collections
 *     compile group: 'commons-collections', name: 'commons-collections', version: '3.2.2'
 */
public class FileUtils2 {

    /**
     * 通过zip解压后 搜索相应的文字
     *
     * sourceTarget 压缩文件的目录
     * destTarget 解压后的目录
     */
    public void searchWordsByZIP(String keyWords ,String sourceTarget ,String destTarget){
        // 第一步解压
        try {
            //DeCompressUtil.unzip(sourceTarget,destTarget);
            getoutZIPARAR( sourceTarget , destTarget ,"zip");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("=================ZIP 解压失败！");
        }
        System.out.println("文件已经解压出来的了！");
        // 第二步 遍历文件
       //forEachSearch( keyWords, destTarget);
        forEachSearchMutilThread( keyWords, destTarget);
    }


    /**
     * 通过rar解压后 搜索相应的文字
     *
     * sourceTarget 压缩文件的目录
     * destTarget 解压后的目录
     */
    public void searchWordsByRAR(String keyWords ,String sourceTarget ,String destTarget){
        // 第一步解压
        try {
            getoutZIPARAR( sourceTarget , destTarget ,"rar");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("=================ZIP 解压失败！");
        }
        System.out.println("文件已经解压出来的了！");
        // 第二步 遍历文件
        //forEachSearch( keyWords, destTarget);
        forEachSearchMutilThread( keyWords, destTarget);
    }

    // 遍历查询文件 单线程
    public void forEachSearch(String keyWords,String destTarget){
        File fils0= new File(destTarget);
        Iterator itemIterator =FileUtils.iterateFiles(fils0,new String[]{"txt"},true);
        while(itemIterator.hasNext()){
            File fis =(File)itemIterator.next();
            try {
                LineIterator lineIterator = FileUtils.lineIterator(fis,"GBK");
                long lineNum=1;
                while(lineIterator.hasNext()){
                    String linestr = lineIterator.nextLine();
                    if(linestr.contains(keyWords)){
                        System.out.println("fileName:"+fis.getAbsolutePath()+" and Lines:"+lineNum);
                        System.out.println("content is ==================="+linestr);
                    }
                    lineNum++;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //遍历查询 多线程查询
    public void forEachSearchMutilThread(String keyWords,String destTarget){

          List<File> fils = getFileList2File(destTarget);
          int baseSize = fils.size();
          double size = Math.ceil(fils.size()/3.0);
          int lastsize = (int)size;
          System.out.println("========一共多少个线程："+lastsize+":"+baseSize);
          ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
          for(int init=lastsize, y=0; y<lastsize;y++){
              int laston = (y+1)*3 > baseSize ? baseSize:(y+1)*3;
              System.out.println("线程："+(y+1)+":"+(y*3)+":"+(laston-1));
              List<File> filsOne = fils.subList(y*3,laston);
              System.out.println("filsOne:"+filsOne);
              MyThread thss = new MyThread(keyWords,filsOne);
              fixedThreadPool.execute(thss);
          }

        fixedThreadPool.shutdown();
        if(fixedThreadPool.isTerminated()){
           System.out.println("结束任务！");
        }
    }


    // 解压文件
    public void getoutZIPARAR(String sourceFolder ,String saveTarget ,String type){
        List<String> filePaths = new ArrayList<>();
        File file =new File(sourceFolder);
        if(file.exists()){
            File[] files = file.listFiles();
            for (File file2 : files) {
                if(file2.getName().endsWith("zip")|| file2.getName().endsWith("rar")){
                    filePaths.add(file2.getAbsolutePath());
                }
            }
        }
        for(String singleFile:filePaths){
            try {
                if(type.equals("rar")){
                    DeCompressUtil.unrar(singleFile,saveTarget);
                }else{
                    DeCompressUtil.unzip(singleFile,saveTarget);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    //用于多线程遍历使用的
    public class MyThread extends Thread{
        private String keyWords;
        private List<File> allFiles;
        MyThread(){

        }
        MyThread(String keyWords ,List<File> allFiles){
             this.keyWords = keyWords;
             this.allFiles = allFiles;
        }
        public void run(){
            System.out.println("MyThread running:"+Thread.currentThread().getName());
            for(File fis : allFiles){
                if(fis.getAbsolutePath().endsWith(".txt")){
                    try {
                        LineIterator lineIterator = FileUtils.lineIterator(fis,"GBK");
                        long lineNum=1;
                        while(lineIterator.hasNext()){
                            String linestr = lineIterator.nextLine();
                            if(linestr.contains(keyWords)){
                                System.out.println("fileName:"+fis.getAbsolutePath()+" and Lines:"+lineNum);
                                System.out.println("content is ==================="+linestr);
                            }
                            lineNum++;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public String getKeyWords() {
            return keyWords;
        }

        public void setKeyWords(String keyWords) {
            this.keyWords = keyWords;
        }

        public List<File> getAllFiles() {
            return allFiles;
        }

        public void setAllFiles(List<File> allFiles) {
            this.allFiles = allFiles;
        }
    }

    // 遍历文件成字符串集合
    public List<String> getFileList(String filepath){
        List<String> fileList = new ArrayList<>();
        File files = new File(filepath);
        if(!files.exists()) return null;
        if(files.isDirectory()){
            File [] filesArray = files.listFiles();
            for(File singleFile:filesArray){
                if(singleFile.isDirectory()){
                    List<String> returnslist= getFileList(singleFile.getAbsolutePath());
                    fileList.addAll(returnslist);
                }else{
                    fileList.add(singleFile.getAbsolutePath());
                }
            }
        }else{
            fileList.add(filepath);
        }
        return fileList;
    }

    // 遍历文件目录 成文件对象
    public List<File> getFileList2File(String filepath){
        List<File> fileList = new ArrayList<>();
        File files = new File(filepath);
        if(!files.exists()) return null;
        if(files.isDirectory()){
            File [] filesArray = files.listFiles();
            for(File singleFile:filesArray){
                if(singleFile.isDirectory()){
                    List<File> returnslist= getFileList2File(singleFile.getAbsolutePath());
                    fileList.addAll(returnslist);
                }else{
                    fileList.add(singleFile);
                }
            }
        }else{
            fileList.add(files);
        }
        return fileList;
    }



  //================================================== 下面是测试的方法 ==========================================================



    public  static void main(String [] arg){
        testFileUtils2ZIP();
    }

    public static void testFileUtils2ZIP(){
        long beginTime = System.currentTimeMillis();
        System.out.println("beginTime is :" +beginTime);
        FileUtils2 fileUtils2 = new FileUtils2();
        String sourceFolder="C:\\Users\\Administrator\\Desktop\\ML\\testzip\\origin";
        String saveTarget = "C:\\Users\\Administrator\\Desktop\\ML\\testzip\\savezip";
        fileUtils2.searchWordsByZIP("ouyang23423@#@iduud",sourceFolder,saveTarget);
        System.out.println("endTime is :" +(System.currentTimeMillis()-beginTime));
    }

    public static void testFileUtils2RAR(){
        FileUtils2 fileUtils2 = new FileUtils2();
        String sourceFolder="C:\\Users\\Administrator\\Desktop\\ML\\testzip\\origin";
        String saveTarget = "C:\\Users\\Administrator\\Desktop\\ML\\testzip\\savezip";
        fileUtils2.searchWordsByRAR("ouyang23423@#@iduud",sourceFolder,saveTarget);
    }


}
