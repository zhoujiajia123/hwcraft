package com.huawei;

import java.util.*;

public class SolveByDirect {
    private int[][] crossMatrix;
    private Map<Integer,int[]> roadMap;
    private Map<Integer,int[]> carMap;
    public void init( List<String> carList, List<String> roadList,List<String> crossList){
        crossMatrix=new int[crossList.size()][crossList.size()];
        roadMap=new HashMap<>();
        carMap=new TreeMap<>();
        for(String s:roadList){
            int[] temp=StringUtil.splitString(s);
            int from=temp[4]-1,to=temp[5]-1;
            if (temp[6]==1){
                crossMatrix[to][from]=temp[0];
            }
            crossMatrix[from][to]=temp[0];
            roadMap.put(temp[0],temp);
        }
        for (String s:carList){
            int[] temp1=StringUtil.splitString(s);
            carMap.put(temp1[0],temp1);
        }
    }
    public List<List<Integer>> solve(){
        List<Map.Entry<Integer,int[]>> list = new ArrayList<>(carMap.entrySet());
//        Collections.sort(list, new Comparator<Map.Entry<Integer, int[]>>() {
//            @Override
//            public int compare(Map.Entry<Integer, int[]> o1, Map.Entry<Integer, int[]> o2) {
//                return o2.getValue()[3]-o1.getValue()[3];
//            }
//        });
        List<List<Integer>> ans=new ArrayList<>();
        List<Integer> res;
        int plan=0;
        double wait=0;
        int counter=0;
        for (Map.Entry<Integer,int[]> entry:list) {
            List<Integer> roadCon=new ArrayList<>();
            int carid=entry.getKey(),from=entry.getValue()[1]-1,to=entry.getValue()[2]-1,plantime=entry.getValue()[4];
            if (counter%250<150){
                plan=4;
            }else {
                plan=4;
            }
            res=dijkstra(carid,from,to,plan);
            for (int j = 0; j < res.size()-1; j++) {
                roadCon.add(crossMatrix[res.get(j)][res.get(j+1)]);
            }
            if (counter<200){
                roadCon.add(0,carid);
                roadCon.add(1,plantime);
                System.out.println(plantime);
            }else {
                if (counter%250==0){
                    wait+=19;
                }
                roadCon.add(0,carid);
                roadCon.add(1,(int)wait+plantime);
                System.out.println(wait+plantime);
            }
            ans.add(roadCon);
            counter++;
        }
        return ans;
    }

    private List<Integer> dijkstra(int car,int from,int to,int plan){
        int[][] x=new int[crossMatrix.length][crossMatrix.length];
        int[] D=new int[x.length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                if (crossMatrix[i][j]!=0){
                    //过道路时间最短
                    if (plan==0){
                        x[i][j]=roadMap.get(crossMatrix[i][j])[1]/Math.min(carMap.get(car)[3],roadMap.get(crossMatrix[i][j])[2]);
                    }
                    //道路距离最短
                    if (plan==1) {
                        x[i][j]=roadMap.get(crossMatrix[i][j])[1]+Math.abs(carMap.get(car)[3]-roadMap.get(crossMatrix[i][j])[2])*2+1;
                    }
                    //距离加车道
                    if (plan==2) {
                        x[i][j]=roadMap.get(crossMatrix[i][j])[1]+10/roadMap.get(crossMatrix[i][j])[3];
                    }
                    //速度最匹配
                    if (plan==3){
                        x[i][j]=Math.abs(carMap.get(car)[3]-roadMap.get(crossMatrix[i][j])[2])*2+1;
                    }
                    if (plan==4){
                        x[i][j]=roadMap.get(crossMatrix[i][j])[1];
                    }
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
