package com.neworld.youyou.bean;

import java.util.List;

/**
 * Created by user on 2017/11/9.
 */

public class ReportBean {


    /**
     * results : [{"sumCount":2,"imgs":[],"Iconimgs":"http://106.14.251.200:8082/olopicture/BUGImg/10851201711091822261510222946357.jpeg|http://106.14.251.200:8082/olopicture/BUGImg/10851201711091822271510222947079.jpeg","bugId":1149,"parent_cid":0,"from_uid":10851,"content":"小男孩被爸爸打屁股气炸了，可看到最后又被暖到哭//爸爸打了小男孩的屁屁，小男孩逻辑好，口才好，还有着非常出色的谈判技巧，不依不饶。爸爸没办法了，无奈地看着小正太，问\u201c可以给我个抱抱吗？\u201d犹豫半秒后，小男孩别别扭扭伸出胳膊环抱住了老爸，给了他一个拥抱。这就是孩子！不管他心里有多怎么生气，嘴上说着绝不原谅，可只要爸妈再走进一点点，孩子还是会无条件地爱他。即使有时候，孩子被我们伤害了、忽略了，只要你想起来，伸伸手，孩子们永远都会毫无芥蒂地跑过来","createDate":"2017-11-09 18:22:13","status":1},{"sumCount":null,"imgs":[],"bugId":1146,"parent_cid":0,"from_uid":10851,"content":"  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉","createDate":"2017-11-03 17:43:35","status":1},{"sumCount":1,"imgs":[],"Iconimgs":"http://106.14.251.200:8082/olopicture/BUGImg/10851201711011437181509518238663.png","bugId":1145,"parent_cid":0,"from_uid":10851,"content":"小男孩被爸爸打屁股气炸了，可看到最后又被暖到哭//爸爸打了小男孩的屁屁，小男孩逻辑好，口才好，还有着非常出色的谈判技巧，不依不饶。爸爸没办法了，无奈地看着小正太，问\u201c可以给我个抱抱吗？\u201d犹豫半秒后，小男孩别别扭扭伸出胳膊环抱住了老爸，给了他一个拥抱。这就是孩子！不管他心里有多怎么生气，嘴上说着绝不原谅，可只要爸妈再走进一点点，孩子还是会无条件地爱他。即使有时候，孩子被我们伤害了、忽略了，只要你想起来，伸伸手，孩子们永远都会毫无芥蒂地跑过来","createDate":"2017-11-01 14:37:17","status":1},{"sumCount":null,"imgs":[],"bugId":1143,"parent_cid":0,"from_uid":10851,"content":"小男孩被爸爸打屁股气炸了，可看到最后又被暖到哭//爸爸打了小男孩的屁屁，小男孩逻辑好，口才好，还有着非常出色的谈判技巧，不依不饶。爸爸没办法了，无奈地看着小正太，问\u201c可以给我个抱抱吗？\u201d犹豫半秒后，小男孩别别扭扭伸出胳膊环抱住了老爸，给了他一个拥抱。这就是孩子！不管他心里有多怎么生气，嘴上说着绝不原谅，可只要爸妈再走进一点点，孩子还是会无条件地爱他。即使有时候，孩子被我们伤害了、忽略了，只要你想起来，伸伸手，孩子们永远都会毫无芥蒂地跑过来","createDate":"2017-11-01 14:27:23","status":1},{"sumCount":null,"imgs":[],"bugId":1140,"parent_cid":0,"from_uid":10851,"content":"  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉  补补觉","createDate":"2017-10-31 16:27:50","status":1},{"sumCount":1,"imgs":[],"Iconimgs":"http://106.14.251.200:8082/olopicture/BUGImg/10851201710301202551509336175697.jpeg","bugId":1139,"parent_cid":0,"from_uid":10851,"content":"vbb","createDate":"2017-10-30 12:02:54","status":1},{"sumCount":1,"imgs":[],"Iconimgs":"http://106.14.251.200:8082/olopicture/BUGImg/10851201710201344371508478277138.jpeg","bugId":1134,"parent_cid":0,"from_uid":10851,"content":"  补补觉","createDate":"2017-10-20 13:44:36","status":1},{"sumCount":6,"imgs":[],"Iconimgs":"http://106.14.251.200:8082/olopicture/BUGImg/10851201710201120281508469628028.jpeg|http://106.14.251.200:8082/olopicture/BUGImg/10851201710201120291508469629149.jpeg|http://106.14.251.200:8082/olopicture/BUGImg/10851201710201120301508469630761.jpeg|http://106.14.251.200:8082/olopicture/BUGImg/10851201710201120331508469633006.jpeg|http://106.14.251.200:8082/olopicture/BUGImg/10851201710201120341508469634246.png|http://106.14.251.200:8082/olopicture/BUGImg/10851201710201120351508469635888.png","bugId":1133,"parent_cid":0,"from_uid":10851,"content":"见见狂款号","createDate":"2017-10-20 11:20:26","status":1}]
     * status : 0
     */

    public int status;
    public List<ResultsBean> results;

    public static class ResultsBean {
        /**
         * sumCount : 2
         * imgs : []
         * Iconimgs : http://106.14.251.200:8082/olopicture/BUGImg/10851201711091822261510222946357.jpeg|http://106.14.251.200:8082/olopicture/BUGImg/10851201711091822271510222947079.jpeg
         * bugId : 1149
         * parent_cid : 0
         * from_uid : 10851
         * content : 小男孩被爸爸打屁股气炸了，可看到最后又被暖到哭//爸爸打了小男孩的屁屁，小男孩逻辑好，口才好，还有着非常出色的谈判技巧，不依不饶。爸爸没办法了，无奈地看着小正太，问“可以给我个抱抱吗？”犹豫半秒后，小男孩别别扭扭伸出胳膊环抱住了老爸，给了他一个拥抱。这就是孩子！不管他心里有多怎么生气，嘴上说着绝不原谅，可只要爸妈再走进一点点，孩子还是会无条件地爱他。即使有时候，孩子被我们伤害了、忽略了，只要你想起来，伸伸手，孩子们永远都会毫无芥蒂地跑过来
         * createDate : 2017-11-09 18:22:13
         * status : 1
         */

        public int sumCount;
        public String Iconimgs;
        public String bugId;
        public int parent_cid;
        public int from_uid;
        public String content;
        public String createDate;
        public int status;
        public List<admin> imgs;

        public static class admin {
            public String content;
        }
    }
}
