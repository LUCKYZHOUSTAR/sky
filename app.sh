#!/bin/sh

#
#Java程序启动脚本 v2.0
# created by zhouchaoqiang
#
###################################
# 解析命令行参数
# -s 指定要执行的命令，取值范围：{start|stop|restart|status|info}，如参数不在指定范围之内，则打印帮助信息
# -d 指定程序根目录，如果不指定，则取当前目录
# -j 可执行 jar 包名称，如果不在指定，则从 -d 目录下的 pom.properites 中查找
# -m JVM 最大堆内存，默认为 2048m
###################################
SIGNAL=""
PROFILE="default"
# Java 程序所在的目录
APP_HOME=$(pwd)
#启动日志的输出路径信息
LOGS_DIR=$APP_HOME/logs
JAR=""
#系统4个g内存的话，经验值，堆内存大小
Xmx=2048m
while getopts s:d:j:m: opt 2>/dev/null
do
    case $opt in
    s) SIGNAL=$OPTARG
        ;;
    d) APP_HOME=$OPTARG
        ;;
    j) JAR=$OPTARG
		;;
	m) Xmx=$OPTARG
		;;
    esac
done



#指定应用jar的名称信息
if [ -z "$JAR" ]; then
    if [ -f "$APP_HOME/pom.properties" ]; then
        . $APP_HOME/pom.properties
        # 可执行jar包的名称
        JAR="$artifactId-$version.jar"
    fi
fi


JAVA_HOME=""
JAVA_PATH="java"
JPS_PATH="jps"
if [ -n "$JAVA_HOME" ]; then
    JAVA_PATH="$JAVA_HOME/bin/$JAVA_PATH"
    JPS_PATH="$JAVA_HOME/bin/$JPS_PATH"
fi


# 可执行jar包的所在的路径
JAR_PATH="$APP_HOME/lib/$JAR"

