package com.huawei;

public class StringUtil {
    public static int[] splitString(String string){
        String[] s=string.substring(1,string.length()-1).split(", ");
        int[] res=new int[s.length];
        for (int i = 0; i < res.length; i++) {
            res[i]=Integer.parseInt(s[i]);
        }
        return res;
    }
}
