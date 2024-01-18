## Introduction

Linear regression analysis helps us to predict the value of a variable based on the value of another variable. The variable to be predicted is known as the `dependent` variable while the variable used for predicting other variables is known as the `independent variable`. It estimates the coefficients of the linear equation with one or more independent variables. Linear regression creates a straight line that minimizes the differences between the predicted and the expected output values. 

In this article, we will be creating a linear regression model that predicts the number of ice cream sales based on temperature using Java and GridDB. `Temperature` will be the indepedent variable while `ice cream sales` will be the dependent variable. 

## Import Packages

Let's begin by importing the java libraries that will enable us to work with GridDB: 

```java
import com.toshiba.mwcloud.gs.Collection;
import com.toshiba.mwcloud.gs.GSException;
import com.toshiba.mwcloud.gs.GridStore;
import com.toshiba.mwcloud.gs.GridStoreFactory;
import com.toshiba.mwcloud.gs.Query;
import com.toshiba.mwcloud.gs.RowKey;
import com.toshiba.mwcloud.gs.RowSet;
import java.util.*;


import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
```

## Write the Data into GridDB

The dataset to be used shows the number of ice cream sales for different temperature values. It has been divided into two, that is, training and test datasets. 
However, we want to move the training dataset into GridDB as it offers various benefits including faster query performance. We will define a GridDB container for storing the data. The container will be defined as a java class as shown below:

```java
 static class SalesData {
	@RowKey int id;
        double temperature;
	double icecreamsales;
     }
```
	
A GridDB container can be seen as a SQL table. 

For us to write data into the GridDB container, we should first connect to GridDB. We have to create a `Properties` file from the `java.util` package and provide the credentials of our GridDB installation using the `key:value` pairs syntax as shown below:

```java
Properties props = new Properties();
props.setProperty("notificationMember", "127.0.1.1:10001");
props.setProperty("clusterName", "myCluster");
props.setProperty("user", "admin");
props.setProperty("password", "admin");
GridStore store = GridStoreFactory.getInstance().getGridStore(props);
```
		
We have also created the `store` variable of type `GridStore` to help us interact with the database. 

## Store the Training Dataset in GridDB

We want to store the training dataset into GridDB. Let us first define the data rows as instances of the `SalesData` class. 

```java
SalesData  row1 = new SalesData();
row1.id=1;
row1.temperature=14.2;
row1.icecreamsales=215;

SalesData  row2 = new SalesData();
row2.id=2;
row2.temperature=16.4;
row2.icecreamsales=325;

SalesData  row3 = new SalesData();
row3.id=3;
row3.temperature=11.9;
row3.icecreamsales=185;

SalesData  row4 = new SalesData();
row4.id=4;
row4.temperature=15.2;
row4.icecreamsales=332;

SalesData  row5 = new SalesData();
row5.id=5;
row5.temperature=18.5;
row5.icecreamsales=406;

SalesData  row6 = new SalesData();
row6.id=6;
row6.temperature=19.4;
row6.icecreamsales=412;

SalesData  row7 = new SalesData();
row7.id=7;
row7.temperature=25.1;
row7.icecreamsales=614;

SalesData  row8 = new SalesData();
row8.id=8;
row8.temperature=23.4;
row8.icecreamsales=544;

SalesData  row9 = new SalesData();
row9.id=9;
row9.temperature=18.1;
row9.icecreamsales=421;

SalesData  row10 = new SalesData();
row10.id=10;
row10.temperature=22.6;
row10.icecreamsales=445;

SalesData  row11 = new SalesData();
row11.id=11;
row11.temperature=17.2;
row11.icecreamsales=408;
```

Let us now select the `SalesData` GridDB container where the data will be stored:

```java
Collection<String, SalesData> sd= store.putCollection("SalesData", SalesData.class);
```

We can use the `put()` function to add the data into the container:

```java
sd.put(row1);
sd.put(row2);
sd.put(row3);
sd.put(row4);
sd.put(row5);
sd.put(row6);
sd.put(row7);
sd.put(row8);
sd.put(row9);
sd.put(row10);
sd.put(row11);
```

## Retrieve the Training Data

We now want to retrieve the training data from GridDB and use it to fit a machine learning model. We will write a TQL query to retrieve all the data in the `SalesData` container as shown below:

```java
Query<SalesData> query = sd.query("select *");
RowSet<SalesData> rs = query.fetch(false);

while (rs.hasNext()) {
SalesData sd1 = rs.next();
double[][] data = {{sd1.temperature},{sd1.icecreamsales}};
}
```

We have used the `select *` TQL statement to retrieve all the data stored in the container. The data has then been stored in a 2D array named `data`.

## Create Weka Instances

