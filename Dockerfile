FROM openjdk:8
# 在docker容器中自建容器卷,用于数据保存和持久化工作
VOLUME /tmp
#用于复制文件并解压缩(COPY不能解压缩)
#将当前的nemo-controller/target/nemo-controller-1.0.0-SNAPSHOT.jar复制到docker容器根目录下
ADD nemo-controller/target/nemo-controller-1.0.0-SNAPSHOT.jar app.jar
#运行过程中创建一个app.jar文件
RUN sh -c 'touch /app.jar'
ENV JAVA_OPTS=""
#执行传入的参数的linux命令,启动jar包
#不同CMD的是：多个CMD命令只能是最后一个生效,CMD会被docker run之后的参数替换
#ENTRYPOINT 执行的命令会追加不会覆盖
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]