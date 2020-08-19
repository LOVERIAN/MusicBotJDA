package com.loverian.Dmusic.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormatUtil.class);


    public static String formatTime(long duration) {
        if (duration == Long.MAX_VALUE)
            return "LIVE";
        long seconds = Math.round(duration / 1000.0);
        long hours = seconds / (60 * 60);
        seconds %= 60 * 60;
        long minutes = seconds / 60;
        seconds %= 60;
        return (hours > 0 ? hours + ":" : "") + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public static String extractYTId(String uri) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(uri); //url is youtube url for which you want to extract the id.
        if (matcher.find()) {
            return ("https://img.youtube.com/vi/" +  matcher.group() + "/sddefault.jpg");
        }

        return ("https://img.youtube.com/vi/wf5w-rxstHM/sddefault.jpg");
    }

    public static String progressBar(double percent)
    {
        StringBuilder str = new StringBuilder();
        for(int i=0; i<12; i++)
            if(i == (int)(percent*12))
                str.append("\uD83D\uDD18"); // 🔘
            else
                str.append("\u25AC");
        return str.toString();
    }

    public static String extractYahoo(String q) {
        q = q.replaceAll(" ", "+");
        LOGGER.info("Using yahoo search" + q);
        String url = "https://in.video.search.yahoo.com/search/video;_ylt=AwrPhx5wbiBfQUoAAiXmHAx.?" +
                "fr=sfp&fr2=sb-top-in.video.search.yahoo.com&ei=UTF-8&p=" + q + "&vsite=youtube";

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .get();

            System.out.println(doc.title());
            Element links = doc.select("a[data-rurl]").first();
            if(links == null){ return null;}
            return links.attr("data-rurl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

