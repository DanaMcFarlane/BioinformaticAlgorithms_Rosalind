import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;
public class LloydsAlgorithm {
	public static void main(String[] args) throws FileNotFoundException {
	/******************************************************************
		Given: Integers k and m followed by a set of points, Data, in m-dimensional space
		Return: A set Centers consisting of k points (centers) resulting from applying the Lloyd algorithm to Data and Centers, where the first k points from Dana are selected as the first k centers. 
		Sample Input: 
			2 2
			1.3 1.1
			1.3 0.2
			0.6 2.8
			3.0 3.2
			1.2 0.7
			1.4 1.6
			1.2 1.0
			1.2 1.1
			0.6 1.5
			1.8 2.6
			1.2 1.3
			1.2 1.0
			0.0 1.9
		Sample Output:
			1.800 2.867
			1.060 1.140
	******************************************************************/
		File file = new File("rosalind_ba8c.txt");
		Scanner sc = new Scanner(file);
		int k = sc.nextInt();
		int m = sc.nextInt();
		ArrayList<double[]> points = new ArrayList<double[]>();
		while(sc.hasNextDouble()) {
			double[] s = new double[m];
			for(int i = 0; i < m; i++) {
				s[i] = sc.nextDouble();
			}
			points.add(s);
		}
		
		//first k data point(s) = first k center(s)
		ArrayList<double[]> center = new ArrayList<double[]>();
		for(int i = 0; i < k; i++) {
			center.add(points.get(i));
		}
		//when the points converge
		ArrayList<double[]> ret = kmeans(center, k, points, m);
		for(double[] x: ret) {
			for(int i = 0; i < x.length; i++) {
				DecimalFormat df = new DecimalFormat("#.000");
				System.out.print(df.format(x[i])+ " ");
			}
			System.out.println();
		}
	}
		

	public static ArrayList<double[]> kmeans(ArrayList<double[]> center, int k, ArrayList<double[]> points, int m) {
		//calculate nearest center
		double converge = Double.POSITIVE_INFINITY;
		while(converge > 0) {
			ArrayList<double[]> centers = new ArrayList<double[]>();

			//with every iteration, we'll have new centers
			HashMap<double[], ArrayList<double[]>> clusters = new HashMap<double[], ArrayList<double[]>>();	
			for(int i = 0; i < k; i++) {	
				clusters.put(center.get(i), new ArrayList<double[]>());
			}
			
			ArrayList<Integer> indices = new ArrayList<Integer>();
			for(int i = 0; i < points.size(); i++) {
				double nearest_center_val = Double.POSITIVE_INFINITY;
				double[] nearest_center_arr = center.get(0);
				int index = -1;
				//find the best center for a single point
				for(int j = 0; j < k; j++) {
					double check = distance(points.get(i), center.get(j), m);
					if(check < nearest_center_val) {
						nearest_center_val = check;
						nearest_center_arr = center.get(j);
						index = j;
					}
				}
				//getting clusters
				indices.add(index);
				clusters.get(nearest_center_arr).add(points.get(i));
			}
			
			//find the center of gravity for each cluster
			for(double[] key: clusters.keySet()) {
				double[] x = center_of_gravity(clusters.get(key), m);
				centers.add(x);
			}
			
			double sum = 0.0;
			for(int i = 0; i < k; i++) {
			   sum += distance(centers.get(i), center.get(i), m);
			}
			converge = sum;
			center = centers;
		}
		return center;
	}
	
	public static double distance(double[] point, double[] center, int m) {
		double sum = 0.0;
		for(int i = 0; i < m; i++) {
			sum += (point[i] - center[i])*(point[i] - center[i]);
		}
		return Math.sqrt(sum);
	}
	
	public static double[] center_of_gravity(ArrayList<double[]> cluster, int m) {
		for(int j = 0; j < cluster.size(); j++) {
		}
		double[] center = new double[m];
		for(int i = 0; i < m; i++) {
			double sum = 0.0;
			for(int j = 0; j < cluster.size(); j++) {
				sum += cluster.get(j)[i];
			}
			center[i] = sum/cluster.size();	
		}
		return center;
	}
}

