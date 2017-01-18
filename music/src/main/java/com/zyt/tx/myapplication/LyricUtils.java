package com.zyt.tx.myapplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by MJS on 2017/1/18.
 */

public class LyricUtils {

    public static LyricInfo setupLyricResource(InputStream inputStream, String charSet) {
        if (inputStream != null) {
            try {
                LyricInfo lyricInfo = new LyricInfo();

                lyricInfo.songLines = new ArrayList<>();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charSet);

                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    analyzeLyric(lyricInfo, line);
                }

                reader.close();
                inputStream.close();
                inputStreamReader.close();

                return lyricInfo;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private static void analyzeLyric(LyricInfo lyricInfo, String line) {
        if (line == null) {
            return;
        }
        int index = line.lastIndexOf("]");
        if (line.startsWith("[offset:")) {
            String string = line.substring(8, index).trim();
            lyricInfo.offset = Long.parseLong(string);
            return;
        }
        if (line.startsWith("[ti:")) {
            lyricInfo.song_title = line.substring(4, index).trim();;
            return;
        }
        if (line.startsWith("[ar:")) {
            lyricInfo.song_artist = line.substring(4, index).trim();
            return;
        }
        if (line.startsWith("[al:")) {
            lyricInfo.song_album = line.substring(4, index).trim();
            return;
        }
        if (index == 9 && line.trim().length() > 10) {
            LineInfo lineInfo = new LineInfo();
            lineInfo.content = line.substring(10, line.length());
            lineInfo.start = parseStartTimeMillis(line.substring(0, 10));
            lyricInfo.songLines.add(lineInfo);
        }
    }

    private static long parseStartTimeMillis(String str) {
        long minute = Long.parseLong(str.substring(1, 3));
        long second = Long.parseLong(str.substring(4, 6));
        long millisecond = Long.parseLong(str.substring(7, 9));
        return millisecond + second * 1000 + minute * 60 * 1000;
    }
}
