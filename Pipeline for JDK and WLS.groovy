pipeline {
    agent {label 'Node'}
    stages {
        stage('Install Java') {
            steps {
                sh '''
                echo $HOSTNAME
                ls -l /u01/app/software
                cd $masterdirectory
                tar -zxvf /u01/app/software/jdk*.gz
                export JAVA_HOME=$masterdirectory/jdk1.8.0_261
                export PATH=$JAVA_HOME/bin:$PATH
                java -version
                '''
            }
        }
        stage('Install Weblogic') {
                    steps {
                        sh '''
                        export JAVA_HOME=$masterdirectory/jdk1.8.0_261
                        export PATH=$JAVA_HOME/bin:$PATH
                        INSTALLER=/u01/app/software/fmw_14.1.1.0.0_wls_lite_Disk1_1of1.zip
                        ORAINST=/u01/app/software/oraInst.loc
                        RSPFILE=/u01/app/software/fmw14cinstall.rf
                        java -version |& grep 'version' |& awk -F" " '{ print $3 }'
                        if [ $? -eq 0 ]; then
                        echo "Java prerequisites exists"
                        echo "check installer,unzip to /tmp"
                        test -f /tmp/fmw_14.1.1.0.0_wls_lite_generic.jar && rm /tmp/fmw_14.1.1.0.0_wls_lite_generic.jar
                        test -f $INSTALLER && unzip -d /tmp $INSTALLER
                        test -f $ORAINST && echo $ORAINST exists 
                        test -f $RSPFILE && echo $RSPFILE exists
                        java -jar /tmp/fmw_14.1.1.0.0_wls_lite_generic.jar \
                        -silent -invPtrLoc $ORAINST \
                        -responseFile $RSPFILE
                        else
                        echo FAIL
                        exit
                        fi
                        export MW_HOME=$masterdirectory/fmw
                        export WL_HOME=$MW_HOME/wlserver
                        export CLASSPATH=$WL_HOME/server/lib/weblogic.jar:.
                        java weblogic.version
                        if  [ $? -eq 0 ]; then
                            echo "Installation successful"
                        else
                            echo "installation failed"
                        fi
                     '''
                    }
                }
    }
}
