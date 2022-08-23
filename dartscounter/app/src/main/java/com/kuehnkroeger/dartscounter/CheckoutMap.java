package com.kuehnkroeger.dartscounter;

/**
 * holds data for checkout ways
 */
public class CheckoutMap {

    /** placeholder for whenever there is no possible way to finish a score */
    public static final int[] noFinish = {-1};

    /** holds finishes for mode double out */
    private final static int[][] doubleOut =
            {{320,320,225}, noFinish, noFinish, {320,319,225}, noFinish, noFinish, {320,318,225},
             noFinish, noFinish, {320,317,225}, {320,320,220}, noFinish, {320,316,225}, {320,319,220},
             {320,320,218}, {320,315,225}, {320,318,220}, {320,319,218}, {320,320,216}, {320,317,220},
             {320,318,218}, {320,319,216}, {320,316,220}, {320,317,218}, {320,318,216}, {320,315,220},
             {320,320,212}, {320,317,216}, {320,314,220}, {320,319,212}, {320,316,216}, {320,313,220},
             {320,318,212}, {319,316,216}, {320,320,208}, {320,317,212}, {320,314,216}, {320,319,208},
             {320,316,212}, {320,313,216}, {320,318,208}, {319,316,212}, {318,314,216}, {320,317,208},
             {319,119,225}, {318,313,216}, {320,316,208}, {319,314,212}, {318,320,204}, {317,318,208},
             {320,120,220}, {319,310,216}, {318,316,208}, {320,117,220}, {320,116,220}, {319,118,220},
             {320,114,220}, {319,116,220}, {320,120,216}, {319,114,220}, {320,225}, {319,312,208},
             {319,119,216}, {319,225}, {320,106,220}, {319,116,216}, {318,225}, {319,106,220},
             {320,110,216}, {317,225}, {320,220}, {319,110,216}, {320,219}, {319,220}, {320,218},
             {319,219}, {318,220}, {319,218}, {320,216}, {317,220}, {318,218}, {319,216}, {316,220},
             {317,218}, {318,216}, {315,220}, {320,214}, {317,216}, {314,220}, {319,212}, {316,216},
             {313,220}, {318,212}, {319,210}, {320,208}, {317,212}, {314,216}, {319,208}, {316,212},
             {313,216}, {318,208}, {319,206}, {320,204}, {317,208}, {314,212}, {315,210}, {316,208},
             {313,212}, {310,216}, {315,208}, {120,220}, {119,220}, {118,220}, {117,220}, {116,220},
             {115,220}, {114,220}, {113,220}, {120,216}, {119,216}, {118,216}, {117,216}, {116,216},
             {115,216}, {106,220}, {113,216}, {112,216}, {111,216}, {110,216}, {109,216}, {220},
             {107,216}, {219}, {105,216}, {218}, {103,216}, {217}, {117,208}, {216}, {115,208}, {215},
             {113,208}, {214}, {111,208}, {213}, {109,208}, {212}, {107,208}, {211}, {105,208}, {210},
             {103,208}, {209}, {101,208}, {208}, {107,204}, {207}, {105,204}, {206}, {103,204}, {205},
             {101,204}, {204}, {103,202}, {203}, {101,202}, {202}, {101, 201}, {201}, noFinish};

    /**
     * return a finish for mode double out and a given number of darts remaining
     * @param score score to finish
     * @param darts number of darts remaining
     * @return way to finish or {@link #noFinish}
     */
    public static int[] doubleOut(int score, int darts) {
        switch (darts) {
            case 3: return doubleOutThreeDarts(score);
            case 2: return doubleOutTwoDarts(score);
            case 1: return doubleOutOneDart(score);
            default: return noFinish;
        }
    }

    /**
     * return a finish for mode master out and a given number of darts remaining
     * @param score score to finish
     * @param darts number of darts remaining
     * @return way to finish or {@link #noFinish}
     */
    public static int[] masterOut(int score, int darts) {
        switch (darts) {
            case 3: return masterOutThreeDarts(score);
            case 2: return masterOutTwoDarts(score);
            case 1: return masterOutOneDart(score);
            default: return noFinish;
        }
    }

    /**
     * return a finish for mode single out and a given number of darts remaining
     * @param score score to finish
     * @param darts number of darts remaining
     * @return way to finish or {@link #noFinish}
     */
    public static int[] singleOut(int score, int darts) {
        switch (darts) {
            case 3: return singleOutThreeDarts(score);
            case 2: return singleOutTwoDarts(score);
            case 1: return singleOutOneDart(score);
            default: return noFinish;
        }
    }

