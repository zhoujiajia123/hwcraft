package com.huawei;

import org.apache.log4j.Logger;


import java.util.List;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        if (args.length != 4) {
            logger.info("please input args: inputFilePath, resultFilePath");
            return;
        }

        logger.info("Start...");
        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start read input files");
        List<String> carList=FileUtil.readFile(carPath);
        List<String> roadList=FileUtil.readFile(roadPath);
        List<String> crossList=FileUtil.readFile(crossPath);
        SolveByDirect mysolve=new SolveByDirect();
        mysolve.init(carList,roadList,crossList);

        // TODO: calc
        List<List<Integer>> result=mysolve.solve();

        // TODO: write answer.txt
        logger.info("Start write output file");
        FileUtil.writeFile(answerPath,result);
        logger.info("End...");
    }
}
