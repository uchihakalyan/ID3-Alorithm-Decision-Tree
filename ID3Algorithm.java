import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

public class ID3Algorithm
{
	public static BinaryTree tree = new BinaryTree();

	public static void main(String args[])
	{
		// Saving Training data 
		ArrayList<String> list1 = readExcelFromPath(args[0]);
		int len1 = getLengthFromString(list1.get(1));
		String[] trainingDataFeatureNames = getAttributeNames(list1, len1);
		boolean[][] trainingData = dataConversionToArray(list1,len1);
		boolean[] trainingOutput = new boolean[trainingData.length];

		tree.insert(0, 0, 1, false, "TopNode");
		ID3(tree.root, trainingDataFeatureNames, trainingData);
		tree.trim(tree.root);

		//For printing the tree
		System.out.println("Decision Tree:");
		print();
		System.out.println();
		System.out.println();

		// Saving Test data 
		ArrayList<String> list2 = readExcelFromPath(args[1]);
		int len2 = getLengthFromString(list2.get(1));
		String[] testDataFeatureNames = getAttributeNames(list2, len2);
		boolean[][] testData = dataConversionToArray(list2,len2);
		boolean[] testOutput = new boolean[testData.length];

		// Saving Validation data 
		ArrayList<String> list3 = readExcelFromPath(args[2]);
		int len3 = getLengthFromString(list3.get(1));
		String[] validationDataFeatureNames = getAttributeNames(list3, len3);
		boolean[][] validationData = dataConversionToArray(list3,len3);
		boolean[] validationOutput = new boolean[validationData.length];

		double pruningFactor =Double.parseDouble(args[3]); 
		//System.out.println(pruningFactor);
		
		//For predicting output and calculating accuracy on Test set
		int correctPredictionsTestData=0, correctPredictionsValidationData=0, correctPredictionsTrainingData=0;
		for(int i=0;i<testData.length;i++) {
			testOutput[i]=predictOutput(testData[i], testDataFeatureNames);
			if(testOutput[i]==testData[i][testData[0].length-1]) {
				correctPredictionsTestData++;
			}
		}
		
		for(int i=0;i<trainingData.length;i++) {	
			trainingOutput[i]=predictOutput(trainingData[i], trainingDataFeatureNames);
			if(trainingOutput[i]==trainingData[i][trainingData[0].length-1]) {
				correctPredictionsTrainingData++;
			}
		}
		
		for(int i=0;i<validationData.length;i++) {
			validationOutput[i]=predictOutput(validationData[i], validationDataFeatureNames);
			if(validationOutput[i]==validationData[i][validationData[0].length-1]) {
				correctPredictionsValidationData++;
			}
		}

		double trainingAccuracy = correctPredictionsTrainingData*100.0/trainingData.length;
		double validationAccuracy = correctPredictionsValidationData*100.0/validationData.length;
		double testingAccuracy = correctPredictionsTestData*100.0/testData.length;

		System.out.println("Pre-Pruned Accuracy:");
		System.out.println("-------------------");
		System.out.println("Number of training instances = "+trainingData.length);
		System.out.println("Number of training attributes = "+(trainingDataFeatureNames.length-1));
		System.out.println("Total number of nodes in the tree = "+tree.size);
		System.out.println("Number of leaf nodes in the tree = "+tree.getLeafNodes(tree.root, true));
		System.out.println("Accuracy of the model on the training dataset = "+trainingAccuracy+"%");
		System.out.println();
		System.out.println("Number of validation instances = "+validationData.length);
		System.out.println("Number of validation attributes = "+(validationDataFeatureNames.length-1));
		System.out.println("Accuracy of the model on the validation dataset = "+validationAccuracy+"%");
		System.out.println();
		System.out.println("Number of testing instances = "+testData.length);
		System.out.println("Number of testing attributes = "+(testDataFeatureNames.length-1));
		System.out.println("Accuracy of the model on the testing dataset = "+testingAccuracy+"%");
		System.out.println();

		//PRONING
		pruning(pruningFactor, validationData, validationDataFeatureNames, validationAccuracy);
		
		correctPredictionsTestData=0; correctPredictionsValidationData=0; correctPredictionsTrainingData=0;
		for(int i=0;i<testData.length;i++) {
			testOutput[i]=predictOutput(testData[i], testDataFeatureNames);
			if(testOutput[i]==testData[i][testData[0].length-1]) {
				correctPredictionsTestData++;
			}
		}

		for(int i=0;i<validationData.length;i++) {
			validationOutput[i]=predictOutput(validationData[i], validationDataFeatureNames);
			if(validationOutput[i]==validationData[i][validationData[0].length-1]) {
				correctPredictionsValidationData++;
			}
		}

		for(int i=0;i<trainingData.length;i++) {	
			trainingOutput[i]=predictOutput(trainingData[i], trainingDataFeatureNames);
			if(trainingOutput[i]==trainingData[i][trainingData[0].length-1]) {
				correctPredictionsTrainingData++;
			}
		}

		trainingAccuracy = correctPredictionsTrainingData*100.0/trainingData.length;
		validationAccuracy = correctPredictionsValidationData*100.0/validationData.length;
		testingAccuracy = correctPredictionsTestData*100.0/testData.length;

		System.out.println("Post-Pruned Accuracy:");
		System.out.println("-------------------");
		System.out.println("Number of training instances = "+trainingData.length);
		System.out.println("Number of training attributes = "+(trainingDataFeatureNames.length-1));
		System.out.println("Total number of nodes in the tree = "+tree.size);
		System.out.println("Number of leaf nodes in the tree = "+tree.getLeafNodes(tree.root, true));
		System.out.println("Accuracy of the model on the training dataset = "+trainingAccuracy+"%");
		System.out.println();
		System.out.println("Number of validation instances = "+validationData.length);
		System.out.println("Number of validation attributes = "+(validationDataFeatureNames.length-1));
		System.out.println("Accuracy of the model on the validation dataset = "+validationAccuracy+"%");
		System.out.println();
		System.out.println("Number of testing instances = "+testData.length);
		System.out.println("Number of testing attributes = "+(testDataFeatureNames.length-1));
		System.out.println("Accuracy of the model on the testing dataset = "+testingAccuracy+"%");
		System.out.println();
		
	}