    /**
     * returns a finish for mode double out and three darts remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] doubleOutThreeDarts(int score) {
        return doubleOut[170-score];
    }

    /**
     * returns a finish for mode double out and two darts remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] doubleOutTwoDarts(int score) {
        int[] finish_two = doubleOut[170-score];
        if(finish_two.length < 3) return finish_two;
        return new int[] {finish_two[0], finish_two[1]};
    }

    /**
     * returns a finish for mode double out and one dart remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] doubleOutOneDart(int score) {
        if(score == 50) return new int[]{225};
        int[] finish_one = doubleOut[170-score];
        if(finish_one.length < 2) return finish_one;
        return new int[] {finish_one[0]};
    }

    /** holds finishes for mode master out */
    private final static int[][] masterOut =
            {{320,320,320}, noFinish, noFinish, {320,320,319}, noFinish, noFinish, {320,320,318},
             noFinish, noFinish, {320,320,317}, {320,320,225},
             noFinish, {320,320,216}, {320,319,225}, noFinish, {320,320,315}, {320,318,225}, noFinish,
             {320,320,214}, {320,317,225}, {320,320,220}, {320,319,314}, {320,316,225}, {320,319,220},
             {320,320,218}, {320,315,225}, {320,318,220}, {320,319,218}, {320,320,216}, {320,317,220},
             {320,318,218}, {320,319,216}, {320,316,220}, {320,317,218}, {320,318,216}, {320,315,220},
             {320,320,212}, {320,317,216}, {320,314,220}, {320,319,212}, {320,316,216}, {320,313,220},
             {320,318,212}, {319,316,216}, {320,320,208}, {320,317,212}, {320,314,216}, {320,319,208},
             {320,316,212}, {320,313,216}, {320,318,208}, {319,316,212}, {318,314,216}, {320,317,208},
             {319,119,225}, {318,313,216}, {320,316,208}, {319,314,212}, {318,320,204}, {317,318,208},
             {320,120,220}, {319,310,216}, {318,316,208}, {320,117,220}, {320,116,220}, {319,118,220},
             {320,114,220}, {319,116,220}, {320,120,216}, {319,114,220}, {320,225}, {319,312,208},
             {319,119,216}, {319,225}, {320,106,220}, {319,116,216}, {318,225}, {319,106,220},
             {320,110,216}, {317,225}, {320,220}, {319,110,216}, {320,219}, {319,220}, {320,218},
             {319,219}, {318,220}, {319,218}, {320,216}, {317,220}, {318,218}, {319,216}, {316,220},
             {317,218}, {318,216}, {315,220}, {320,214}, {317,216}, {314,220}, {319,212}, {316,216},
             {313,220}, {318,212}, {319,210}, {320,208}, {317,212}, {314,216}, {319,208}, {316,212},
             {313,216}, {318,208}, {319,206}, {320,204}, {317,208}, {314,212}, {315,210}, {316,208},
             {313,212}, {310,216}, {315,208}, {120,220}, {119,220}, {118,220}, {117,220}, {116,220},
             {115,220}, {114,220}, {113,220}, {120,216}, {119,216}, {118,216}, {117,216}, {116,216},
             {115,216}, {106,220}, {113,216}, {112,216}, {111,216}, {110,216}, {109,216}, {220},
             {107,216}, {219}, {105,216}, {218}, {103,216}, {217}, {117,208}, {216}, {115,208}, {215},
             {113,208}, {214}, {111,208}, {213}, {109,208}, {212}, {107,208}, {211}, {105,208}, {210},
             {103,208}, {209}, {101,208}, {208}, {107,204}, {207}, {105,204}, {206}, {103,204}, {205},
             {101,204}, {204}, {103,202}, {203}, {101,202}, {202}, {101, 201}, {201}, noFinish};

    /**
     * returns a finish for mode master out and three darts remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] masterOutThreeDarts(int score) {
        return masterOut[180-score];
    }

    /** holds helper values for mode master out and two darts remaining */
    private final static int[][] masterOutTD =
            {{320,320}, {319,310}, {318,316}, {320,319}, {320,116}, {319,118}, {320,318}, {319,116},
             {320,120}, {320,317}, {320,225}, {319,312}, {318,318}, {319,225}, {320,106}, {320,315},
             {318,225}, {319,106}, {320,314}, {317,225}, {320,220}, {319,314}};

