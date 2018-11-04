# -*- coding:utf-8 -*-
import zipfile,os,time
import rarfile


"""
解压 rar文件 需要依赖 rarfile

"""
#解压 zip文件的
def un_zip(file_name,dir_folder):
    zip_file = zipfile.ZipFile(file_name)
    del_file(dir_folder)
    for fis in zip_file.namelist():
        zip_file.extract(fis,dir_folder)
    zip_file.close()

def un_rar(file_name,dir_folder):
    """unrar zip file"""
    rar = rarfile.RarFile(file_name)
    rar.extractall(dir_folder)  # 解压指定文件路径
    rar.close()


# 遍历 压缩文件的
def listdirISZIP_RAR(path):
    t=int(time.time())
    list_file=[]
    for root,dirs,files in os.walk(path):
        for filepath in files:
            if filepath.endswith(".zip") or filepath.endswith(".rar"):
                list_file.append(os.path.join(root,filepath));
    print("take time is :%d"%(time.time()-t))
    return list_file

# 遍历 txt后缀的文件
def  listFilesIStxt(path,type):
    t = int(time.time())
    list_file = []
    for root, dirs, files in os.walk(path):
        for filepath in files:
            if filepath.endswith(type):
                path=os.path.join(root,filepath)
                if not zipfile.is_zipfile(path):
                    ## 解决中文文件名乱码问题
                    try:
                        filepath = filepath.encode('cp437').decode('gbk')
                        new_path = os.path.join(root, filepath)
                        os.rename(path, new_path)
                        list_file.append(new_path)
                    except Exception as e:
                        print('error:', e)

    print("take time is :%d" % (time.time() - t))
    return list_file

import threading
class DealSearch(threading.Thread):
    def __init__(self,list,keyWord):
        threading.Thread.__init__(self)
        self.list=list
        self.keyWord = keyWord

    def run(self):
        for fish in self.list:
            file1=open(fish,"r")
            print("遍历文件："+fish)
            linenum=0;
            while True:
                linenum=linenum+1
                line=file1.readline()
                if line:
                    if line.find(self.keyWord)>-1:
                        print("linenum is %d ,and line content is %s"%(linenum,line))
                else:
                    break

import math
def searchWord(Keyword,soure_folder,dest_folder):
    zip_files=listdirISZIP_RAR(soure_folder)
    for zps in zip_files:
        un_zip(zps, dest_folder)
    all_txt_files = listFilesIStxt(dest_folder,".txt")
    print(all_txt_files)
    size = len(all_txt_files)
    thread_num = math.ceil(size / 3.0)
    t = int(time.time())
    print("thread_num:"+str(thread_num))
    # thread = DealSearch(all_txt_files, Keyword)
    # thread.start()
    for i in range(thread_num):
        bignum = (i+1)*3
        print("bignum:"+str(bignum))
        if(bignum>size):
           bignum = size
        lists=all_txt_files[i*3:bignum]
        thread = DealSearch(lists,Keyword)
        thread.start()

    print("take time is :%d" % (time.time() - t))



def del_file(path):
    ls = os.listdir(path)
    for i in ls:
        c_path = os.path.join(path, i)
        if os.path.isdir(c_path):
            del_file(c_path)
        else:
            os.remove(c_path)

if __name__=="__main__":
    #del_file("C:\\Users\\Administrator\\Desktop\ML\\testzip\\savezip")
    searchWord("ouyang23423@#@iduud",
               "C:\\Users\\Administrator\\Desktop\ML\\testzip\\origin",
               "C:\\Users\\Administrator\\Desktop\ML\\testzip\\savezip")