	public static ArrayList<String> readExcelFromPath(String path)
	{

		FileReader fr;
		BufferedReader br;
		ArrayList<String> list = new ArrayList<String>();
		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);  
			String s;  
			while((s = br.readLine()) != null) { 
				if(!s.matches("[\\s,]*")) {
					list.add(s); 
				}
			}
			fr.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return list;
	}

	public static String[] getAttributeNames(ArrayList<String> list, int length){
		String[] featureNames = new String[length];

		StringTokenizer input = new StringTokenizer(list.get(0), ",");

		int i = 0;
		while(input.hasMoreTokens()){
			featureNames[i++] = input.nextToken();
		}

		return featureNames;
	}

	public static boolean[][] dataConversionToArray(ArrayList<String> list, int length){

		boolean[][] data = new boolean[list.size()-1][length];

		for(int i=1;i<list.size();i++){
			StringTokenizer input = new StringTokenizer(list.get(i),",");

			int j = 0;
			while(input.hasMoreTokens()){
				String s = input.nextToken();
				if(s.equals("0")){
					data[i-1][j++] = false;
				} else if(s.equals("1")){
					data[i-1][j++] = true;
				}
			}
		}

		return data;
	}

	public static int getLengthFromString(String data){
		int count=0;

		StringTokenizer input = new StringTokenizer(data,",");
		while(input.hasMoreTokens()){
			input.nextToken();
			count++;
		}

		return count;
	}

	public static double calculateEntropy(int a, int b){

		if(a==0 || b==0){
			return 0;
		}
		else{
			return -a * Math.log10(a*1.0/(a+b))/Math.log10(2)/(a+b) -b * Math.log10(b*1.0/(a+b))/Math.log10(2)/(a+b); 
		}
	}

	public static double calculateInformationGain(double entropy, int a1, int b1, int a2, int b2){
		return entropy -(((a1+b1)*(calculateEntropy(a1,b1))/(a1+b1+a2+b2)) + ((a2+b2)*(calculateEntropy(a2,b2)))/(a1+b1+a2+b2));
	}

	public static void ID3(Node node,String[] featureNames, boolean[][] data){
		double maxGain=0.0;
		int size = data[0].length;
		String featureName="";
		int a3=0,b3=0,a4=0,b4=0,index=0;

		for(int i=0; i<size-1; i++){
			int a1 = 0, b1 = 0, a2 = 0, b2 = 0;
			for(int j=0; j<data.length; j++){
				if(!data[j][i]){
					if(!data[j][size-1]){
						b1++;
					}else{
						a1++;
					}
				}else{
					if(!data[j][size-1]){
						b2++;
					}else{
						a2++;
					}
				}
			}
			double gain = calculateInformationGain(node.entropy, a1, b1, a2, b2);
			if(gain>maxGain){
				maxGain=gain;
				a3=a1;b3=b1;a4=a2;b4=b2;index=i;
				featureName=featureNames[i];
			}
		}

		tree.insert(node, a3, b3, calculateEntropy(a3, b3), false, featureName);
		tree.insert(node, a4, b4, calculateEntropy(a4, b4), true, featureName);

		if(a3==0) {
			node.left.classOutput=false;
		}
		if(b3==0) {
			node.left.classOutput=true;
		}
		if(a4==0) {
			node.right.classOutput=false;
		}
		if(b4==0) {
			node.right.classOutput=true;
		}

		if(a3!=0 && b3!=0){
			String[] updatedFeatureNames=new String[featureNames.length-1];
			int j=0;
			for(int i=0;i<featureNames.length;i++){
				if(i!=index){
					updatedFeatureNames[j++]=featureNames[i];
				}
			}

			boolean[][] updatedData = new boolean[data.length][size];

			int i1=0;
			for(int i=0;i<data.length;i++){
				if(!data[i][index]){
					int j1=0;
					for(int k=0;k<size;k++){
						updatedData[i1][j1++]=data[i][k];
					}
					i1++;
				}
			}

			boolean[][] updatedData1 = new boolean[i1][size-1];

			for(int i=0;i<i1;i++){
				int j1=0;
				for(int k=0;k<size;k++){
					if(k!=index){
						updatedData1[i][j1++]=updatedData[i][k];
					}
				}
			}

			ID3(node.left, updatedFeatureNames, updatedData1);
		}

		if(a4!=0 && b4!=0){
			String[] updatedFeatureNames=new String[featureNames.length-1];
			int j=0;
			for(int i=0;i<featureNames.length;i++){
				if(i!=index){
					updatedFeatureNames[j++]=featureNames[i];
				}
			}

			boolean[][] updatedData = new boolean[data.length][size];

			int i1=0;
			for(int i=0;i<data.length;i++){
				if(data[i][index]){
					int j1=0;
					for(int k=0;k<size;k++){
						updatedData[i1][j1++]=data[i][k];
					}
					i1++;
				}
			}

			boolean[][] updatedData1 = new boolean[i1][size-1];

			for(int i=0;i<i1;i++){
				int j1=0;
				for(int k=0;k<size;k++){
					if(k!=index){
						updatedData1[i][j1++]=updatedData[i][k];
					}
				}
			}

			ID3(node.right, updatedFeatureNames, updatedData1);
		}
	}

	public static void print() {
		tree.preorder(tree.root);
	}

	public static boolean predictOutput(boolean[] input, String[] FeatureNames) {
		Node node = tree.root;

		while(true){
			if(node.classOutput != null) {
				break;
			}
			String name = node.left.featureName;

			int index=0;
			for(;index<FeatureNames.length;index++) {
				if(name.equalsIgnoreCase(FeatureNames[index])) {
					break;
				}
			}
			
			if(!input[index]) {
				node=node.left;
			}else {
				node=node.right;
			}
		}

		return node.classOutput;
	}

	public static void pruning(double pruningFactor, boolean[][] validationData, String[] validationDataFeatureNames, double validationAccuracy) {
		int count = new Double(pruningFactor*tree.size).intValue();
		
		while(count-->0) {
			int count1=15;
			while(count1-->0) {
				boolean flag = true;
				Node n1=tree.getRandomNode(new Random().nextInt(tree.size-1), tree.root, true);
				if(n1==null) {
					continue;
				}
				Node n2 = tree.getParentNode(tree.root, n1);
				Node n3=null;

				if(n2.left==n1) {
					n2.classOutput=calculateOutputAfterRemovingNode(n2,0,0);
					n3=n2.right;
					n2.right = null;
					n2.left=null;
				}else if(n2.right==n1) {
					n2.classOutput=calculateOutputAfterRemovingNode(n2,0,0);
					flag = false;
					n3=n2.left;
					n2.right=null;
					n2.left=null;
				}else {
					continue;
				}


				boolean[] validationOutput= new boolean[validationData.length];
				int correctPredictionsValidationData=0;

				for(int i=0;i<validationData.length;i++) {
					validationOutput[i]=predictOutput(validationData[i], validationDataFeatureNames);
					if(validationOutput[i]==validationData[i][validationData[0].length-1]) {
						correctPredictionsValidationData++;
					}
				}

				if(validationAccuracy<correctPredictionsValidationData*100.0/validationData.length) {
					validationAccuracy=correctPredictionsValidationData*100.0/validationData.length;
					break;
				}else {
					//join node
					n2.classOutput=null;
					if(flag) {
						n2.left=n1;
						n2.right=n3;
					}else {
						n2.right=n1;
						n2.left=n3;
					}
				}
			}
		}
		tree.settingSizeAfterPruning(tree.root, true);
	}

	
	public static boolean calculateOutputAfterRemovingNode(Node node,int count1,int count2) {
		if(node!=null){
			if(node.classOutput!=null) {
				if(node.classOutput) {
					count2+=node.a+node.b;
				} else {
					count1+=node.a+node.b;
				}
			}

			calculateOutputAfterRemovingNode(node.left,count1,count2);
			calculateOutputAfterRemovingNode(node.right,count1,count2);
		}
		return count1<count2;
	}
}
