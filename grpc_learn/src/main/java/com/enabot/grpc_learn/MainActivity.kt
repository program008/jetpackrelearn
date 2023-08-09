package com.enabot.grpc_learn

import com.enabot.grpc_learn.databinding.ActivityMainBinding
import com.enabot.mylibrary.BaseActivity
import com.enabot.mylibrary.utils.log
import com.enabot.mylibrary.utils.setOnUnFastClickListener
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.examples.helloworld.GreeterGrpc
import io.grpc.examples.helloworld.HelloReply
import io.grpc.examples.helloworld.HelloRequest
import io.grpc.examples.simple.HeartbeatGrpc
import io.grpc.examples.simple.Reply
import io.grpc.examples.simple.Request
import io.grpc.examples.simple.SimpleProto
import io.grpc.netty.NettyServerBuilder
import io.grpc.stub.StreamObserver

/**
 * GRPC四种通讯模式
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {
    val PORT = 56333
    val NAME = "hello world"
    val HOST = "localhost"
    var mChannel: ManagedChannel? = null
    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        log("start")
        startServer(PORT)
        log("start server.")
        startClient(HOST, PORT, NAME)
        log("start client.")
        // 简单一元模式
        viewBinding.btnSimpleUnaryMode.setOnUnFastClickListener {
            log("simple hello")
            simpleHello()
        }
        //客户端流模式
        viewBinding.btnClientStreamMode.setOnUnFastClickListener {
            clientStream()
        }
        //服务端流模式
        viewBinding.btnServerStreamMode.setOnUnFastClickListener {
            serverStream()
        }
        //双向流模式
        viewBinding.btnBothwayStreamMode.setOnUnFastClickListener {
            bothFlowStream()
        }
    }

    fun startServer(port: Int) {
        try {
            NettyServerBuilder.forPort(port)
                .addService(GreeterImpl())
                .addService(HeartbeatImpl())
                .build()
                .start()
        } catch (e: Exception) {
            e.printStackTrace()
            log("${e.message}")
        }
    }

    fun startClient(host: String, port: Int, name: String) {
        mChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
        val stub = GreeterGrpc.newStub(mChannel)
        val message: HelloRequest = HelloRequest.newBuilder().setName(name).build()
        stub.sayHello(message, object : StreamObserver<HelloReply> {
            override fun onNext(value: HelloReply) {
                log("onNext:" + value.message)
            }

            override fun onError(t: Throwable?) {
                log("sayHello onError.")
            }

            override fun onCompleted() {
                log("sayHello onCompleted.")
            }
        })
    }

    /**
     * 1.简单一元模式
     * 所谓简单一元模式，实际上就是客户端和服务端进行一问一答的通信。
     * 应用场景有无线设备之间和客户端之间保持连接的心跳检测，每隔一段时间就给服务端发送一个心跳检测包，
     * 服务端接收到心跳包后就知道相应客户端处于连接状态。
     *
     * <img src="https://img-blog.csdnimg.cn/20f305570b144f478e4b69701831f060.png" alt=""/>
     *
     */
    fun simpleHello() {
        val request = Request.newBuilder().setRepInfo("simpleHello").build()
        val stub = HeartbeatGrpc.newStub(mChannel)
        stub.sayHi(request, object : StreamObserver<Reply> {
            override fun onNext(value: Reply) {
                log("simpleHello onNext.")
                val info = "[服务端->客户端]" + value.repInfo
                log(info)
            }

            override fun onError(t: Throwable) {
                log("simpleHello onError.")
            }

            override fun onCompleted() {
                log("simpleHello onCompleted.")
            }

        })
    }

    /**
     *
     * 2.客户端流模式
     * 客户端流模式的意思就是客户端可以一次性发送多个数据片段，当然数据片段是一个类，具体的类有哪些字段都是你在最开始的proto文件中进行指定的。
     * 这种模式的应用场景就比如客户端向服务端发送一连串的数据，然后服务端最后发送一个响应数据表示接收成功。
     * <img src="https://img-blog.csdnimg.cn/23a90a149e5240e4ad6471cd41d15805.png" alt="">
     *
     * 在客户端流模式中，客户端可以在onCompleted之前使用多个onNext进行数据发送
     */
    fun clientStream() {
        val stub = HeartbeatGrpc.newStub(mChannel)
        val requestStreamObserver = stub.clientStream(object : StreamObserver<Reply> {
            override fun onNext(value: Reply) {
                log("client stream onNext.")
                val info = "[服务端->客户端]" + value.repInfo
                log(info)
            }

            override fun onError(t: Throwable) {
                log("client stream onError")
            }

            override fun onCompleted() {
                log("client stream onCompleted.")
            }
        })

        requestStreamObserver.onNext(Request.newBuilder().setRepInfo("clientStream1").build())
        requestStreamObserver.onNext(Request.newBuilder().setRepInfo("clientStream2").build())
        requestStreamObserver.onCompleted()
    }

    /**
     * 3.服务端流模式
     * 服务端流模式和客户端流模式正好相反，应用场景有客户端发送一个数据包告诉服务端，我需要某某数据，
     * 然后服务器将对应的所有信息都发送给客户端。
     * ![](https://img-blog.csdnimg.cn/6e40766d5cb847138fdafb436964c9d8.png)
     */
    fun serverStream() {
        val stub = HeartbeatGrpc.newStub(mChannel)
        val request = Request.newBuilder().setRepInfo("serverStream").build()
        stub.serverStream(request, object : StreamObserver<Reply> {
            override fun onNext(value: Reply?) {
                log("serverStream onNext.")
                val info = "[服务端->客户端]" + value?.repInfo
                log(info)
            }

            override fun onError(t: Throwable?) {
                log("serverStream onError")
            }

            override fun onCompleted() {
                log("serverStream onCompleted.")
            }
        })
    }

    /**
     * 4.双向流模式
     * 双向流模式是最后一种，也是最常用的一种，在这种模式中，客户端和服务端的通信没有什么限制，是比较理想的通信模式，
     * 应用场景也最为广泛，因为在这种模式中，你也可以只发送一个数据包。
     * ![](https://img-blog.csdnimg.cn/5931b855498245808a8f1b88080b56a0.png)
     */
    fun bothFlowStream() {
        val stub = HeartbeatGrpc.newStub(mChannel)
        val streamObserver = stub.bothStream(object : StreamObserver<Reply> {
            override fun onNext(value: Reply?) {
                log("client bothFlowStream onNext.")
                val info = "【服务端->客户端】" + value?.repInfo
                log(info)
            }

            override fun onError(t: Throwable?) {
                log("client bothFlowStream onError.")
            }

            override fun onCompleted() {
                log("client bothFlowStream onCompleted.")
            }
        })
        streamObserver.onNext(Request.newBuilder().setRepInfo("client bothFlowStream1").build())
        streamObserver.onNext(Request.newBuilder().setRepInfo("client bothFlowStream2").build())
        streamObserver.onCompleted()
    }

    class GreeterImpl : GreeterGrpc.GreeterImplBase() {
        override fun sayHello(request: HelloRequest, responseObserver: StreamObserver<HelloReply>) {
            responseObserver.onNext(sayHello(request))
            responseObserver.onCompleted()
        }

        private fun sayHello(request: HelloRequest): HelloReply {
            return HelloReply.newBuilder().setMessage(request.name).build()
        }
    }

    class HeartbeatImpl : HeartbeatGrpc.HeartbeatImplBase() {
        override fun sayHi(request: Request, responseObserver: StreamObserver<Reply>) {
            log("服务端调用sayHi")
            val info = "[客户端->服务端]" + request.repInfo
            log(info)
            responseObserver.onNext(Reply.newBuilder().setRepInfo("simpleHello").build())
            responseObserver.onCompleted()
            //super.sayHi(request, responseObserver)
        }

        override fun clientStream(responseObserver: StreamObserver<Reply>?): StreamObserver<Request> {
            val streamObserver: StreamObserver<Request> = object : StreamObserver<Request> {
                override fun onNext(value: Request?) {
                    log("server clientStream onNext.")
                    val info = "[客户端->服务端]" + value?.repInfo
                    log(info)
                }

                override fun onError(t: Throwable?) {
                    log("server clientStream onError.")
                }

                override fun onCompleted() {
                    log("server clientStream onCompleted.")
                    responseObserver?.onNext(Reply.newBuilder().setRepInfo("clientStream").build())
                    responseObserver?.onCompleted()
                }

            }
            return streamObserver
        }

        override fun serverStream(request: Request?, responseObserver: StreamObserver<Reply>?) {
            val info = "【客户端->服务端】" + request?.repInfo
            log(info)
            responseObserver?.onNext(Reply.newBuilder().setRepInfo("serverStream1").build())
            responseObserver?.onNext(Reply.newBuilder().setRepInfo("serverStream2").build())
            responseObserver?.onCompleted()

        }

        override fun bothStream(responseObserver: StreamObserver<Reply>?): StreamObserver<Request> {
            val streamObserver = object : StreamObserver<Request> {
                override fun onNext(value: Request?) {
                    log("server bothFlowStream onNext.")
                    val info = "[客户端->服务端]" + value?.repInfo
                    log(info)
                }

                override fun onError(t: Throwable?) {
                    log("bothFlowStream onError.")
                }

                override fun onCompleted() {
                    responseObserver?.onNext(
                        Reply.newBuilder().setRepInfo("server bothFlowStream1").build()
                    )
                    responseObserver?.onNext(
                        Reply.newBuilder().setRepInfo("server bothFlowStream2").build()
                    )
                    responseObserver?.onCompleted()
                }
            }
            return streamObserver
        }
    }
}