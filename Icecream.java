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
public class Icecream {

    
    public static class TrainingDataset{
    
         @RowKey int temperature;
   	 int icecreamsales;
    
    }
    
    public static class TestingDataset{
    
         @RowKey int temps;
   	 int icesales;
    
    }
    
      
    public static void main(String[] args) throws FileNotFoundException {
        
       
        Properties props = new Properties();
        props.setProperty("notificationMember", "239.0.0.1:31999");
        props.setProperty("clusterName", "defaultCluster");
        props.setProperty("user", "admin");
        props.setProperty("password", "admin");
        GridStore store = GridStoreFactory.getInstance().getGridStore(props);
        
        Collection<String, TrainingDataset> traincoll = store.putCollection("TRAININGDATASET", TrainingDataset.class);

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
                        
    Collection<String, TestingDataset> coll2 = store.putCollection("TESTINGDATASET", TestingDataset.class);
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

    Query<TrainingDataset> query = traincoll.query("select *");
                RowSet<TrainingDataset> rs = query.fetch(false);
	        RowSet res = query.fetch();
                
    BufferedReader bufferedReader
                = new BufferedReader(
                    new FileReader(res));
 
            // Create dataset instances
            Instances datasetInstances = new Instances(bufferedReader);
            
            /** Classifier here is Linear Regression */
		Classifier classifier = new weka.classifiers.functions.LinearRegression();
		/** */
		classifier.buildClassifier(datasetInstances);
                
                Query<TestingDataset> query2 = coll2.query("select *");
                RowSet<TestingDataset> rs2 = query2.fetch(false);
	        RowSet res2 = query2.fetch();
                
                BufferedReader bufferedReader2
                = new BufferedReader(
                    new FileReader(res2));
 
            // Create dataset instances
            Instances datasetInstances2
                = new Instances(bufferedReader2);
            
            Evaluation eval = new Evaluation(datasetInstances);
		eval.evaluateModel(classifier, datasetInstances2);
		/** Print the algorithm summary */
		System.out.println("** Evaluation of the Linear Regression **");
		System.out.println(eval.toSummaryString());
		System.out.print(" The expression of the input data according to the alogorithm is ");
		System.out.println(classifier);
    }
    
}
}
