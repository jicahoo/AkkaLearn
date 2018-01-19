# AkkaLearn
Examples of Akka


## Akka
* 线程模型：https://stackoverflow.com/questions/20673206/how-are-akka-actors-implemented-on-underlying-threads
* Dispatcher指得并不是某一个线程，而是包含一组线程进行消息**处理**工作。基于生产者消费者模式，Actor发送消息其实是把对应的MailBox提交到一个BlockingQueue, 会有一组线程从这个队列上取新的任务，也就是新的MailBox，并**执行**MailBox。为什么说是“执行“？因为MailBox其实是个Runnable。MailBox的run方法会调到ActorCell.invoke(Evelope)的方法。所以，Actor的receive方法的逻辑是在Dispacther对应的线程池中的某个线程执行的。觉得Dispatcher命名有点不是很准确。
* 当Dispatcher中的线程，从BlockingQueue中取到某个MailBox，只会处理一定数量的消息，处理完成后，会继续尝试将这个MailBox放入队列。
* Dispatcher并不是扫描某种MailBox相关的数据结构，将消息分发给某些woker线程去处理的。
* 消息发送的时候，应该是想办法(可能需要一些路由)将消息放入目标Actor的信箱，ActorCell是实现了Dipatch特征, Dispatch的sendMessage方法调用了dispatcher.dispatch方法，在Dispatch方法中，直接调用MailBox的enqueue方法，将消息放入信箱，然后再将MailBox本身提交到BlockingQueue.
* 类Dispatcher继承自MessageDispatcher
* 如何处理阻塞的IO。用一定数量的线程池。用线程池的大小控制同一时刻可以并发的IO操作。在Akka中，阻塞操作需要谨慎地管理：https://doc.akka.io/docs/akka/current/dispatchers.html#blocking-needs-careful-management
* 阻塞的IO意味着什么，IO意味着：1. CPU上下文切换，2. 线程的栈空间和状态数据, 3. 当数据准备好的时候，操作系统在调度该线程到可执行队列。这就是IO阻塞要耗费的资源。这种调度发生在操作系统层面，需要消耗更多的资源，如果在编程语言层面做调度用的资源相对较少，就避免了操作系统层面的CPU上下文切换，而是协程的切换（Python和Go都有类似协程的东西）。


## Blogs
* Throttling Message http://letitcrash.com/post/28901663062/throttling-messages-in-akka-2
