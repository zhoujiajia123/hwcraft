package com.huawei;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<String> readFile(String path){
        List<String> res=new ArrayList<>();
        FileReader fr=null;
        BufferedReader br=null;
        try {
            fr=new FileReader(path);
            br=new BufferedReader(fr);
            String s=null;
            br.readLine();
            while((s=br.readLine())!=null){
                res.add(s);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (br!=null){
                    br.close();
                }
                if (fr!=null){
                    fr.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return res;
    }

    public static void writeFile(String path,List<List<Integer>> lists){
        FileWriter fw=null;
        BufferedWriter bw=null;
        try {
            fw=new FileWriter(path);
            bw=new BufferedWriter(fw);
            for(List<Integer> list:lists){
               String s=list.toString();
               bw.write("("+s.substring(1,s.length()-1)+")");
               bw.newLine();
           }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (bw!=null){
                    bw.close();
                }
                if (fw!=null){
                    fw.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
