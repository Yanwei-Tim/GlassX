1、命名规则
    全局变量以m开头，采用驼峰命名规则
    局部变量首字母小写，采用驼峰命名规则

2、尽量多使用组合方式，少使用继承方式

3、glass launcher 运行于眼镜端，主要功能是获取眼镜端的摄像头，麦克风等音视频数据,tcp server

4、glass manager 运行于手机端，主要控制眼镜端，大型的运算功能需要在手机端进行,tcp client

5、glass core 是一个library，包括netty tcp socket连接，usb otg的连接，
    以及基于这两种连接上的与通信接口，这个最终会变成一个aar的包，供第三方使用

6、glass core 设计原则
    a) 尽量采用单例模式
    b) 其余项目调用接口只需要传入接口调用类型
    c) 所有的逻辑控制在glass core 内部完成

7、功能分布
    a)voice service 运行于眼镜端
    b)设置  wifi连接，通过二维码扫描方式连接，眼镜管家生成二维码
    c)首页上显示语音命令词，Martin，Camera，Setting
    d)Camera 在眼镜端是一个独立功能，可以拍照，录像，存储在本地，但是同时需要将数据传递到手机端显示。
        手机端可以按拍照，录像的按键给眼镜端发送命令，眼镜端也可以直接语音命令进行拍照录像操作
    e)Martin直接使用服务器端来进行识别


8、tcp server / tcp client 连接逻辑
   a) glass server Launcher启动的时候就启动server服务监听 5081,5082,5083
   b) glass client 首先进行局域网扫描，然后依次连接，如果能够连上，就会获得服务端的IP，SN等信息。
   c) glass client 列出局域网内有效的server 地址，然后由用户选择连接
   d)
   e)
   f)
   g)
   h)
   i)
   j)
   k)
   l)
   m)
   n)
   o)
   p)
   q)