#首先将java版本号信息输出到标准输出，然后查找’64-bit’信息，目的就是判断jdk版本是否为64位
JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`

#JVM启动基本参数，这里根据应用自行调整
#-Xmx2688M -Xms2688M -Xmn960M -XX:MaxMetaspaceSize=256M -XX:MetaspaceSize=256M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark
#-Xmx2688M -Xms2688M -Xmn960M -XX:MaxMetaspaceSize=256M -XX:MetaspaceSize=256M -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark
JAVA_MEM_SIZE_OPTS="-Xmx$Xmx -Xms$Xmx -Xmn960M -XX:MaxMetaspaceSize=256M -XX:MetaspaceSize=256M"

#根据32位和64位配置不同的启动java垃圾回收参数，根据应用自行调整
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server $JAVA_MEM_SIZE_OPTS -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark "
else
    JAVA_MEM_OPTS=" -server $JAVA_MEM_SIZE_OPTS -XX:+UseConcMarkSweepGC -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses -XX:+CMSClassUnloadingEnabled -XX:+ParallelRefProcEnabled -XX:+CMSScavengeBeforeRemark "
fi



#如果logs目录不存在，就创建一个
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi

echo "LOGS_DIR :$LOGS_DIR"

#控制台日志输出收集位置
STDOUT_FILE=$LOGS_DIR/stdout.log




###################################
# (函数)判断程序是否已启动
#
# 说明：
# 使用JDK自带的JPS命令及grep命令组合，准确查找pid
# jps 加 l 参数，表示显示java的完整包路径
###################################
# 初始化psid变量（全局）
psid=0
checkpid(){
	# service
	psid=$(pgrep -f $JAR)

	if [ $psid -z ]; then
	  psid=0
	fi
}



###################################
# (函数)启动程序

# 说明：
# 1. 首先调用checkpid函数，刷新$psid全局变量
# 2. 如果程序已经启动（$psid不等于0），则提示程序已启动
# 3. 如果程序没有被启动，则执行启动命令行
# 4. 启动命令执行后，再次调用checkpid函数
# 5. 如果步骤4的结果能够确认程序的pid,则打印[OK]，否则打印[Failed]
# 注意：echo -n 表示打印字符后，不换行
# 注意: "nohup 某命令 >/dev/null 2>&1 &" 的用法
###################################
start() {
    checkpid
    #如果pid不等于0，代表应用已经启动，直接警告退出
    if [ $psid -ne 0 ]; then
        echo "================================"
        echo "Warn: $JAR already started! (pid=$psid)"
        echo "================================"
        exit 1
    else
        echo "Starting $JAR ..."
        #启动参数信息
        echo "启动参数：java  $JAVA_MEM_OPTS"
        # 如果要指定账户运行
        # JAVA_CMD="nohup $JAVA_PATH $JAVA_OPTS -classpath $CLASSPATH $APP_MAINCLASS >/dev/null 2>&1 &"
        # su - $RUNNING_USER -c "$JAVA_CMD"
        # 作为后台进程启动
        nohup $JAVA_PATH $JAVA_MEM_OPTS -jar $JAR_PATH >$STDOUT_FILE 2>&1 &
        #再检查一下应用是否启动，
        checkpid
        if [ $psid -ne 0 ]; then
          echo "================================"
          echo "$JAR SUCCESSED started! (pid=$psid)"
          echo "================================"
        fi
    fi
}


###################################
# (函数)直接运行程序

# 说明：此方法主要用于测试运行
###################################
run(){
	echo "Running $JAR"
  $JAVA_PATH $JAVA_OPTS -jar $JAR_PATH

}


###################################
# (函数)停止程序
#
# 说明：
# 1. 首先调用checkpid函数，刷新$psid全局变量
# 2. 如果程序已经启动（$psid不等于0），则开始执行停止，否则，提示程序未运行
# 3. 使用kill -9 pid命令进行强制杀死进程
# 4. 执行kill命令行紧接其后，马上查看上一句命令的返回值: $?
# 5. 如果步骤4的结果$?等于0,则打印[OK]，否则打印[Failed]
# 6. 为了防止java程序被启动多次，这里增加反复检查进程，反复杀死的处理（递归调用stop）。
# 注意：echo -n 表示打印字符后，不换行
# 注意: 在shell编程中，"$?" 表示上一句命令或者一个函数的返回值
###################################
stop() {
    checkpid
    if [ $psid -ne 0 ]; then
        echo "Stopping $JAR ...(pid=$psid) "
        # su - $RUNNING_USER -c "kill -9 $psid"
        kill -9 $psid
        if [ $? -eq 0 ]; then
            echo "[OK]"
        else
            echo "[Failed]"
        fi

        #再次核对应用是否关闭，否则递归stop
        checkpid
        if [ $psid -ne 0 ]; then
          stop
        fi

    else
        echo "================================"
        echo "Warn: $JAR is not running"
        echo "================================"
    fi
}

###################################
# (函数)检查程序运行状态
#
# 说明：
# 1. 首先调用checkpid函数，刷新$psid全局变量
# 2. 如果程序已经启动（$psid不等于0），则提示正在运行并表示出pid
# 3. 否则，提示程序未运行
###################################
status() {
    checkpid
        #如果为空
    if [ $psid -ne 0 ];  then
        echo "$JAR is running! (pid=$psid)"
    else
        echo "$JAR is not running"
    fi
}

###################################
# (函数)打印系统环境参数
###################################
info() {
    echo "System Information:"
    echo "****************************"
    # echo `head -n 1 /etc/issue`
    echo `uname -a`
    echo
    echo "JAVA_HOME=$JAVA_HOME"
    echo `$JAVA_PATH -version`
    echo "APP_HOME=$APP_HOME"
    echo "JAR=$JAR"
    echo "JAR_PATH=$JAR_PATH"
    echo "JAVA_OPTS=$JAVA_OPTS"
    echo "****************************"
}

case "$SIGNAL" in
    'run')
        run
        ;;
    'start')
        start
        ;;
    'stop')
        stop
        ;;
    'restart')
        stop
        start
        ;;
    'status')
        status
        ;;
    'info')
        info
        ;;
    *)
        echo -e "用法：app.sh -s 命令 -d 目录 -j jar包 -p 环境"
        echo -e "\t-s\t 必填，可用命令：{run|start|stop|restart|status|info}"
        echo -e "\t\t run 启动程序，但会挂起，控制台输出日志，一般用于调试"
        echo -e "\t\t start 启动程序，作为后台进程运行"
        echo -e "\t-d\t 可选，Java 程序所在目录，如果为空则取当前目录"
		    echo -e "\t-m\t 可选，最大堆内存，默认为 2048m"
        exit 1
esac
