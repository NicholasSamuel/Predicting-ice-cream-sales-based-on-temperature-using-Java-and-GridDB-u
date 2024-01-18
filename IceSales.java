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
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class IceSales {
    
   static double[][] data = {{14.2,16.4,11.9,15.2,18.5,22.1,19.4,25.1,23.4,18.1,22.6,17.2},
       {215,325,185,332,406,522,412,614,544,421,445,408}};
    static double[][] data2 = {{14.2,16.4,11.9},{215,325,185}};
    
      static class SalesData {
	@RowKey int id;
        double temperature;
	double icecreamsales;
     }
    
    
        // TODO code application logic here
     
   
public static void main(String[] args) {   
    
    try{

Properties props = new Properties();
props.setProperty("notificationMember", "127.0.1.1:10001");
props.setProperty("clusterName", "myCluster");
props.setProperty("user", "admin");
props.setProperty("password", "admin");


GridStore store = GridStoreFactory.getInstance().getGridStore(props);
                



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


Collection<String, SalesData> sd= store.putCollection("SalesData", SalesData.class);
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


Query<SalesData> query = sd.query("select *");
RowSet<SalesData> rs = query.fetch(false);


    
while (rs.hasNext()) {
SalesData sd1 = rs.next();
double[][] data = {{sd1.temperature},{sd1.icecreamsales}};
}
	           int numInstances = data[0].length;
                   FastVector atts = new FastVector();
                         
                   List<Instance> instances = new ArrayList<Instance>();
                         
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
                         
Instances newDataset = new Instances("Dataset", atts, instances.size());
        

newDataset.setClassIndex(1);
for(Instance inst : instances)
newDataset.add(inst);
Classifier classifier = new weka.classifiers.functions.LinearRegression();



    

        int numInstances2 = data2[0].length;
        FastVector atts2 = new FastVector();
        List<Instance> instances2 = new ArrayList<Instance>();


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

    Instances newDataset2 = new Instances("Dataset", atts2, instances2.size());
        

newDataset2.setClassIndex(1);

    for(Instance inst2 : instances2)
       newDataset2.add(inst2);

   
 
       try{
classifier.buildClassifier(newDataset);
        System.out.println(classifier);
        
        Evaluation eval = new Evaluation(newDataset);
		eval.evaluateModel(classifier, newDataset2);
		
		
		System.out.println(eval.toSummaryString());
                
                Instance pd = newDataset2.lastInstance();
                double value = classifier.classifyInstance(pd);
                
		
		System.out.println(value);
        } catch (Exception ex) {
        Logger.getLogger(SalesData.class.getName()).log(Level.SEVERE, null, ex);
    }
       
		
   
    
  //} close 
     
}
   
catch (GSException e) {
                        System.out.println("An error occurred when creating the container.");
                        e.printStackTrace();
                        System.exit(1);
}
}
}
