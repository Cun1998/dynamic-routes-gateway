
基于网关的灰度发布

一、需求概述

灰度发布（又名金丝雀发布）是指在黑与白之间，能够平滑过渡的一种发布方式。在其上可以进行A/B testing，即让一部分用户继续用产品特性A，一部分用户开始用产品特性B，如果用户对B没有什么反对意见，那么逐步扩大范围，把所有用户都迁移到B上面来

部分项目上线后首先会进行灰度发布，仅供部分人员使用，由网关路由到不同的实例服务，实现用户默认跳转不同业务系统的功能

二、功能设计

2.1 动态配置

可以基于内存缓存/Nacos/redis/MySQL等对网关进行动态配置

内存缓存：效率高，但操作相对不方便

Nacos：符合现有架构，但是要注意Nacos动态配置刷新时间

redis：需要外部依赖，可选

mysql：性能太差


2.2 路由规则

通过实现ServiceInstanceListSupplier来自定义服务筛选逻辑

2.2.1 基于用户账号路由

需要转发的接口请求中能获取到用户信息

企业微信扫码登录无法获取用户账号

根据用户登录时，拦截用户账号/token等信息，与动态路由配置信息对比，获取该用户账号对应访问实例版本，进行指定实例路由

2.2.2 基于ip/ip段路由

获取用户ip，查询用户ip是否属于配置信息内某个指定实例可访问的ip段，对该ip段内的用户指定实例进行路由

2.2.3 基于请求头参数路由

可指定用户访问http请求头，新增访问实例版本信息，与实例注册版本进行匹配路由

2.3 服务注册

服务注册是可在application.yaml文件或nacos控制台中指定元数据spring.cloud.naocs.discovery.metadata新增version=版本号

例如 oa-data-producer服务注册nacos时

spring:
application:
name: oa-data-producer
profiles:
active: @profile.name@
cloud:
nacos:
discovery:
metadata:
version: 1.0

三、系统设计

系统架构图



类图设计



四、配置文件

4.1 配置实例
route.json

[
{
"instanceName":"casbin-demo",
"routes": [
{
"version":"v1",
"user":[
"JH050169"
],
"ip":[
"192.168.7.1-192.168.7.255"
],
"header":[
"*"
]
},
{
"version":"v2",
"user":[
"*"
],
"ip":[
"192.168.116.248"
],
"header":[
"version=2.0","custom-header=headerValue"
]
}
]
}
]

4.2 配置详解

● instanceName：即需要被灰度发布的实例名称，只有存在配置的实例才会被匹配路由（暂只适用于lb://instanceName）
● version：为对应服务实例注册到nacos上的metadata.version=xx
● user：用户账号（暂定请求header上携带current-user参数，current-user=JH050169）
● ip：用户ip，//IP段用 "-"分隔
● header：用户请求携带headers

某一请求必须满足该实例的所有配置要求才会被选择路由到该实例上，例如上述v1实例，必须同时满足用户为JH050169，且ip在192.168.7.0-192.168.7.254内，header任意
请求会被优先路由到能够精确匹配一项或一项以上配置的实例
当所有实例配置都不满足该请求时，会路由当前请求到所有配置都为 "*"的实例上，如没有所有配置都为 "*"的实例，则拒绝该请求


五、待完成任务

5.1 多配置源动态切换

暂时只支持nacos配置文件动态配置，后续可以集成redis作为配置中心等

5.2 路由规则扩展

继承即可扩展新路由规则


六、存在问题

1.用户账号匹配问题

分析现有统一认证系统，可以拦截账号密码登录接口，实现部分用户试用门户，但企业微信扫码登录无法获取用户信息，即无法使用用户信息进行匹配路由转发

2.企业微信扫码登录

由于企业微信扫码登录使用webSokcet，多版本实例运行下，存在webSocket无法进行多实例间共享问题，需要对同一ws请求进行定向转发

3.如何获取用户信息

由于在ServiceInstanceListSupplier中无法拦截body参数，需要前端在相应接口携带用户账号信息（url参数或header参数）

4.与旧网关兼容问题

此功能为开发人员自行搭建开发，与旧网关（jeecg-boot gateway 2.4.6版本）不兼容，缺少hystix功能
