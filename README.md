Build and run instructions for the model:

Download Weka API from the following URL:

http://www.java2s.com/Code/Jar/w/weka.htm

Login as the "gsadm" user and move your Icecream.java file to the "bin" folder of GridDB which can be found in the following path:

/griddb_4.6.0-1_amd64/usr/griddb-4.6.0/bin


Set the path for the "gridstore.jar" file by executing the following command on the terminal:


export CLASSPATH=$CLASSPATH:/home/osboxes/Downloads/griddb_4.6.0-1_amd64/usr/griddb-4.6.0/bin/gridstore.jar

Next, run the following command to compile your Icecream.java file:

javac -cp weka-3-7-0/weka.jar Icecream.java

Run the generated .class file using the following command:

java -cp .:weka-3-7-0/weka.jar Icecream





