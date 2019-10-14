jarFile='slackServer.jar'
P_ID=`jps -ml|grep "$jarFile"|awk '{print $1}'`
        if [ "$P_ID" == "" ]; then
                echo "$jarFile process not exits"
        else
                kill $P_ID
                echo "stop $jarFile success!!"
      fi