We will use the Weka machine learning library to fit a linear regression model. Hence, we must convert our data into Weka instances. We will first create attributes for the dataset and store them in a FastVector data structure. We can then create Weka instances of the dataset. 

Let's first create the data structures for storing the attributes and the instances:

```java
int numInstances = data[0].length;
FastVector atts = new FastVector();
List<Instance> instances = new ArrayList<Instance>();
```

Next, we create a `for` loop and use it to iterate over the data items and populate the FastVector data structure with the attributes:

```java
for(int dim = 0; dim < 2; dim++)
    {
        Attribute current = new Attribute("Attribute" + dim, dim);

        if(dim == 0)
        {
            for(int obj = 0; obj < numInstances; obj++)
            {
                instances.add(new SparseInstance(numInstances));
            }
        }

        for(int obj = 0; obj < numInstances; obj++)
        {
            instances.get(obj).setValue(current, data[dim][obj]);
            
        }
        atts.addElement(current);
    }
```

Let's use the `Instance` class of Weka to generate instances and store them in an Instance variable named `newDataset`. 

```java
Instances newDataset = new Instances("Dataset", atts, instances.size());
```

## Fit a Linear Regression Model

Since the data instances are ready, we can fit a machine learning model. But first, let us import the necessary libraries from Weka:

```java
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
```

Let us specify the class attribute for the dataset before feeding it into the model:

```java
newDataset.setClassIndex(1);
```

We can now use the `LinearRegression()` function of the Weka library to fit a Linear Regression classifier:

```java
newDataset.setClassIndex(1);
for(Instance inst : instances)
newDataset.add(inst);
Classifier classifier = new weka.classifiers.functions.LinearRegression();

classifier.buildClassifier(newDataset);
        System.out.println(classifier);
```

Our linear regression model is now ready!

## Evaluate and Test Model

We will evaluate the model using the training dataset and test it using the test dataset. Let us define an array named `data2` and populate it with the test data:

```java
    static double[][] data2 = {{14.2,16.4,11.9},{215,325,185}};
```

For us to feed the data into the model, we must first convert it into Weka instances. We will store the attributes in a FastVector data structure and the instances in an ArrayList. Let us define them:

```java
int numInstances2 = data2[0].length;
        FastVector atts2 = new FastVector();
        List<Instance> instances2 = new ArrayList<Instance>();
```

We can now use a `for` loop to iterate over the data and populate the attributes into the FastVector:

```java
for(int dim2 = 0; dim2 < 2; dim2++)
    {
        Attribute current2 = new Attribute("Attribute" + dim2, dim2);

        if(dim2 == 0)
        {
            for(int obj2 = 0; obj2 < numInstances2; obj2++)
            {
                instances2.add(new SparseInstance(numInstances2));
            }
        }

        for(int obj2 = 0; obj2 < numInstances2; obj2++)
        {
            instances2.get(obj2).setValue(current2, data2[dim2][obj2]);
            
        }
        atts2.addElement(current2);
    }
```

Let us create instances of the training dataset and store them in an Instance variable named `newDataset2`:

```java
    Instances newDataset2 = new Instances("Dataset", atts2, instances2.size());
```

Let us specify the class attribute of the dataset before feeding it into the model:

```java
newDataset2.setClassIndex(1);
```

Next, we feed the data into the model and print the evaluation summary:

```java
for(Instance inst2 : instances2)
       newDataset2.add(inst2);

       Evaluation eval = new Evaluation(newDataset);
		eval.evaluateModel(classifier, newDataset2);
	
		System.out.println(eval.toSummaryString());
```

## Make a Prediction

We can now use our model to predict the number of ice cream sales for a particular temperature value. Let's use the last instance of the test dataset to make the prediction:

```java
Instance pd = newDataset2.lastInstance();
double value = classifier.classifyInstance(pd);              
	System.out.println(value);
```

## Execute the Model

Download the Weka API from the following URL:

```
http://www.java2s.com/Code/Jar/w/weka.htm
```
I will be using Weka version 3.7.0. 

Set the class paths for the `gridstore.jar` and `weka-3-7-0.jar` files by executing the following commands on the terminal:

```
export CLASSPATH=$CLASSPATH:/usr/share/java/gridstore.jar
```

```
export CLASSPATH=$CLASSPATH:/mnt/c/Users/user/Desktop/weka-3.7.0.jar
```
Note that the above commands may change depending on the location of the files. 

Next, run the following command to compile your `.java` file:

```
javac IceSales.java
```

Execute the generated .class file using the following command:

```
java IceSales
```

The model returned a correlation coefficient of 0.9456. Correlation values range between -1 and 1, where 1 is very strong and linear correlation, -1 is inverse linear relation and 0 means no relation. The model also predicted 198 ice cream sales.  





























