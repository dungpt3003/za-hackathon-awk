package com.zalo.hackathon.detector;

import jvntextpro.data.Sentence;
import vnu.uet.vietsentiwordnet.apis.OpinionFinder;
import vnu.uet.vietsentiwordnet.objects.ResultObject;
import vnu.uet.vietsentiwordnet.objects.SentenceLevel;

import java.io.IOException;

public class SentimentDetector {
    private static SentenceLevel sl;
    private static SentimentDetector instance;
    private SentimentDetector() {
        sl = OpinionFinder.getInstance().loadModels();
    }

    public static SentimentDetector getInstance() {
        if (instance == null) {
            instance = new SentimentDetector();
        }

        return instance;
    }

    public int detectSentiment(String sentence) {
        ResultObject res;
        double score = 0;
        try {
            res = sl.doSenLevel(sentence);
            score = res.getScore();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (score >= 0.25)
            return 1; // Positive feedback
        else if (score <= -0.25)
            return -1; // Negative feedback
        else
            return 0; // Neutral
    }
}
