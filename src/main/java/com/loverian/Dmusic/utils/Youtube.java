package com.loverian.Dmusic.utils;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.loverian.Dmusic.Config;

import java.util.List;

public class Youtube {
    private final YouTube youTube;
    public Youtube() {
        YouTube temp = null;
        try {
            temp = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null
            )
                    .setApplicationName("MusicBotJDA")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        youTube = temp;
    }
    public String ytSearch(String input) {
        try {
            List<SearchResult> results = youTube.search()
                    .list("id,snippet")
                    .setQ(input)
                    .setMaxResults(1L)
                    .setType("video")
                    .setFields("items(id/kind,id/videoId,snippet/title)")
                    .setKey(Config.get("YTKEY"))
                    .execute()
                    .getItems();

            if (!results.isEmpty()) {
                String videoId = results.get(0).getId().getVideoId();
                System.out.println("Api search");

                return "https://www.youtube.com/watch?v=" + videoId;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
