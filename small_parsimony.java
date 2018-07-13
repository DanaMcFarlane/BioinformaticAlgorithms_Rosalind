import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class SmallParsimonyProblem {
	public static void smallParsimony (ArrayList<String> labels, 
		TreeMap<Integer, ArrayList<Integer>> tree, HashMap<Integer, Integer> parents) {
		
		ArrayList<Character> alphabet = new ArrayList<Character>();
		Collections.addAll(alphabet, 'A', 'C', 'G', 'T');
		ArrayList<int[][]> sk = new ArrayList<int[][]>();
		
	 /* for each symbol k in the alphabet, sk(v) = 0 else max integer value
		col ex = [A C G T]
		row = [C; A; A; A; T; C; C; C] - string indexed
		do this for each individual index of the strings at the leaves  */
		for(String label : labels) {
			int[][] sk_labels = new int[label.length()][4];
			for(int[] row : sk_labels) {
				Arrays.fill(row, 48);
			}
			for(int i = 0; i < label.length(); i++) {
				int k = alphabet.indexOf(label.charAt(i));
				sk_labels[i][k] = 0;
			}
			//after each one, we add a new sk to the list
			sk.add(sk_labels);
		}
		//adding sk values for the external nodes
		for(int i = labels.size(); i < 2*(labels.size()-1) + 1; i++) {
			int[][] sk_labels = new int[labels.get(0).length()][4];
			for(int[] row : sk_labels) {
				Arrays.fill(row, 48);
			}
			sk.add(sk_labels);
		}
		//start at root, and traverse to leaf nodes
		traverse(alphabet, tree, sk, tree.lastKey());
		char[][] chars = new char[sk.size()][sk.get(0).length];
		
		//add labels to char
		for(int i = 0; i < labels.size(); i++) {
			chars[i] = labels.get(i).toCharArray();
		}

		backtrack(alphabet, tree, sk, tree.lastKey(), parents, chars);

		//gets the parsimony
		int parsimony = 0;
		for(int i = 0; i < sk.get(tree.lastKey()).length; i++) {
			Arrays.sort(sk.get(tree.lastKey())[i]);
			parsimony += sk.get(tree.lastKey())[i][0];
		}
		System.out.println(parsimony);
		ArrayList<String> strings = new ArrayList<String>();
		String parent;
		String left_child;
		String right_child;

		//concatenate the parents
		//concatenate the left child
		//concatenate the right child
		for(int key: tree.keySet()) {
			parent = new String(chars[key]);
			left_child = new String(chars[tree.get(key).get(0)]);
			right_child = new String(chars[tree.get(key).get(1)]);
			strings.add(parent + "->" + left_child + ":" + distance(parent, left_child));
			strings.add(parent + "->" + right_child + ":" + distance(parent, right_child));
		}

		//splitting the string and then reconstructing it
		for(String s : strings) {
			System.out.println(s);
			String[] split = s.split("->");
			String get_parent = split[0];
			String child = split[1].split(":")[0];
			int distance = Integer.parseInt(split[1].split(":")[1]);
			System.out.println(child + "->" + get_parent + ":" + distance);
		}
	}
	
	//return the hamming distance between both strings
	public static int distance(String a, String b) {
		int count = 0;
		for(int i = 0; i < a.length(); i++) {
			if(a.charAt(i) != b.charAt(i)) {
				count++;
			}
		}
		return count;
	}
	
	//getting sk values
	public static void traverse(ArrayList<Character> alphabet, 
			TreeMap<Integer, ArrayList<Integer>> tree, ArrayList<int[][]> sk, int root) {
		
		//once we get to our leaves, return 
		if(root < tree.firstKey()) {
			return;
		} 
		//postorder traversal
		int daughter = tree.get(root).get(0);
		int son = tree.get(root).get(1);
		traverse(alphabet, tree, sk, daughter);
		traverse(alphabet, tree, sk, son);
		
		/* for each symbol k in the alphabet
   			sk(v) ← minimum over all symbols i {si(Daughter(v))+δi,k} + minimum over all symbols j {sj(Son(v))+δj,k}
		 */
		//get sk for left tree and right tree
		for(int i = 0; i < sk.get(0).length; i++) {
			for(char k : alphabet) {
				int min = Integer.MAX_VALUE;
				int[] delta_son = {1, 1, 1, 1};
				delta_son[alphabet.indexOf(k)] = 0;
				int[] delta_daughter = {1, 1, 1, 1};
				delta_daughter[alphabet.indexOf(k)] = 0;
				int check = find_min(sum(sk.get(daughter)[i],delta_daughter), 
					sum(sk.get(son)[i],delta_son));
				if(check < min) {
					min = check;
				}
				sk.get(root)[i][alphabet.indexOf(k)] = min;
			}
		}
	}
	
	//backtrack sk
	public static void backtrack(ArrayList<Character> alphabet, 
			TreeMap<Integer, ArrayList<Integer>> tree, ArrayList<int[][]> sk, 
			int root, HashMap<Integer, Integer> parents, char[][] chars) {

		if(root < tree.firstKey()) {
			return;
		} 
		for(int i = 0; i < sk.get(root).length; i++) { 
			int[] c = sk.get(root)[i];
			//if it's the root, just return the character at the min parsimony
			if(root == tree.lastKey()) {
				int min = c[0];
				int index = 0;
				for (int j = 0; j < sk.get(root)[i].length; j++) {
					int check = c[j];
					if(check < min) {
						min = check;
						index = j;
					}
				}
				chars[root][i] = alphabet.get(index);

			} else {
				//get the element at the head, and backtrack from the minimum
				int place = alphabet.indexOf(chars[parents.get(root)][i]);
				int[] delta = {1,1,1,1};
				delta[place] = 0;
				sum(c, delta);
				int min = delta[0];
				int index = 0;
				for (int j = 0; j < delta.length; j++) {
					int check = delta[j];
					if(check < min) {
						min = check;
						index = j;
					}
				}
				chars[root][i] = alphabet.get(index);
			}
		}
		//after we finish a leaf, backtrack for its daughter and son
		backtrack(alphabet, tree, sk, tree.get(root).get(0), parents, chars);
		backtrack(alphabet, tree, sk, tree.get(root).get(1), parents, chars);
	}
	
	public static int[] sum(int[] a, int[] b) {
		for(int i = 0; i < a.length; i++) {
			b[i] += a[i];
		}
		return b;
	}
	public static int find_min(int[] a, int[] b) {
		Arrays.sort(a);
		Arrays.sort(b);
		return a[0] + b[0];
	}
	
	public static void main(String[] args) throws FileNotFoundException {
	/******************************************************************
		Sample Input: 
			4
			4->CAAATCCC
			4->ATTGCGAC
			5->CTGCGCTG
			5->ATGGACGA
			6->4
			6->5
		Sample Output:
			16
			ATTGCGAC->ATAGCCAC:2
			ATAGACAA->ATAGCCAC:2
			ATAGACAA->ATGGACTA:2
			ATGGACGA->ATGGACTA:1
			CTGCGCTG->ATGGACTA:4
			ATGGACTA->CTGCGCTG:4
			ATGGACTA->ATGGACGA:1
			ATGGACTA->ATAGACAA:2
			ATAGCCAC->CAAATCCC:5
			ATAGCCAC->ATTGCGAC:2
			ATAGCCAC->ATAGACAA:2
			CAAATCCC->ATAGCCAC:5
	******************************************************************/
		File file = new File("rosalind_ba7f.txt");
		Scanner sc = new Scanner(file);
		int n = sc.nextInt();
		sc.nextLine();
		TreeMap<Integer, ArrayList<Integer>> tree = new TreeMap<Integer, ArrayList<Integer>>();
		HashMap<Integer, Integer> parents = new HashMap<Integer, Integer>();
		ArrayList<String> labels = new ArrayList<String>();
		int count = -1;
		while(sc.hasNext()) {
			String s = sc.nextLine();
			if(s.equals("exit")) {
				break;
			}
			String[] split = s.split("->");
			int first = Integer.parseInt(split[0]);
			
			//creating the tree structure
			//if the second element is an edge labeling
			if(isInteger(split[1])) {
				if(tree.containsKey(first)) {
					tree.get(first).add(Integer.parseInt(split[1]));
				} else {
					tree.put(first, new ArrayList<Integer>());
					tree.get(first).add(Integer.parseInt(split[1]));
				}
			//if it's a leaf/a sequence of characters
				parents.put(Integer.parseInt(split[1]), first);
			} else {
				count++;
				labels.add(split[1]);
				if(tree.containsKey(first)) {
					tree.get(first).add(count);
				} else {
					tree.put(first, new ArrayList<Integer>());
					tree.get(first).add(count);
				}
				parents.put(count, first);
			} 
		}
		
		smallParsimony(labels, tree, parents);
		
	}
	public static boolean isInteger(String s) {
	      boolean isValidInteger = false;
	      try {
	         Integer.parseInt(s);
	         isValidInteger = true;
	      }
	      catch (NumberFormatException ex) {}
	 
	      return isValidInteger;
	   }
 
}