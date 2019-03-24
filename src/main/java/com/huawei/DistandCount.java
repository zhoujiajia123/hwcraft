package com.huawei;

import java.util.*;

public class DistandCount {
    private int[][] crossMatrix;
    private Map<Integer,int[]> roadMap;
    private int[][] carFactor;
    public void init( List<String> carList, List<String> roadList,List<String> crossList){
        crossMatrix=new int[crossList.size()][crossList.size()];
        roadMap=new HashMap<>();
        carFactor=new int[carList.size()][5];
        for(String s:roadList){
            int[] temp=StringUtil.splitString(s);
            int from=temp[4]-1,to=temp[5]-1;
            if (temp[6]==1){
                crossMatrix[to][from]=temp[0];
            }
            crossMatrix[from][to]=temp[0];
            int[] temp2=new int[3];
            for (int i = 0; i < temp2.length; i++) {
                temp2[i]=temp[i+1];
            }
            roadMap.put(temp[0],temp2);
        }
        for (int i = 0; i < carFactor.length; i++) {
            carFactor[i]=StringUtil.splitString(carList.get(i));
        }
    }
    public List<List<Integer>> solve(){
        List<List<Integer>> ans=new ArrayList<>();
        List<Integer> res;
        for (int i = 0; i < carFactor.length; i++) {
            List<Integer> roadCon=new ArrayList<>();
            res=dijkstra(carFactor[i][1]-1,carFactor[i][2]-1);
            for (int j = 0; j < res.size()-1; j++) {
                roadCon.add(crossMatrix[res.get(j)][res.get(j+1)]);
            }
            roadCon.add(0,carFactor[i][0]);
            roadCon.add(1,carFactor[i][4]);
            ans.add(roadCon);
        }
        return ans;
    }

    private List<Integer> dijkstra(int from,int to){
        int[][] x=new int[crossMatrix.length][crossMatrix.length];
        int[] D=new int[x.length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                if (crossMatrix[i][j]!=0){
                    x[i][j]=roadMap.get(crossMatrix[i][j])[0];
                }else {
                    x[i][j]=Integer.MAX_VALUE;
                }
            }
        }
        boolean[] travel=new boolean[x.length];
        travel[from]=true;
        int[] path=new int[x.length];
        for (int i = 0; i < path.length; i++) {
            D[i]=x[from][i];
            path[i]=-1;
        }
        for (int n = 1; n < x.length; n++) {
            int min=Integer.MAX_VALUE,cur=-1;
            for (int i = 0; i < x.length; i++) {
                if (!travel[i]&&D[i]<min){
                    min=D[i];
                    cur=i;
                }
            }
            travel[cur]=true;
            for (int i = 0; i < x.length; i++) {
                if (!travel[i]){
                    if (x[cur][i]!=Integer.MAX_VALUE&&D[i]+x[cur][i]<x[from][i]){
                        D[i]=D[i]+x[cur][i];
                        path[i]=cur;
                    }
                }
            }
        }
        List<Integer> list=new ArrayList<>();
        int des=to;
        list.add(des);
        while (path[des]!=-1){
            list.add(path[des]);
            des=path[des];
        }
        list.add(from);
        Collections.reverse(list);
        return list;
    }
}
