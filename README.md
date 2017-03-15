影音播放器




功能说明:
>1. 播放本地视频
    1)系统解码
    2)vitamio万能解码
2. 播放本地音乐
    1) 通过service 实现音乐后台播放
    2) 切换播放模式
    3) 后台播放时显示通知栏
3. 播放网络视频
    获取并播放网络视频文件
4. 播放网络音乐



### 本地视频播放器
- - -
>说明: 扫描sd卡视屏文件,生成视频列表


### 本地音乐播放器
- - -
>说明: 扫描sd卡音频文件,生成音乐列表列表

#### 01加载本地音乐列表
#### 02设置音乐播放器
#### 03控制面板
#### 04音乐播放启用服务
#### 05控制音乐播放暂停
#### 06通过广播跟新信息
#### 07时间和播放进度
#### 08任务栏显示音乐播放
#### 09 解决bug
#### 10通知栏多次点击bug
#### 11设置播放模式
#### 12上一首下一首
#### 13歌词显示
1. 创建歌词文件对应的bean 对象
2. 创建utils 实现从歌词文件中读取歌词
3. 创建显示歌词的textview
4. 播放器activity : AudioPlayerActivity.java 中发送handler消息, 更新歌词

>4.1 handler代码:

```
    case UPDATE_LYRIC://跟新显示歌词
                        try {
                            if (isNewLyric) {//需要更新歌词地址
                                //歌曲路径: "/storage/sdcard0/刘惜君 - 悠蓝曲.mp3"
                                //storage/sdcard0/刘惜君 - 悠蓝曲.+.lrc (.txt)
                                String audioPath = service.getAudioPath();
                                String path = audioPath.substring(0,audioPath.lastIndexOf("."));
                                String lyricPath = path+".lrc";
                                tvMusicLyrics.setLyricUrl(lyricPath);

                                isNewLyric = false;
                            }

                            //1. 获取当前进度
                            int timePoint = service.getCurrentProgress();//得到当前时间
                            //2. 将进度传入ShowLyricView.java-->index
                            tvMusicLyrics.setTimePoint(timePoint);//设置时间-->index

                            //3. 发送消息
                            handler.removeMessages(UPDATE_LYRIC);
                            handler.sendEmptyMessage(UPDATE_LYRIC);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
```

>4.2 需要发送handler消息更新歌词的情况
          1) 当播放完一首歌曲,进行下一曲播放时   歌曲准备完成广播:---->onrecevier() 中发送handler消息更新进度
          2) 当从通知栏进入播放界面时         播放器service连接:----->onServiceConnecteddfaf

5 实现歌词平滑移动

```
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            //当歌词不为空时, 绘制歌词
            if (lyric != null) {
                //根据歌词显示时间,平移画布,实现歌词平滑移动
                /**
                 * 原理:
                 * 1.  当前句已经显示时间 /显示时间 =  平移高度 / 行的高度
                 *  --> pushTime /showTime = push/textHight
                 *  --> pushTime == (current - lyric.getLineLyrics().get(index).getTimePoint());
                 */
                float pushHight;
                if(showTime==0) {
                    pushHight = 0;
                }else {
                    pushHight= (pushTime/showTime)*textHight+textHight ;
                }
                canvas.translate(0,-pushHight);
                //1. 绘制当前行歌词
                String lineContent = lyric.getLineLyrics().get(index).getContent();//index 对应行的歌词内容
                canvas.drawText(lineContent, width / 2, hight / 2, paint);
                //2. 绘制当前行之前歌词
                float tempY = hight / 2;
                for (int i = index - 1; i > 0; i--) {
                    String preContent = lyric.getLineLyrics().get(i).getContent();
                    tempY = tempY - textHight-textHight/2; //textHight/4 为设置的行间距
                    if (tempY < 0) {
                        break;
                    }
                    canvas.drawText(preContent, width / 2, tempY, whitePaint);
                }

                //2. 绘制之后行
                tempY = hight / 2;
                for (int i = index + 1; i < lyric.getLineLyrics().size(); i++) {
                    String preContent = lyric.getLineLyrics().get(i).getContent();
                    tempY = tempY + textHight+textHight/2;
                    if (tempY > hight) {
                        break;
                    }
                    canvas.drawText(preContent, width / 2, tempY, whitePaint);
                }
            } else {
                canvas.drawText("没有歌词", width / 2, hight / 2, paint);
            }
        }

```

6 判断文件编码:

```

    /**
     * 获取文件编码
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
```


