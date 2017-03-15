package com.example.lyrictest;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by chen on 2017/3/14.
 * 解析歌词工具类:
 * 功能: 根据路径,将歌词文件中的内容解析到 Lyric 中
 */

public class LrcUtils {


    public Lyric lyric;
    private boolean isLyricExist; //歌词文件是否存在


    public void readLyricFile(File file) {
        if (file == null || !file.exists()) {
            isLyricExist = false;
            //lyrics = null;

            lyric = null;
        } else {//歌词文件存在--解析歌词文件
            //lyrics = new ArrayList<>();
            isLyricExist = true;

            lyric = new Lyric();

            BufferedReader breader = null;

            try {
                // br = new BufferedReader(new InputStreamReader(new FileInputStream(file),getCharset(file)));

                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, getCharset(file));
                breader = new BufferedReader(isr);

                String line = "";
                while ((line = breader.readLine()) != null) {//读取歌词文件中一行数据
                    line = parseLyricLine(line);
                }


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    /**
     * 解析从buffer中读取的某一行数据
     *
     * @param line
     * @return
     */
    private String parseLyricLine(String line) {
        // 例如: [00:31.16]当我走在这里的每一条街道

        if (line.contains("]") && line.contains("[")) {
            //判断是否包含
             /* [ti:北京北京]
                [ar:汪峰]
                [al:勇敢的心]
                [by:(5nd音乐网)www.5nd.com]
            */
            if (line.contains("ti") && line.contains(":")) {
                String title = line.substring(line.indexOf(":") + 1, line.indexOf("]"));
                lyric.setTitle(title);
            } else if (line.contains("ar") && line.contains(":")) {
                String artist = line.substring(line.indexOf(":") + 1, line.indexOf("]"));
                lyric.setArtist(artist);
            } else if (line.contains("al") && line.contains(":")) {
                String al = line.substring(line.indexOf(":") + 1, line.indexOf("]"));
                lyric.setAl(al);
            } else if (line.contains("by") && line.contains(":")) {
                String by = line.substring(line.indexOf(":") + 1, line.indexOf("]"));
                lyric.setBy(by);
            } else {//是歌词行

                //1. 从buffer读取的行中 提取歌词内容
                String content = line.substring(line.lastIndexOf("]") + 1);///得到  当我走在这里的每一条街道

                //2. 从buffer读取的行中 提取时间戳
                String sTime = "";
                sTime = line.substring(0, line.lastIndexOf("]") + 1);//[00:31.16][00:31.16]
                while (sTime.contains("[") && sTime.contains("]")) { //00:31.16][00:31.16

                    String tempTime = sTime.substring(1, sTime.indexOf("]"));

                    long time = stringTime2Longtime(tempTime);


                    //将 时间戳--歌词内容 保存到 lyrics的列表中

                    Lyric.LineLyric lineLyric = new Lyric.LineLyric();
                    lineLyric.setTimePoint(time);
                    lineLyric.setContent(content);

                    lyric.getLineLyrics().add(lineLyric);

                    //解析一组时间戳后 更新sTime [00:31.16][00:31.16] -->[00:31.16]
                    sTime = sTime.substring(sTime.indexOf("]") + 1);
                }

            }
        }

        return null;

    }

    private long stringTime2Longtime(String stringTime) {// 00:31.16

        long result = -1;

        if (stringTime.contains(":") && stringTime.contains(".")) {


            String minStr = stringTime.substring(0, stringTime.indexOf(":") - 1); // 00:31.16-->00

            String secStr = stringTime.substring(stringTime.indexOf(":") + 1, stringTime.indexOf(".") - 1);

            String milStr = stringTime.substring(stringTime.indexOf(".") + 1);

            result = Long.parseLong(milStr) * 60 * 1000 + Long.parseLong(secStr) * 1000 + Long.parseLong(milStr) * 10;

        }

        return result;
    }


    /**
     * 获取文件编码
     *
     * @param file
     * @return
     */
    private String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }


}
