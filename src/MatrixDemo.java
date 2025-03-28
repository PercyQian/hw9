/**
 * Very simple matrix operations demo
 */
public class MatrixDemo {
    
    public static void main(String[] args) {
        System.out.println("Matrix Demo - Press Enter to show a 3x3 constant matrix");
        
        try {
            // Wait for user to press Enter
            System.in.read();
            
            // Simply show a constant matrix demo
            MatrixMap<Integer> matrix = MatrixMap.constant(3, 5);
            System.out.println("\n3x3 Constant Matrix (value 5):");
            System.out.println(matrix);
            
            // Also show a RoamingMap example
            System.out.println("\nRoamingMap Example:");
            RoamingMap<String, Integer> map = new RoamingMap<>();
            map.put("one", 1);
            map.put("two", 2);
            System.out.println("Map contents: " + map);
            
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} 