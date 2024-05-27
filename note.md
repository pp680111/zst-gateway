* 基于SpringMVC实现gateway时，为了避免Controller层影响网关接口的逻辑，可以在filter层就开始拦截Request，然后判断是否需要执行网关的逻辑
* 了解一下WebFlux的RouterFunction
* 写一个RouterFunction，拦截请求，中转到对应的服务(ok)
* loadBalancer、服务节点过滤等操作需要链路化优化(ok)
* 集成注册中心(ok)


TODO
* 集成配置中心
* 思考一下有什么更加优雅的方式注册RouterFunction（编程式的），现在这种用Bean来声明RouterFunction的，总感觉使用起来不是很便利（把GatewayEntranceRouter挪到discovery包的代码中进行手动声明）
* zstRegistry包中的异常名称需要更正一下
* 优化一下错误响应处理逻辑
* 设计一套前后过滤器，提供数据过滤转换功能（比如路径重写之类的）