    /**
     * returns a finish for mode master out and two darts remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] masterOutTwoDarts(int score) {
        int[] finish_two = masterOut[180-score];
        if(finish_two.length < 3) return finish_two;
        if(score > 98 && score < 121) return masterOutTD[120-score];
        return new int[] {finish_two[0], finish_two[1]};
    }

    /**
     * returns a finish for mode master out and one dart remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] masterOutOneDart(int score) {
        if(score == 50) return new int[]{225};
        if(score%2==0 && score/2<=20) return new int[]{200+score/2};
        if(score%3==0 && score/3<=20) return new int[]{300+score/3};
        return new int[] {masterOut[180-score][0]};
    }

    /** holds finishes for mode single out */
    private final static int[][] singleOut =
            {{320,320,320}, noFinish, noFinish, {320,320,319}, noFinish, noFinish, {320,320,318},
             noFinish, noFinish, {320,320,317}, {320,320,225},
             noFinish, {320,320,216}, {320,319,225}, noFinish, {320,320,315}, {320,318,225}, noFinish,
             {320,320,214}, {320,317,225}, {320,320,220}, {320,319,314}, {320,316,225}, {320,319,220},
             {320,320,218}, {320,315,225}, {320,318,220}, {320,319,218}, {320,320,216}, {320,317,220},
             {320,318,218}, {320,319,216}, {320,316,220}, {320,317,218}, {320,318,216}, {320,315,220},
             {320,320,212}, {320,317,216}, {320,314,220}, {320,319,212}, {320,316,216}, {320,313,220},
             {320,318,212}, {319,316,216}, {320,320,208}, {320,317,212}, {320,314,216}, {320,319,208},
             {320,316,212}, {320,313,216}, {320,318,208}, {319,316,212}, {318,314,216}, {320,317,208},
             {319,119,225}, {318,313,216}, {320,316,208}, {319,314,212}, {318,320,204}, {317,318,208},
             {320,120,220}, {319,310,216}, {318,316,208}, {320,117,220}, {320,116,220}, {319,118,220},
             {320,114,220}, {319,116,220}, {320,120,216}, {319,114,220}, {320,225}, {319,312,208},
             {319,119,216}, {319,225}, {320,106,220}, {319,116,216}, {318,225}, {319,106,220},
             {320,110,216}, {317,225}, {320,220}, {319,110,216}, {320,219}, {319,220}, {320,218},
             {319,219}, {318,220}, {319,218}, {320,216}, {317,220}, {318,218}, {319,216}, {316,220},
             {317,218}, {318,216}, {315,220}, {320,214}, {317,216}, {314,220}, {319,212}, {316,216},
             {313,220}, {318,212}, {319,210}, {320,208}, {317,212}, {314,216}, {319,208}, {316,212},
             {313,216}, {318,208}, {319,206}, {320,204}, {317,208}, {314,212}, {315,210}, {316,208},
             {313,212}, {310,216}, {315,208}, {120,220}, {119,220}, {118,220}, {117,220}, {116,220},
             {115,220}, {114,220}, {113,220}, {120,216}, {119,216}, {118,216}, {117,216}, {116,216},
             {115,216}, {106,220}, {113,216}, {112,216}, {111,216}, {110,216}, {109,216}, {220},
             {107,216}, {219}, {105,216}, {218}, {103,216}, {217}, {117,208}, {216}, {115,208}, {215},
             {113,208}, {214}, {111,208}, {213}, {109,208}, {212}, {107,208}, {211}, {105,208}, {210},
             {103,208}, {209}, {101,208}, {208}, {107,204}, {207}, {105,204}, {206}, {103,204}, {205},
             {101,204}, {204}, {103,202}, {203}, {101,202}, {202}, {101, 201}, {201}, {101}};

    /**
     * returns a finish for mode single out and three darts remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] singleOutThreeDarts(int score) {
        return singleOut[180-score];
    }

    /**
     * returns a finish for mode single out and two darts remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] singleOutTwoDarts(int score) {
        int[] finish_two = singleOut[180-score];
        if(finish_two.length < 3) return finish_two;
        if(score > 98 && score < 121) return masterOutTD[120-score];
        return new int[] {finish_two[0], finish_two[1]};
    }

    /**
     * returns a finish for mode single out and one dart remaining
     * @param score score to finish
     * @return way to finish or {@link #noFinish}
     */
    private static int[] singleOutOneDart(int score) {
        if(score == 50) return new int[]{225};
        if(score%2==0 && score/2<=20) return new int[]{200+score/2};
        if(score%3==0 && score/3<=20) return new int[]{300+score/3};
        if(score == 25 || score <= 20) return new int[]{100+score};
        return new int[] {singleOut[180-score][0]};
    }



}
