import java.util.HashMap;

public class BinaryTree
{

	Node root;
	public HashMap<String, Integer> map = new HashMap<String, Integer>();
	int counter = 0, size = 0, noOfLeafNodes = 0, count = 0;
	Node randomNode=null, parentNode=null;

	public BinaryTree(){
		root = null;
	}

	public boolean isEmpty(){
		return root == null;
	}

	public void insert(int a, int b, double entropy, boolean data, String featureName){
		root = insert(root, a, b, entropy, data, featureName);
		root.counter=count++;
		size++;
	}

	public Node insert(Node node, int a, int b, double entropy, boolean data, String featureName){
		if (node == null){
			node = new Node(a, b, entropy, data, featureName);
			node.counter=count++;
			size++;
		}else{
			if (node.left  == null){
				node.left = insert(node.left, a, b, entropy, data, featureName);
				node.left.counter=count++;
				size++;
			}else{
				node.right = insert(node.right, a, b, entropy, data, featureName);
				node.right.counter=count++;
				size++;
			}
		}
		return node;
	}

	public void preorder(Node node){
		if (node != null){
			if(!node.featureName.equals("TopNode")) {
				if(!map.containsKey(node.featureName)){
					map.put(node.featureName, counter);
				}else{
					counter=map.get(node.featureName);
					map.remove(node.featureName);
				}

				for(int i=0;i<counter;i++) {
					System.out.print("| ");
				}

				int a=0;
				if(node.data){
					a=1;
				}
				System.out.print(node.featureName+" = "+a+" : ");
				if(node.classOutput!=null){
					int b=0;
					if(node.classOutput){
						b=1;
					}
					System.out.println(b);
				}else {
					counter++;
					System.out.println();
				}
			}

			preorder(node.left);
			preorder(node.right);
		}
	}

	public int getLeafNodes(Node node, boolean flag){
		if(flag){
			noOfLeafNodes = 0;
		}

		if(node!=null){
			if(node.classOutput!=null){
				noOfLeafNodes++;
			}
			getLeafNodes(node.left, false);
			getLeafNodes(node.right, false);
		}
		return noOfLeafNodes;
	}
	
	public int settingSizeAfterPruning(Node node, boolean flag){
		if(flag){
			size = 0;
		}

		if(node!=null){
			size++;
			
			settingSizeAfterPruning(node.left, false);
			settingSizeAfterPruning(node.right, false);
		}
		return noOfLeafNodes;
	}

	public Node getRandomNode(int randomNumber, Node node, boolean flag) {
		if(flag){
			randomNode=null;
		}
		if(node!=null){
			if(node.counter==randomNumber){
				randomNode=node;
			}
			getRandomNode(randomNumber, node.left, false);
			getRandomNode(randomNumber, node.right, false);
		}

		return randomNode;
	}

	public Node getParentNode(Node node, Node child) {
		if(node!=null){
			if(node.left==child || node.right==child){
				parentNode=node;
			}

			getParentNode(node.left, child);
			getParentNode(node.right, child);
		}

		return parentNode;
	}

	public void trim(Node node) {
		if(node!=null) {
			if(node.left!=null && node.left.featureName.equals("")) {
				node.classOutput=node.left.classOutput;
				node.left=null;
				node.right=null;
			}
			
			trim(node.left);
			trim(node.right);
		}
	}
} 