### 官方cas认证流程
![avatar](https://apereo.github.io/cas/5.3.x/images/cas_flow_diagram.png)

流程分析:

- *用户第一次访问app1*
> 1. 用户通过浏览器访问app1
> 2. app1校验本地会话,校验失败则重定向到cas server
> 3. cas server进行cas会话校验,校验失败则返回登录页面
> 4. 用户提交登录信息,cas server校验通过后,生成TGC(Ticket-Granting Cookie):cas会话cookie,它里面存储TGT(Ticket Granting Ticket),TGT是cas实际进行会话保持的标识;生成ST(Service Ticket),用于授权浏览器访问app1;重定向到app1;
> 5. app1请求cas server校验ST,成功则创建本地会话;提供用户服务;

- *用户第一次访问app2*
> 1. 用户通过浏览器访问app2
> 2. app2重定向到cas server进行会话校验,校验TGT,校验通过返回ST,重定向到app2
> 3. app2请求cas server校验ST,通过则可以创建本地会话,提供用户服务

### cas的rest协议

##### 1. 获取TGT
- 请求方式,路径,http协议及请求参数:
> POST /cas/v1/tickets HTTP/1.0   
> username=battags&password=password&additionalParam1=paramvalue

- 请求响应
> 201 Created
> Location: http://www.whatever.com/cas/v1/tickets/{TGT id}

--------------------------------------
##### 2. 获取ST
- 请求方式,路径,http协议及请求参数:
> POST /cas/v1/tickets/{TGT id} HTTP/1.0   
> service={form encoded parameter for the service url}

- 请求响应
> 200 OK
> ST-1-FFDFHDSJKHSDFJKSDHFJKRUEYREWUIFSD2132

##### 3. 校验ST
> 请求方式及请求路径
> GET /cas/p3/serviceValidate?service={service url}&ticket={service ticket}

- 请求响应
> 状态码,200成功;200请求失败;415不支持的媒体类型;

##### 4. 登出
> 请求方式,请求路径和http协议
> DELETE /cas/v1/tickets/{TGT} HTTP/1.0

- 请求响应
> 返回注销的TGT