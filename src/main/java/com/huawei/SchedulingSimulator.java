package com.huawei;

import java.util.*;

public class SchedulingSimulator {
    final int DIRECT = 1;
    final int LEFT = 2;
    final int RIGHT = 3;

    int[][] matrix;
    Map<Integer, int[]> carsdet;
    Map<Integer, int[]> roadsdet;
    TreeMap<Integer, int[]> crossdet;

    Map<String, roadbind> carinroad = new HashMap<>();
    Map<Integer, List<Integer>> roadline;
    Map<Integer, List<Integer>> crossline;

    /*创建道路，每个channel是一个队列*/
    public void prepared() {
        for (Map.Entry<Integer, int[]> entry : roadsdet.entrySet()) {
            List<LinkedList<Carplace>> roadqueue = new ArrayList<>();
            for (int i = 0; i < entry.getValue()[2]; i++) {
                LinkedList<Carplace> queue = new LinkedList<>();
                roadqueue.add(queue);
            }
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (matrix[i][j] == entry.getKey()) {
                        carinroad.put(entry.getKey() + "," + j, new roadbind(j, entry.getKey(), roadqueue));
                    }
                }
            }
        }
    }
    /*开始调度*/
    public void start() {
        for (int t = 1; ; t++) {

            /*为每辆车标记状态*/
            Map<String,PriorityQueue> waitcarsqueue=new HashMap<>();
            for (Map.Entry<String, roadbind> entry : carinroad.entrySet()) {
                final int crossid = Integer.parseInt(entry.getKey().split(",")[1]);
                final int roadid = Integer.parseInt(entry.getKey().split(",")[0]);
                /*优先队列按出路口顺序装被标记的车*/
                PriorityQueue<Carprio> priorityQueue=new PriorityQueue<>(new Comparator<Carprio>() {
                    @Override
                    public int compare(Carprio o1, Carprio o2) {
                       if (o1.row!=o2.row){
                           return o1.row-o2.row;
                       }else {
                           return o1.channel-o2.channel;
                       }
                    }
                });
                List<LinkedList<Carplace>> roads = entry.getValue().carin;
                for (LinkedList<Carplace> road : roads) {
                    if (!road.isEmpty()) {
                        for (int i = 0; i < road.size(); i++) {
                            Carplace carplace = road.get(i);
                            int speed = Math.min(roadsdet.get(roadid)[2], carsdet.get(carplace.carid)[3]);
                            if (i == 0) {
                                if (carplace.drivelen + speed > carplace.roadlen) {
                                    if (Math.min(roadsdet.get(carplace.nextroadid)[2],carsdet.get(carplace.carid)[3])>carplace.roadlen-carplace.drivelen){
                                        carplace.status = 1;
                                        priorityQueue.offer(new Carprio(carplace,i,carplace.channelid));
                                    }else {
                                        carplace.drivelen=carplace.roadlen;
                                    }
                                } else {
                                    road.get(i).drivelen += speed;
                                }
                            } else {
                                if (road.get(i - 1).status == 0) {
                                    if (carplace.drivelen + speed >= road.get(i - 1).drivelen) {
                                        carplace.drivelen = road.get(i - 1).drivelen - 1;
                                    } else {
                                        carplace.drivelen += speed;
                                    }
                                } else {
                                    if (carplace.drivelen + speed >= road.get(i - 1).drivelen) {
                                        carplace.status = 1;
                                        priorityQueue.offer(new Carprio(carplace,i,carplace.channelid));
                                    } else {
                                        carplace.drivelen += speed;
                                    }
                                }
                            }
                        }
                    }
                }
                waitcarsqueue.put(entry.getKey(),priorityQueue);
            }

            /*调度每个路口对应的（1、2、3或4）个优先队列里的车*/
            for (Map.Entry<Integer, int[]> entry : crossdet.entrySet()) {
                List<PriorityQueue<Carprio>> cross_road = new ArrayList<>();
                int crossid = entry.getKey();
                int[] roaddir = entry.getValue();
                int[] order=new int[roaddir.length-1];
                for (int i = 0; i < order.length; i++) {
                    order[i]=roaddir[i-1];
                }
                for (int i = 1; i < roaddir.length; i++) {
                    if (roaddir[i] != -1) {
                        PriorityQueue<Carprio> queue= waitcarsqueue.get(roaddir[i] + "," + crossid);
                        if (!queue.isEmpty()){
                            cross_road.add(queue);
                        }
                    }
                }
                List<Carplace> change=new ArrayList<>();
                while (true){
                    for (int i = 0; i < cross_road.size(); i++) {
                        PriorityQueue<Carprio> queue=cross_road.get(i);
                        if (!queue.isEmpty()){
                            Carprio carprio=queue.peek();
                            while (canChangeStatus(cross_road,carprio,i,change)){
                                queue.poll();
                                if (queue.isEmpty()){
                                    break;
                                }
                                carprio=queue.peek();
                            }
                        }
                    }
                    int count=0;
                    for( PriorityQueue<Carprio> q:cross_road){
                        if (q.isEmpty()){
                            count++;
                        }
                    }
                    if (count==cross_road.size()){
                        break;
                    }
                }
            }
        }
    }
    /*判断队列中车能否改变状态*/
    public boolean canChangeStatus(List<PriorityQueue<Carprio>> cross_road,Carprio carprio,int i,List<Carplace> change){
        boolean res=true;
        int place=-1;
        Carplace carplace=carprio.carplace;
        for (int j = change.size()-1; j >= 0; j--) {
            if (change.get(i).channelid==carplace.channelid){
                place=i;
                break;
            }
        }
        if (place==-1){
            //路口第一辆车必然要判断过路口
            if (carplace.destination==carplace.crossid){
                carplace.status=3;
                change.add(carplace);
                carinroad.get(carplace.roadid).carin.get(carplace.channelid-1).poll();
                return true;
            }
            for (int index = 0; index < cross_road.size(); index++) {
                if (index!=i){
                    Carprio p=cross_road.get(i).peek();
                    if (carplace.nextroadid==carplace.nextroadid&&carplace.direction<carplace.direction){
                        res=false;
                    }else {
                        //处理车辆的各种信息
                        int nextdis=Math.min(roadsdet.get(carplace.nextroadid)[2],carsdet.get(carplace.carid)[3])-(carplace.roadlen-carplace.drivelen);
                        int nextroadid=carplace.nextroadid;
                        int nextcrossid=carplace.nextcrossid;
                        List<LinkedList<Carplace>> road=carinroad.get(nextroadid+","+nextcrossid).carin;
                        boolean can=false;
                        int ch=0;
                        for(LinkedList<Carplace> queue:road){
                            ch++;
                            if (queue.getLast().drivelen==1){
                                continue;
                            }else {
                                can=true;
                                carplace.status=3;
                                change.add(carplace);
                                //进入了下一条路
                                int drivelen=Math.min(queue.getLast().drivelen-1,nextdis);
                                int roadid=carplace.nextroadid;
                                int crossid=carplace.nextcrossid;
                                int carid=carplace.carid;
                                int channelid=ch;
                                int roadlen=roadsdet.get(roadid)[1];
                                int speed=Math.min(carsdet.get(carplace.carid)[3],roadsdet.get(roadid)[2]);
                                queue.offer(new Carplace());
                                break;
                            }
                        }
                        if (!can){
                            carplace.status=0;
                            carplace.drivelen=carplace.drivelen;
                            change.add(carplace);
                        }
                    }
                }
            }
        }else {
            //不是路口第一辆车依据前车的状态
            Carplace carinlist=change.get(place);
            if (carinlist.status==3){
                //前车已经出了路口
                if (carplace.drivelen+carplace.speed>carplace.roadlen){
                    if (carplace.destination==carplace.nextcrossid){
                        carplace.status=3;
                        change.add(carplace);
                        carinroad.get(carplace.roadid).carin.get(carplace.channelid-1).poll();
                        return true;
                    }
                    if (Math.min(roadsdet.get(carplace.nextroadid)[2],carsdet.get(carplace.carid)[3])>carplace.roadlen-carplace.drivelen){
                        for (int index = 0; index < cross_road.size(); index++) {
                            if (index!=i){
                                Carprio p=cross_road.get(i).peek();
                                if (carplace.nextroadid==carplace.nextroadid&&carplace.direction<carplace.direction){
                                    res=false;
                                }else {
                                    //改变车辆状态
                                    int nextdis=Math.min(roadsdet.get(carplace.nextroadid)[2],carsdet.get(carplace.carid)[3])-(carplace.roadlen-carplace.drivelen);
                                    int nextroadid=carplace.nextroadid;
                                    int nextcrossid=carplace.nextcrossid;
                                    List<LinkedList<Carplace>> road=carinroad.get(nextroadid+","+nextcrossid).carin;
                                    boolean can=false;
                                    int ch=0;
                                    for(LinkedList<Carplace> queue:road){
                                        ch++;
                                        if (queue.getLast().drivelen==1){
                                            continue;
                                        }else {
                                            can=true;
                                            carplace.status=3;
                                            change.add(carplace);
                                            //进入了下一条路
                                            int drivelen=Math.min(queue.getLast().drivelen-1,nextdis);
                                            int roadid=carplace.nextroadid;
                                            int crossid=carplace.nextcrossid;
                                            int carid=carplace.carid;
                                            int channelid=ch;
                                            int roadlen=roadsdet.get(roadid)[1];
                                            int speed=Math.min(carsdet.get(carplace.carid)[3],roadsdet.get(roadid)[2]);
                                            queue.offer(new Carplace());
                                            break;
                                        }
                                    }
                                    if (!can){
                                        carplace.status=0;
                                        carplace.drivelen=carplace.drivelen;
                                        change.add(carplace);
                                    }
                                }
                            }
                        }
                    }else {
                        carplace.status=0;
                        carplace.drivelen=carplace.roadlen;
                        change.add(carplace);
                    }
                }else {
                    carplace.status=0;
                    carplace.drivelen+=carplace.speed;
                    change.add(carplace);
                }
            }else {
                //前车还在当前道路
                carplace.status=0;
                carplace.drivelen=Math.min(carinlist.drivelen,carplace.drivelen+carplace.speed);
                change.add(carplace);
            }
        }
        return res;
    }

    public void initcar(int T, Map<Integer,int[]> carsdet,Map<Integer, List<Integer>> roadline,Map<Integer, List<Integer>> crossline){
        for(int[] d:carsdet.values()){
            if (d[1]-T==1){
                int carid=d[0];
                int roadid=roadline.get(carid).get(0);
                int crossid=crossline.get(carid).get(0);
                int channelid=0;
                int roadlen=roadsdet.get(roadid)[1];
                int roadnumth=1;
                int crossnumth=1;
                int nextroadid=roadline.get(carid).get(roadnumth);
                int nextcrossid=crossline.get(carid).get(crossnumth);
                int speed=Math.min(roadsdet.get(roadid)[2],carsdet.get(carid)[3]);
                int drivelen=speed;
                int status=0;
                int[] dir=crossdet.get(crossid);
                int direction=DIRECT;
                int r1=0,r2=0;
                for (int i = 1; i < dir.length; i++) {
                    if (dir[i]==roadid){
                        r1=i;
                    }
                    if (dir[i]==nextroadid){
                        r2=i;
                    }
                }
                if (Math.abs(r2-r1)==2){
                    direction=DIRECT;
                }
                if (r2-r1==1||r2-r1==-3){
                    direction=LEFT;
                }
                if (r2-r1==-1||r2-r1==3){
                    direction=RIGHT;
                }
                int destination=carsdet.get(carid)[2];
            }
        }
    }

    class Carplace {
        public int carid;
        public int roadid;
        public int crossid;
        public int channelid;
        public int drivelen;
        public int roadlen;
        public int roadnumth;
        public int crossnumth;
        public int nextroadid;
        public int nextcrossid;
        public int speed;
        public int status;
        public int direction;
        public int destination;

//        public Carplace(int carId, int driveLen, int roadLen, int speed, int destination, int direction, int roadnumth, int nextCross) {
//            this.carId = carId;
//            this.driveLen = driveLen;
//            this.roadLen = roadLen;
//            this.speed = speed;
//            this.destination = destination;
//            this.direction = direction;
//            this.roadnumth = roadnumth;
//            this.nextCross = nextCross;
//        }
    }

    class roadbind {
        public int roadid;
        public int crossid;
        List<LinkedList<Carplace>> carin;

        public roadbind(int roadid, int crossid, List<LinkedList<Carplace>> carin) {
            this.roadid = roadid;
            this.crossid = crossid;
            this.carin = carin;
        }
    }

    class Waitqueue{
        public int roadid;
        public int crossid;
        LinkedList<Carplace> carqueue;
        public Waitqueue(){

        }
        public Waitqueue(int roadid, int crossid, LinkedList<Carplace> carqueue) {
            this.roadid = roadid;
            this.crossid = crossid;
            this.carqueue = carqueue;
        }
    }

    class Carprio{
        Carplace carplace;
        int row;
        int channel;

        public Carprio(Carplace carplace, int row, int channel) {
            this.carplace = carplace;
            this.row = row;
            this.channel = channel;
        }
    }
}



