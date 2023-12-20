## Introduction

Linear regression analysis helps us to predict the value of a variable based on the value of another variable. The variable to be predicted is known as the `dependent` variable while the variable used for predicting other variables is known as the `independent variable`. It estimates the coefficients of the linear equation with one or more independent variables. Linear regression creates a straight line that minimizes the differences between the predicted and the expected output values. 

In this article, we will be creating a linear regression model that predicts the number of ice cream sales based on temperature using Java and GridDB. `Temperature` will be the indepedent variable while `ice cream sales` will be the dependent variable. 

## Import Packages

Let's begin by importing the java libraries that will enable us to work with GridDB: 

```java
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.Properties;
import java.util.Collection;


import com.toshiba.mwcloud.gs.Query;
import com.toshiba.mwcloud.gs.RowKey;
import com.toshiba.mwcloud.gs.RowSet;
import com.toshiba.mwcloud.gs.GridStore;
import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.GSException;
import com.toshiba.mwcloud.gs.GridStoreFactory;
```

## Move the Data into GridDB

The dataset to be used shows the number of icre cream sales for different temperature values. It has been divided into two, that is, training and test datasets, and stored in two separate CSV files. 
However, we want to move the data into GridDB as it offers various benefits including faster query performance. We will define two GridDB containers for storing the data. Each container will be defined as a java class as shown below:

```java
 public static class TrainingDataset{
     @RowKey int temperature;
   	 int icecreamsales;
    }
    
    public static class TestingDataset{
     @RowKey int temps;
   	 int icesales;
    }
```
	
A GridDB container can be seen as a SQL table. 

For us to write data into the GridDB containers, we should first connect to GridDB. We have to create a `Properties` file from the `java.util` package and provide the credentials of our GridDB installation using the `key:value` pairs syntax as shown below:

```java
Properties props = new Properties();
        props.setProperty("notificationMember", "239.0.0.1:31999");
        props.setProperty("clusterName", "defaultCluster");
        props.setProperty("user", "admin");
        props.setProperty("password", "admin");
        GridStore store = GridStoreFactory.getInstance().getGridStore(props);
```
		
We have also created the `store` variable of type `GridStore` to help us interact with the database. 

## Store the Training Dataset in GridDB

The training dataset is stored in a CSV file named `testingset.csv`. Let us open the `TrainingDataset` container in GridDB where it will be stored:

```java
Collection<String TrainingDataset> traincoll = store.putCollection("TRAININGDATASET", TrainingDataset.class);
```

We can use the following code to read the data from the file and store it in GridDB:

```java
File file1 = new File("trainingset.csv");
                Scanner sc = new Scanner(file1);

                while (sc.hasNext()){
                        String scData = sc.next();
                        String dataList[] = scData.split(",");
                        String temperature = dataList[0];
                        String icecreamsales = dataList[1];
                        
                        TrainingDataset trainset = new TrainingDataset();
    
                      trainset.temperature = Integer.parseInt(temperature);
                      trainset.icecreamsales = Integer.parseInt(icecreamsales);
                      traincoll.append(trainset);
```
We have used comma (,) as the delimiter while reading data from the CSV file. 

## Store the Testing Dataset in GridDB

The testing dataset has been stored in a CSV file named `testingset.csv`. We want to store it in a GridDB container. Let us first open the container where the data will be stored:

```Java
    Collection<String, TestingDataset> coll2 = store.putCollection("TESTINGDATASET", TestingDataset.class);
```

Let us read data from the `testingset.csv` file and store it in GridDB:

```java
    File file2 = new File("testingset.csv");
                Scanner scs = new Scanner(file2);

                while (scs.hasNext()){
                        String scDatas = scs.next();
                        String dataLists[] = scDatas.split(",");
                        String temps = dataLists[0];
                        String icesales = dataLists[1];
                        
                        TestingDataset testset = new TestingDataset();
    
                        testset.temps = Integer.parseInt(temps);
                        testset.icesales = Integer.parseInt(icesales);
                        coll2.append(testset);
```
	
## Retrieve the Training Data

We now want to retrieve the training data from GridDB and use it to fit a machine learning model. We will write a TQL query to retrieve all the data in the `TrainingDataset` container as shown below:

```java
Query<TrainingDataset> query = traincoll.query("select *");
                RowSet<TrainingDataset> rs = query.fetch(false);
	        RowSet res = query.fetch();
```

We have used the `select *` TQL statement to retrieve all the data stored in the container. 

## Fit a Linear Regression Model

We now want to use the Weka machine learning library to fit a linear regression model using our training dataset. But first, let us import the necessary libraries:

```java
import weka.core.Instances;
import weka.filters.Filter;
import java.io.IOException;
import weka.core.Instance;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.converters.ArffLoader;
import weka.classifiers.trees.RandomForest;
import weka.filters.unsupervised.attribute.StringToWordVector;
```

We can now create instances from the dataset:

```java
BufferedReader bufferedReader
                = new BufferedReader(
                    new FileReader(res));
 
            // Create dataset instances
            Instances datasetInstances
                = new Instances(bufferedReader);
```

Next, we can now use `LinearRegression()` function of the Weka library to fit a Linear Regression classifier:

```java
/** Classifier here is Linear Regression */
		Classifier classifier = new weka.classifiers.functions.LinearRegression();
		/** */
		classifier.buildClassifier(datasetInstances);
```

## Evaluate and Test the Model

We will evaluate the model using the training dataset and test it using the test dataset. But first, let us retrieve the test dataset from GridDB:

```java
Query<TestingDataset> query2 = coll2.query("select *");
                RowSet<TestingDataset> rs2 = query2.fetch(false);
	        RowSet res2 = query2.fetch();
```

Let us create instances from the test dataset:

```java
BufferedReader bufferedReader2
                = new BufferedReader(
                    new FileReader(res2));
 
            // Create dataset instances
            Instances datasetInstances2
                = new Instances(bufferedReader2);
```
	
We can now evaluate and test the model:

```java
Evaluation eval = new Evaluation(datasetInstances);
		eval.evaluateModel(classifier, datasetInstances2);
		/** Print the algorithm summary */
		System.out.println("** Evaluation of the Linear Regression **");
		System.out.println(eval.toSummaryString());
		System.out.print(" The expression of the input data according to the alogorithm is ");
		System.out.println(classifier);
```

## Execute the Model

Download the Weka API from the following URL:

```
http://www.java2s.com/Code/Jar/w/weka.htm
```

Login as the `gsadm` user and move your `.java` file to the `bin` folder of GridDB which can be found in the following path:

```
/griddb_4.6.0-1_amd64/usr/griddb-4.6.0/bin
```

Set the path for the `gridstore.jar` file by executing the following command on the terminal:

```
export CLASSPATH=$CLASSPATH:/home/osboxes/Downloads/griddb_4.6.0-1_amd64/usr/griddb-4.6.0/bin/gridstore.jar
```

Next, run the following command to compile your `.java` file:

```
javac -cp weka-3-7-0/weka.jar Icecream.java
```

Run the generated .class file using the following command:

```
java -cp .:weka-3-7-0/weka.jar Icecream
```

The model returned a correlation coefficient of 0.945. Correlation values range between -1 and 1, where 1 is very strong and linear correlation, -1 is inverse linear relation and 0 means no relation. 





























