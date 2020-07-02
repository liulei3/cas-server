### 项目介绍:

#### 目录介绍:
###### src/main/java:是项目代码目录,目录结构如下:
- conf:项目配置目录
- domain:认证实体
- handler:认证处理类
- util:工具包
- controller:处理器定义
- exception: 自定义异常
###### src/main/resources:项目资源目录
- META-INF:启动项目自定义配置文件
- services:服务注册配置
- **templates**:项目页面模板
- **cas-theme-default.properties**:配置页面css,js等
- **static**:静态文件路径
- webflow:spring webflow流程定义
###### maven:maven打包依赖包
###### target:maven项目打包路径,需要使用mvn package命令生成

> 项目采用cas war overlay方式部署,target目录默认生成项目模板,
> 自定义内容只要将其放置在src/main/resources中复制修改target中文件即可.
> 参考资料:
> **css,js文件介绍及配置**:https://apereo.github.io/cas/development/ux/User-Interface-Customization-CSSJS.html

--------------------------------------------
### 项目依赖
因为是Java项目,所以需要下载jdk(Java Developer`s Kit Java开发工具包)和maven打包工具;

- jdk下载部署
> 1. 下载路径:http://download.oracle.com/otn-pub/java/jdk/8u191-b12/2787e4a523244c269598db4e85c51e0c/jdk-8u191-windows-x64.exe
> 2. jdk部署方式:按步骤安装->配置环境变量->打开命令行执行命令 java -version,成功则是显示版本信息
> 3. 参考资料:https://www.cnblogs.com/iwin12021/p/6057890.html

- maven下载部署
> 1. 下载路径:http://mirror.bit.edu.cn/apache/maven/maven-3/3.5.4/binaries/apache-maven-3.5.4-bin.zip
> 2. maven部署方式:解压zip包->配置环境变量->打开命令行执行命令 mvn -v,成功则是显示版本信息
> 3. 参考资料:https://www.cnblogs.com/LexMoon/p/JavaMaven.html

- 项目启动方式:
> 1. 在项目根路径打开cmd命令行  
> 2. 执行maven打包命令: mvn clean package   
> 3. 运行cas服务命令:java -jar cas.war
> 4. 访问:https://localhost:8443/cas

- **登录密码**
> 用户名:cuijian 密码:0

- 注册成功用户名数据回显
> 访问路径:https://localhost:8443/cas/login?username=xxx
> 通过username参数获取已注册用户名

### 版本控制
项目为了减少配置文件数量,通过maven的profile方式进行版本控制.
使用方式:
> 1 在pom.xml中定义对应的版本参数,为降低维护成本,推荐使用三个版本:master(生产),dev(开发),test(测试),目前只配置了master,dev;版本衍生顺序:master->dev->dev;代码发布顺序:test->dev->master
> 2 在application.properties中引入对应的参数;
> 3 项目中通过@Value引入application.properties参数进行使用;
