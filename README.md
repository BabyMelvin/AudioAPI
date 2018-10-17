# 音频播放
## 支持的音频格式
> Android支持多种播放的音频文件格式和编解码器

* `AAC`:高级音频编码（Adavanced Audio Coding）编解码器（HEAAC高效AAC）的两个配置文件,对应`.m4a`(`audio/m4a`)或`.3gp`（`audio/3gpp`）文件。Android 在MPEG-4和3GP文件中
* `MP3`：MPEG-1 Audio Layer3(MPEG-1 音频层3)，对应是`.mp3`(`audio/mp3`)文件。支持最广泛的文件格式
* `ARM`:自适应多速率编解码器(Adaptive Multi-Rate)包含AMR窄带AMR-NB和AMR宽带AMR-WB,对应是`.3gp`(`audio/3gpp`)或`.amr`(`audio/amr`)文件。
* `Ogg`:Ogg Vorbis,对应是`.ogg`(`application/ogg`)文件。开源无专利权限音频编解码器。
* `PCM`:脉冲编码调制(Pulse Code Modulation)通常用于WAVE或者WAV文件(Waveform Audio Format,波形音频格式)，对应的是`.wav`（`audio/x-wav`）文件。

PCM是用于计算机和其他数字设备上存储音频技术，它通常是一个未压缩的音频文件，其数据标书已选音频随时间而变化的振幅。

* 采样率:表示存储振幅附属的频率
* 位深:表示一个单独采样所需的位数

采样率为16kHz,位深为32位的一段音频数据意味着它以32位数据表示音频振幅，而且每秒钟包含160000个这样的数据。
## MIME
> 代表多用途Internet拓展，最初用在邮件中,现在用在HTTP或Web服务中。

每种文件类型都有一个特定MIME类型，分为两个部分，以`/`分开；
如 第一部分**更通用**，第二部分**更具体**。
`audio/mpeg`,这个通常用于MP3文件的MIME类型。
### res文件扩展名
在res文件夹下，文件扩展名忽略。
### 资源文件的Uri
资源文件ID，不适合所有的目的。我们可以对一个已存在的资源文件构建Uri，构建需要以`android.resource://`开头，后面加包名，再接着文件的资源ID。
# 网络音频
> 研究基于Web的音频或通过Http传输音频
### HTTP音频播放

1.直接访问服务器音频文件

`http://www.mobvcating.com/android/audio/fade.mp3`

2.HTTP流式音频
> 在线音频常用的在线传输方式之一是通过HTTP流。

有多种方法属于HTTP流方法分支：
* 服务器推送：历史上一直用于浏览器中不断地刷新网路摄像头图像进行显示。
* Apple、Adobe和Microsoft等公司提出新方法，它们各自媒体播放应用程序。
* NullSoft创建的在线音频流服务器，称为SHOUTcast.SHOUTcast使用ICY协议，其扩展HTTP协议。

Internet广播电台通常并不公布它们音频流URL。这么做的好处：

1.浏览器不能直接支持ICY流，而是需要一个辅助应用程序或者插件来播放流。

为了知道要打开一个辅助应用程序，Internet广播会传递一个特定MIME类型中间件，其包含一个指向在线流的指针。

在使用ICY流的情况下，这通常是一个PLS文件或一个M3U文件。

* PLS文件是一个多媒体播放列表文件，其MIME类型是`audio/x-scpls`
* M3U文件也是一个存储多媒体播放列表的文件，但是采用一种更基本的格式，其MIME类型为`audio/x-mpegurl`

例如：M3U文件内容,其指向一个虚假的在线流

```
#EXTM3U
#EXTINF:0,live Stream Name
http://www.nostreanhere.org:8000/
其中可以有多个
#EXTINF:0,Other Live Stream Name
http://www.nosteamhere.org/
```	
其中，第一行的`#EXTM3U`是必需的，指向下面一个扩展的M3U文件，其中包含额外信息。可以播放列表条目的上一行指定额外信息，`#EXTINF:`开始，随后是以秒为单位的持续时间和逗号，然后是媒体的名称。
















