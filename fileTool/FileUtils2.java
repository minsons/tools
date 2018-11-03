package com.ouyangm.tool;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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
        forEachSearch( keyWords, destTarget);
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
        forEachSearch( keyWords, destTarget);
    }



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


    public  static void main(String [] arg){
        FileUtils2 fileUtils2 = new FileUtils2();
        String sourceFolder="C:\\Users\\Administrator\\Desktop\\ML\\testzip\\origin";
        String saveTarget = "C:\\Users\\Administrator\\Desktop\\ML\\testzip\\savezip";
        fileUtils2.searchWordsByZIP("ouyang23423@#@iduud",sourceFolder,saveTarget);
    }

    public void testFileUtils2ZIP(){
        FileUtils2 fileUtils2 = new FileUtils2();
        String sourceFolder="C:\\Users\\Administrator\\Desktop\\ML\\testzip\\origin";
        String saveTarget = "C:\\Users\\Administrator\\Desktop\\ML\\testzip\\savezip";
        fileUtils2.searchWordsByZIP("ouyang23423@#@iduud",sourceFolder,saveTarget);
    }

    public void testFileUtils2RAR(){
        FileUtils2 fileUtils2 = new FileUtils2();
        String sourceFolder="C:\\Users\\Administrator\\Desktop\\ML\\testzip\\origin";
        String saveTarget = "C:\\Users\\Administrator\\Desktop\\ML\\testzip\\savezip";
        fileUtils2.searchWordsByRAR("ouyang23423@#@iduud",sourceFolder,saveTarget);
    }


}
