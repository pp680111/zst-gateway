* 基于SpringMVC实现gateway时，为了避免Controller层影响网关接口的逻辑，可以在filter层就开始拦截Request，然后判断是否需要执行网关的逻辑
* 了解一下WebFlux的RouterFunction

TODO
* 写一个RouterFunction，拦截请求，中转到对应的服务
* 集成配置中心
* 集成注册中心