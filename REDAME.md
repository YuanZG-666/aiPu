## REDAME



本项目基于SpringBoot+Vue2完成，功能实现主要使用Vue来完成，同时实现前后端的互通。

### **项目启动：**

在IDEA配置前端启动，选择Edit Configurations，添加npm serve，如下如所示：



然后启动前端，当然，也可以在项目根目录views/vue文件夹下，通过“npm run serve”，前提都必须安装Node.JS。

前端启动后，后端在/src/main/java/SpringBootApplication.java运行，无报错，代表启动成功。



一般的端口都是8080，我们在router/index.js配置路由，默认为http://localhost:8080,即http://localhost:8080/home，配置默认根目录/重定向到/home。

进入主界面后我们选择上传一张图片，在右边画板会显示预览图片，目前支持两个功能：高清图片下载与PDF打印或者识别。如下图所示：

在预览图左侧可以手动调节图纸宽度与最多使用颜色数。图纸宽度越多，细节更多，图像帧数越高。最多使用颜色越多，越接近与原图。





















































